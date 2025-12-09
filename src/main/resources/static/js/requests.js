/**
 * Maintenance requests management functions
 */

/**
 * Load maintenance requests list
 */
async function loadRequests() {
    try {
        const response = await fetch(`${API_BASE}/maintenance-requests?page=${requestsTablePage}&size=${requestsTablePageSize}`);
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
                              title="Hover or click to view details">
                            ${req.spaceshipSerial}
                        </span>
                    ` : '-'}
                </td>
                <td>${req.comment || '-'}</td>
                <td><span class="badge bg-info">${req.status || 'NEW'}</span></td>
                <td>
                    ${req.assignee ? `
                        <span class="text-primary" style="cursor: pointer; text-decoration: underline;" 
                              onmouseenter="showRepairmanTooltip(${req.assignee})" 
                              onmouseleave="hideRepairmanTooltip()"
                              onclick="hideRepairmanTooltip(); viewRepairman(${req.assignee})" 
                              title="Hover or click to view details">
                            ${req.assignee}
                        </span>
                    ` : '-'}
                </td>
                <td>${req.createdAt ? new Date(req.createdAt).toLocaleString() : '-'}</td>
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
            '<tr><td colspan="7" class="text-center text-danger">Error loading requests</td></tr>';
    }
}

/**
 * Show request modal for create/edit
 * @param {number|null} id - Request ID (null for create)
 */
async function showRequestModal(id = null) {
    document.getElementById('requestModalTitle').textContent = id ? 'Edit Request' : 'Add Request';
    document.getElementById('requestId').value = id || '';
    
    // Show/hide status field based on create/edit mode
    const statusContainer = document.getElementById('requestStatusContainer');
    if (id) {
        statusContainer.style.display = 'block';
    } else {
        statusContainer.style.display = 'none';
    }
    
    // Reset dropdowns
    document.getElementById('spaceshipSelectedText').textContent = 'Select spaceship...';
    document.getElementById('assigneeSelectedText').textContent = 'Select repairman...';
    spaceshipPage = 0;
    assigneePage = 0;
    
    // Load first page of dropdowns
    await Promise.all([loadSpaceshipsForDropdown(0), loadRepairmenForDropdown(0)]);
    
    if (id) {
        fetch(`${API_BASE}/maintenance-requests/${id}`)
            .then(r => r.json())
            .then(data => {
                document.getElementById('requestSpaceshipSerial').value = data.spaceshipSerial || '';
                if (data.spaceshipSerial) {
                    // Find and display the selected spaceship
                    fetch(`${API_BASE}/spaceships/${data.spaceshipSerial}`)
                        .then(r => r.json())
                        .then(ship => {
                            document.getElementById('spaceshipSelectedText').textContent = 
                                `${ship.serial} - ${ship.name || 'Unnamed'}`;
                        });
                }
                document.getElementById('requestComment').value = data.comment || '';
                document.getElementById('requestAssignee').value = data.assignee || '';
                if (data.assignee) {
                    // Find and display the selected repairman
                    fetch(`${API_BASE}/repairmen/${data.assignee}`)
                        .then(r => r.json())
                        .then(repairman => {
                            document.getElementById('assigneeSelectedText').textContent = 
                                `${repairman.id} - ${repairman.name}`;
                        });
                }
                document.getElementById('requestStatus').value = data.status || 'NEW';
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
            const errorText = await response.text();
            throw new Error(errorText || `HTTP ${response.status}: ${response.statusText}`);
        }
        
        bootstrap.Modal.getInstance(document.getElementById('requestModal')).hide();
        loadRequests();
    } catch (error) {
        alert('Error saving request: ' + error.message);
    }
}

/**
 * Delete request
 * @param {number} id - Request ID
 */
async function deleteRequest(id) {
    if (!confirm('Are you sure you want to delete this request?')) return;
    try {
        await fetch(`${API_BASE}/maintenance-requests/${id}`, { method: 'DELETE' });
        loadRequests();
    } catch (error) {
        alert('Error deleting request: ' + error);
    }
}

/**
 * Edit request
 * @param {number} id - Request ID
 */
function editRequest(id) {
    showRequestModal(id);
}

