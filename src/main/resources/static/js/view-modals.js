/**
 * Read-only view modals for spaceships and repairmen
 */

// Hover timers for delayed modal display
let spaceshipHoverTimer = null;
let repairmanHoverTimer = null;

/**
 * Show spaceship tooltip on hover (with delay)
 * @param {number} serial - Spaceship serial
 */
function showSpaceshipTooltip(serial) {
    // Clear any existing timer
    if (spaceshipHoverTimer) {
        clearTimeout(spaceshipHoverTimer);
    }
    // Show modal after 500ms hover
    spaceshipHoverTimer = setTimeout(() => {
        viewSpaceship(serial);
    }, 500);
}

/**
 * Hide spaceship tooltip
 */
function hideSpaceshipTooltip() {
    if (spaceshipHoverTimer) {
        clearTimeout(spaceshipHoverTimer);
        spaceshipHoverTimer = null;
    }
}

/**
 * Show repairman tooltip on hover (with delay)
 * @param {number} id - Repairman ID
 */
function showRepairmanTooltip(id) {
    // Clear any existing timer
    if (repairmanHoverTimer) {
        clearTimeout(repairmanHoverTimer);
    }
    // Show modal after 500ms hover
    repairmanHoverTimer = setTimeout(() => {
        viewRepairman(id);
    }, 500);
}

/**
 * Hide repairman tooltip
 */
function hideRepairmanTooltip() {
    if (repairmanHoverTimer) {
        clearTimeout(repairmanHoverTimer);
        repairmanHoverTimer = null;
    }
}

/**
 * View spaceship details in read-only modal
 * @param {number} serial - Spaceship serial
 */
async function viewSpaceship(serial) {
    try {
        const response = await fetch(`${API_BASE}/spaceships/${serial}`);
        const ship = await response.json();
        
        const dimensions = ship.dimensions ? 
            `${ship.dimensions.length || 0}м × ${ship.dimensions.width || 0}м × ${ship.dimensions.height || 0}м, ` +
            `Вес: ${roundTo3Decimals(ship.dimensions.weight || 0)} тонн, Объём: ${roundTo3Decimals(ship.dimensions.volume || 0)} м³` : 'Не указано';
        const engine = ship.engine ? 
            `Модель: ${ship.engine.model || 'Н/Д'}, Тяга: ${ship.engine.thrust || 0} кН, ` +
            `Тип топлива: ${translateFuelType(ship.engine.fuelType || '') || 'Н/Д'}, Расход: ${roundTo3Decimals(ship.engine.fuelConsumption || 0)} в час` : 'Не указано';
        const manufactureDate = ship.manufactureDate ? 
            new Date(ship.manufactureDate).toLocaleString('ru-RU') : 'Не указано';
        
        let crewHtml = '';
        if (ship.crew && ship.crew.length > 0) {
            crewHtml = '<h6 class="mb-3 text-primary">Члены экипажа</h6><ul class="list-group mb-3">';
            ship.crew.forEach(member => {
                const birthDate = member.birthDate ? new Date(member.birthDate).toLocaleDateString('ru-RU') : 'Не указано';
                crewHtml += `
                    <li class="list-group-item">
                        <strong>${member.fullName || 'Н/Д'}</strong><br>
                        <small>Звание: ${member.rank || 'Н/Д'}, Опыт: ${member.experienceYears || 0} лет, Дата рождения: ${birthDate}</small>
                    </li>
                `;
            });
            crewHtml += '</ul>';
        } else {
            crewHtml = '<p class="text-muted">Члены экипажа не назначены</p>';
        }
        
        document.getElementById('spaceshipViewContent').innerHTML = `
            <h6 class="mb-3 text-primary">Основная информация</h6>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Серийный номер:</strong> ${ship.serial}
                </div>
                <div class="col-md-6">
                    <strong>Название:</strong> ${ship.name || 'Н/Д'}
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Производитель:</strong> ${ship.manufacturer || 'Н/Д'}
                </div>
                <div class="col-md-6">
                    <strong>Тип:</strong> <span class="badge bg-primary">${translateSpaceShipType(ship.type || '') || 'Н/Д'}</span>
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Дата производства:</strong> ${manufactureDate}
                </div>
                <div class="col-md-6">
                    <strong>Максимальная скорость:</strong> ${ship.maxSpeed || 0}
                </div>
            </div>
            <hr class="my-4">
            <h6 class="mb-3 text-primary">Размеры</h6>
            <p>${dimensions}</p>
            <hr class="my-4">
            <h6 class="mb-3 text-primary">Двигатель</h6>
            <p>${engine}</p>
            <hr class="my-4">
            ${crewHtml}
        `;
        
        const modal = new bootstrap.Modal(document.getElementById('spaceshipViewModal'));
        modal.show();
        
        // Clear hover timer when modal is shown
        hideSpaceshipTooltip();
    } catch (error) {
        alert('Ошибка загрузки деталей корабля: ' + error);
        hideSpaceshipTooltip();
    }
}

/**
 * View repairman details in read-only modal
 * @param {number} id - Repairman ID
 */
async function viewRepairman(id) {
    try {
        const response = await fetch(`${API_BASE}/repairmen/${id}`);
        const repairman = await response.json();
        
        document.getElementById('repairmanViewContent').innerHTML = `
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
        
        const modal = new bootstrap.Modal(document.getElementById('repairmanViewModal'));
        modal.show();
        
        // Clear hover timer when modal is shown
        hideRepairmanTooltip();
    } catch (error) {
        alert('Ошибка загрузки деталей ремонтника: ' + error);
        hideRepairmanTooltip();
    }
}

