package ru.itmo.spaceships.manual.benchmark.config;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.statistics.manufacturer.*;
import ru.itmo.spaceships.statistics.overall.ConcurrentStreamSpaceShipStatistics;
import ru.itmo.spaceships.statistics.overall.SequenceStreamSpaceShipStatistics;
import ru.itmo.spaceships.statistics.overall.SpliteratorConcurrentStreamSpaceShipStatistics;

/**
 * Состояние со всеми статистиками.
 * Вынесено в STATE-класс, чтобы не тратить время на инициализацию во время замеров
 */
@Getter
@State(Scope.Benchmark)
public class StatisticsConfig {
	private final CycleManufacturerCounterStatistics cycleManufacturerCounterStatistics =
			new CycleManufacturerCounterStatistics();

	private final StreamManufacturerCounterStatistics streamManufacturerCounterStatistics =
			new StreamManufacturerCounterStatistics();
	private final DelayedStreamManufacturerCounterStatistics delayedStreamManufacturerCounterStatistics =
			new DelayedStreamManufacturerCounterStatistics();

	private final ConcurrentStreamManufacturerCounterStatistics concurrentStreamManufacturerCounterStatistics =
			new ConcurrentStreamManufacturerCounterStatistics();
	private final ConcurrentDelayedStreamManufacturerCounterStatistics concurrentDelayedStreamManufacturerCounterStatistics =
			new ConcurrentDelayedStreamManufacturerCounterStatistics();

	private final SpliteratorStreamManufacturerCounterStatistics spliteratorStreamManufacturerCounterStatistics =
			new SpliteratorStreamManufacturerCounterStatistics();
	private final SpliteratorDelayedStreamManufacturerCounterStatistics spliteratorDelayedStreamManufacturerCounterStatistics =
			new SpliteratorDelayedStreamManufacturerCounterStatistics();

	private final ConcurrentStreamSpaceShipStatistics concurrentStreamSpaceShipStatistics =
			new ConcurrentStreamSpaceShipStatistics();
	private final SpliteratorConcurrentStreamSpaceShipStatistics spliteratorStreamSpaceShipStatistics =
			new SpliteratorConcurrentStreamSpaceShipStatistics();
	private final ConcurrentStreamSpaceShipStatistics concurrentDelayedStreamSpaceShipStatistics =
			new ConcurrentStreamSpaceShipStatistics(5L);
	private final SequenceStreamSpaceShipStatistics sequenceStreamSpaceShipStatistics =
			new SequenceStreamSpaceShipStatistics();
	private final SequenceStreamSpaceShipStatistics sequenceDelayedStreamSpaceShipStatistics =
			new SequenceStreamSpaceShipStatistics(5L);
}
