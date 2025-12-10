/**
 * Maintenance requests management functions
 */

// Track currently displayed/edited maintenance request ID
let currentRequestId = null;

// Track if modal has unsaved changes
let requestModalHasChanges = false;

// Store the last received update for the current request
let pendingRequestUpdate = null;

/**
 * Status transition rules matching the backend MaintenanceStatus enum
 * Maps each status to its allowed transitions (including staying in the same status)
 */
const STATUS_TRANSITIONS = {
    'NEW': ['NEW', 'ACCEPTED', 'CANCELLED'],
    'ACCEPTED': ['ACCEPTED', 'DIAGNOSTICS', 'CANCELLED'],
    'DIAGNOSTICS': ['DIAGNOSTICS', 'APPROVAL', 'CANCELLED'],
    'APPROVAL': ['APPROVAL', 'WAITING_PARTS', 'IN_REPAIR', 'CANCELLED'],
    'WAITING_PARTS': ['WAITING_PARTS', 'IN_REPAIR', 'CANCELLED'],
    'IN_REPAIR': ['IN_REPAIR', 'QUALITY_CHECK', 'CANCELLED'],
    'QUALITY_CHECK': ['QUALITY_CHECK', 'READY_FOR_PICKUP', 'CANCELLED'],
    'READY_FOR_PICKUP': ['READY_FOR_PICKUP', 'COMPLETED', 'CANCELLED'],
    'COMPLETED': ['COMPLETED'],
    'CANCELLED': ['CANCELLED']
};

/**
 * All possible status values
 */
const ALL_STATUSES = ['NEW', 'ACCEPTED', 'DIAGNOSTICS', 'APPROVAL', 'WAITING_PARTS', 
                      'IN_REPAIR', 'QUALITY_CHECK', 'READY_FOR_PICKUP', 'COMPLETED', 'CANCELLED'];

/**
 * Populate status dropdown with only allowed transitions from current status
 * @param {string} currentStatus - Current status of the request
 */
function populateStatusDropdown(currentStatus) {
    const statusSelect = document.getElementById('requestStatus');
    
    // Normalize status (handle null/undefined and uppercase)
    const normalizedStatus = (currentStatus || 'NEW').toUpperCase();
    
    // Get allowed transitions (fallback to current status only if not found)
    const allowedStatuses = STATUS_TRANSITIONS[normalizedStatus] || [normalizedStatus];
    
    // Clear existing options
    statusSelect.innerHTML = '';
    
    // Add only allowed statuses
    allowedStatuses.forEach(status => {
        const option = document.createElement('option');
        option.value = status;
        option.textContent = translateMaintenanceStatus(status);
        statusSelect.appendChild(option);
    });
    
    // Set current status as selected (if it's in the allowed list)
    if (allowedStatuses.includes(normalizedStatus)) {
        statusSelect.value = normalizedStatus;
    } else if (allowedStatuses.length > 0) {
        // Fallback to first allowed status if current status is not in list
        statusSelect.value = allowedStatuses[0];
    }
}

/**
 * Load maintenance requests list
 */
