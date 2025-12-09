/**
 * Spaceships management functions
 */

let crewMemberIndex = 0;

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
    new bootstrap.Modal(document.getElementById('spaceshipModal')).show();
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

