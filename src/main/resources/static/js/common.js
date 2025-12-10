/**
 * Common utilities and configuration
 */

// API base URL
const API_BASE = 'http://localhost:8080';

/**
 * Округляет число до 3 знаков после запятой
 * @param {number} num - Число для округления
 * @returns {number} Округлённое число
 */
function roundTo3Decimals(num) {
    if (num === null || num === undefined || isNaN(num)) {
        return 0;
    }
    return Math.round(num * 1000) / 1000;
}

/**
 * Парсит timestamp с возможным timezone offset.
 * Если timezone присутствует в строке - использует его, иначе считает UTC.
 * @param {string} timestamp - Timestamp в формате ISO 8601 (может содержать timezone offset)
 * @returns {Date|null} Date объект или null если не удалось распарсить
 */
function parseTimestamp(timestamp) {
    if (!timestamp) {
        return null;
    }
    
    // Пытаемся распарсить timestamp
    // Date конструктор автоматически обрабатывает timezone offset если он присутствует
    // Если offset отсутствует, считаем что это UTC (добавляем 'Z' в конец)
    let dateString = timestamp.trim();
    
    // Если строка не заканчивается на 'Z' и не содержит timezone offset (+/-HH:MM)
    // то считаем что это UTC и добавляем 'Z'
    if (!dateString.endsWith('Z') && !/[\+\-]\d{2}:\d{2}$/.test(dateString)) {
        // Если нет timezone, добавляем 'Z' чтобы указать UTC
        if (!dateString.includes('T')) {
            // Если нет времени, добавляем время
            dateString += 'T00:00:00Z';
        } else {
            dateString += 'Z';
        }
    }
    
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
        return null;
    }
    return date;
}

/**
 * Конвертирует timestamp с timezone offset в локальное время для datetime-local input.
 * Backend может отправлять timestamp с timezone offset или без него (UTC).
 * datetime-local input ожидает локальное время без timezone.
 * @param {string} timestamp - Timestamp в формате ISO 8601 (может содержать timezone offset)
 * @returns {string} Локальное время в формате YYYY-MM-DDTHH:mm для datetime-local input
 */
