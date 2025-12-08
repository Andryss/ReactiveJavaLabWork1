package ru.itmo.spaceships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions.StoreConversions;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import ru.itmo.spaceships.converter.CrewMemberListReadingConverter;
import ru.itmo.spaceships.converter.CrewMemberListWritingConverter;
import ru.itmo.spaceships.converter.DimensionsReadingConverter;
import ru.itmo.spaceships.converter.DimensionsWritingConverter;
import ru.itmo.spaceships.converter.EngineReadingConverter;
import ru.itmo.spaceships.converter.EngineWritingConverter;
import ru.itmo.spaceships.converter.SpaceShipTypeReadingConverter;
import ru.itmo.spaceships.converter.SpaceShipTypeWritingConverter;
import ru.itmo.spaceships.converter.MaintenanceStatusReadingConverter;
import ru.itmo.spaceships.converter.MaintenanceStatusWritingConverter;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

import java.util.List;

/**
 * R2DBC configuration for custom converters.
 */
@Configuration
public class R2dbcConfig {

    /**
     * Configure R2DBC custom conversions.
     *
     * @param objectMapperWrapper object mapper wrapper for JSON serialization
     * @return R2DBC custom conversions
     */
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ObjectMapperWrapper objectMapperWrapper) {
        return new R2dbcCustomConversions(
                StoreConversions.of(PostgresDialect.INSTANCE.getSimpleTypeHolder()),
                List.of(
                        new DimensionsWritingConverter(objectMapperWrapper),
                        new DimensionsReadingConverter(objectMapperWrapper),
                        new EngineWritingConverter(objectMapperWrapper),
                        new EngineReadingConverter(objectMapperWrapper),
                        new CrewMemberListWritingConverter(objectMapperWrapper),
                        new CrewMemberListReadingConverter(objectMapperWrapper),
                        new SpaceShipTypeWritingConverter(),
                        new SpaceShipTypeReadingConverter(),
                        new MaintenanceStatusWritingConverter(),
                        new MaintenanceStatusReadingConverter()
                )
        );
    }
}

