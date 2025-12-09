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

/**
 * Start server status monitoring via SSE
 */
function startPingStream() {
    const evtSource = new EventSource(`${API_BASE}/pinger`);
    evtSource.onmessage = () => setStatus(true);
    evtSource.onerror = () => setStatus(false);
}

// Initialize status monitor
startPingStream();

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

