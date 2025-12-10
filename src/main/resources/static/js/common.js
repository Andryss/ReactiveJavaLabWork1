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

// Initialize status monitor
startPingStream();

// Initialize repairman updates stream
startRepairmanUpdatesStream();

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

