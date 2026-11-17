package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Phase in which players build houses in towns.
 */
public interface BuildingHouses {

    void startPhase(List<PlayerColor> playerOrder);

    Variant<PhaseStatus, RequestError> buyHouses(PlayerColor player, List<String> townNames);

    String getState();
}
