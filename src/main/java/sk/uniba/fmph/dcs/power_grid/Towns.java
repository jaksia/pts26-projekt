package sk.uniba.fmph.dcs.power_grid;

import java.util.List;

/**
 * Towns and the houses built in them.
 */
public interface Towns {

    Variant<Integer, RequestError> buyHouses(PlayerColor player, List<String> townNames, int maxMoney);

    int getNumberOfHouses(PlayerColor player);

    int getMaxNumberOfHouses();

    void startStep2();

    void startStep3();
}
