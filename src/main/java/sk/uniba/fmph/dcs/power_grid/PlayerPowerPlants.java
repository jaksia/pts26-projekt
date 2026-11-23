package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A player's power plants.
 */
public interface PlayerPowerPlants {

    /**
     * Returns the player's highest-numbered power plant.
     *
     * @return the highest power plant number
     */
    PowerPlantNumber bestPowerPlant(); /* Should be called largest(?) */

    /**
     * Adds a power plant to the player's collection if the limit allows it.
     *
     * @param pp the power plant to receive
     * @return {@code false} if the player is above the power plant limit
     */
    boolean receivePowerPlant(PowerPlant pp);

    /**
     * Removes a power plant after checking that the surviving resources fit into
     * the remaining plants.
     *
     * @param ppn the power plant to remove
     * @param newResources counts of the resources that survive the trashing
     * @return an error if the plant is not owned or the resources do not fit
     */
    Optional<RequestError> removePowerPlant(PowerPlantNumber ppn, Map<Resource, Integer> newResources);

    /**
     * Calculates the largest number of cities the player can supply.
     *
     * @param resources counts of each available resource type
     * @return the maximum number of suppliable cities
     */
    int numberOfSuppliableCities(Map<Resource, Integer> resources);

    /**
     * Checks whether the given resource counts fit into the player's plants.
     *
     * @param resources counts of each resource type
     * @return {@code true} if every resource can be stored
     */
    boolean canStoreResources(Map<Resource, Integer> resources);

    /**
     * Calculates the cities supplied by an exact selection of the player's plants
     * and resources.
     *
     * @param ppns the selected power plant numbers
     * @param resourcesToBurn counts of each resource type to burn
     * @return the number of supplied cities, or an error for an invalid selection
     */
    Variant<Integer, RequestError> usePowerPlant(
            List<PowerPlantNumber> ppns, Map<Resource, Integer> resourcesToBurn);
}
