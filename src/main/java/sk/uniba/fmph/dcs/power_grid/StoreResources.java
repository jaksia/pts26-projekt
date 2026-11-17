package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Determines whether resources can be stored in the given power plants.
 */
public interface StoreResources {

    boolean canStoreResources(List<Resource> resources, List<PowerPlant> powerPlants);
}
