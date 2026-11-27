package sk.uniba.fmph.dcs.power_grid;

import static org.junit.Assert.assertEquals;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/** Tests for {@link MaximumNumberOfUsablePowerPlantsImpl}. */
public class MaximumNumberOfUsablePowerPlantsImplTest {

    /**
     * Creates a test power plant.
     *
     * @param number
     *            the plant number
     * @param accepted
     *            the accepted resource types
     * @param required
     *            the fuel needed per turn
     * @param cities
     *            the number of cities the plant can supply
     *
     * @return the power plant
     */
    private static PowerPlant plant(int number, List<Resource> accepted, int required, int cities) {
        return new PowerPlant(new PowerPlantNumber(number), accepted, required, cities);
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

    private final MaximumNumberOfUsablePowerPlants maximum = new MaximumNumberOfUsablePowerPlantsImpl();

    /** All plants are used when every one can be fueled. */
    @Test
    public void usesAllPlantsWhenResourcesAllow() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2, 1);
        PowerPlant oil = plant(4, List.of(Resource.OIL), 1, 2);
        assertEquals(3, maximum.numberOfSuppliableCities(List.of(coal, oil),
                counts(Resource.COAL, Resource.COAL, Resource.OIL)));
    }

    /** A plant without fuel is simply skipped. */
    @Test
    public void skipsPlantsThatCannotBeFueled() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2, 1);
        PowerPlant oil = plant(4, List.of(Resource.OIL), 1, 2);
        assertEquals(1, maximum.numberOfSuppliableCities(List.of(coal, oil), counts(Resource.COAL, Resource.COAL)));
    }

    /** When plants share fuel, the most productive combination wins. */
    @Test
    public void choosesTheBestSubsetWhenPlantsShareFuel() {
        PowerPlant small = plant(3, List.of(Resource.COAL), 2, 1);
        PowerPlant hybrid = plant(7, List.of(Resource.COAL, Resource.OIL), 2, 5);
        assertEquals(5, maximum.numberOfSuppliableCities(List.of(small, hybrid), counts(Resource.COAL, Resource.COAL)));
    }

    /** Ecological plants contribute for free. */
    @Test
    public void includesEcologicalPlantsForFree() {
        PowerPlant ecological = plant(50, List.of(), 0, 2);
        PowerPlant coal = plant(3, List.of(Resource.COAL), 1, 1);
        assertEquals(3, maximum.numberOfSuppliableCities(List.of(ecological, coal), counts(Resource.COAL)));
    }

    /** Unusable plants yield zero cities. */
    @Test
    public void returnsZeroWithoutUsablePlants() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 1, 4);
        assertEquals(0, maximum.numberOfSuppliableCities(List.of(coal), Map.of()));
    }

    /** A subset may leave resources unused when that gives a higher yield. */
    @Test
    public void allowsSubsetThatLeavesResourcesUnused() {
        PowerPlant efficient = plant(3, List.of(Resource.COAL), 1, 5);
        PowerPlant greedy = plant(4, List.of(Resource.COAL), 2, 4);
        assertEquals(5,
                maximum.numberOfSuppliableCities(List.of(efficient, greedy), counts(Resource.COAL, Resource.COAL)));
    }
}
