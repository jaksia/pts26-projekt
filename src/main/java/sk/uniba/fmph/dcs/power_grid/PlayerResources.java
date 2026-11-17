package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Optional;

/**
 * A player's stored resources.
 */
public interface PlayerResources {

    Optional<RequestError> removePowerPlant(
            PowerPlantNumber ppn, List<Resource> removedResources);

    int numberOfConnectableCities();

    Optional<RequestError> buyResources(List<Resource> resources);

    Variant<Integer, RequestError> usePowerPlant(
            List<PowerPlantNumber> ppns, List<Resource> resourcesToBurn);
}
