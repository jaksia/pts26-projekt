package sk.uniba.fmph.dcs.power_grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sk.uniba.fmph.dcs.power_grid.PlayerColor;


public final class DeterminePlayerOrderImpl implements DeterminePlayerOrder {

    private final List<PlayerColor> players;
    private final Map<PlayerColor, PlayerBoardFacade> boards;
    private final Towns towns;
    private final Random rng;
    private boolean orderAlreadySet = false;

    //the constructor receives random to be able to reproduce the same order
    public DeterminePlayerOrderImpl(final List<PlayerColor> players,
                                final Map<PlayerColor, PlayerBoardFacade> boards,
                                final Towns towns) {
        this(players, boards, towns, new Random());
    }


    public DeterminePlayerOrderImpl(final List<PlayerColor> players,
                                final Map<PlayerColor, PlayerBoardFacade> boards,
                                final Towns towns,
                                final Random rng) {
        this.players = new ArrayList<>(players);
        this.boards = boards;
        this.towns = towns;
        this.rng = rng;
    }

    //Order for next round, first call shuffles randomly (start)
    @Override
    public List<PlayerColor> reorder() {
        if (!orderAlreadySet) {
            orderAlreadySet = true;
            Collections.shuffle(players, rng);
        } else {
            players.sort(orderComparator());
        }
        return new ArrayList<>(players);
    }

    //returns game result (the first is the winner)
    @Override
    public List<PlayerColor> gameResult() {
        List<PlayerColor> result = new ArrayList<>(players);
        result.sort(resultComparator());
        return result;
    }

    //based on rules returns player order
    private Comparator<PlayerColor> orderComparator() {
        return Comparator
                .comparingInt(towns::getNumberOfHouses).reversed()
                .thenComparing(
                        Comparator.comparingInt((PlayerColor p) ->
                                boards.get(p).bestPowerPlantNumber().number()).reversed());
    }

    //game result, firstly powered cities, then money, then more points
    private Comparator<PlayerColor> resultComparator() {
        return Comparator
                .comparingInt(this::poweredCities).reversed()
                .thenComparing(
                        Comparator.comparingInt((PlayerColor p) ->
                                boards.get(p).outstandingMoney()).reversed())
                .thenComparing(
                        Comparator.comparingInt(towns::getNumberOfHouses).reversed());
    }

    //cannot supply more houses than built houses
    private int poweredCities(final PlayerColor player) {
        return Math.min(
                boards.get(player).numberOfConnectableCities(),
                towns.getNumberOfHouses(player));
    }
}

