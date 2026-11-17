package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Determines the maximum number of power plants that can be used.
 */
public interface MaximumNumberOfUsablePowerPlants {

    int numberOfSuppliableTowns(List<Resource> resources, List<PowerPlant> powerPlants);
}
