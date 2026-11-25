package sk.uniba.fmph.dcs.power_grid;

/**
 * A player's money.
 */
public interface PlayerMoney {

    boolean hasEnoughMoney(int amount);

    void receiveMoney(int numberOfTowns);

    void spendMoney(int amount);
}
