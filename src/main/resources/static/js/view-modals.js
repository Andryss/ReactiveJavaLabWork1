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
            `${ship.dimensions.length || 0}m × ${ship.dimensions.width || 0}m × ${ship.dimensions.height || 0}m, ` +
            `Weight: ${ship.dimensions.weight || 0} tons, Volume: ${ship.dimensions.volume || 0} m³` : 'Not specified';
        const engine = ship.engine ? 
            `Model: ${ship.engine.model || 'N/A'}, Thrust: ${ship.engine.thrust || 0} kN, ` +
            `Fuel Type: ${ship.engine.fuelType || 'N/A'}, Consumption: ${ship.engine.fuelConsumption || 0} per hour` : 'Not specified';
        const manufactureDate = ship.manufactureDate ? 
            new Date(ship.manufactureDate).toLocaleString() : 'Not specified';
        
        let crewHtml = '';
        if (ship.crew && ship.crew.length > 0) {
            crewHtml = '<h6 class="mb-3 text-primary">Crew Members</h6><ul class="list-group mb-3">';
            ship.crew.forEach(member => {
                const birthDate = member.birthDate ? new Date(member.birthDate).toLocaleDateString() : 'Not specified';
                crewHtml += `
                    <li class="list-group-item">
                        <strong>${member.fullName || 'N/A'}</strong><br>
                        <small>Rank: ${member.rank || 'N/A'}, Experience: ${member.experienceYears || 0} years, Birth Date: ${birthDate}</small>
                    </li>
                `;
            });
            crewHtml += '</ul>';
        } else {
            crewHtml = '<p class="text-muted">No crew members assigned</p>';
        }
        
        document.getElementById('spaceshipViewContent').innerHTML = `
            <h6 class="mb-3 text-primary">Basic Information</h6>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Serial Number:</strong> ${ship.serial}
                </div>
                <div class="col-md-6">
                    <strong>Name:</strong> ${ship.name || 'N/A'}
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Manufacturer:</strong> ${ship.manufacturer || 'N/A'}
                </div>
                <div class="col-md-6">
                    <strong>Type:</strong> <span class="badge bg-primary">${ship.type || 'N/A'}</span>
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-md-6">
                    <strong>Manufacture Date:</strong> ${manufactureDate}
                </div>
                <div class="col-md-6">
                    <strong>Max Speed:</strong> ${ship.maxSpeed || 0}
                </div>
            </div>
            <hr class="my-4">
            <h6 class="mb-3 text-primary">Dimensions</h6>
            <p>${dimensions}</p>
            <hr class="my-4">
            <h6 class="mb-3 text-primary">Engine</h6>
            <p>${engine}</p>
            <hr class="my-4">
            ${crewHtml}
        `;
        
        const modal = new bootstrap.Modal(document.getElementById('spaceshipViewModal'));
        modal.show();
        
        // Clear hover timer when modal is shown
        hideSpaceshipTooltip();
    } catch (error) {
        alert('Error loading spaceship details: ' + error);
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
                <strong>Name:</strong> ${repairman.name || 'N/A'}
            </div>
            <div class="mb-3">
                <strong>Position:</strong> ${repairman.position || 'N/A'}
            </div>
        `;
        
        const modal = new bootstrap.Modal(document.getElementById('repairmanViewModal'));
        modal.show();
        
        // Clear hover timer when modal is shown
        hideRepairmanTooltip();
    } catch (error) {
        alert('Error loading repairman details: ' + error);
        hideRepairmanTooltip();
    }
}

