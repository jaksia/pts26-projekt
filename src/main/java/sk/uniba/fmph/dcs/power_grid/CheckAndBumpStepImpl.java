package sk.uniba.fmph.dcs.power_grid;

import java.util.List;
import java.util.Objects;

public final class CheckAndBumpStepImpl implements CheckAndBumpStep {
    private final Towns towns;
    private final ResourceMarket resourceMarket;
    private final PowerPlantMarket powerPlantMarket;
    private final List<PlayerColor> players;
    private final int step2Threshold;

    private int step = 1;

    public CheckAndBumpStepImpl(final Towns towns, final ResourceMarket resourceMarket,
                                final PowerPlantMarket powerPlantMarket, final List<PlayerColor> players) {
        Objects.requireNonNull(towns, "towns must not be null");
        Objects.requireNonNull(resourceMarket, "resource market must not be null");
        Objects.requireNonNull(powerPlantMarket, "power plant market must not be null");
        Objects.requireNonNull(players, "players must not be null");
        if (players.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("players must not contain null values");
        }

        this.towns = towns;
        this.resourceMarket = resourceMarket;
        this.powerPlantMarket = powerPlantMarket;
        this.players = List.copyOf(players);
        this.step2Threshold = step2ThresholdFor(players.size());
    }

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public void checkAndBumpToStep2() {
        if (step != 1) {
            return;
        }

        for (PlayerColor player : players) {
            if (towns.getNumberOfHouses(player) >= step2Threshold) {
                startStep2();
                return;
            }
        }
    }

    @Override
    public void checkAndBumpToStep3() {
        if (step == 3) {
            return;
        }

        if (step == 1) {
            startStep2();
        }

        towns.startStep3();
        resourceMarket.startStep3();
        powerPlantMarket.startStep3();
        step = 3;
    }

    private void startStep2() {
        towns.startStep2();
        resourceMarket.startStep2();
        powerPlantMarket.startStep2();
        step = 2;
    }

    private static int step2ThresholdFor(final int playerCount) {
        if (playerCount < 2 || playerCount > 6) {
            throw new IllegalArgumentException("Invalid player count: " + playerCount);
        }
        if (playerCount == 6) {
            return 6;
        }

        return 7;
    }
}