async function loadRequests() {
    try {
        const response = await fetch(`${API_BASE}/maintenance-requests?page=${requestsTablePage}&size=${requestsTablePageSize}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            document.getElementById('requestsTableBody').innerHTML = 
                `<tr><td colspan="7" class="text-center text-danger">Ошибка загрузки заявок: ${errorMessage}</td></tr>`;
            return;
        }
        
        const data = await response.json();
        requestsTableTotalItems = data.length;
        
        const tbody = document.getElementById('requestsTableBody');
        tbody.innerHTML = data.map(req => `
            <tr style="cursor: pointer;" onclick="viewRequest(${req.id})">
                <td>${req.id}</td>
                <td>
                    ${req.spaceshipSerial ? `
                        <span class="text-primary" style="cursor: pointer; text-decoration: underline;" 
                              onmouseenter="showSpaceshipTooltip(${req.spaceshipSerial})" 
                              onmouseleave="hideSpaceshipTooltip()"
                              onclick="hideSpaceshipTooltip(); viewSpaceship(${req.spaceshipSerial})" 
                              title="Наведите курсор или нажмите для просмотра деталей">
                            ${req.spaceshipSerial}
                        </span>
                    ` : '-'}
                </td>
                <td>${req.comment || '-'}</td>
                <td><span class="badge bg-info">${translateMaintenanceStatus(req.status || 'NEW')}</span></td>
                <td>
                    ${req.assignee ? `
                        <span class="text-primary" style="cursor: pointer; text-decoration: underline;" 
                              onmouseenter="showRepairmanTooltip(${req.assignee})" 
                              onmouseleave="hideRepairmanTooltip()"
                              onclick="hideRepairmanTooltip(); viewRepairman(${req.assignee})" 
                              title="Наведите курсор или нажмите для просмотра деталей">
                            ${req.assignee}
                        </span>
                    ` : '-'}
                </td>
                <td>${formatTimestamp(req.createdAt, 'datetime')}</td>
                <td onclick="event.stopPropagation();">
                    <button class="btn btn-sm btn-primary" onclick="editRequest(${req.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteRequest(${req.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        
        // Update pagination buttons
        updateRequestsTablePaginationButtons();
    } catch (error) {
        document.getElementById('requestsTableBody').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Ошибка загрузки заявок</td></tr>';
    }
}

/**
 * Show request modal for create/edit
 * @param {number|null} id - Request ID (null for create)
 */
async function showRequestModal(id = null) {
    currentRequestId = id; // Track currently edited request
    requestModalHasChanges = false; // Reset changes tracking
    pendingRequestUpdate = null; // Clear any pending updates
    hideRequestUpdateWarning(); // Hide warning if visible
    
    document.getElementById('requestModalTitle').textContent = id ? 'Редактировать заявку' : 'Добавить заявку';
    document.getElementById('requestId').value = id || '';
    
    // Show/hide status field based on create/edit mode
    const statusContainer = document.getElementById('requestStatusContainer');
    if (id) {
        statusContainer.style.display = 'block';
        // Status dropdown will be populated when request data is loaded
    } else {
        statusContainer.style.display = 'none';
    }
    
    // Reset dropdowns
    document.getElementById('spaceshipSelectedText').textContent = 'Выберите корабль...';
    document.getElementById('assigneeSelectedText').textContent = 'Выберите ремонтника...';
    spaceshipPage = 0;
    assigneePage = 0;
    
    // Load first page of dropdowns
    await Promise.all([loadSpaceshipsForDropdown(0), loadRepairmenForDropdown(0)]);
    
    if (id) {
        fetch(`${API_BASE}/maintenance-requests/${id}`)
            .then(async r => {
                if (!r.ok) {
                    const errorMessage = await extractErrorMessage(r);
                    throw new Error(errorMessage);
                }
                return r.json();
            })
            .then(data => {
                document.getElementById('requestSpaceshipSerial').value = data.spaceshipSerial || '';
                if (data.spaceshipSerial) {
                    // Find and display the selected spaceship
                    fetch(`${API_BASE}/spaceships/${data.spaceshipSerial}`)
                        .then(async r => {
                            if (!r.ok) {
                                const errorMessage = await extractErrorMessage(r);
                                throw new Error(errorMessage);
                            }
                            return r.json();
                        })
                        .then(ship => {
                            document.getElementById('spaceshipSelectedText').textContent = 
                                `${ship.serial} - ${ship.name || 'Без названия'}`;
                        })
                        .catch(error => {
                            console.error('Ошибка загрузки данных корабля:', error);
                        });
                }
                document.getElementById('requestComment').value = data.comment || '';
                document.getElementById('requestAssignee').value = data.assignee || '';
                if (data.assignee) {
                    // Find and display the selected repairman
                    fetch(`${API_BASE}/repairmen/${data.assignee}`)
                        .then(async r => {
                            if (!r.ok) {
                                const errorMessage = await extractErrorMessage(r);
                                throw new Error(errorMessage);
                            }
                            return r.json();
                        })
                        .then(repairman => {
                            document.getElementById('assigneeSelectedText').textContent = 
                                `${repairman.id} - ${repairman.name}`;
                        })
                        .catch(error => {
                            console.error('Ошибка загрузки данных ремонтника:', error);
                        });
                }
                const currentStatus = data.status || 'NEW';
                // Populate status dropdown with only allowed transitions
                populateStatusDropdown(currentStatus);
            })
            .catch(error => {
                alert('Ошибка загрузки данных заявки: ' + (error.message || error));
            });
    } else {
        document.getElementById('requestForm').reset();
        document.getElementById('requestStatus').value = 'NEW';
    }
    
    // Track form changes
    const commentInput = document.getElementById('requestComment');
    const statusSelect = document.getElementById('requestStatus');
    const assigneeInput = document.getElementById('requestAssignee');
    const spaceshipInput = document.getElementById('requestSpaceshipSerial');
    
    const trackChanges = () => {
        requestModalHasChanges = true;
    };
    
    commentInput.addEventListener('input', trackChanges);
    statusSelect.addEventListener('change', trackChanges);
    assigneeInput.addEventListener('change', trackChanges);
    spaceshipInput.addEventListener('change', trackChanges);
    
    const modal = new bootstrap.Modal(document.getElementById('requestModal'));
    
    // Clear tracked ID and reset state when modal is hidden
    const requestModalElement = document.getElementById('requestModal');
    requestModalElement.addEventListener('hidden.bs.modal', () => {
        currentRequestId = null;
        requestModalHasChanges = false;
        pendingRequestUpdate = null;
        hideRequestUpdateWarning();
        commentInput.removeEventListener('input', trackChanges);
        statusSelect.removeEventListener('change', trackChanges);
        assigneeInput.removeEventListener('change', trackChanges);
        spaceshipInput.removeEventListener('change', trackChanges);
    }, { once: true });
    
    modal.show();
}

/**
 * Save request (create or update)
 */
async function saveRequest() {
    const id = document.getElementById('requestId').value;
    const spaceshipSerial = document.getElementById('requestSpaceshipSerial').value;
    const data = {
        spaceshipSerial: parseInt(spaceshipSerial),
        comment: document.getElementById('requestComment').value
    };
    
    if (id) {
        // On update, include all fields except createdAt/updatedAt
        const assignee = document.getElementById('requestAssignee').value;
        if (assignee) data.assignee = parseInt(assignee);
        // Status is only included when updating
        data.status = document.getElementById('requestStatus').value;
    } else {
        // On create, status is ignored (always set to NEW by backend)
        // Also allow assignee to be set
        const assignee = document.getElementById('requestAssignee').value;
        if (assignee) data.assignee = parseInt(assignee);
    }
    
    const url = id ? `${API_BASE}/maintenance-requests/${id}` : `${API_BASE}/maintenance-requests`;
    const method = id ? 'PUT' : 'POST';
    
    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка сохранения заявки: ' + errorMessage);
            return;
        }
        
        bootstrap.Modal.getInstance(document.getElementById('requestModal')).hide();
        currentRequestId = null; // Clear tracked ID after save
        requestModalHasChanges = false; // Reset changes tracking
        pendingRequestUpdate = null; // Clear pending updates
        hideRequestUpdateWarning(); // Hide warning
        loadRequests();
    } catch (error) {
        alert('Ошибка сохранения заявки: ' + (error.message || error));
    }
}

