package ru.itmo.spaceships.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.SpaceShipType;

/**
 * Конвертер для записи enum SpaceShipType в VARCHAR.
 */
@WritingConverter
public class SpaceShipTypeWritingConverter implements Converter<SpaceShipType, String> {
    @Override
    @NonNull
    public String convert(@NonNull SpaceShipType source) {
        return source.name();
    }
}

