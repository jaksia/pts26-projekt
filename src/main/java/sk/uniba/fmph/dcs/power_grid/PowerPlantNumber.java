package sk.uniba.fmph.dcs.power_grid;

public record PowerPlantNumber(int number) {
    public PowerPlantNumber {
        if (number < 3 || (number > 40 && number != 42 && number != 44 && number != 46 && number != 50)) {
            throw new IllegalArgumentException("Invalid power plant number: " + number);
        }
    }
}
