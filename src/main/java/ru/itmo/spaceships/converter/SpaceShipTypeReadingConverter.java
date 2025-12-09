package ru.itmo.spaceships.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.SpaceShipType;

/**
 * Конвертер для чтения enum SpaceShipType из VARCHAR.
 */
@ReadingConverter
public class SpaceShipTypeReadingConverter implements Converter<String, SpaceShipType> {
    @Override
    @NonNull
    public SpaceShipType convert(@NonNull String source) {
        return SpaceShipType.valueOf(source);
    }
}

