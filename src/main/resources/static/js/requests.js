/**
 * Maintenance requests management functions
 */

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
            <tr>
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
                <td>${req.createdAt ? new Date(req.createdAt).toLocaleString('ru-RU') : '-'}</td>
                <td>
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
    new bootstrap.Modal(document.getElementById('requestModal')).show();
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

