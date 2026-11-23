package sk.uniba.fmph.dcs.power_grid.phases;

import sk.uniba.fmph.dcs.power_grid.PlayerBoardFacade;
import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Towns;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BuildingHousesImpl implements BuildingHouses {
    private final Map<PlayerColor, PlayerBoardFacade> boards;
    private final Towns towns;

    private List<PlayerColor> turnOrder = List.of();
    private int currentIndex;
    private int gameEndThreshold;
    private boolean active;
    private boolean gameEnding;

    public BuildingHousesImpl(final Map<PlayerColor, PlayerBoardFacade> boards, final Towns towns) {
        Objects.requireNonNull(boards, "boards must not be null");
        Objects.requireNonNull(towns, "towns must not be null");

        this.boards = new EnumMap<>(PlayerColor.class);
        for (Map.Entry<PlayerColor, PlayerBoardFacade> entry : boards.entrySet()) {
            this.boards.put(entry.getKey(), Objects.requireNonNull(entry.getValue(), "boards must not contain null values"));
        }
        this.towns = towns;
    }

    @Override
    public void startPhase(final List<PlayerColor> playerOrder) {
        Objects.requireNonNull(playerOrder, "playerOrder must not be null");
        if (playerOrder.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("playerOrder must not contain null values");
        }

        List<PlayerColor> reversed = new ArrayList<>(playerOrder);
        Collections.reverse(reversed);
        this.turnOrder = List.copyOf(reversed);
        this.currentIndex = 0;
        this.gameEndThreshold = thresholdFor(playerOrder.size());
        this.gameEnding = false;
        this.active = !this.turnOrder.isEmpty();
    }

    @Override
    public Variant<PhaseStatus, RequestError> buyHouses(final PlayerColor player, final List<String> townNames) {
        Objects.requireNonNull(townNames, "town names must not be null");
        Objects.requireNonNull(player, "player must not be null");
        if (townNames.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("town names must not contain null values");
        }

        if (!active) {
            return error("the building phase has not been started or is already over");
        }
        if (!player.equals(currentPlayer())) {
            return error("it is " + currentPlayer() + "'s turn, not " + player + "'s");
        }

        final PlayerBoardFacade board = boards.get(player);
        if (board == null) {
            return error("no board found for player " + player);
        }
        for (String town : townNames) {
            if (town == null || town.isBlank()) {
                return error(player + " requested an invalid town name in " + townNames);
            }
        }

        if (!townNames.isEmpty()) {
            final int money = board.outstandingMoney();
            final Variant<Integer, RequestError> result = towns.buyHouses(player, townNames, money);

            if (result instanceof Variant.Failure<Integer, RequestError> failure) {
                return error(player + " cannot build in " + townNames
                        + " (" + failure.error().description() + ")");
            }

            final int cost = ((Variant.Success<Integer, RequestError>) result).value();
            if (!board.hasEnoughMoney(cost)) {
                return error(player + " has " + money
                        + " Elektro but houses in " + townNames + " cost " + cost);
            }
            board.spendMoney(cost);

            if (towns.getNumberOfHouses(player) >= gameEndThreshold) {
                gameEnding = true;
            }
        }

        currentIndex++;
        if (currentIndex < turnOrder.size()) {
            return Variant.success(PhaseStatus.PHASE_CONTINUES);
        }
        active = false;
        return Variant.success(gameEnding ? PhaseStatus.GAME_ENDS : PhaseStatus.PHASE_ENDS);
    }

    private PlayerColor currentPlayer() {
        return currentIndex < turnOrder.size() ? turnOrder.get(currentIndex) : null;
    }

    private static int thresholdFor(final int playerCount) {
        if (playerCount < 2 || playerCount > 6) {
            throw new IllegalArgumentException("Invalid player count: " + playerCount);
        }

        if (playerCount == 6) {
            return 14;
        }
        if (playerCount == 5) {
            return 15;
        }
        if (playerCount == 3 || playerCount == 4) {
            return 17;
        }
        return 18;
    }

    private static Variant<PhaseStatus, RequestError> error(final String description) {
        return Variant.failure(new RequestError("Cannot build houses: " + description));
    }

    @Override
    public String getState() {
        // TODO later: implement
        return "";
    }
}
