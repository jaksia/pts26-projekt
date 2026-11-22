package sk.uniba.fmph.dcs.power_grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Connections between towns backed by an all-pairs shortest-path matrix.
 *
 * <p>The graph is fixed at construction time, so Floyd-Warshall is run once and
 * every later cost query is a scan of a precomputed row.
 */
public final class ConnectionsImpl implements Connections {

    /**
     * An undirected connection between two towns and its building cost.
     *
     * @param a first town
     * @param b second town
     * @param cost building cost of the connection
     */
    public record Edge(String a, String b, int cost) {
    }

    private static final int INF = Integer.MAX_VALUE / 4;

    private final Map<String, Integer> index = new HashMap<>();
    private final int[][] dist;

    /**
     * Builds the graph and precomputes shortest paths between all pairs of towns.
     *
     * @param distances undirected connections; repeated pairs keep the cheapest cost
     */
    public ConnectionsImpl(final List<Edge> distances) {
        for (Edge edge : distances) {
            if (!index.containsKey(edge.a())) {
                index.put(edge.a(), index.size());
            }
            if (!index.containsKey(edge.b())) {
                index.put(edge.b(), index.size());
            }
        }

        int n = index.size();
        dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = i == j ? 0 : INF;
            }
        }
        for (Edge edge : distances) {
            if (edge.cost() < 0) {
                throw new IllegalArgumentException("Negative connection cost: " + edge.cost());
            }
            int u = index.get(edge.a());
            int v = index.get(edge.b());
            dist[u][v] = Math.min(dist[u][v], edge.cost());
            dist[v][u] = Math.min(dist[v][u], edge.cost());
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    int candidate = dist[i][k] + dist[k][j];
                    if (candidate < dist[i][j]) {
                        dist[i][j] = candidate;
                    }
                }
            }
        }
    }

    @Override
    public int calculateConnectingCost(final List<String> from, final List<String> to) {
        if (from.isEmpty()) {
            throw new IllegalArgumentException("At least one connected city is required.");
        }

        Set<String> connected = new HashSet<>();
        for (String city : from) {
            connected.add(requireKnown(city));
        }

        int total = 0;
        for (String city : to) {
            requireKnown(city);
            int target = index.get(city);
            int best = INF;
            for (String source : connected) {
                best = Math.min(best, dist[target][index.get(source)]);
            }
            if (best == INF) {
                throw new IllegalArgumentException("No path from " + city + " to the connected network.");
            }
            total += best;
            connected.add(city);
        }
        return total;
    }

    private String requireKnown(final String city) {
        if (!index.containsKey(city)) {
            throw new IllegalArgumentException("Unknown city: " + city);
        }
        return city;
    }
}
