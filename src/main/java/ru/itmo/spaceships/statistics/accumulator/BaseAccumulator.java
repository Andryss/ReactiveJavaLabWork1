package ru.itmo.spaceships.statistics.accumulator;

public class BaseAccumulator implements Accumulator {
	Long accumulator = 0L;

	@Override
	public String getResult() {
		return accumulator.toString();
	}
}
