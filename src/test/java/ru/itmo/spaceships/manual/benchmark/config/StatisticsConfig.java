package ru.itmo.spaceships.manual.benchmark.config;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.statistics.manufacturer.*;
import ru.itmo.spaceships.statistics.overall.*;

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
	private final StreamManufacturerCounterStatistics delayedStreamManufacturerCounterStatistics =
			new StreamManufacturerCounterStatistics(3L);

	private final ConcurrentStreamManufacturerCounterStatistics concurrentStreamManufacturerCounterStatistics =
			new ConcurrentStreamManufacturerCounterStatistics();
	private final ConcurrentStreamManufacturerCounterStatistics concurrentDelayedStreamManufacturerCounterStatistics =
			new ConcurrentStreamManufacturerCounterStatistics(1L);

	private final SpliteratorStreamManufacturerCounterStatistics spliteratorStreamManufacturerCounterStatistics =
			new SpliteratorStreamManufacturerCounterStatistics();
	private final SpliteratorStreamManufacturerCounterStatistics spliteratorDelayedStreamManufacturerCounterStatistics =
			new SpliteratorStreamManufacturerCounterStatistics(3L);

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

	private final RxManufacturerCounterStatistics rxManufacturerCounterStatistics =
			new RxManufacturerCounterStatistics();
	private final RxManufacturerCounterStatistics rxDelayedManufacturerCounterStatistics =
			new RxManufacturerCounterStatistics(1L);
	private final RxBackpressureManufacturerCounterStatistics rxBackpressureManufacturerCounterStatistics =
			new RxBackpressureManufacturerCounterStatistics(1_000);

	private final RXSpaceShipStatistics rxSpaceShipStatistics = new RXSpaceShipStatistics();
	private final RXSpaceShipStatistics rxSpaceShipStatisticsDelayed = new RXSpaceShipStatistics(5L);
	private final RXBackpressureShipStatistics rxBackpressureShipStatistics = new RXBackpressureShipStatistics(1000, false);

}
