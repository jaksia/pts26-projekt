package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

import sk.uniba.fmph.dcs.power_grid.PlayerColor;
/**
 * Determines the player order and the final game result.
 */
public interface DeterminePlayerOrder {

    List<PlayerColor> reorder();

    List<PlayerColor> gameResult();
}

