package sk.uniba.fmph.dcs.power_grid.phases;


import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Resource;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.List;

/**
 * Phase in which players buy resources from the resource market.
 */
public interface BuyingResources {

    void startPhase(List<PlayerColor> playerOrder);

    Variant<PhaseStatus, RequestError> buyResources(
            PlayerColor player, List<Resource> removedResources);

    String getState();
}
