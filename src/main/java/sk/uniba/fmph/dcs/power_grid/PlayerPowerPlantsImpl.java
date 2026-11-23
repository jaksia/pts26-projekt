package sk.uniba.fmph.dcs.power_grid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A player's power plants, delegating the rule calculations to injected helpers.
 */
public final class PlayerPowerPlantsImpl implements PlayerPowerPlants {

    public static final int DEFAULT_MAX_POWER_PLANTS = 3;

    private final Map<PowerPlantNumber, PowerPlant> catalog;
    private final StoreResources storeResources;
    private final UsePowerPlants usePowerPlants;
    private final MaximumNumberOfUsablePowerPlants maximumNumberOfUsablePowerPlants;
    private final int maxPowerPlants;
    private final List<PowerPlant> powerPlants = new ArrayList<>();

    /**
     * Creates an instance using the standard power plant limit.
     *
     * @param catalog maps power plant numbers to power plants; it is only read and
     *        must not be modified by the caller
     * @param storeResources storage capacity rule
     * @param usePowerPlants power plant usage rule
     * @param maximumNumberOfUsablePowerPlants maximum city count rule
     */
    public PlayerPowerPlantsImpl(final Map<PowerPlantNumber, PowerPlant> catalog,
            final StoreResources storeResources, final UsePowerPlants usePowerPlants,
            final MaximumNumberOfUsablePowerPlants maximumNumberOfUsablePowerPlants) {
        this(catalog, storeResources, usePowerPlants, maximumNumberOfUsablePowerPlants, DEFAULT_MAX_POWER_PLANTS);
    }

    /**
     * Creates an instance with a custom power plant limit.
     *
     * @param catalog maps power plant numbers to power plants; it is only read and
     *        must not be modified by the caller
     * @param storeResources storage capacity rule
     * @param usePowerPlants power plant usage rule
     * @param maximumNumberOfUsablePowerPlants maximum city count rule
     * @param maxPowerPlants maximum number of power plants the player may own
     */
    public PlayerPowerPlantsImpl(final Map<PowerPlantNumber, PowerPlant> catalog,
            final StoreResources storeResources, final UsePowerPlants usePowerPlants,
            final MaximumNumberOfUsablePowerPlants maximumNumberOfUsablePowerPlants, final int maxPowerPlants) {
        if (catalog == null || storeResources == null || usePowerPlants == null
                || maximumNumberOfUsablePowerPlants == null) {
            throw new IllegalArgumentException("Dependencies may not be null");
        }
        if (maxPowerPlants < 0) {
            throw new IllegalArgumentException("Invalid power plant limit: " + maxPowerPlants);
        }
        this.catalog = catalog;
        this.storeResources = storeResources;
        this.usePowerPlants = usePowerPlants;
        this.maximumNumberOfUsablePowerPlants = maximumNumberOfUsablePowerPlants;
        this.maxPowerPlants = maxPowerPlants;
    }

    @Override
    public PowerPlantNumber bestPowerPlant() {
        if (powerPlants.isEmpty()) {
            throw new IllegalStateException("Player has no power plants.");
        }
        // Compare the wrapped numbers, not the records themselves.
        return powerPlants.stream()
                .max(Comparator.comparingInt(plant -> plant.number().number()))
                .orElseThrow()
                .number();
    }

    @Override
    public boolean receivePowerPlant(final PowerPlant pp) {
        if (pp == null) {
            throw new IllegalArgumentException("Power plant may not be null");
        }
        if (powerPlants.size() >= maxPowerPlants) {
            return false; // keep the existing collection untouched
        }
        powerPlants.add(pp);
        return true;
    }

    @Override
    public Optional<RequestError> removePowerPlant(
            final PowerPlantNumber ppn, final Map<Resource, Integer> newResources) {
        if (ppn == null) {
            throw new IllegalArgumentException("Power plant number may not be null");
        }
        Optional<PowerPlant> removed = findOwned(ppn);
        if (removed.isEmpty()) {
            return Optional.of(new RequestError("Power plant not owned: " + ppn));
        }
        // Check storage on a copy first so a failure leaves the player unchanged.
        List<PowerPlant> remaining = new ArrayList<>(powerPlants);
        remaining.remove(removed.get());
        if (!storeResources.canStoreResources(newResources, remaining)) {
            return Optional.of(new RequestError("Remaining power plants cannot hold remaining resources."));
        }
        powerPlants.remove(removed.get());
        return Optional.empty();
    }

    @Override
    public boolean canStoreResources(final Map<Resource, Integer> resources) {
        return storeResources.canStoreResources(resources, List.copyOf(powerPlants));
    }

    @Override
    public int numberOfSuppliableCities(final Map<Resource, Integer> resources) {
        return maximumNumberOfUsablePowerPlants.numberOfSuppliableCities(List.copyOf(powerPlants), resources);
    }

    @Override
    public Variant<Integer, RequestError> usePowerPlant(
            final List<PowerPlantNumber> ppns, final Map<Resource, Integer> resourcesToBurn) {
        List<PowerPlant> selected = new ArrayList<>();
        Set<PowerPlantNumber> seen = new HashSet<>();
        for (PowerPlantNumber ppn : ppns) {
            if (ppn == null) {
                throw new IllegalArgumentException("Power plant number may not be null");
            }
            if (!seen.add(ppn)) {
                return Variant.failure(new RequestError("Power plant listed twice: " + ppn));
            }
            Optional<PowerPlant> plant = Optional.ofNullable(catalog.get(ppn));
            if (plant.isEmpty() || !powerPlants.contains(plant.get())) {
                return Variant.failure(new RequestError("Power plant not owned: " + ppn));
            }
            selected.add(plant.get());
        }
        return usePowerPlants.numberOfSuppliableCities(selected, resourcesToBurn);
    }

    /**
     * Finds an owned power plant by its number.
     *
     * @param ppn the number to look for
     * @return the owned plant, or empty if the player does not own it
     */
    private Optional<PowerPlant> findOwned(final PowerPlantNumber ppn) {
        for (PowerPlant plant : powerPlants) {
            if (plant.number().equals(ppn)) {
                return Optional.of(plant);
            }
        }
        return Optional.empty();
    }
}
