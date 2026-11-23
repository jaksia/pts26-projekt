package sk.uniba.fmph.dcs.power_grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/** Tests for {@link UsePowerPlantsImpl}. */
public class UsePowerPlantsImplTest {

    /**
     * Creates a test power plant.
     *
     * @param number the plant number
     * @param accepted the accepted resource types
     * @param required the fuel needed per turn
     * @param cities the number of cities the plant can supply
     * @return the power plant
     */
    private static PowerPlant plant(int number, List<Resource> accepted, int required, int cities) {
        return new PowerPlant(new PowerPlantNumber(number), accepted, required, cities);
    }

    /**
     * Counts the given resource tokens into a map.
     *
     * @param resources the tokens to count
     * @return counts by resource type
     */
    private static Map<Resource, Integer> counts(Resource... resources) {
        Map<Resource, Integer> counts = new EnumMap<>(Resource.class);
        for (Resource resource : resources) {
            counts.merge(resource, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * Extracts the success value from a variant result.
     *
     * @param result the result to inspect
     * @return the number of supplied cities
     */
    private static int expectCities(Variant<Integer, RequestError> result) {
        assertTrue(result instanceof Variant.Success);
        return (Integer) ((Variant.Success<?, ?>) result).value();
    }

    private final UsePowerPlants usePowerPlants = new UsePowerPlantsImpl();

    /** Exactly the required tokens are accepted. */
    @Test
    public void burnsExactResourceAmount() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2, 1);
        assertEquals(1, expectCities(usePowerPlants.numberOfSuppliableCities(
                List.of(coal), counts(Resource.COAL, Resource.COAL))));
    }

    /** Too few tokens, or tokens of the wrong type, are rejected. */
    @Test
    public void rejectsInsufficientOrUnburnableResources() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2, 1);
        assertTrue(usePowerPlants.numberOfSuppliableCities(
                List.of(coal), counts(Resource.COAL)) instanceof Variant.Failure);
        assertTrue(usePowerPlants.numberOfSuppliableCities(
                List.of(coal), counts(Resource.OIL, Resource.OIL)) instanceof Variant.Failure);
    }

    /** Tokens beyond the exact requirement are reported as an error. */
    @Test
    public void rejectsSurplusResources() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 2, 1);
        assertTrue(usePowerPlants.numberOfSuppliableCities(
                List.of(coal), counts(Resource.COAL, Resource.COAL, Resource.COAL)) instanceof Variant.Failure);
    }

    /** A hybrid plant draws its requirement from coal and oil together. */
    @Test
    public void hybridPlantMayMixCoalAndOil() {
        PowerPlant hybrid = plant(7, List.of(Resource.COAL, Resource.OIL), 2, 3);
        assertEquals(3, expectCities(usePowerPlants.numberOfSuppliableCities(
                List.of(hybrid), counts(Resource.COAL, Resource.OIL))));
    }

    /** Ecological plants supply cities without consuming anything. */
    @Test
    public void ecologicalPlantNeedsNoResources() {
        PowerPlant ecological = plant(50, List.of(), 0, 2);
        assertEquals(2, expectCities(usePowerPlants.numberOfSuppliableCities(List.of(ecological), Map.of())));
    }

    /** The supplied cities of all selected plants are summed. */
    @Test
    public void sumsCitiesOfAllSelectedPlants() {
        PowerPlant coal = plant(3, List.of(Resource.COAL), 1, 2);
        PowerPlant oil = plant(4, List.of(Resource.OIL), 1, 3);
        assertEquals(5, expectCities(usePowerPlants.numberOfSuppliableCities(
                List.of(coal, oil), counts(Resource.COAL, Resource.OIL))));
    }
}
