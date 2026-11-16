package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Objects;

/**
 * A power plant card.
 *
 * @param number            the number of this power plant
 * @param acceptedResources the resources this power plant can burn
 * @param resourceAmount    the number of resources needed to operate it
 * @param numberOfCities    the number of cities it can supply
 */
public record PowerPlant(
        PowerPlantNumber number,
        List<Resource> acceptedResources,
        int resourceAmount,
        int numberOfCities) {

    public PowerPlant {
        Objects.requireNonNull(number, "number must not be null");
        acceptedResources = List.copyOf(acceptedResources);

        if (resourceAmount < 0) {
            throw new IllegalArgumentException("resourceAmount must not be negative");
        }
        if (numberOfCities < 1) {
            throw new IllegalArgumentException("numberOfCities must be positive");
        }
    }
}
