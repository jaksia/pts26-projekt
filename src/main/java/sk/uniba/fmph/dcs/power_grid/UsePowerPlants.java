package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Map;

/**
 * Determines how many cities a set of power plants can supply.
 */
public interface UsePowerPlants {

    /**
     * Calculates the number of cities supplied by the given power plants when exactly the given resources are burned.
     *
     * @param powerPlants
     *            the power plants to use
     * @param resources
     *            counts of each resource type to burn
     *
     * @return the number of supplied cities, or an error if the selection or resource counts are invalid
     */
    Variant<Integer, RequestError> numberOfSuppliableCities(List<PowerPlant> powerPlants,
            Map<Resource, Integer> resources);
}
