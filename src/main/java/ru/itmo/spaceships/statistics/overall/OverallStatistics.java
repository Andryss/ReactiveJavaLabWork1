package ru.itmo.spaceships.statistics.overall;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Data;

@Data
public class OverallStatistics {
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private ConcurrentMap<String, Long> countByManufacturer = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Long> countByFuelType = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Long> countByDate = new ConcurrentHashMap<>();
    private LongSummaryStatistics aggregateMaxSpeed = new LongSummaryStatistics();
    private LongSummaryStatistics aggregateCrewMembers = new LongSummaryStatistics();
    private LongSummaryStatistics aggregateLength = new LongSummaryStatistics();
    private ConcurrentMap<String, LongSummaryStatistics> aggregateCrewByShipType = new ConcurrentHashMap<>();
}