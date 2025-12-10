/**
 * Repairmen management functions
 */

// Track currently displayed/edited repairman ID
let currentRepairmanId = null;

// Track if modal has unsaved changes
let repairmanModalHasChanges = false;

// Store the last received update for the current repairman
let pendingRepairmanUpdate = null;

/**
 * Load repairmen list
 */
async function loadRepairmen() {
    try {
        const response = await fetch(`${API_BASE}/repairmen?page=${repairmenTablePage}&size=${repairmenTablePageSize}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            document.getElementById('repairmenTableBody').innerHTML = 
                `<tr><td colspan="4" class="text-center text-danger">Ошибка загрузки ремонтников: ${errorMessage}</td></tr>`;
            return;
        }
        
        const data = await response.json();
        repairmenTableTotalItems = data.length;
        
        const tbody = document.getElementById('repairmenTableBody');
        tbody.innerHTML = data.map(repairman => `
            <tr style="cursor: pointer;" onclick="viewRepairman(${repairman.id})">
                <td>${repairman.id}</td>
                <td>${repairman.name}</td>
                <td>${repairman.position}</td>
                <td onclick="event.stopPropagation();">
                    <button class="btn btn-sm btn-primary" onclick="editRepairman(${repairman.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteRepairman(${repairman.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        
        // Update pagination buttons
        updateRepairmenTablePaginationButtons();
    } catch (error) {
        document.getElementById('repairmenTableBody').innerHTML = 
            '<tr><td colspan="4" class="text-center text-danger">Ошибка загрузки ремонтников</td></tr>';
    }
}

/**
 * Show repairman modal for create/edit
 * @param {number|null} id - Repairman ID (null for create)
 */
function showRepairmanModal(id = null) {
    currentRepairmanId = id; // Track currently edited repairman
    repairmanModalHasChanges = false; // Reset changes tracking
    pendingRepairmanUpdate = null; // Clear any pending updates
    hideRepairmanUpdateWarning(); // Hide warning if visible
    
    document.getElementById('repairmanModalTitle').textContent = id ? 'Редактировать ремонтника' : 'Добавить ремонтника';
    document.getElementById('repairmanId').value = id || '';
    if (id) {
        fetch(`${API_BASE}/repairmen/${id}`)
            .then(async r => {
                if (!r.ok) {
                    const errorMessage = await extractErrorMessage(r);
                    throw new Error(errorMessage);
                }
                return r.json();
            })
            .then(data => {
                document.getElementById('repairmanName').value = data.name || '';
                document.getElementById('repairmanPosition').value = data.position || '';
            })
            .catch(error => {
                alert('Ошибка загрузки данных ремонтника: ' + (error.message || error));
            });
    } else {
        document.getElementById('repairmanForm').reset();
    }
    
    // Track form changes
    const nameInput = document.getElementById('repairmanName');
    const positionInput = document.getElementById('repairmanPosition');
    
    const trackChanges = () => {
        repairmanModalHasChanges = true;
    };
    
    nameInput.addEventListener('input', trackChanges);
    positionInput.addEventListener('input', trackChanges);
    
    const modal = new bootstrap.Modal(document.getElementById('repairmanModal'));
    
    // Clear tracked ID and reset state when modal is hidden
    const repairmanModalElement = document.getElementById('repairmanModal');
    repairmanModalElement.addEventListener('hidden.bs.modal', () => {
        currentRepairmanId = null;
        repairmanModalHasChanges = false;
        pendingRepairmanUpdate = null;
        hideRepairmanUpdateWarning();
        nameInput.removeEventListener('input', trackChanges);
        positionInput.removeEventListener('input', trackChanges);
    }, { once: true });
    
    modal.show();
}

/**
 * Save repairman (create or update)
 */
async function saveRepairman() {
    const id = document.getElementById('repairmanId').value;
    const data = {
        name: document.getElementById('repairmanName').value,
        position: document.getElementById('repairmanPosition').value
    };
    
    const url = id ? `${API_BASE}/repairmen/${id}` : `${API_BASE}/repairmen`;
    const method = id ? 'PUT' : 'POST';
    
    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка сохранения ремонтника: ' + errorMessage);
            return;
        }
        
        bootstrap.Modal.getInstance(document.getElementById('repairmanModal')).hide();
        currentRepairmanId = null; // Clear tracked ID after save
        repairmanModalHasChanges = false; // Reset changes tracking
        pendingRepairmanUpdate = null; // Clear pending updates
        hideRepairmanUpdateWarning(); // Hide warning
        loadRepairmen();
    } catch (error) {
        alert('Ошибка сохранения ремонтника: ' + (error.message || error));
    }
}

/**
 * Delete repairman
 * @param {number} id - Repairman ID
 */
