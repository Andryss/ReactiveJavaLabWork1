/**
 * Translation utilities for enums and statuses
 */

/**
 * Translate SpaceShipType enum to Russian
 */
function translateSpaceShipType(type) {
    const translations = {
        'CARGO': 'Грузовой',
        'EXPLORATION': 'Исследовательский',
        'SCOUT': 'Разведывательный',
        'BATTLE': 'Боевой',
        'PASSENGER': 'Пассажирский'
    };
    return translations[type] || type;
}

/**
 * Translate FuelType enum to Russian
 */
function translateFuelType(type) {
    const translations = {
        'LIQUID_HYDROGEN': 'Жидкий водород',
        'KEROSENE': 'Керосин',
        'METHANE': 'Метан',
        'ELECTRIC': 'Ионное/электрическое',
        'NUCLEAR': 'Ядерное топливо'
    };
    return translations[type] || type;
}

/**
 * Translate MaintenanceStatus enum to Russian
 */
function translateMaintenanceStatus(status) {
    const translations = {
        'NEW': 'Новая',
        'ACCEPTED': 'Принята в работу',
        'DIAGNOSTICS': 'Диагностика',
        'APPROVAL': 'Согласование работ',
        'WAITING_PARTS': 'В ожидании запчастей',
        'IN_REPAIR': 'В ремонте',
        'QUALITY_CHECK': 'Тестирование / контроль качества',
        'READY_FOR_PICKUP': 'Готово / ожидает выдачи',
        'COMPLETED': 'Завершено / выдано',
        'CANCELLED': 'Отменена'
    };
    return translations[status] || status;
}

/**
 * Get Russian label for SpaceShipType option
 */
function getSpaceShipTypeOptionLabel(value) {
    return translateSpaceShipType(value);
}

/**
 * Get Russian label for FuelType option
 */
function getFuelTypeOptionLabel(value) {
    return translateFuelType(value);
}

/**
 * Get Russian label for MaintenanceStatus option
 */
function getMaintenanceStatusOptionLabel(value) {
    return translateMaintenanceStatus(value);
}

