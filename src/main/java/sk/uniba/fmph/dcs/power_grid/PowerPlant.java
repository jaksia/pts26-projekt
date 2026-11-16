package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Objects;

public record PowerPlant(PowerPlantNumber number, List<Resource> acceptedResources, int requiredResourceAmount,
                         int numberOfCities) {
    public PowerPlant {
        if (number == null) {
            throw new IllegalArgumentException("Power plant number may not be null");
        }
        if (acceptedResources == null) {
            throw new IllegalArgumentException("Accepted resources may not be null");
        }
        if (acceptedResources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Accepted resources may not contain null");
        }
        acceptedResources = List.copyOf(acceptedResources);
        if (requiredResourceAmount < 0) {
            throw new IllegalArgumentException("Invalid resource amount: " + requiredResourceAmount);
        }
        if (acceptedResources.size() > 2) {
            throw new IllegalArgumentException("Invalid accepted resources: " + acceptedResources);
        }
        if (acceptedResources.size() == 2
                && !(acceptedResources.contains(Resource.COAL) && acceptedResources.contains(Resource.OIL))) {
            throw new IllegalArgumentException("Invalid hybrid factory: " + acceptedResources);
        }
        if (!acceptedResources.isEmpty() && requiredResourceAmount == 0) {
            throw new IllegalArgumentException("Non-ecological power plants must use resources");
        }
        if (acceptedResources.isEmpty() && requiredResourceAmount != 0) {
            throw new IllegalArgumentException("Ecological power plants can't use resources");
        }
        if (numberOfCities < 1) {
            throw new IllegalArgumentException("Invalid number of cities: " + numberOfCities);
        }
    }
}
