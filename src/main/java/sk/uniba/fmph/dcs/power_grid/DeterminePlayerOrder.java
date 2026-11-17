package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Determines the player order and the final game result.
 */
public interface DeterminePlayerOrder {

    List<PlayerColor> reorder();

    List<PlayerColor> gameResult();
}
