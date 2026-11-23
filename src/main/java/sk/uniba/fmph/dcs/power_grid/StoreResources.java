package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Map;

/**
 * Determines whether resources can be stored in the given power plants.
 */
public interface StoreResources {

    /**
     * Checks whether the given resource counts fit into the given power plants.
     *
     * @param resources counts of each resource type
     * @param powerPlants the power plants that would store the resources
     * @return {@code true} if every resource can be stored
     */
    boolean canStoreResources(Map<Resource, Integer> resources, List<PowerPlant> powerPlants);
}
