package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Optional;

/**
 * Facade over a player's money, power plants and resources.
 */
public interface PlayerBoardFacade {

    PowerPlantNumber bestPowerPlantNumber();

    int outstandingMoney();

    void spendMoney(int amount);

    int numberOfConnectableCities();

    boolean hasEnoughMoney(int amount);

    /**
     * @param ppn
     *            the number of the power plant to receive
     * @param price
     *            the price paid for the power plant
     *
     * @return {@code false} if the player is above the power plant limit
     */
    boolean receivePowerPlant(PowerPlantNumber ppn, int price);

    Optional<RequestError> removePowerPlant(PowerPlantNumber ppn, List<Resource> removedResources);

    Optional<RequestError> buyResources(List<Resource> resources);

    Optional<RequestError> buyPowerPlant(List<PowerPlantNumber> ppns, List<Resource> resourcesToBurn);

    Optional<RequestError> usePowerPlants(PlayerColor player, List<PowerPlantNumber> ppns,
            List<Resource> resourcesToBurn);

    String state();
}
