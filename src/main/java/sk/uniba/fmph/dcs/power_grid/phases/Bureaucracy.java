package sk.uniba.fmph.dcs.power_grid.phases;

import sk.uniba.fmph.dcs.power_grid.CheckAndBumpStep;
import sk.uniba.fmph.dcs.power_grid.PlayerBoardFacade;
import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.PowerPlantNumber;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Resource;
import sk.uniba.fmph.dcs.power_grid.ResourceMarket;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Phase in which players use their power plants and receive income.
 */
public final class Bureaucracy {
    private final Map<PlayerColor, PlayerBoardFacade> boards;
    private final ResourceMarket market;
    private final CheckAndBumpStep checkAndBumpStep;

    private List<PlayerColor> turnOrder = List.of();
    private int currentIndex;
    private boolean active;

    public Bureaucracy(final Map<PlayerColor, PlayerBoardFacade> boards, final ResourceMarket market,
                       final CheckAndBumpStep checkAndBumpStep) {
        Objects.requireNonNull(boards, "boards must not be null");
        Objects.requireNonNull(market, "market must not be null");
        Objects.requireNonNull(checkAndBumpStep, "step tracker must not be null");

        this.boards = new EnumMap<>(PlayerColor.class);
        for (Map.Entry<PlayerColor, PlayerBoardFacade> entry : boards.entrySet()) {
            this.boards.put(entry.getKey(), Objects.requireNonNull(entry.getValue(), "boards must not contain null values"));
        }
        this.market = market;
        this.checkAndBumpStep = checkAndBumpStep;
    }

    public void startPhase(final List<PlayerColor> playerOrder) {
        Objects.requireNonNull(playerOrder, "playerOrder must not be null");
        if (playerOrder.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("playerOrder must not contain null values");
        }

        checkAndBumpStep.checkAndBumpToStep2();

        this.turnOrder = List.copyOf(playerOrder);
        this.currentIndex = 0;
        this.active = !this.turnOrder.isEmpty();
    }

    public Variant<PhaseStatus, RequestError> usePowerPlants(
            final PlayerColor player, final List<PowerPlantNumber> ppns, final List<Resource> resourcesToBurn) {
        Objects.requireNonNull(player, "player must not be null");

        Objects.requireNonNull(ppns, "ppns must not be null");
        if (ppns.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("ppns must not contain null values");
        }

        Objects.requireNonNull(resourcesToBurn, "resourcesToBurn must not be null");
        if (resourcesToBurn.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("resourcesToBurn must not contain null values");
        }

        if (!active) {
            return error("the bureaucracy phase has not been started or is already over");
        }

        if (!player.equals(currentPlayer())) {
            return error("it is " + currentPlayer() + "'s turn, not " + player + "'s");
        }

        final PlayerBoardFacade board = boards.get(player);
        if (board == null) {
            return error("no board found for player " + player);
        }

        final Optional<RequestError> useError = board.usePowerPlants(player, ppns, resourcesToBurn);
        if (useError.isPresent()) {
            return error(player + " cannot use power plants " + ppns + " with " + resourcesToBurn
                    + " (" + useError.get().description() + ")");
        }

        currentIndex++;
        if (currentIndex < turnOrder.size()) {
            return Variant.success(PhaseStatus.PHASE_CONTINUES);
        }

        market.refill();
        active = false;
        return Variant.success(PhaseStatus.PHASE_ENDS);
    }

    public String getState() {
        // TODO later: implement
        return "";
    }

    private PlayerColor currentPlayer() {
        return currentIndex < turnOrder.size() ? turnOrder.get(currentIndex) : null;
    }

    private static Variant<PhaseStatus, RequestError> error(final String description) {
        return Variant.failure(new RequestError("Cannot use power plants: " + description));
    }
}
