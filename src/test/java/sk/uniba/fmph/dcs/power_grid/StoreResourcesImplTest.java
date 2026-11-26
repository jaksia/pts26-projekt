package sk.uniba.fmph.dcs.power_grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/** Tests for {@link StoreResourcesImpl}. */
public class StoreResourcesImplTest {

    /**
     * Creates a test power plant supplying one city.
     *
     * @param number
     *            the plant number
     * @param accepted
     *            the accepted resource types
     * @param required
     *            the fuel needed per turn
     *
     * @return the power plant
     */
    private static PowerPlant plant(int number, List<Resource> accepted, int required) {
        return new PowerPlant(new PowerPlantNumber(number), accepted, required, 1);
    }

    /**
     * Counts the given resource tokens into a map.
     *
     * @param resources
     *            the tokens to count
     *
     * @return counts by resource type
     */
    private static Map<Resource, Integer> counts(Resource... resources) {
        Map<Resource, Integer> counts = new EnumMap<>(Resource.class);
        for (Resource resource : resources) {
            counts.merge(resource, 1, Integer::sum);
        }
        return counts;
    }

    private final StoreResources storeResources = new StoreResourcesImpl();

    /** A single-fuel plant stores exactly twice its firing requirement. */
    @Test
    public void storesTwiceTheRequiredAmountOfAcceptedResource() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2);
        assertTrue(storeResources.canStoreResources(counts(Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL),
                List.of(coal)));
        assertFalse(storeResources.canStoreResources(
                counts(Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL), List.of(coal)));
        assertFalse(storeResources.canStoreResources(counts(Resource.OIL), List.of(coal)));
    }

    /** Coal and oil compete for the same hybrid capacity. */
    @Test
    public void hybridPlantSharesCapacityBetweenCoalAndOil() {
        PowerPlant hybrid = plant(7, List.of(Resource.COAL, Resource.OIL), 2);
        assertTrue(storeResources.canStoreResources(counts(Resource.COAL, Resource.COAL, Resource.OIL, Resource.OIL),
                List.of(hybrid)));
        assertFalse(storeResources.canStoreResources(
                counts(Resource.COAL, Resource.COAL, Resource.COAL, Resource.OIL, Resource.OIL), List.of(hybrid)));
    }

    /** Capacity adds up across plants accepting the same type. */
    @Test
    public void capacityIsSummedOverPlantsAcceptingTheResource() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2);
        PowerPlant oil = plant(4, List.of(Resource.OIL), 1);
        assertTrue(storeResources.canStoreResources(
                counts(Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL, Resource.OIL, Resource.OIL),
                List.of(coal, oil)));
        assertFalse(storeResources.canStoreResources(
                counts(Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL, Resource.COAL), List.of(coal, oil)));
    }

    /** Ecological plants have no storage space. */
    @Test
    public void ecologicalPlantStoresNothing() {
        PowerPlant ecological = plant(50, List.of(), 0);
        assertTrue(storeResources.canStoreResources(Map.of(), List.of(ecological)));
        assertFalse(storeResources.canStoreResources(counts(Resource.URANIUM), List.of(ecological)));
    }
}
