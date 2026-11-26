package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Map;

/**
 * Determines the maximum number of power plants that can be used.
 */
public interface MaximumNumberOfUsablePowerPlants {

    /**
     * Finds the largest number of cities that any subset of the given power plants can supply using at most the given
     * resources.
     *
     * @param powerPlants
     *            the available power plants
     * @param resources
     *            counts of each available resource type
     *
     * @return the maximum number of suppliable cities
     */
    int numberOfSuppliableCities(List<PowerPlant> powerPlants, Map<Resource, Integer> resources);
}
