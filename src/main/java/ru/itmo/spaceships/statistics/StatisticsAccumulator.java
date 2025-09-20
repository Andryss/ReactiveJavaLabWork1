package ru.itmo.spaceships.statistics;

import lombok.Data;
import ru.itmo.spaceships.statistics.accumulator.AggregateAccumulator;
import ru.itmo.spaceships.statistics.accumulator.GroupedAccumulator;
import ru.itmo.spaceships.statistics.accumulator.GroupedAggregateAccumulator;

@Data
public class StatisticsAccumulator {
	private GroupedAccumulator countByManufacturer = new GroupedAccumulator();
	private GroupedAccumulator countByFuelType = new GroupedAccumulator();
	private GroupedAccumulator countByDate = new GroupedAccumulator();
	private AggregateAccumulator aggregateMaxSpeed = new AggregateAccumulator();
	private AggregateAccumulator aggregateCrewMembers = new AggregateAccumulator();
	private AggregateAccumulator aggregateLength = new AggregateAccumulator();
	private GroupedAggregateAccumulator aggregateCrewByShipType = new GroupedAggregateAccumulator();

	public String getStatistics() {
		return "\ncountByManufacturer: " + countByManufacturer.getResult() + "\n" +
				"aggregateMaxSpeed: " + aggregateMaxSpeed.getResult() + "\n" +
				"aggregateCrewMembers: " + aggregateCrewMembers.getResult() + "\n" +
				"aggregateLength: " + aggregateLength.getResult() + "\n" +
				"countByFuelType: " + countByFuelType.getResult() + "\n" +
				"aggregateCrewByShipType: " + aggregateCrewByShipType.getResult() + "\n" +
				"countByDate: " + countByDate.getResult() + "\n";
	}
}