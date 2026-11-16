package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Optional;

/**
 * A player's power plants.
 */
public interface PlayerPowerPlants {

    PowerPlantNumber bestPowerPlant(); /* Should be called largest(?) */

    /**
     * @return {@code false} if the player is above the power plant limit
     */
    boolean receivePowerPlant(PowerPlant pp);

    Optional<RequestError> removePowerPlant(
            PowerPlantNumber ppn, List<Resource> newResources);

    int numberOfConnectableCities(List<Resource> resources);

    boolean canStoreResources(List<Resource> resources);

    Variant<Integer, RequestError> usePowerPlant(
            List<PowerPlantNumber> ppns, List<Resource> resourcesToBurn);
}
