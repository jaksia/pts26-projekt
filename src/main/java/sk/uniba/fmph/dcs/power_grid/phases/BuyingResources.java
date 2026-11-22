package sk.uniba.fmph.dcs.power_grid.phases;

import sk.uniba.fmph.dcs.power_grid.PlayerBoardFacade;
import sk.uniba.fmph.dcs.power_grid.PlayerColor;
import sk.uniba.fmph.dcs.power_grid.RequestError;
import sk.uniba.fmph.dcs.power_grid.Resource;
import sk.uniba.fmph.dcs.power_grid.ResourceMarket;
import sk.uniba.fmph.dcs.power_grid.Variant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Phase in which players buy resources from the resource market.
 */
public class BuyingResources {
    private final Map<PlayerColor, PlayerBoardFacade> boards;
    private final ResourceMarket market;

    private List<PlayerColor> turnOrder = List.of();
    private int currentIndex;
    private boolean active;

    public BuyingResources(Map<PlayerColor, PlayerBoardFacade> boards, ResourceMarket market) {
        Objects.requireNonNull(boards, "boards must not be null");
        Objects.requireNonNull(market, "market must not be null");

        this.boards = new EnumMap<>(PlayerColor.class);
        for (Map.Entry<PlayerColor, PlayerBoardFacade> entry : boards.entrySet()) {
            this.boards.put(entry.getKey(), Objects.requireNonNull(entry.getValue(), "boards must not contain null values"));
        }
        this.market = market;
    }

    public void startPhase(List<PlayerColor> playerOrder) {
        Objects.requireNonNull(playerOrder, "playerOrder must not be null");
        if (playerOrder.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("playerOrder must not contain null values");

        List<PlayerColor> reversed = new ArrayList<>(playerOrder);
        Collections.reverse(reversed);
        this.turnOrder = List.copyOf(reversed);
        this.currentIndex = 0;
        this.active = !this.turnOrder.isEmpty();
    }

    public Variant<PhaseStatus, RequestError> buyResources(PlayerColor player, List<Resource> removedResources) {
        Objects.requireNonNull(player, "player must not be null");
        Objects.requireNonNull(removedResources, "removedResources must not be null");
        if (removedResources.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("removedResources must not contain null values");

        if (!active) return error("the buying phase has not been started or is already over");

        if (!player.equals(currentPlayer()))
            return error("it is " + currentPlayer() + "'s turn, not " + player + "'s");

        final PlayerBoardFacade board = boards.get(player);
        if (board == null) {
            return error("no board found for player " + player);
        }

        final Integer price = market.getPrice(removedResources);
        if (price == null) {
            return error(player + " requested " + removedResources
                    + " but they are not available in the market");
        }
        if (!board.hasEnoughMoney(price)) {
            return error(player + " has " + board.outstandingMoney()
                    + " Elektro but needs " + price + " for " + removedResources);
        }

        final Optional<RequestError> storageError = board.buyResources(removedResources);
        if (storageError.isPresent()) {
            return error(player + " cannot store " + removedResources
                    + " (" + storageError.get().description() + ")");
        }

        market.takeResources(removedResources);
        board.spendMoney(price);

        currentIndex++;
        if (currentIndex >= turnOrder.size()) {
            active = false;
            return Variant.success(PhaseStatus.PHASE_ENDS);
        }
        return Variant.success(PhaseStatus.PHASE_CONTINUES);
    }

    private PlayerColor currentPlayer() {
        return currentIndex < turnOrder.size() ? turnOrder.get(currentIndex) : null;
    }

    private static Variant<PhaseStatus, RequestError> error(final String description) {
        return Variant.failure(new RequestError("Cannot buy resources: " + description));
    }

    public String getState() {
        // TODO: implement
        return "";
    }
}
