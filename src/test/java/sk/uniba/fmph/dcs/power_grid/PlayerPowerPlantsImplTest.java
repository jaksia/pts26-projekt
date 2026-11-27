package sk.uniba.fmph.dcs.power_grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/** Tests for {@link PlayerPowerPlantsImpl}. */
public class PlayerPowerPlantsImplTest {

    private static final PowerPlantNumber COAL_NUMBER = new PowerPlantNumber(3);
    private static final PowerPlantNumber OIL_NUMBER = new PowerPlantNumber(4);
    private static final PowerPlantNumber UNOWNED_NUMBER = new PowerPlantNumber(5);

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

    private PowerPlant coal;
    private PowerPlant oil;
    private PowerPlant unowned;
    private Map<PowerPlantNumber, PowerPlant> catalog;
    private PlayerPowerPlantsImpl player;

    /** Builds a player with two owned plants and one plant only in the catalog. */
    @Before
    public void setUp() {
        coal = plant(3, List.of(Resource.COAL), 1, 2);
        oil = plant(4, List.of(Resource.OIL), 1, 3);
        unowned = plant(5, List.of(Resource.COAL), 1, 4);
        catalog = Map.of(COAL_NUMBER, coal, OIL_NUMBER, oil, UNOWNED_NUMBER, unowned);
        player = new PlayerPowerPlantsImpl(new StoreResourcesImpl(), new UsePowerPlantsImpl(),
                new MaximumNumberOfUsablePowerPlantsImpl());
    }

    /** The highest owned plant number is returned. */
    @Test
    public void bestPowerPlantIsTheHighestNumber() {
        assertThrows(IllegalStateException.class, () -> player.bestPowerPlant());
        player.receivePowerPlant(coal);
        assertEquals(COAL_NUMBER, player.bestPowerPlant());
        player.receivePowerPlant(oil);
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
    }

    /** Reaching the limit leaves the existing plants untouched. */
    @Test
    public void receivePowerPlantRespectsLimitWithoutChangingState() {
        PlayerPowerPlantsImpl limited = new PlayerPowerPlantsImpl(new StoreResourcesImpl(), new UsePowerPlantsImpl(),
                new MaximumNumberOfUsablePowerPlantsImpl(), 2);
        assertTrue(limited.receivePowerPlant(coal));
        assertTrue(limited.receivePowerPlant(oil));
        assertFalse(limited.receivePowerPlant(unowned));
        assertEquals(OIL_NUMBER, limited.bestPowerPlant());
    }

    /** Removing a plant succeeds when the survivors fit the remaining capacity. */
    @Test
    public void removePowerPlantChecksStorageOfRemainingResources() {
        player.receivePowerPlant(coal);
        player.receivePowerPlant(oil);

        assertEquals(Optional.empty(), player.removePowerPlant(COAL_NUMBER, counts(Resource.OIL)));
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
    }

    /** Failed removals do not change the player's plants. */
    @Test
    public void failedRemovalLeavesStateUnchanged() {
        player.receivePowerPlant(coal);
        player.receivePowerPlant(oil);

        assertTrue(player.removePowerPlant(COAL_NUMBER, counts(Resource.OIL)).isEmpty());
        assertTrue(player.removePowerPlant(OIL_NUMBER, counts(Resource.OIL)).isPresent());
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
        assertTrue(player.removePowerPlant(COAL_NUMBER, Map.of()).isPresent());
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
    }

    /** Storage validation is delegated to the helper. */
    @Test
    public void canStoreResourcesDelegates() {
        player.receivePowerPlant(coal);
        assertTrue(player.canStoreResources(counts(Resource.COAL, Resource.COAL)));
        assertFalse(player.canStoreResources(counts(Resource.COAL, Resource.COAL, Resource.COAL)));
        assertFalse(player.canStoreResources(counts(Resource.OIL)));
    }

    /** The maximum city count is delegated to the helper. */
    @Test
    public void numberOfSuppliableCitiesDelegates() {
        player.receivePowerPlant(coal);
        assertEquals(2, player.numberOfSuppliableCities(counts(Resource.COAL)));
        player.receivePowerPlant(oil);
        assertEquals(5, player.numberOfSuppliableCities(counts(Resource.COAL, Resource.OIL)));
    }

    /** A valid selection returns cities without consuming anything. */
    @Test
    public void usePowerPlantReturnsCitiesWithoutChangingState() {
        player.receivePowerPlant(coal);
        player.receivePowerPlant(oil);

        Variant<Integer, RequestError> result = player.usePowerPlant(List.of(OIL_NUMBER, COAL_NUMBER),
                counts(Resource.OIL, Resource.COAL));
        assertTrue(result instanceof Variant.Success);
        assertEquals(Integer.valueOf(5), (Integer) ((Variant.Success<?, ?>) result).value());
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
    }

    /** Unknown, unowned, and repeated plant numbers are rejected. */
    @Test
    public void usePowerPlantRejectsInvalidSelections() {
        player.receivePowerPlant(coal);
        player.receivePowerPlant(oil);
        assertTrue(player.usePowerPlant(List.of(new PowerPlantNumber(6)), Map.of()) instanceof Variant.Failure);
        assertTrue(player.usePowerPlant(List.of(UNOWNED_NUMBER), Map.of()) instanceof Variant.Failure);
        assertTrue(player.usePowerPlant(List.of(COAL_NUMBER, COAL_NUMBER),
                counts(Resource.COAL, Resource.COAL)) instanceof Variant.Failure);
        assertEquals(OIL_NUMBER, player.bestPowerPlant());
    }
}
