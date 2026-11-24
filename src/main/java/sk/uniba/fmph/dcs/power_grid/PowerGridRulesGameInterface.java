package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * External interface for the Power Grid rules component.
 * <p>
 * Resources are encoded as a string, where {@code C} stands for one coal, {@code O} for one oil, {@code G} for one
 * garbage and {@code U} for one uranium.
 * </p>
 * <p>
 * {@link #bidForCurrentPowerPlant(int, int)} can finish the bidding when nobody else can bid. Similarly,
 * {@link #doNotBidForCurrentPowerPlant(int)} may end the bidding process. After a player wins the bidding and has too
 * many power plants, the player must remove a power plant and possibly some resources using
 * {@link #removePowerPlant(int, int, String)}.
 * </p>
 */
public interface PowerGridRulesGameInterface {

    Variant<SerializedGameState, RequestError> selectPowerPlantToBid(int playerId, int powerPlantNumber, int firstBid);

    Variant<SerializedGameState, RequestError> doNotWantPowerPlant(int playerId);

    Variant<SerializedGameState, RequestError> bidForCurrentPowerPlant(int playerId, int bid);

    Variant<SerializedGameState, RequestError> doNotBidForCurrentPowerPlant(int playerId);

    Variant<SerializedGameState, RequestError> removePowerPlant(int playerId, int powerPlantNumber,
            String removedResources);

    Variant<SerializedGameState, RequestError> buyResources(int playerId, String resources);

    /**
     * @param playerId
     *            the player buying houses
     * @param townNames
     *            towns in which houses should be built; may be empty
     *
     * @return the new game state, or an error if the houses cannot be built
     */
    Variant<SerializedGameState, RequestError> buyHouses(int playerId, List<String> townNames);

    Variant<SerializedGameState, RequestError> usePowerPlants(int playerId, List<Integer> powerPlantNumbers,
            String resourcesToBurn);
}
