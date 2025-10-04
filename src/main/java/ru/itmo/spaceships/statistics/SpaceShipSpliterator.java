package ru.itmo.spaceships.statistics;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import ru.itmo.spaceships.model.SpaceShip;

public class SpaceShipSpliterator implements Spliterator<SpaceShip> {

    public static final int MIN_BATCH_SIZE = 50;

    private final List<SpaceShip> list;
    private int currentIndex;
    private final int endIndex;

    public SpaceShipSpliterator(List<SpaceShip> list) {
        this(list, 0, list.size());
    }

    private SpaceShipSpliterator(List<SpaceShip> list, int start, int end) {
        this.list = list;
        this.currentIndex = start;
        this.endIndex = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super SpaceShip> action) {
        if (currentIndex < endIndex) {
            action.accept(list.get(currentIndex++));
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<SpaceShip> trySplit() {
        int remaining = endIndex - currentIndex;
        if (remaining <= MIN_BATCH_SIZE) {
            return null;
        }
        int midIndex = currentIndex + remaining / 2;
        SpaceShipSpliterator newSplit = new SpaceShipSpliterator(list, currentIndex, midIndex);
        currentIndex = midIndex;
        return newSplit;
    }

    @Override
    public long estimateSize() {
        return endIndex - currentIndex;
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED | IMMUTABLE | NONNULL;
    }
}
