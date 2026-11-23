package sk.uniba.fmph.dcs.power_grid;

/**
 * Tracks the current game step and advances it when the conditions are met.
 */
public interface CheckAndBumpStep {

    int getStep();

    void checkAndBumpToStep2();

    void checkAndBumpToStep3();
}