/**
 * Delete request
 * @param {number} id - Request ID
 */
async function deleteRequest(id) {
    if (!confirm('Вы уверены, что хотите удалить эту заявку?')) return;
    try {
        const response = await fetch(`${API_BASE}/maintenance-requests/${id}`, { method: 'DELETE' });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка удаления заявки: ' + errorMessage);
            return;
        }
        
        loadRequests();
    } catch (error) {
        alert('Ошибка удаления заявки: ' + (error.message || error));
    }
}

/**
 * Edit request
 * @param {number} id - Request ID
 */
function editRequest(id) {
    showRequestModal(id);
}

/**
 * Update request in table row
 * @param {Object} request - Updated request data
 */
function updateRequestInTable(request) {
    const tbody = document.getElementById('requestsTableBody');
    const rows = tbody.getElementsByTagName('tr');
    
    for (let row of rows) {
        const firstCell = row.cells[0];
        if (firstCell && firstCell.textContent.trim() === String(request.id)) {
            // Preserve onclick handler for the row
            if (!row.onclick) {
                row.style.cursor = 'pointer';
                row.onclick = () => viewRequest(request.id);
            }
            
            // Update the row with new data
            row.cells[1].innerHTML = request.spaceshipSerial ? `
                <span class="text-primary" style="cursor: pointer; text-decoration: underline;" 
                      onmouseenter="showSpaceshipTooltip(${request.spaceshipSerial})" 
                      onmouseleave="hideSpaceshipTooltip()"
                      onclick="event.stopPropagation(); hideSpaceshipTooltip(); viewSpaceship(${request.spaceshipSerial})" 
                      title="Наведите курсор или нажмите для просмотра деталей">
                    ${request.spaceshipSerial}
                </span>
            ` : '-';
            row.cells[2].textContent = request.comment || '-';
            row.cells[3].innerHTML = `<span class="badge bg-info">${translateMaintenanceStatus(request.status || 'NEW')}</span>`;
            row.cells[4].innerHTML = request.assignee ? `
                <span class="text-primary" style="cursor: pointer; text-decoration: underline;" 
                      onmouseenter="showRepairmanTooltip(${request.assignee})" 
                      onmouseleave="hideRepairmanTooltip()"
                      onclick="event.stopPropagation(); hideRepairmanTooltip(); viewRepairman(${request.assignee})" 
                      title="Наведите курсор или нажмите для просмотра деталей">
                    ${request.assignee}
                </span>
            ` : '-';
            row.cells[5].textContent = formatTimestamp(request.createdAt, 'datetime');
            break;
        }
    }
}

/**
 * Show warning about request update when editing
 * @param {Object} request - Updated request data
 */
function showRequestUpdateWarning(request) {
    const warningDiv = document.getElementById('requestUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'block';
        pendingRequestUpdate = request;
    }
}

/**
 * Hide warning about request update
 */
function hideRequestUpdateWarning() {
    const warningDiv = document.getElementById('requestUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'none';
        pendingRequestUpdate = null;
    }
}

