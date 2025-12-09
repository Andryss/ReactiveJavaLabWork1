/**
 * Pagination state management for main tables
 */

// Pagination state for main tables
let spaceshipsTablePage = 0;
let spaceshipsTablePageSize = 20;
let spaceshipsTableTotalItems = 0;

let repairmenTablePage = 0;
let repairmenTablePageSize = 20;
let repairmenTableTotalItems = 0;

let requestsTablePage = 0;
let requestsTablePageSize = 20;
let requestsTableTotalItems = 0;

/**
 * Update pagination buttons for spaceships table
 */
function updateSpaceshipsTablePaginationButtons() {
    const prevBtn = document.getElementById('spaceshipsPrevBtn');
    const nextBtn = document.getElementById('spaceshipsNextBtn');
    const pageInfo = document.getElementById('spaceshipsPageInfo');
    
    prevBtn.disabled = spaceshipsTablePage === 0;
    nextBtn.disabled = spaceshipsTableTotalItems < spaceshipsTablePageSize;
    pageInfo.textContent = `Страница ${spaceshipsTablePage + 1}`;
}

/**
 * Navigate spaceships table page
 * @param {number} direction - -1 for previous, 1 for next
 */
function navigateSpaceshipsPage(direction) {
    const newPage = spaceshipsTablePage + direction;
    if (newPage >= 0) {
        spaceshipsTablePage = newPage;
        loadSpaceships();
    }
}

/**
 * Update pagination buttons for repairmen table
 */
function updateRepairmenTablePaginationButtons() {
    const prevBtn = document.getElementById('repairmenPrevBtn');
    const nextBtn = document.getElementById('repairmenNextBtn');
    const pageInfo = document.getElementById('repairmenPageInfo');
    
    prevBtn.disabled = repairmenTablePage === 0;
    nextBtn.disabled = repairmenTableTotalItems < repairmenTablePageSize;
    pageInfo.textContent = `Страница ${repairmenTablePage + 1}`;
}

/**
 * Navigate repairmen table page
 * @param {number} direction - -1 for previous, 1 for next
 */
function navigateRepairmenPage(direction) {
    const newPage = repairmenTablePage + direction;
    if (newPage >= 0) {
        repairmenTablePage = newPage;
        loadRepairmen();
    }
}

/**
 * Update pagination buttons for requests table
 */
function updateRequestsTablePaginationButtons() {
    const prevBtn = document.getElementById('requestsPrevBtn');
    const nextBtn = document.getElementById('requestsNextBtn');
    const pageInfo = document.getElementById('requestsPageInfo');
    
    prevBtn.disabled = requestsTablePage === 0;
    nextBtn.disabled = requestsTableTotalItems < requestsTablePageSize;
    pageInfo.textContent = `Страница ${requestsTablePage + 1}`;
}

/**
 * Navigate requests table page
 * @param {number} direction - -1 for previous, 1 for next
 */
function navigateRequestsPage(direction) {
    const newPage = requestsTablePage + direction;
    if (newPage >= 0) {
        requestsTablePage = newPage;
        loadRequests();
    }
}

