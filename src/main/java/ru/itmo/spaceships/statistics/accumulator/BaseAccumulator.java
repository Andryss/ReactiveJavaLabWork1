package ru.itmo.spaceships.statistics.accumulator;

public class BaseAccumulator implements Accumulator {
	Long accumulator = 0L;

	@Override
	public String toString() {
		return accumulator.toString();
	}
}
