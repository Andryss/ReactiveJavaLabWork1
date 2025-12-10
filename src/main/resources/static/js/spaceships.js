/**
 * Spaceships management functions
 */

let crewMemberIndex = 0;

// Track currently displayed/edited spaceship serial
let currentSpaceshipSerial = null;

// Track if modal has unsaved changes
let spaceshipModalHasChanges = false;

// Store the last received update for the current spaceship
let pendingSpaceshipUpdate = null;

/**
 * Load spaceships list
 */
async function loadSpaceships() {
    try {
        const response = await fetch(`${API_BASE}/spaceships?page=${spaceshipsTablePage}&size=${spaceshipsTablePageSize}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            document.getElementById('spaceshipsTableBody').innerHTML = 
                `<tr><td colspan="10" class="text-center text-danger">Ошибка загрузки кораблей: ${errorMessage}</td></tr>`;
            return;
        }
        
        const data = await response.json();
        spaceshipsTableTotalItems = data.length;
        
        const tbody = document.getElementById('spaceshipsTableBody');
        tbody.innerHTML = data.map(ship => {
            const dimensions = ship.dimensions ? 
                `${ship.dimensions.length || 0}м × ${ship.dimensions.width || 0}м × ${ship.dimensions.height || 0}м` : '-';
            const engine = ship.engine ? 
                `${ship.engine.model || 'Н/Д'} (${translateFuelType(ship.engine.fuelType || '') || 'Н/Д'})` : '-';
            const crewCount = ship.crew && ship.crew.length > 0 ? ship.crew.length : 0;
            const manufactureDate = ship.manufactureDate ? 
                new Date(ship.manufactureDate).toLocaleDateString('ru-RU') : '-';
            
            return `
            <tr style="cursor: pointer;" onclick="viewSpaceship(${ship.serial})">
                <td>${ship.serial}</td>
                <td>${ship.name || '-'}</td>
                <td>${ship.manufacturer || '-'}</td>
                <td><span class="badge bg-primary">${translateSpaceShipType(ship.type || '') || '-'}</span></td>
                <td>${manufactureDate}</td>
                <td>${ship.maxSpeed || 0}</td>
                <td><small>${dimensions}</small></td>
                <td><small>${engine}</small></td>
                <td>${crewCount} ${crewCount === 1 ? 'человек' : crewCount < 5 ? 'человека' : 'человек'}</td>
                <td onclick="event.stopPropagation();">
                    <button class="btn btn-sm btn-primary" onclick="editSpaceship(${ship.serial})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteSpaceship(${ship.serial})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
            `;
        }).join('');
        
        // Update pagination buttons
        updateSpaceshipsTablePaginationButtons();
    } catch (error) {
        document.getElementById('spaceshipsTableBody').innerHTML = 
            '<tr><td colspan="10" class="text-center text-danger">Ошибка загрузки кораблей</td></tr>';
    }
}

/**
 * Add a crew member to the form
 */
function addCrewMember() {
    const container = document.getElementById('crewMembersContainer');
    const memberDiv = document.createElement('div');
    memberDiv.className = 'card mb-3';
    memberDiv.id = `crewMember-${crewMemberIndex}`;
    memberDiv.innerHTML = `
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h6 class="mb-0">Член экипажа</h6>
                <button type="button" class="btn btn-sm btn-danger" onclick="removeCrewMember(${crewMemberIndex})">
                    <i class="bi bi-trash"></i> Удалить
                </button>
            </div>
            <div class="row">
                <div class="col-md-6 mb-2">
                    <label class="form-label">Полное имя</label>
                    <input type="text" class="form-control" id="crewFullName-${crewMemberIndex}">
                </div>
                <div class="col-md-6 mb-2">
                    <label class="form-label">Звание</label>
                    <input type="text" class="form-control" id="crewRank-${crewMemberIndex}">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-2">
                    <label class="form-label">Опыт (лет)</label>
                    <input type="number" class="form-control" id="crewExperience-${crewMemberIndex}" value="0">
                </div>
                <div class="col-md-6 mb-2">
                    <label class="form-label">Дата рождения</label>
                    <input type="date" class="form-control" id="crewBirthDate-${crewMemberIndex}">
                </div>
            </div>
        </div>
    `;
    container.appendChild(memberDiv);
    crewMemberIndex++;
}

/**
 * Remove a crew member from the form
 * @param {number} index - Crew member index
 */
function removeCrewMember(index) {
    const memberDiv = document.getElementById(`crewMember-${index}`);
    if (memberDiv) {
        memberDiv.remove();
    }
}

/**
 * Show spaceship modal for create/edit
 * @param {number|null} serial - Spaceship serial (null for create)
 */
function showSpaceshipModal(serial = null) {
    currentSpaceshipSerial = serial; // Track currently edited spaceship
    spaceshipModalHasChanges = false; // Reset changes tracking
    pendingSpaceshipUpdate = null; // Clear any pending updates
    hideSpaceshipUpdateWarning(); // Hide warning if visible
    
    document.getElementById('spaceshipModalTitle').textContent = serial ? 'Редактировать корабль' : 'Добавить корабль';
    document.getElementById('spaceshipSerial').value = serial || '';
    document.getElementById('spaceshipSerialInput').value = serial || '';
    document.getElementById('spaceshipSerialInput').disabled = !!serial;
    
    // Clear crew members container
    document.getElementById('crewMembersContainer').innerHTML = '';
    crewMemberIndex = 0;
    
    if (serial) {
        fetch(`${API_BASE}/spaceships/${serial}`)
            .then(async r => {
                if (!r.ok) {
                    const errorMessage = await extractErrorMessage(r);
                    throw new Error(errorMessage);
                }
                return r.json();
            })
            .then(data => {
                document.getElementById('spaceshipName').value = data.name || '';
                document.getElementById('spaceshipManufacturer').value = data.manufacturer || '';
                document.getElementById('spaceshipType').value = data.type || '';
                document.getElementById('spaceshipMaxSpeed').value = data.maxSpeed || 0;
                if (data.manufactureDate) {
                    const date = new Date(data.manufactureDate);
                    document.getElementById('spaceshipManufactureDate').value = 
                        date.toISOString().slice(0, 16);
                }
                
                // Populate dimensions
                if (data.dimensions) {
                    document.getElementById('dimensionsLength').value = data.dimensions.length || 0;
                    document.getElementById('dimensionsWidth').value = data.dimensions.width || 0;
                    document.getElementById('dimensionsHeight').value = data.dimensions.height || 0;
                    document.getElementById('dimensionsWeight').value = roundTo3Decimals(data.dimensions.weight || 0);
                    document.getElementById('dimensionsVolume').value = roundTo3Decimals(data.dimensions.volume || 0);
                }
                
                // Populate engine
                if (data.engine) {
                    document.getElementById('engineModel').value = data.engine.model || '';
                    document.getElementById('engineThrust').value = data.engine.thrust || 0;
                    document.getElementById('engineFuelType').value = data.engine.fuelType || '';
                    document.getElementById('engineFuelConsumption').value = roundTo3Decimals(data.engine.fuelConsumption || 0);
                }
                
                // Populate crew
                if (data.crew && data.crew.length > 0) {
                    data.crew.forEach(member => {
                        addCrewMember();
                        const index = crewMemberIndex - 1;
                        document.getElementById(`crewFullName-${index}`).value = member.fullName || '';
                        document.getElementById(`crewRank-${index}`).value = member.rank || '';
                        document.getElementById(`crewExperience-${index}`).value = member.experienceYears || 0;
                        if (member.birthDate) {
                            const date = new Date(member.birthDate);
                            document.getElementById(`crewBirthDate-${index}`).value = 
                                date.toISOString().slice(0, 10);
                        }
                    });
                }
            })
            .catch(error => {
                alert('Ошибка загрузки данных корабля: ' + (error.message || error));
            });
    } else {
        document.getElementById('spaceshipForm').reset();
    }
    
    // Track form changes for all fields (including complex objects)
    const nameInput = document.getElementById('spaceshipName');
    const manufacturerInput = document.getElementById('spaceshipManufacturer');
    const typeSelect = document.getElementById('spaceshipType');
    const maxSpeedInput = document.getElementById('spaceshipMaxSpeed');
    const manufactureDateInput = document.getElementById('spaceshipManufactureDate');
    
    // Dimensions fields
    const dimensionsLengthInput = document.getElementById('dimensionsLength');
    const dimensionsWidthInput = document.getElementById('dimensionsWidth');
    const dimensionsHeightInput = document.getElementById('dimensionsHeight');
    const dimensionsWeightInput = document.getElementById('dimensionsWeight');
    const dimensionsVolumeInput = document.getElementById('dimensionsVolume');
    
    // Engine fields
    const engineModelInput = document.getElementById('engineModel');
    const engineThrustInput = document.getElementById('engineThrust');
    const engineFuelTypeInput = document.getElementById('engineFuelType');
    const engineFuelConsumptionInput = document.getElementById('engineFuelConsumption');
    
    const trackChanges = () => {
        spaceshipModalHasChanges = true;
    };
    
    // Track primitive fields
    nameInput.addEventListener('input', trackChanges);
    manufacturerInput.addEventListener('input', trackChanges);
    typeSelect.addEventListener('change', trackChanges);
    maxSpeedInput.addEventListener('input', trackChanges);
    manufactureDateInput.addEventListener('change', trackChanges);
    
    // Track dimensions fields
    dimensionsLengthInput.addEventListener('input', trackChanges);
    dimensionsWidthInput.addEventListener('input', trackChanges);
    dimensionsHeightInput.addEventListener('input', trackChanges);
    dimensionsWeightInput.addEventListener('input', trackChanges);
    dimensionsVolumeInput.addEventListener('input', trackChanges);
    
    // Track engine fields
    engineModelInput.addEventListener('input', trackChanges);
    engineThrustInput.addEventListener('input', trackChanges);
    engineFuelTypeInput.addEventListener('change', trackChanges);
    engineFuelConsumptionInput.addEventListener('input', trackChanges);
    
    // Track crew changes (delegate to container since crew members are dynamically added)
    const crewContainer = document.getElementById('crewMembersContainer');
    crewContainer.addEventListener('input', trackChanges, true); // Use capture phase
    crewContainer.addEventListener('change', trackChanges, true); // Use capture phase
    
    const modal = new bootstrap.Modal(document.getElementById('spaceshipModal'));
    
    // Clear tracked serial and reset state when modal is hidden
    const spaceshipModalElement = document.getElementById('spaceshipModal');
    spaceshipModalElement.addEventListener('hidden.bs.modal', () => {
        currentSpaceshipSerial = null;
        spaceshipModalHasChanges = false;
        pendingSpaceshipUpdate = null;
        hideSpaceshipUpdateWarning();
        
        // Remove all event listeners
        nameInput.removeEventListener('input', trackChanges);
        manufacturerInput.removeEventListener('input', trackChanges);
        typeSelect.removeEventListener('change', trackChanges);
        maxSpeedInput.removeEventListener('input', trackChanges);
        manufactureDateInput.removeEventListener('change', trackChanges);
        dimensionsLengthInput.removeEventListener('input', trackChanges);
        dimensionsWidthInput.removeEventListener('input', trackChanges);
        dimensionsHeightInput.removeEventListener('input', trackChanges);
        dimensionsWeightInput.removeEventListener('input', trackChanges);
        dimensionsVolumeInput.removeEventListener('input', trackChanges);
        engineModelInput.removeEventListener('input', trackChanges);
        engineThrustInput.removeEventListener('input', trackChanges);
        engineFuelTypeInput.removeEventListener('change', trackChanges);
        engineFuelConsumptionInput.removeEventListener('input', trackChanges);
        crewContainer.removeEventListener('input', trackChanges, true);
        crewContainer.removeEventListener('change', trackChanges, true);
    }, { once: true });
    
    modal.show();
}

/**
 * Save spaceship (create or update)
 */
async function saveSpaceship() {
    const serial = document.getElementById('spaceshipSerial').value;
    const data = {
        serial: parseInt(document.getElementById('spaceshipSerialInput').value),
        name: document.getElementById('spaceshipName').value,
        manufacturer: document.getElementById('spaceshipManufacturer').value,
        type: document.getElementById('spaceshipType').value,
        maxSpeed: parseInt(document.getElementById('spaceshipMaxSpeed').value) || 0
    };
    
    const manufactureDate = document.getElementById('spaceshipManufactureDate').value;
    if (manufactureDate) {
        data.manufactureDate = new Date(manufactureDate).toISOString();
    }
    
    // Add dimensions
    const dimensionsLength = document.getElementById('dimensionsLength').value;
    const dimensionsWidth = document.getElementById('dimensionsWidth').value;
    const dimensionsHeight = document.getElementById('dimensionsHeight').value;
    const dimensionsWeight = document.getElementById('dimensionsWeight').value;
    const dimensionsVolume = document.getElementById('dimensionsVolume').value;
    
    if (dimensionsLength || dimensionsWidth || dimensionsHeight || dimensionsWeight || dimensionsVolume) {
        data.dimensions = {
            length: parseInt(dimensionsLength) || 0,
            width: parseInt(dimensionsWidth) || 0,
            height: parseInt(dimensionsHeight) || 0,
            weight: parseFloat(dimensionsWeight) || 0,
            volume: parseFloat(dimensionsVolume) || 0
        };
    }
    
    // Add engine
    const engineModel = document.getElementById('engineModel').value;
    const engineThrust = document.getElementById('engineThrust').value;
    const engineFuelType = document.getElementById('engineFuelType').value;
    const engineFuelConsumption = document.getElementById('engineFuelConsumption').value;
    
    if (engineModel || engineThrust || engineFuelType || engineFuelConsumption) {
        data.engine = {
            model: engineModel || '',
            thrust: parseInt(engineThrust) || 0,
            fuelType: engineFuelType || null,
            fuelConsumption: parseFloat(engineFuelConsumption) || 0
        };
    }
    
    // Add crew
    const crew = [];
    for (let i = 0; i < crewMemberIndex; i++) {
        const memberDiv = document.getElementById(`crewMember-${i}`);
        if (memberDiv) {
            const fullName = document.getElementById(`crewFullName-${i}`).value;
            const rank = document.getElementById(`crewRank-${i}`).value;
            const experience = document.getElementById(`crewExperience-${i}`).value;
            const birthDate = document.getElementById(`crewBirthDate-${i}`).value;
            
            if (fullName || rank || experience || birthDate) {
                const member = {
                    fullName: fullName || '',
                    rank: rank || '',
                    experienceYears: parseInt(experience) || 0
                };
                if (birthDate) {
                    member.birthDate = birthDate;
                }
                crew.push(member);
            }
        }
    }
    if (crew.length > 0) {
        data.crew = crew;
    }
    
    const url = serial ? `${API_BASE}/spaceships/${serial}` : `${API_BASE}/spaceships`;
    const method = serial ? 'PUT' : 'POST';
    
    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка сохранения корабля: ' + errorMessage);
            return;
        }
        
        bootstrap.Modal.getInstance(document.getElementById('spaceshipModal')).hide();
        currentSpaceshipSerial = null; // Clear tracked serial after save
        spaceshipModalHasChanges = false; // Reset changes tracking
        pendingSpaceshipUpdate = null; // Clear pending updates
        hideSpaceshipUpdateWarning(); // Hide warning
        loadSpaceships();
    } catch (error) {
        alert('Ошибка сохранения корабля: ' + (error.message || error));
    }
}

/**
 * Delete spaceship
 * @param {number} serial - Spaceship serial
 */
async function deleteSpaceship(serial) {
    if (!confirm('Вы уверены, что хотите удалить этот корабль?')) return;
    try {
        const response = await fetch(`${API_BASE}/spaceships/${serial}`, { method: 'DELETE' });
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка удаления корабля: ' + errorMessage);
            return;
        }
        
        loadSpaceships();
    } catch (error) {
        alert('Ошибка удаления корабля: ' + (error.message || error));
    }
}

/**
 * Edit spaceship
 * @param {number} serial - Spaceship serial
 */
function editSpaceship(serial) {
    showSpaceshipModal(serial);
}

/**
 * Update spaceship in table row
 * @param {Object} spaceship - Updated spaceship data
 */
function updateSpaceshipInTable(spaceship) {
    const tbody = document.getElementById('spaceshipsTableBody');
    const rows = tbody.getElementsByTagName('tr');
    
    for (let row of rows) {
        const firstCell = row.cells[0];
        if (firstCell && firstCell.textContent.trim() === String(spaceship.serial)) {
            // Preserve onclick handler for the row
            if (!row.onclick) {
                row.style.cursor = 'pointer';
                row.onclick = () => viewSpaceship(spaceship.serial);
            }
            
            // Update the row with new data
            const dimensions = spaceship.dimensions ? 
                `${spaceship.dimensions.length || 0}м × ${spaceship.dimensions.width || 0}м × ${spaceship.dimensions.height || 0}м` : '-';
            const engine = spaceship.engine ? 
                `${spaceship.engine.model || 'Н/Д'} (${translateFuelType(spaceship.engine.fuelType || '') || 'Н/Д'})` : '-';
            const crewCount = spaceship.crew && spaceship.crew.length > 0 ? spaceship.crew.length : 0;
            const manufactureDate = spaceship.manufactureDate ? 
                new Date(spaceship.manufactureDate).toLocaleDateString('ru-RU') : '-';
            
            row.cells[1].textContent = spaceship.name || '-';
            row.cells[2].textContent = spaceship.manufacturer || '-';
            row.cells[3].innerHTML = `<span class="badge bg-primary">${translateSpaceShipType(spaceship.type || '') || '-'}</span>`;
            row.cells[4].textContent = manufactureDate;
            row.cells[5].textContent = spaceship.maxSpeed || 0;
            row.cells[6].innerHTML = `<small>${dimensions}</small>`;
            row.cells[7].innerHTML = `<small>${engine}</small>`;
            row.cells[8].textContent = `${crewCount} ${crewCount === 1 ? 'человек' : crewCount < 5 ? 'человека' : 'человек'}`;
            break;
        }
    }
}

/**
 * Show warning about spaceship update when editing
 * @param {Object} spaceship - Updated spaceship data
 */
function showSpaceshipUpdateWarning(spaceship) {
    const warningDiv = document.getElementById('spaceshipUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'block';
        pendingSpaceshipUpdate = spaceship;
    }
}

/**
 * Hide warning about spaceship update
 */
function hideSpaceshipUpdateWarning() {
    const warningDiv = document.getElementById('spaceshipUpdateWarning');
    if (warningDiv) {
        warningDiv.style.display = 'none';
        pendingSpaceshipUpdate = null;
    }
}

/**
 * Ignore spaceship update warning
 */
function ignoreSpaceshipUpdate() {
    hideSpaceshipUpdateWarning();
}

/**
 * Update spaceship modal with fresh data from backend
 */
async function updateSpaceshipModalFromBackend() {
    if (!currentSpaceshipSerial || !pendingSpaceshipUpdate) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/spaceships/${currentSpaceshipSerial}`);
        
        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            alert('Ошибка загрузки обновлённых данных корабля: ' + errorMessage);
            return;
        }
        
        const ship = await response.json();
        
        // Update form fields with fresh data
        document.getElementById('spaceshipName').value = ship.name || '';
        document.getElementById('spaceshipManufacturer').value = ship.manufacturer || '';
        document.getElementById('spaceshipType').value = ship.type || '';
        document.getElementById('spaceshipMaxSpeed').value = ship.maxSpeed || 0;
        if (ship.manufactureDate) {
            const date = new Date(ship.manufactureDate);
            document.getElementById('spaceshipManufactureDate').value = 
                date.toISOString().slice(0, 16);
        }
        
        // Populate dimensions (handle null/undefined by clearing fields)
        if (ship.dimensions) {
            document.getElementById('dimensionsLength').value = ship.dimensions.length || 0;
            document.getElementById('dimensionsWidth').value = ship.dimensions.width || 0;
            document.getElementById('dimensionsHeight').value = ship.dimensions.height || 0;
            document.getElementById('dimensionsWeight').value = roundTo3Decimals(ship.dimensions.weight || 0);
            document.getElementById('dimensionsVolume').value = roundTo3Decimals(ship.dimensions.volume || 0);
        } else {
            // Clear dimensions if not present
            document.getElementById('dimensionsLength').value = 0;
            document.getElementById('dimensionsWidth').value = 0;
            document.getElementById('dimensionsHeight').value = 0;
            document.getElementById('dimensionsWeight').value = 0;
            document.getElementById('dimensionsVolume').value = 0;
        }
        
        // Populate engine (handle null/undefined by clearing fields)
        if (ship.engine) {
            document.getElementById('engineModel').value = ship.engine.model || '';
            document.getElementById('engineThrust').value = ship.engine.thrust || 0;
            document.getElementById('engineFuelType').value = ship.engine.fuelType || '';
            document.getElementById('engineFuelConsumption').value = roundTo3Decimals(ship.engine.fuelConsumption || 0);
        } else {
            // Clear engine if not present
            document.getElementById('engineModel').value = '';
            document.getElementById('engineThrust').value = 0;
            document.getElementById('engineFuelType').value = '';
            document.getElementById('engineFuelConsumption').value = 0;
        }
        
        // Populate crew (always clear and rebuild)
        document.getElementById('crewMembersContainer').innerHTML = '';
        crewMemberIndex = 0;
        if (ship.crew && ship.crew.length > 0) {
            ship.crew.forEach(member => {
                addCrewMember();
                const index = crewMemberIndex - 1;
                document.getElementById(`crewFullName-${index}`).value = member.fullName || '';
                document.getElementById(`crewRank-${index}`).value = member.rank || '';
                document.getElementById(`crewExperience-${index}`).value = member.experienceYears || 0;
                if (member.birthDate) {
                    const date = new Date(member.birthDate);
                    document.getElementById(`crewBirthDate-${index}`).value = 
                        date.toISOString().slice(0, 10);
                }
            });
        }
        
        // Reset changes tracking since we're updating with fresh data
        spaceshipModalHasChanges = false;
        
        // Hide warning
        hideSpaceshipUpdateWarning();
    } catch (error) {
        alert('Ошибка загрузки обновлённых данных корабля: ' + (error.message || error));
    }
}