function timestampToLocalDateTime(timestamp) {
    const date = parseTimestamp(timestamp);
    if (!date) {
        return '';
    }
    
    // Получаем локальное время и форматируем для datetime-local input
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

/**
 * Конвертирует локальное время из datetime-local input в timestamp с timezone offset устройства.
 * datetime-local input возвращает локальное время без timezone.
 * Нужно добавить timezone offset устройства к отправляемому timestamp.
 * @param {string} localDateTime - Локальное время в формате YYYY-MM-DDTHH:mm
 * @returns {string} Timestamp в формате ISO 8601 с timezone offset устройства
 */
function localDateTimeToTimestamp(localDateTime) {
    if (!localDateTime) {
        return null;
    }
    
    // Создаём Date объект из локального времени (браузер интерпретирует как локальное)
    const date = new Date(localDateTime);
    if (isNaN(date.getTime())) {
        return null;
    }
    
    // Получаем timezone offset устройства в минутах
    const timezoneOffset = date.getTimezoneOffset();
    
    // Преобразуем offset в формат +/-HH:MM
    const offsetHours = Math.floor(Math.abs(timezoneOffset) / 60);
    const offsetMinutes = Math.abs(timezoneOffset) % 60;
    const offsetSign = timezoneOffset <= 0 ? '+' : '-'; // Инвертируем знак, т.к. getTimezoneOffset возвращает обратный знак
    const offsetString = `${offsetSign}${String(offsetHours).padStart(2, '0')}:${String(offsetMinutes).padStart(2, '0')}`;
    
    // Форматируем дату в ISO формат с timezone offset
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    const milliseconds = String(date.getMilliseconds()).padStart(3, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.${milliseconds}${offsetString}`;
}

/**
 * Форматирует timestamp для отображения в локальном времени устройства.
 * Парсит timestamp с возможным timezone offset и отображает в локальном времени.
 * @param {string} timestamp - Timestamp в формате ISO 8601 (может содержать timezone offset)
 * @param {string} format - Формат: 'date' для даты, 'datetime' для даты и времени, 'time' для времени
 * @returns {string} Отформатированная строка в локальном времени устройства
 */
function formatTimestamp(timestamp, format = 'datetime') {
    const date = parseTimestamp(timestamp);
    if (!date) {
        return '-';
    }
    
    if (format === 'date') {
        return date.toLocaleDateString('ru-RU');
    } else if (format === 'datetime') {
        return date.toLocaleString('ru-RU');
    } else if (format === 'time') {
        return date.toLocaleTimeString('ru-RU');
    }
    return date.toLocaleString('ru-RU');
}

/**
 * Извлекает сообщение об ошибке из ответа API.
 * Если ответ содержит ErrorObject, возвращает humanMessage.
 * Иначе возвращает текст ошибки или стандартное сообщение.
 * @param {Response} response - Объект ответа fetch
 * @returns {Promise<string>} Сообщение об ошибке
 */
async function extractErrorMessage(response) {
    try {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const errorObject = await response.json();
            if (errorObject.humanMessage) {
                return errorObject.humanMessage;
            }
            if (errorObject.message) {
                return errorObject.message;
            }
        }
        const text = await response.text();
        if (text) {
            return text;
        }
    } catch (e) {
        // Если не удалось распарсить ответ, используем стандартное сообщение
    }
    return `HTTP ${response.status}: ${response.statusText}`;
}

// Server Status Monitor
const statusDot = document.getElementById('statusDot');
const statusText = document.getElementById('statusText');

/**
 * Set server status indicator
 * @param {boolean} online - true if server is online
 */
function setStatus(online) {
    if (online) {
        statusDot.className = 'status-dot online';
        statusText.textContent = 'Онлайн';
    } else {
        statusDot.className = 'status-dot offline';
        statusText.textContent = 'Офлайн';
    }
}

// Global EventSource instance for server status monitoring
let pingEventSource = null;

/**
 * Start server status monitoring via SSE (opens once and remains open during session)
 */
function startPingStream() {
    // Don't create a new connection if one already exists and is open
    if (pingEventSource && pingEventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    
    // Close existing connection if any (shouldn't happen, but just in case)
    if (pingEventSource) {
        pingEventSource.close();
    }
    
    // Create new EventSource connection
    pingEventSource = new EventSource(`${API_BASE}/pinger`);
    pingEventSource.onmessage = () => setStatus(true);
    pingEventSource.onerror = () => setStatus(false);
    pingEventSource.onopen = () => {
        console.log('Server status monitor connected');
    };
}

// Global EventSource instance for repairman updates stream
let repairmanUpdatesEventSource = null;

/**
 * Start repairman updates stream via SSE (opens once and remains open during session)
 */
function startRepairmanUpdatesStream() {
    // Don't create a new connection if one already exists and is open
    if (repairmanUpdatesEventSource && repairmanUpdatesEventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    
    // Close existing connection if any (shouldn't happen, but just in case)
    if (repairmanUpdatesEventSource) {
        repairmanUpdatesEventSource.close();
    }
    
    // Create new EventSource connection
    repairmanUpdatesEventSource = new EventSource(`${API_BASE}/repairmen/updates/stream`);
    
    repairmanUpdatesEventSource.onmessage = (event) => {
        try {
            // Parse the repairman update from SSE
            // Spring WebFlux sends JSON objects directly in the data field
            const data = event.data;
            if (data && data.trim() !== '') {
                const repairman = JSON.parse(data);
                if (repairman && repairman.id) {
                    handleRepairmanUpdate(repairman);
                }
            }
        } catch (error) {
            console.error('Error parsing repairman update from stream:', error, event.data);
        }
    };
    
    repairmanUpdatesEventSource.onerror = () => {
        // No reconnection - similar to ping stream
    };
    
    repairmanUpdatesEventSource.onopen = () => {
        console.log('Repairman updates stream connected');
    };
}

// Global EventSource instance for maintenance request updates stream
let maintenanceRequestUpdatesEventSource = null;

/**
 * Start maintenance request updates stream via SSE (opens once and remains open during session)
 */
function startMaintenanceRequestUpdatesStream() {
    // Don't create a new connection if one already exists and is open
    if (maintenanceRequestUpdatesEventSource && maintenanceRequestUpdatesEventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    
    // Close existing connection if any (shouldn't happen, but just in case)
    if (maintenanceRequestUpdatesEventSource) {
        maintenanceRequestUpdatesEventSource.close();
    }
    
    // Create new EventSource connection
    maintenanceRequestUpdatesEventSource = new EventSource(`${API_BASE}/maintenance-requests/updates/stream`);
    
    maintenanceRequestUpdatesEventSource.onmessage = (event) => {
        try {
            // Parse the maintenance request update from SSE
            // Spring WebFlux sends JSON objects directly in the data field
            const data = event.data;
            if (data && data.trim() !== '') {
                const request = JSON.parse(data);
                if (request && request.id) {
                    // Handle the update
                    if (typeof handleRequestUpdate === 'function') {
                        handleRequestUpdate(request);
                    }
                }
            }
        } catch (error) {
            console.error('Error parsing maintenance request update from stream:', error, event.data);
        }
    };
    
    maintenanceRequestUpdatesEventSource.onerror = () => {
        // No reconnection - similar to ping stream
    };
    
    maintenanceRequestUpdatesEventSource.onopen = () => {
        console.log('Maintenance request updates stream connected');
    };
}

// Global EventSource instance for spaceship updates stream
let spaceshipUpdatesEventSource = null;

/**
 * Start spaceship updates stream via SSE (opens once and remains open during session)
 */
function startSpaceshipUpdatesStream() {
    // Don't create a new connection if one already exists and is open
    if (spaceshipUpdatesEventSource && spaceshipUpdatesEventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    
    // Close existing connection if any (shouldn't happen, but just in case)
    if (spaceshipUpdatesEventSource) {
        spaceshipUpdatesEventSource.close();
    }
    
    // Create new EventSource connection
    spaceshipUpdatesEventSource = new EventSource(`${API_BASE}/spaceships/updates/stream`);
    
    spaceshipUpdatesEventSource.onmessage = (event) => {
        try {
            // Parse the spaceship update from SSE
            // Spring WebFlux sends JSON objects directly in the data field
            const data = event.data;
            if (data && data.trim() !== '') {
                const spaceship = JSON.parse(data);
                if (spaceship && spaceship.serial) {
                    // Handle the update
                    if (typeof handleSpaceshipUpdate === 'function') {
                        handleSpaceshipUpdate(spaceship);
                    }
                }
            }
        } catch (error) {
            console.error('Error parsing spaceship update from stream:', error, event.data);
        }
    };
    
    spaceshipUpdatesEventSource.onerror = () => {
        // No reconnection - similar to ping stream
    };
    
    spaceshipUpdatesEventSource.onopen = () => {
        console.log('Spaceship updates stream connected');
    };
}

// Initialize status monitor
startPingStream();

// Initialize repairman updates stream
startRepairmanUpdatesStream();

// Initialize maintenance request updates stream
startMaintenanceRequestUpdatesStream();

// Initialize spaceship updates stream
startSpaceshipUpdatesStream();

// Load data when tabs are shown
document.getElementById('spaceships-tab').addEventListener('shown.bs.tab', () => {
    spaceshipsTablePage = 0;
    loadSpaceships();
});
document.getElementById('staff-tab').addEventListener('shown.bs.tab', () => {
    repairmenTablePage = 0;
    loadRepairmen();
});
document.getElementById('requests-tab').addEventListener('shown.bs.tab', () => {
    requestsTablePage = 0;
    loadRequests();
});

