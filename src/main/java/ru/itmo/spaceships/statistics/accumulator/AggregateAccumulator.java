package ru.itmo.spaceships.statistics.accumulator;

import java.util.LongSummaryStatistics;

public class AggregateAccumulator extends LongSummaryStatistics implements Accumulator {

	@Override
	public String toString() {
		return String.format("min: %s, max: %s, avg: %s, count: %s", getMin(), getMax(), getAverage(), getCount());
	}
}
