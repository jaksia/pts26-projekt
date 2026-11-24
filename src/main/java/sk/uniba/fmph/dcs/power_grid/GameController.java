package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Optional;

/**
 * Coordinates the phases of a Power Grid game.
 */
public interface GameController {

    Optional<RequestError> selectPowerPlantToBid(PlayerColor player, PowerPlantNumber ppn, int firstBid);

    Optional<RequestError> doNotWantPowerPlant(PlayerColor player);

    Optional<RequestError> bidForCurrentPowerPlant(PlayerColor player, int bid);

    Optional<RequestError> doNotBidForCurrentPowerPlant(PlayerColor player);

    Optional<RequestError> removePowerPlant(PlayerColor player, PowerPlantNumber ppn, List<Resource> resources);

    Optional<RequestError> buyResources(PlayerColor player, List<Resource> removedResources);

    Optional<RequestError> buyHouses(PlayerColor player, List<String> townNames);

    Optional<RequestError> usePowerPlants(PlayerColor player, List<PowerPlantNumber> ppns,
            List<Resource> resourcesToBurn);

    String getState();
}
