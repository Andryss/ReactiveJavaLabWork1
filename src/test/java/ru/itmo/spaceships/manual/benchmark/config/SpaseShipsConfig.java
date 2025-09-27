package ru.itmo.spaceships.manual.benchmark.config;

import java.util.List;

import lombok.Getter;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.model.SpaceShip;

@Getter
@State(Scope.Benchmark)
public class SpaseShipsConfig {

    private List<SpaceShip> smallBatch;
    private List<SpaceShip> mediumBatch;
    private List<SpaceShip> largeBatch;

    @Setup(Level.Trial)
    public void setUp(RandomConfig randomConfig) {
        SpaseShipGenerator generator = new SpaseShipGenerator(randomConfig.getSeededRandom());
        smallBatch = generator.generateMany(5_000);
        mediumBatch = generator.generateMany(50_000);
        largeBatch = generator.generateMany(250_000);
    }
}
