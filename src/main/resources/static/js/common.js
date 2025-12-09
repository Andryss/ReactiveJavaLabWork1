/**
 * Common utilities and configuration
 */

// API base URL
const API_BASE = 'http://localhost:8080';

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
        statusText.textContent = 'Online';
    } else {
        statusDot.className = 'status-dot offline';
        statusText.textContent = 'Offline';
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

