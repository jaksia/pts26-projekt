package sk.uniba.fmph.dcs.power_grid;

/**
 * Number identifying a power plant.
 *
 * @param number the numeric value of the power plant number
 */
public record PowerPlantNumber(int number) {

    public PowerPlantNumber {
        if (!isValid(number)) {
            throw new IllegalArgumentException(
                    "As per game rules, power plant number must be one of 03-40, 42, 44, 46 or 50.");
        }
    }

    private static boolean isValid(final int number) {
        return (number >= 3 && number <= 40)
                || number == 42
                || number == 44
                || number == 46
                || number == 50;
    }
}
