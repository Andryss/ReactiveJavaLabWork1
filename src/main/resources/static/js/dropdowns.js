/**
 * Paginated dropdown functions for spaceships and repairmen
 */

// Pagination state for dropdowns
let spaceshipPage = 0;
let spaceshipPageSize = 10;
let spaceshipTotalItems = 0;
let allSpaceships = [];

let assigneePage = 0;
let assigneePageSize = 10;
let assigneeTotalItems = 0;
let allRepairmen = [];

/**
 * Load spaceships for dropdown
 * @param {number} page - Page number
 */
async function loadSpaceshipsForDropdown(page = 0) {
    try {
        const response = await fetch(`${API_BASE}/spaceships?page=${page}&size=${spaceshipPageSize}`);
        const data = await response.json();
        
        const menu = document.getElementById('spaceshipDropdownMenu');
        
        if (data.length === 0) {
            // Don't update page number, just disable next button
            const prevBtn = document.getElementById('spaceshipPrevBtn');
            const nextBtn = document.getElementById('spaceshipNextBtn');
            prevBtn.disabled = spaceshipPage === 0;
            nextBtn.disabled = true; // Disable next button when no items
            return;
        }

        menu.innerHTML = '';
        
        // Only update page number if we have items
        spaceshipPage = page;
        allSpaceships = data;
        spaceshipTotalItems = data.length;
        
        data.forEach(ship => {
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.className = 'dropdown-item';
            a.href = '#';
            a.textContent = `${ship.serial} - ${ship.name || 'Без названия'} (${ship.manufacturer || 'Неизвестно'})`;
            a.onclick = (e) => {
                e.preventDefault();
                document.getElementById('requestSpaceshipSerial').value = ship.serial;
                document.getElementById('spaceshipSelectedText').textContent = `${ship.serial} - ${ship.name || 'Без названия'}`;
                bootstrap.Dropdown.getInstance(document.getElementById('spaceshipDropdownBtn'))?.hide();
            };
            li.appendChild(a);
            menu.appendChild(li);
        });
        
        // Show pagination if we have items
        const pagination = document.getElementById('spaceshipPagination');
        if (data.length > 0) {
            pagination.style.display = 'flex';
            updateSpaceshipPaginationButtons();
        } else {
            pagination.style.display = 'none';
        }
        
        // Update dropdown button text if item is already selected
        const selectedSerial = document.getElementById('requestSpaceshipSerial').value;
        if (selectedSerial) {
            const selectedShip = data.find(s => s.serial == selectedSerial);
            if (selectedShip) {
                document.getElementById('spaceshipSelectedText').textContent = 
                    `${selectedShip.serial} - ${selectedShip.name || 'Без названия'}`;
            }
        }
    } catch (error) {
        console.error('Error loading spaceships:', error);
        document.getElementById('spaceshipDropdownMenu').innerHTML = 
            '<li><div class="px-3 py-2 text-danger">Ошибка загрузки кораблей</div></li>';
    }
}

/**
 * Update spaceship dropdown pagination buttons
 */
function updateSpaceshipPaginationButtons() {
    const prevBtn = document.getElementById('spaceshipPrevBtn');
    const nextBtn = document.getElementById('spaceshipNextBtn');
    const pageInfo = document.getElementById('spaceshipPageInfo');
    
    prevBtn.disabled = spaceshipPage === 0;
    nextBtn.disabled = spaceshipTotalItems < spaceshipPageSize;
    pageInfo.textContent = `Страница ${spaceshipPage + 1}`;
}

/**
 * Navigate spaceship dropdown page
 * @param {number} direction - -1 for previous, 1 for next
 * @param {Event} event - Click event (optional)
 */
function navigateSpaceshipPage(direction, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    const newPage = spaceshipPage + direction;
    if (newPage >= 0) {
        loadSpaceshipsForDropdown(newPage);
    }
}

/**
 * Load repairmen for dropdown
 * @param {number} page - Page number
 */
async function loadRepairmenForDropdown(page = 0) {
    try {
        const response = await fetch(`${API_BASE}/repairmen?page=${page}&size=${assigneePageSize}`);
        const data = await response.json();
        
        const menu = document.getElementById('assigneeDropdownMenu');
        
        if (data.length === 0) {
            // Don't update page number, just disable next button
            const prevBtn = document.getElementById('assigneePrevBtn');
            const nextBtn = document.getElementById('assigneeNextBtn');
            prevBtn.disabled = assigneePage === 0;
            nextBtn.disabled = true; // Disable next button when no items
            return;
        }

        menu.innerHTML = '';
        
        // Only update page number if we have items
        assigneePage = page;
        allRepairmen = data;
        assigneeTotalItems = data.length;
        
        data.forEach(repairman => {
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.className = 'dropdown-item';
            a.href = '#';
            a.textContent = `${repairman.id} - ${repairman.name} (${repairman.position})`;
            a.onclick = (e) => {
                e.preventDefault();
                document.getElementById('requestAssignee').value = repairman.id;
                document.getElementById('assigneeSelectedText').textContent = `${repairman.id} - ${repairman.name}`;
                bootstrap.Dropdown.getInstance(document.getElementById('assigneeDropdownBtn'))?.hide();
            };
            li.appendChild(a);
            menu.appendChild(li);
        });
        
        // Show pagination if we have items
        const pagination = document.getElementById('assigneePagination');
        if (data.length > 0) {
            pagination.style.display = 'flex';
            updateAssigneePaginationButtons();
        } else {
            pagination.style.display = 'none';
        }
        
        // Update dropdown button text if item is already selected
        const selectedAssignee = document.getElementById('requestAssignee').value;
        if (selectedAssignee) {
            const selectedRepairman = data.find(r => r.id == selectedAssignee);
            if (selectedRepairman) {
                document.getElementById('assigneeSelectedText').textContent = 
                    `${selectedRepairman.id} - ${selectedRepairman.name}`;
            }
        }
    } catch (error) {
        console.error('Error loading repairmen:', error);
        document.getElementById('assigneeDropdownMenu').innerHTML = 
            '<li><div class="px-3 py-2 text-danger">Ошибка загрузки ремонтников</div></li>';
    }
}

/**
 * Update assignee dropdown pagination buttons
 */
function updateAssigneePaginationButtons() {
    const prevBtn = document.getElementById('assigneePrevBtn');
    const nextBtn = document.getElementById('assigneeNextBtn');
    const pageInfo = document.getElementById('assigneePageInfo');
    
    prevBtn.disabled = assigneePage === 0;
    nextBtn.disabled = assigneeTotalItems < assigneePageSize;
    pageInfo.textContent = `Страница ${assigneePage + 1}`;
}

/**
 * Navigate assignee dropdown page
 * @param {number} direction - -1 for previous, 1 for next
 * @param {Event} event - Click event (optional)
 */
function navigateAssigneePage(direction, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    const newPage = assigneePage + direction;
    if (newPage >= 0) {
        loadRepairmenForDropdown(newPage);
    }
}

