package sk.uniba.fmph.dcs.power_grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sk.uniba.fmph.dcs.power_grid.ConnectionsImpl.Edge;

public class ConnectionsImplTest {

    private static ConnectionsImpl chainWithDetour() {
        // A--B--C is cheaper than the direct A--C edge, and D hangs off C.
        return new ConnectionsImpl(List.of(
                new Edge("A", "B", 5),
                new Edge("B", "C", 3),
                new Edge("A", "C", 20),
                new Edge("C", "D", 2)));
    }

    @Test
    public void prefersCheapestMultiHopRoute() {
        assertEquals(8, chainWithDetour().calculateConnectingCost(List.of("A"), List.of("C")));
    }

    @Test
    public void newlyConnectedCityBecomesPartOfTheNetwork() {
        // C costs 8 to reach from A, then D costs only 2 from the newly connected C
        // instead of 10 from A.
        assertEquals(10, chainWithDetour().calculateConnectingCost(List.of("A"), List.of("C", "D")));
    }

    @Test
    public void doesNotReorderCitiesToFindCheaperTotal() {
        ConnectionsImpl connections = new ConnectionsImpl(List.of(
                new Edge("A", "X", 10),
                new Edge("A", "Y", 5),
                new Edge("X", "Y", 1)));

        int xThenY = connections.calculateConnectingCost(List.of("A"), List.of("X", "Y"));
        int yThenX = connections.calculateConnectingCost(List.of("A"), List.of("Y", "X"));

        assertEquals(7, xThenY);
        assertEquals(6, yThenX);
        assertNotEquals(xThenY, yThenX);
    }

    @Test
    public void usesCheapestOfRepeatedAndReversedEdges() {
        ConnectionsImpl connections = new ConnectionsImpl(List.of(
                new Edge("A", "B", 7),
                new Edge("B", "A", 3)));
        assertEquals(3, connections.calculateConnectingCost(List.of("A"), List.of("B")));
    }

    @Test
    public void emptyDestinationListCostsNothing() {
        assertEquals(0, chainWithDetour().calculateConnectingCost(List.of("A"), List.of()));
    }

    @Test
    public void doesNotMutateInputLists() {
        List<String> from = new ArrayList<>(List.of("A"));
        List<String> to = new ArrayList<>(List.of("C", "D"));
        chainWithDetour().calculateConnectingCost(from, to);
        assertEquals(List.of("A"), from);
        assertEquals(List.of("C", "D"), to);
    }

    @Test
    public void rejectsEmptySourceList() {
        assertThrows(IllegalArgumentException.class,
                () -> chainWithDetour().calculateConnectingCost(List.of(), List.of("C")));
    }

    @Test
    public void rejectsUnknownCity() {
        ConnectionsImpl connections = chainWithDetour();
        assertThrows(IllegalArgumentException.class,
                () -> connections.calculateConnectingCost(List.of("Unknown"), List.of("C")));
        assertThrows(IllegalArgumentException.class,
                () -> connections.calculateConnectingCost(List.of("A"), List.of("Unknown")));
    }

    @Test
    public void rejectsUnreachableCity() {
        ConnectionsImpl connections = new ConnectionsImpl(List.of(
                new Edge("A", "B", 1),
                new Edge("C", "D", 1)));
        assertThrows(IllegalArgumentException.class,
                () -> connections.calculateConnectingCost(List.of("A"), List.of("C")));
    }

    @Test
    public void rejectsNegativeCost() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectionsImpl(List.of(new Edge("A", "B", -1))));
    }
}
