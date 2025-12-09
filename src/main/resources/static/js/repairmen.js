/**
 * Repairmen management functions
 */

/**
 * Load repairmen list
 */
async function loadRepairmen() {
    try {
        const response = await fetch(`${API_BASE}/repairmen?page=${repairmenTablePage}&size=${repairmenTablePageSize}`);
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
    document.getElementById('repairmanModalTitle').textContent = id ? 'Редактировать ремонтника' : 'Добавить ремонтника';
    document.getElementById('repairmanId').value = id || '';
    if (id) {
        fetch(`${API_BASE}/repairmen/${id}`)
            .then(r => r.json())
            .then(data => {
                document.getElementById('repairmanName').value = data.name || '';
                document.getElementById('repairmanPosition').value = data.position || '';
            });
    } else {
        document.getElementById('repairmanForm').reset();
    }
    new bootstrap.Modal(document.getElementById('repairmanModal')).show();
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
        await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        bootstrap.Modal.getInstance(document.getElementById('repairmanModal')).hide();
        loadRepairmen();
    } catch (error) {
        alert('Ошибка сохранения ремонтника: ' + error);
    }
}

/**
 * Delete repairman
 * @param {number} id - Repairman ID
 */
async function deleteRepairman(id) {
    if (!confirm('Вы уверены, что хотите удалить этого ремонтника?')) return;
    try {
        await fetch(`${API_BASE}/repairmen/${id}`, { method: 'DELETE' });
        loadRepairmen();
    } catch (error) {
        alert('Ошибка удаления ремонтника: ' + error);
    }
}

/**
 * Edit repairman
 * @param {number} id - Repairman ID
 */
function editRepairman(id) {
    showRepairmanModal(id);
}

