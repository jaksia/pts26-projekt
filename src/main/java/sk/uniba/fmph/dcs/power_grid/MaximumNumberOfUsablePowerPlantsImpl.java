package sk.uniba.fmph.dcs.power_grid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Finds the best subset of power plants for a given set of resources.
 */
public final class MaximumNumberOfUsablePowerPlantsImpl implements MaximumNumberOfUsablePowerPlants {

    /** A hybrid plant accepts exactly coal and oil. */
    private static final int HYBRID_RESOURCE_TYPES = 2;

    @Override
    public int numberOfSuppliableCities(final List<PowerPlant> powerPlants, final Map<Resource, Integer> resources) {
        validate(resources);
        // Start with an empty selection and zero cities; unused resources are allowed.
        return best(powerPlants, resources, 0, new ArrayList<>(), 0);
    }

    /**
     * Explores every subset of the available plants and keeps the best feasible one.
     *
     * @param powerPlants
     *            the plants still to decide on
     * @param resources
     *            counts of each available resource type
     * @param index
     *            index of the next plant to include or skip
     * @param chosen
     *            plants included so far
     * @param cities
     *            cities supplied by the chosen plants so far
     *
     * @return the largest feasible city count from this search state
     */
    private static int best(final List<PowerPlant> powerPlants, final Map<Resource, Integer> resources, final int index,
            final List<PowerPlant> chosen, final int cities) {
        if (index == powerPlants.size()) {
            return canRun(chosen, resources) ? cities : 0;
        }
        PowerPlant plant = powerPlants.get(index);
        chosen.add(plant);
        int withPlant = best(powerPlants, resources, index + 1, chosen, cities + plant.numberOfCities());
        chosen.remove(chosen.size() - 1);
        int withoutPlant = best(powerPlants, resources, index + 1, chosen, cities);
        return Math.max(withPlant, withoutPlant);
    }

    /**
     * Checks whether a selection can be fueled by at most the available resources.
     *
     * @param powerPlants
     *            the selected plants
     * @param resources
     *            counts of each available resource type
     *
     * @return {@code true} if every selected plant can run
     */
    private static boolean canRun(final List<PowerPlant> powerPlants, final Map<Resource, Integer> resources) {
        Map<Resource, Integer> remaining = new EnumMap<>(Resource.class);
        remaining.putAll(resources);
        int hybridNeed = 0;
        for (PowerPlant plant : powerPlants) {
            int needed = plant.requiredResourceAmount();
            List<Resource> accepted = plant.acceptedResources();
            if (accepted.isEmpty()) {
                continue; // ecological plants need no fuel
            }
            if (accepted.size() == HYBRID_RESOURCE_TYPES) {
                hybridNeed += needed; // COAL+OIL hybrid draws from the residual pool
            } else {
                Resource type = accepted.get(0);
                if (count(remaining, type) < needed) {
                    return false;
                }
                remaining.put(type, count(remaining, type) - needed);
            }
        }
        return count(remaining, Resource.COAL) + count(remaining, Resource.OIL) >= hybridNeed;
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
