package sk.uniba.fmph.dcs.power_grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DeterminePlayerOrder {

    private final List<PlayerColor> players;
    private final Map<PlayerColor, PlayerBoardFacade> boards;
    private final Towns towns;
    private final Random rng;
    private boolean orderAlreadySet = false;


    //random cez konstruktor nech sa da reprodukovat to iste poradie
    public DeterminePlayerOrder(final List<PlayerColor> players,
                                final Map<PlayerColor, PlayerBoardFacade> boards,
                                final Towns towns) {
        this(players, boards, towns, new Random());
    }


    public DeterminePlayerOrder(final List<PlayerColor> players,
                                final Map<PlayerColor, PlayerBoardFacade> boards,
                                final Towns towns,
                                final Random rng) {
        this.players = new ArrayList<>(players);
        this.boards = boards;
        this.towns = towns;
        this.rng = rng;
    }


    //Poradie na nove kolo, prve volanie zamiesa nahodne (uvod)
    public List<PlayerColor> reorder() {
        if (!orderAlreadySet) {
            orderAlreadySet = true;
            Collections.shuffle(players, rng);
        } else {
            players.sort(orderComparator());
        }
        return new ArrayList<>(players);
    }

    //vrati poradie hry (prvy je vitaz)
    public List<PlayerColor> gameResult() {
        List<PlayerColor> result = new ArrayList<>(players);
        result.sort(resultComparator());
        return result;
    }

    //na zaklade pravidiel vrati poradie hracov
    private Comparator<PlayerColor> orderComparator() {
        return Comparator
                .comparingInt(towns::getNumberOfHouses).reversed()
                .thenComparing(
                        Comparator.comparingInt((PlayerColor p) ->
                                boards.get(p).bestPowerPlantNumber().number()).reversed());
    }

    //vysledok hry, najprv zasobene mesta, potom peniaze, potom viac domov
    private Comparator<PlayerColor> resultComparator() {
        return Comparator
                .comparingInt(this::poweredCities).reversed()
                .thenComparing(
                        Comparator.comparingInt((PlayerColor p) ->
                                boards.get(p).outstandingMoney()).reversed())
                .thenComparing(
                        Comparator.comparingInt(towns::getNumberOfHouses).reversed());
    }


    //nemoze zasobit viac domov ako ma postavenych domov
    private int poweredCities(final PlayerColor player) {
        return Math.min(
                boards.get(player).numberOfConnectableCities(),
                towns.getNumberOfHouses(player));
    }
}
 