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
        spaceshipPage = page;
        allSpaceships = data;
        spaceshipTotalItems = data.length;
        
        const menu = document.getElementById('spaceshipDropdownMenu');
        menu.innerHTML = '';
        
        if (data.length === 0) {
            menu.innerHTML = '<li><div class="px-3 py-2 text-muted">Корабли не найдены</div></li>';
            document.getElementById('spaceshipPagination').style.display = 'none';
            return;
        }
        
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
 */
function navigateSpaceshipPage(direction) {
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
        assigneePage = page;
        allRepairmen = data;
        assigneeTotalItems = data.length;
        
        const menu = document.getElementById('assigneeDropdownMenu');
        menu.innerHTML = '';
        
        if (data.length === 0) {
            menu.innerHTML = '<li><div class="px-3 py-2 text-muted">Ремонтники не найдены</div></li>';
            document.getElementById('assigneePagination').style.display = 'none';
            return;
        }
        
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
 */
function navigateAssigneePage(direction) {
    const newPage = assigneePage + direction;
    if (newPage >= 0) {
        loadRepairmenForDropdown(newPage);
    }
}

