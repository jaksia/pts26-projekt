package sk.uniba.fmph.dcs.power_grid.phases;


import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.PowerPlantNumber;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Resource;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.List;

/**
 * Phase in which players use their power plants and receive income.
 */
public interface Bureaucracy {

    void startPhase(List<PlayerColor> playerOrder);

    Variant<PhaseStatus, RequestError> usePowerPlants(
            PlayerColor player, List<PowerPlantNumber> ppns, List<Resource> resourcesToBurn);

    String getState();
}
