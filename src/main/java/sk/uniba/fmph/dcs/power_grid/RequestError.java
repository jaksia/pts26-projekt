package sk.uniba.fmph.dcs.power_grid;

/**
 * Describes why a player request could not be completed.
 *
 * @param description a human-readable description of the error
 */
public record RequestError(String description) {
}