/**
 * Ignore request update warning
 */
function ignoreRequestUpdate() {
    hideRequestUpdateWarning();
}

/**
 * Update request modal with fresh data from backend
 */
async function updateRequestModalFromBackend() {
    if (!currentRequestId || !pendingRequestUpdate) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/maintenance-requests/${currentRequestId}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка загрузки обновлённых данных заявки: ' + errorMessage);
            return;
        }
        
        const request = await response.json();
        
        // Update form fields with fresh data
        document.getElementById('requestSpaceshipSerial').value = request.spaceshipSerial || '';
        if (request.spaceshipSerial) {
            // Find and display the selected spaceship
            fetch(`${API_BASE}/spaceships/${request.spaceshipSerial}`)
                .then(async r => {
                    if (!r.ok) {
                        const errorMessage = await extractErrorMessage(r);
                        throw new Error(errorMessage);
                    }
                    return r.json();
                })
                .then(ship => {
                    document.getElementById('spaceshipSelectedText').textContent = 
                        `${ship.serial} - ${ship.name || 'Без названия'}`;
                })
                .catch(error => {
                    console.error('Ошибка загрузки данных корабля:', error);
                });
        }
        document.getElementById('requestComment').value = request.comment || '';
        document.getElementById('requestAssignee').value = request.assignee || '';
        if (request.assignee) {
            // Find and display the selected repairman
            fetch(`${API_BASE}/repairmen/${request.assignee}`)
                .then(async r => {
                    if (!r.ok) {
                        const errorMessage = await extractErrorMessage(r);
                        throw new Error(errorMessage);
                    }
                    return r.json();
                })
                .then(repairman => {
                    document.getElementById('assigneeSelectedText').textContent = 
                        `${repairman.id} - ${repairman.name}`;
                })
                .catch(error => {
                    console.error('Ошибка загрузки данных ремонтника:', error);
                });
        }
        const currentStatus = request.status || 'NEW';
        // Populate status dropdown with only allowed transitions
        populateStatusDropdown(currentStatus);
        
        // Reset changes tracking since we're updating with fresh data
        requestModalHasChanges = false;
        
        // Hide warning
        hideRequestUpdateWarning();
    } catch (error) {
        alert('Ошибка загрузки обновлённых данных заявки: ' + (error.message || error));
    }
}

/**
 * Update request in edit modal if it's currently open
 * @param {Object} request - Updated request data
 */
function updateRequestInModal(request) {
    const modal = bootstrap.Modal.getInstance(document.getElementById('requestModal'));
    if (modal && modal._isShown && currentRequestId === request.id) {
        // If modal has unsaved changes, show warning instead of auto-updating
        if (requestModalHasChanges) {
            showRequestUpdateWarning(request);
        } else {
            // No unsaved changes, update directly
            document.getElementById('requestSpaceshipSerial').value = request.spaceshipSerial || '';
            document.getElementById('requestComment').value = request.comment || '';
            document.getElementById('requestAssignee').value = request.assignee || '';
            const currentStatus = request.status || 'NEW';
            populateStatusDropdown(currentStatus);
            
            // Update dropdowns if needed
            if (request.spaceshipSerial) {
                fetch(`${API_BASE}/spaceships/${request.spaceshipSerial}`)
                    .then(async r => {
                        if (!r.ok) return;
                        const ship = await r.json();
                        document.getElementById('spaceshipSelectedText').textContent = 
                            `${ship.serial} - ${ship.name || 'Без названия'}`;
                    })
                    .catch(() => {});
            }
            if (request.assignee) {
                fetch(`${API_BASE}/repairmen/${request.assignee}`)
                    .then(async r => {
                        if (!r.ok) return;
                        const repairman = await r.json();
                        document.getElementById('assigneeSelectedText').textContent = 
                            `${repairman.id} - ${repairman.name}`;
                    })
                    .catch(() => {});
            }
        }
    }
}

/**
 * Update request in view modal if it's currently open
 * @param {Object} request - Updated request data
 */
function updateRequestInViewModal(request) {
    // Check if view modal is showing this request using tracked ID
    if (typeof viewedRequestId !== 'undefined' && viewedRequestId === request.id) {
        const viewModal = bootstrap.Modal.getInstance(document.getElementById('requestViewModal'));
        if (viewModal && viewModal._isShown) {
            // Reload the view modal content
            viewRequest(request.id);
        }
    }
}

/**
 * Handle request update from event stream
 * @param {Object} request - Updated request data
 */
function handleRequestUpdate(request) {
    // Update table if request is displayed there
    updateRequestInTable(request);
    
    // Update edit modal if this request is being edited
    updateRequestInModal(request);
    
    // Update view modal if this request is being viewed
    updateRequestInViewModal(request);
}