/**
 * Update spaceship in edit modal if it's currently open
 * @param {Object} spaceship - Updated spaceship data
 */
function updateSpaceshipInModal(spaceship) {
    const modal = bootstrap.Modal.getInstance(document.getElementById('spaceshipModal'));
    if (modal && modal._isShown && currentSpaceshipSerial === spaceship.serial) {
        // If modal has unsaved changes, show warning instead of auto-updating
        if (spaceshipModalHasChanges) {
            showSpaceshipUpdateWarning(spaceship);
        } else {
            // No unsaved changes, update directly
            document.getElementById('spaceshipName').value = spaceship.name || '';
            document.getElementById('spaceshipManufacturer').value = spaceship.manufacturer || '';
            document.getElementById('spaceshipType').value = spaceship.type || '';
            document.getElementById('spaceshipMaxSpeed').value = spaceship.maxSpeed || 0;
            if (spaceship.manufactureDate) {
                const date = new Date(spaceship.manufactureDate);
                document.getElementById('spaceshipManufactureDate').value = 
                    date.toISOString().slice(0, 16);
            }
            
            // Update dimensions (handle null/undefined by clearing fields)
            if (spaceship.dimensions) {
                document.getElementById('dimensionsLength').value = spaceship.dimensions.length || 0;
                document.getElementById('dimensionsWidth').value = spaceship.dimensions.width || 0;
                document.getElementById('dimensionsHeight').value = spaceship.dimensions.height || 0;
                document.getElementById('dimensionsWeight').value = roundTo3Decimals(spaceship.dimensions.weight || 0);
                document.getElementById('dimensionsVolume').value = roundTo3Decimals(spaceship.dimensions.volume || 0);
            } else {
                // Clear dimensions if not present
                document.getElementById('dimensionsLength').value = 0;
                document.getElementById('dimensionsWidth').value = 0;
                document.getElementById('dimensionsHeight').value = 0;
                document.getElementById('dimensionsWeight').value = 0;
                document.getElementById('dimensionsVolume').value = 0;
            }
            
            // Update engine (handle null/undefined by clearing fields)
            if (spaceship.engine) {
                document.getElementById('engineModel').value = spaceship.engine.model || '';
                document.getElementById('engineThrust').value = spaceship.engine.thrust || 0;
                document.getElementById('engineFuelType').value = spaceship.engine.fuelType || '';
                document.getElementById('engineFuelConsumption').value = roundTo3Decimals(spaceship.engine.fuelConsumption || 0);
            } else {
                // Clear engine if not present
                document.getElementById('engineModel').value = '';
                document.getElementById('engineThrust').value = 0;
                document.getElementById('engineFuelType').value = '';
                document.getElementById('engineFuelConsumption').value = 0;
            }
            
            // Update crew (always clear and rebuild)
            document.getElementById('crewMembersContainer').innerHTML = '';
            crewMemberIndex = 0;
            if (spaceship.crew && spaceship.crew.length > 0) {
                spaceship.crew.forEach(member => {
                    addCrewMember();
                    const index = crewMemberIndex - 1;
                    document.getElementById(`crewFullName-${index}`).value = member.fullName || '';
                    document.getElementById(`crewRank-${index}`).value = member.rank || '';
                    document.getElementById(`crewExperience-${index}`).value = member.experienceYears || 0;
                    if (member.birthDate) {
                        const date = new Date(member.birthDate);
                        document.getElementById(`crewBirthDate-${index}`).value = 
                            date.toISOString().slice(0, 10);
                    }
                });
            }
        }
    }
}

/**
 * Update spaceship in view modal if it's currently open
 * @param {Object} spaceship - Updated spaceship data
 */
function updateSpaceshipInViewModal(spaceship) {
    // Check if view modal is showing this spaceship using tracked serial
    if (typeof viewedSpaceshipSerial !== 'undefined' && viewedSpaceshipSerial === spaceship.serial) {
        const viewModal = bootstrap.Modal.getInstance(document.getElementById('spaceshipViewModal'));
        if (viewModal && viewModal._isShown) {
            // Reload the view modal content
            viewSpaceship(spaceship.serial);
        }
    }
}

/**
 * Handle spaceship update from event stream
 * @param {Object} spaceship - Updated spaceship data
 */
function handleSpaceshipUpdate(spaceship) {
    // Update table if spaceship is displayed there
    updateSpaceshipInTable(spaceship);
    
    // Update edit modal if this spaceship is being edited
    updateSpaceshipInModal(spaceship);
    
    // Update view modal if this spaceship is being viewed
    updateSpaceshipInViewModal(spaceship);
}

