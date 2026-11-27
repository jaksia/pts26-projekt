package sk.uniba.fmph.dcs.power_grid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Checks resource storage capacity using the power plants' own capacities.
 */
public final class StoreResourcesImpl implements StoreResources {

    /** A plant stores twice the fuel it needs to run. */
    private static final int CAPACITY_FACTOR = 2;

    /** A hybrid plant accepts exactly coal and oil. */
    private static final int HYBRID_RESOURCE_TYPES = 2;

    @Override
    public boolean canStoreResources(final Map<Resource, Integer> resources, final List<PowerPlant> powerPlants) {
        validate(resources);

        // Type-specific capacity is filled first: it cannot be used by another type.
        Map<Resource, Integer> dedicatedCapacity = new EnumMap<>(Resource.class);
        int hybridCapacity = 0;
        for (PowerPlant plant : powerPlants) {
            int capacity = CAPACITY_FACTOR * plant.requiredResourceAmount();
            List<Resource> accepted = plant.acceptedResources();
            if (accepted.isEmpty()) {
                continue; // ecological plants cannot store anything
            }
            if (accepted.size() == HYBRID_RESOURCE_TYPES) {
                hybridCapacity += capacity; // COAL+OIL share one pool (enforced by PowerPlant)
            } else {
                dedicatedCapacity.merge(accepted.get(0), capacity, Integer::sum);
            }
        }

        // Hybrids only accept coal/oil, so other types must fit their dedicated capacity.
        for (Resource type : List.of(Resource.GARBAGE, Resource.URANIUM)) {
            if (count(resources, type) > dedicatedCapacity.getOrDefault(type, 0)) {
                return false;
            }
        }
        int coalOnHybrid = Math.max(0,
                count(resources, Resource.COAL) - dedicatedCapacity.getOrDefault(Resource.COAL, 0));
        int oilOnHybrid = Math.max(0, count(resources, Resource.OIL) - dedicatedCapacity.getOrDefault(Resource.OIL, 0));
        return coalOnHybrid + oilOnHybrid <= hybridCapacity;
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
