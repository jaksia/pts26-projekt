package sk.uniba.fmph.dcs.power_grid.phases;

import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.PowerPlantNumber;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Resource;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.List;

/**
 * Phase in which players auction power plants.
 */
public interface AuctionPowerPlants {

    void startPhase(List<PlayerColor> playerOrder);

    Variant<PhaseStatus, RequestError> selectPowerPlantToBid(PlayerColor player, PowerPlantNumber ppn, int firstBid);

    Variant<PhaseStatus, RequestError> doNotWantPowerPlant(PlayerColor player);

    Variant<PhaseStatus, RequestError> bidForCurrentPowerPlant(int playerId, int bid);

    Variant<PhaseStatus, RequestError> doNotBidForCurrentPowerPlant(int playerId);

    Variant<PhaseStatus, RequestError> removePowerPlant(PlayerColor player, PowerPlantNumber ppn,
            List<Resource> resources);

    String getState();
}
