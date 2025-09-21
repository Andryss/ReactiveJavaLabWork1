package ru.itmo.spaceships.statistics;

import ru.itmo.spaceships.model.SpaceShip;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CollectorSpaceShipStatistics implements StatisticsCalculator<SpaceShip, StatisticsAccumulator> {

	@Override
	public StatisticsAccumulator calculate(List<SpaceShip> objects) {
		return objects.stream()
				.collect(new SpaceShipStatisticsCollector());
	}

	private static class SpaceShipStatisticsCollector implements Collector<SpaceShip, StatisticsAccumulator, StatisticsAccumulator> {

		private static final DateTimeFormatter DATE_FORMATTER =
				DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

		@Override
		public Supplier<StatisticsAccumulator> supplier() {
			return StatisticsAccumulator::new;
		}

		@Override
		public BiConsumer<StatisticsAccumulator, SpaceShip> accumulator() {
			return (acc, ship) -> {
				acc.getCountByManufacturer().accumulator.merge(ship.getManufacturer(), 1L, Long::sum);

				acc.getCountByFuelType().accumulator.merge(ship.getEngine().getFuelType().name(), 1L, Long::sum);

				acc.getCountByDate().accumulator.merge(DATE_FORMATTER.format(ship.getManufactureDate()), 1L, Long::sum);

				acc.getAggregateMaxSpeed().accept(ship.getMaxSpeed());
				acc.getAggregateCrewMembers().accept(ship.getCrew().size());
				acc.getAggregateLength().accept(ship.getDimensions().length());

				acc.getAggregateCrewByShipType().accumulator.merge(ship.getType().name(),
						new LongSummaryStatistics(),
						(oldStats, newStats) -> {
							oldStats.accept(ship.getCrew().size());
							return oldStats;
						});
			};
		}

		@Override
		public BinaryOperator<StatisticsAccumulator> combiner() {
			return (acc1, acc2) -> {
				mergeMaps(acc1.getCountByManufacturer().accumulator, acc2.getCountByManufacturer().accumulator);

				mergeMaps(acc1.getCountByFuelType().accumulator, acc2.getCountByFuelType().accumulator);

				mergeMaps(acc1.getCountByDate().accumulator, acc2.getCountByDate().accumulator);

				acc1.getAggregateMaxSpeed().combine(acc2.getAggregateMaxSpeed());
				acc1.getAggregateCrewMembers().combine(acc2.getAggregateCrewMembers());
				acc1.getAggregateLength().combine(acc2.getAggregateLength());

				mergeSummaryStatisticsMaps(acc1.getAggregateCrewByShipType().accumulator,
						acc2.getAggregateCrewByShipType().accumulator);

				return acc1;
			};
		}

		@Override
		public Function<StatisticsAccumulator, StatisticsAccumulator> finisher() {
			return Function.identity();
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Set.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
		}

		private <K> void mergeMaps(Map<K, Long> map1, Map<K, Long> map2) {
			map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
		}

		private void mergeSummaryStatisticsMaps(Map<String, LongSummaryStatistics> map1,
		                                        Map<String, LongSummaryStatistics> map2) {
			map2.forEach((key, stats) -> map1.merge(key, stats, (oldStats, newStats) -> {
				oldStats.combine(newStats);
				return oldStats;
			}));
		}
	}
}

