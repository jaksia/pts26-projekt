package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Towns and the houses built in them.
 */
public interface Towns {

    /*
     * BuildingHouses requires following:
     * - the returned integer is the total cost, including connection costs
     * - `maxMoney` is the player's currently available money and purchases above it are rejected
     * - the step-dependent per-town house limit and town availability are checked here.
     */
    Variant<Integer, RequestError> buyHouses(
            PlayerColor player, List<String> townNames, int maxMoney);

    int getNumberOfHouses(PlayerColor player);

    int getMaxNumberOfHouses();

    void startStep2();

    void startStep3();
}
