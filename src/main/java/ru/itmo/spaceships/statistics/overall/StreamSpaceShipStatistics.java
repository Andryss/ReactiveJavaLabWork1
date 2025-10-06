package ru.itmo.spaceships.statistics.overall;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

public abstract class StreamSpaceShipStatistics implements StatisticsCalculator<SpaceShip, OverallStatistics> {
    protected final long delay;


    public StreamSpaceShipStatistics(long delay) {
        this.delay = delay;
    }

    public StreamSpaceShipStatistics() {
        this.delay = 0;
    }
}
