package ru.itmo.spaceships.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Генератор сущностей одного типа
 * @param <T> тип генерируемого объекта
 */
public interface Generator<T> {
    /**
     * Сгенерировать один объект
     */
    T generateOne();

    /**
     * Сгенерировать заданное количество объектов
     */
    default List<T> generateMany(int count) {
        ArrayList<T> objects = new ArrayList<>(count);
        while (count-- > 0) {
            objects.add(generateOne());
        }
        return objects;
    }
}