async function deleteRepairman(id) {
    if (!confirm('Вы уверены, что хотите удалить этого ремонтника?')) return;
    try {
        const response = await fetch(`${API_BASE}/repairmen/${id}`, { method: 'DELETE' });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка удаления ремонтника: ' + errorMessage);
            return;
        }
        
        loadRepairmen();
    } catch (error) {
        alert('Ошибка удаления ремонтника: ' + (error.message || error));
    }
}

/**
 * Edit repairman
 * @param {number} id - Repairman ID
 */
function editRepairman(id) {
    showRepairmanModal(id);
}

/**
 * Update repairman in table row
 * @param {Object} repairman - Updated repairman data
 */
function updateRepairmanInTable(repairman) {
    const tbody = document.getElementById('repairmenTableBody');
    const rows = tbody.getElementsByTagName('tr');
    
    for (let row of rows) {
        const firstCell = row.cells[0];
        if (firstCell && firstCell.textContent.trim() === String(repairman.id)) {
            // Update the row with new data
            row.cells[1].textContent = repairman.name || '';
            row.cells[2].textContent = repairman.position || '';
            break;
        }
    }
}

/**
 * Show warning about repairman update when editing
 * @param {Object} repairman - Updated repairman data
 */
function showRepairmanUpdateWarning(repairman) {
    const warningDiv = document.getElementById('repairmanUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'block';
        pendingRepairmanUpdate = repairman;
    }
}

/**
 * Hide warning about repairman update
 */
function hideRepairmanUpdateWarning() {
    const warningDiv = document.getElementById('repairmanUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'none';
        pendingRepairmanUpdate = null;
    }
}

/**
 * Ignore repairman update warning
 */
function ignoreRepairmanUpdate() {
    hideRepairmanUpdateWarning();
}

/**
 * Update repairman modal with fresh data from backend
 */
async function updateRepairmanModalFromBackend() {
    if (!currentRepairmanId || !pendingRepairmanUpdate) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/repairmen/${currentRepairmanId}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка загрузки обновлённых данных ремонтника: ' + errorMessage);
            return;
        }
        
        const repairman = await response.json();
        
        // Update form fields with fresh data
        document.getElementById('repairmanName').value = repairman.name || '';
        document.getElementById('repairmanPosition').value = repairman.position || '';
        
        // Reset changes tracking since we're updating with fresh data
        repairmanModalHasChanges = false;
        
        // Hide warning
        hideRepairmanUpdateWarning();
    } catch (error) {
        alert('Ошибка загрузки обновлённых данных ремонтника: ' + (error.message || error));
    }
}

/**
 * Update repairman in edit modal if it's currently open
 * @param {Object} repairman - Updated repairman data
 */
function updateRepairmanInModal(repairman) {
    const modal = bootstrap.Modal.getInstance(document.getElementById('repairmanModal'));
    if (modal && modal._isShown && currentRepairmanId === repairman.id) {
        // If modal has unsaved changes, show warning instead of auto-updating
        if (repairmanModalHasChanges) {
            showRepairmanUpdateWarning(repairman);
        } else {
            // No unsaved changes, update directly
            document.getElementById('repairmanName').value = repairman.name || '';
            document.getElementById('repairmanPosition').value = repairman.position || '';
        }
    }
}

/**
 * Update repairman in view modal if it's currently open
 * @param {Object} repairman - Updated repairman data
 */
function updateRepairmanInViewModal(repairman) {
    // Check if view modal is showing this repairman using tracked ID
    if (typeof viewedRepairmanId !== 'undefined' && viewedRepairmanId === repairman.id) {
        const viewModal = bootstrap.Modal.getInstance(document.getElementById('repairmanViewModal'));
        if (viewModal && viewModal._isShown) {
            // Update the view modal content
            const content = document.getElementById('repairmanViewContent');
            if (content) {
                content.innerHTML = `
                    <div class="mb-3">
                        <strong>ID:</strong> ${repairman.id}
                    </div>
                    <div class="mb-3">
                        <strong>Имя:</strong> ${repairman.name || 'Н/Д'}
                    </div>
                    <div class="mb-3">
                        <strong>Должность:</strong> ${repairman.position || 'Н/Д'}
                    </div>
                `;
            }
        }
    }
}

/**
 * Handle repairman update from event stream
 * @param {Object} repairman - Updated repairman data
 */
function handleRepairmanUpdate(repairman) {
    // Update table if repairman is displayed there
    updateRepairmanInTable(repairman);
    
    // Update edit modal if this repairman is being edited
    updateRepairmanInModal(repairman);
    
    // Update view modal if this repairman is being viewed
    updateRepairmanInViewModal(repairman);
}

