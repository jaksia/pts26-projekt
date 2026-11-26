package sk.uniba.fmph.dcs.power_grid;

import java.util.Optional;

/**
 * Market offering power plants for auction.
 */
public interface PowerPlantMarket {

    Optional<RequestError> canTakePowerPlant(PowerPlantNumber ppn, int price);

    PowerPlant takePowerPlant(PowerPlantNumber ppn);

    void removeDiscountedPowerPlant(int value);

    void removePowerPlantsCheaperThan(int value);

    void prepareNextTurn();

    boolean isStep3Start();

    void startStep2();

    void startStep3();
}
