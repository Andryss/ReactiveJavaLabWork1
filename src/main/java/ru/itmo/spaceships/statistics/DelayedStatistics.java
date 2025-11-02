package ru.itmo.spaceships.statistics;

public abstract class DelayedStatistics {
    protected final long delay;

    public DelayedStatistics(long delay) {
        this.delay = delay;
    }

    public DelayedStatistics() {
        this.delay = 0;
    }

    public long getDelay() {
        return delay;
    }
}
