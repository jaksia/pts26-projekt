package sk.uniba.fmph.dcs.power_grid;

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
