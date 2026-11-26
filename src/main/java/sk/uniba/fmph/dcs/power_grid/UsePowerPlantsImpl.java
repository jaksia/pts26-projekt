package sk.uniba.fmph.dcs.power_grid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Validates a power plant selection and computes the number of cities it supplies.
 */
public final class UsePowerPlantsImpl implements UsePowerPlants {

    /** A hybrid plant accepts exactly coal and oil. */
    private static final int HYBRID_RESOURCE_TYPES = 2;

    @Override
    public Variant<Integer, RequestError> numberOfSuppliableCities(final List<PowerPlant> powerPlants,
            final Map<Resource, Integer> resources) {
        validate(resources);

        Map<Resource, Integer> remaining = new EnumMap<>(Resource.class);
        remaining.putAll(resources);
        int hybridNeed = 0;
        int required = 0;
        int cities = 0;
        for (PowerPlant plant : powerPlants) {
            int needed = plant.requiredResourceAmount();
            required += needed;
            List<Resource> accepted = plant.acceptedResources();
            if (accepted.size() == HYBRID_RESOURCE_TYPES) {
                // COAL+OIL hybrid: its fuel is taken from the residual coal/oil pool.
                hybridNeed += needed;
            } else if (!accepted.isEmpty()) { // If not ecological---ecological plants need no fuel.
                Resource type = accepted.get(0);
                if (count(remaining, type) < needed) {
                    return Variant
                            .failure(new RequestError("Not enough " + type + " for power plant " + plant.number()));
                }
                remaining.put(type, count(remaining, type) - needed);
            }
            cities += plant.numberOfCities();
        }

        // Single-fuel plants are served first; hybrids can use either remaining fuel.
        if (count(remaining, Resource.COAL) + count(remaining, Resource.OIL) < hybridNeed) {
            return Variant.failure(new RequestError("Not enough coal/oil for hybrid power plants."));
        }
        int surplus = total(resources) - required;
        if (surplus > 0) {
            return Variant.failure(new RequestError("Surplus resources: " + surplus + " token(s) cannot be burned."));
        }
        return Variant.success(cities);
    }

    /**
     * Reads a resource count, treating missing types as zero.
     *
     * @param resources
     *            the counts to read
     * @param type
     *            the resource type
     *
     * @return the count of {@code type}
     */
    private static int count(final Map<Resource, Integer> resources, final Resource type) {
        return resources.getOrDefault(type, 0);
    }

    /**
     * Sums all resource counts.
     *
     * @param resources
     *            the counts to sum
     *
     * @return the total number of resource tokens
     */
    private static int total(final Map<Resource, Integer> resources) {
        int total = 0;
        for (int count : resources.values()) {
            total += count;
        }
        return total;
    }

    /**
     * Rejects null or negative resource counts.
     *
     * @param resources
     *            the counts to check
     */
    private static void validate(final Map<Resource, Integer> resources) {
        if (resources == null) {
            throw new IllegalArgumentException("Resource counts may not be null");
        }
        for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() < 0) {
                throw new IllegalArgumentException("Invalid resource counts: " + resources);
            }
        }
    }
}
