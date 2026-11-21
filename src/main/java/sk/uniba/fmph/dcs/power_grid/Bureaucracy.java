package sk.uniba.fmph.dcs.power_grid;

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
