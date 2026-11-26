package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Connections between towns and the cost of building along them.
 */
public interface Connections {

    /**
     * Calculates the cost of connecting each city in {@code to} to the network of cities in {@code from}. Every newly
     * connected city becomes part of the network and may be used by later calculations.
     * <p>
     * The order of {@code to} is taken at face value: it is not rearranged to find a cheaper total, even though a
     * different order could be less expensive. Failure to recognize the cities are being connected in suboptimal order
     * is considered the player's skill issue.
     *
     * @param from
     *            cities already part of the connected network
     * @param to
     *            cities to connect, in the order they should be processed
     *
     * @return total cost of connecting all cities in {@code to}
     */
    int calculateConnectingCost(List<String> from, List<String> to);
}
