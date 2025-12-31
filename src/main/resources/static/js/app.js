/**
 * app.js - Logica comune per tutti i pannelli
 */

// Funzione per controllare lo stato del DB
function checkDatabaseStatus() {
    fetch('/salvavita/api/health')
        .then(response => response.json())
        .then(data => {
            document.getElementById('dbStatus').classList.add('connected');
            document.getElementById('dbStatusText').textContent = 'Connesso';
        })
        .catch(error => {
            document.getElementById('dbStatus').classList.add('error');
            document.getElementById('dbStatusText').textContent = 'Disconnesso';
        });
}

/**
 * Funzione generica per fare fetch con gestione errori
 */
function fetchAPI(endpoint, method = 'GET', data = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (data && method !== 'GET') {
        options.body = JSON.stringify(data);
    }

    return fetch(endpoint, options)
        .then(response => response.json());
}

/**
 * Formatta una data nel formato italiano
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    try {
        return new Date(dateString).toLocaleString('it-IT');
    } catch (e) {
        return '-';
    }
}
