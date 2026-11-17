package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Connections between towns and the cost of building along them.
 */
public interface Connections {

    int calculateConnectingCost(List<String> from, List<String> to);
}
