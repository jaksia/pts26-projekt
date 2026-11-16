package sk.uniba.fmph.dcs.power_grid;

/**
 * Result of an action that may advance or end a phase.
 */
public enum PhaseStatus {
    PHASE_CONTINUES,
    PHASE_ENDS,
    GAME_ENDS
}
