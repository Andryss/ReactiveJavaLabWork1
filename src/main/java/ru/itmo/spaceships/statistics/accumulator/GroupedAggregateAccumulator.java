package ru.itmo.spaceships.statistics.accumulator;

import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;

public class GroupedAggregateAccumulator implements Accumulator {
	public Map<String, LongSummaryStatistics> accumulator = new HashMap<>();

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("\n");
		for (Map.Entry<String, LongSummaryStatistics> entry : accumulator.entrySet()) {
			AggregateAccumulator aggregateAccumulator = new AggregateAccumulator();
			aggregateAccumulator.combine(entry.getValue());
			result.append(entry.getKey()).append(": ").append(aggregateAccumulator).append("\n");
		}
		return result.toString();
	}
}
