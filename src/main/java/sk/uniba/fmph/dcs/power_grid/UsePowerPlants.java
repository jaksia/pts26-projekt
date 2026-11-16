package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Determines how many towns a set of power plants can supply.
 */
public interface UsePowerPlants {

    Variant<RequestError, Integer> numberOfSuppliableTowns(
            List<Resource> resources, List<PowerPlant> powerPlants);
}
