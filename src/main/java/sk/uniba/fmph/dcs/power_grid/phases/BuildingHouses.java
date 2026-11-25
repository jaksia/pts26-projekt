package sk.uniba.fmph.dcs.power_grid.phases;

import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.List;

/**
 * Phase in which players build houses in towns.
 */
public interface BuildingHouses {

    void startPhase(List<PlayerColor> playerOrder);

    Variant<PhaseStatus, RequestError> buyHouses(PlayerColor player, List<String> townNames);

    String getState();
}
