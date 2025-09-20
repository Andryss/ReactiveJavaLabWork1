package ru.itmo.spaceships.statistics.accumulator;

import java.util.HashMap;
import java.util.Map;

public class GroupedAccumulator implements Accumulator {
	public Map<String, Long> accumulator = new HashMap<>();

	@Override
	public String getResult() {
		return accumulator.toString();
	}
}
