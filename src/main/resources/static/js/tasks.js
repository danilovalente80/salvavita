/**
 * tasks.js - Logica specifica per il pannello Task Schedulati
 */

// CARICA SCHEDULED TASKS
function loadScheduledTasks() {
    const contentDiv = document.getElementById('tasksContent');
    const loadingDiv = document.getElementById('tasksLoading');
    const errorDiv = document.getElementById('tasksError');
    const successDiv = document.getElementById('tasksSuccess');

    contentDiv.style.display = 'none';
    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    fetchAPI('/salvavita/api/scheduled-tasks')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                successDiv.textContent = `✓ Caricati ${data.totalRecords} record`;
                successDiv.style.display = 'block';

                if (data.data.length === 0) {
                    contentDiv.innerHTML = '<div class="empty-state"><p>Nessun record trovato</p></div>';
                } else {
                    contentDiv.innerHTML = buildTasksTable(data);
                }

                contentDiv.style.display = 'block';
            } else {
                errorDiv.textContent = '❌ Errore: ' + (data.message || 'Errore sconosciuto');
                errorDiv.style.display = 'block';
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            errorDiv.textContent = '❌ Errore di comunicazione: ' + error.message;
            errorDiv.style.display = 'block';
        });
}

// COSTRUISCI TABELLA TASKS
function buildTasksTable(data) {
    let html = `<div class="record-count">📊 Totale: ${data.totalRecords} record</div>`;
    html += `<div class="table-wrapper"><table>
        <thead>
            <tr>
                <th>Nome Task</th>
                <th>Prossimo Run</th>
            </tr>
        </thead>
        <tbody>`;

    data.data.forEach(row => {
        const prossimoRun = formatDate(row.prossimoRun);
        html += `<tr>
            <td><strong>${row.name}</strong></td>
            <td>${prossimoRun}</td>
        </tr>`;
    });

    html += `</tbody></table></div>`;
    return html;
}

// AUTO-REFRESH FUNCTION
function refreshScheduledTasks() {
    loadScheduledTasks();
    // Opzionale: ricarica ogni 30 secondi
    // setInterval(loadScheduledTasks, 30000);
}

// AVVIA PROCESSI
function avviaProcessi() {
    const tasksLoading = document.getElementById('tasksLoading');
    const tasksError = document.getElementById('tasksError');

    tasksLoading.style.display = 'block';
    tasksError.style.display = 'none';

    fetchAPI('/salvavita/api/avvia-processi', 'POST')
        .then(data => {
            tasksLoading.style.display = 'none';
            if (data.success) {
                // Mostra il modal con il riepilogo
                showTransactionModal(data.recordsAffected, data.queries, 'SYSTEM', 'Riavvio Tasks');
            } else {
                tasksError.style.display = 'block';
                tasksError.innerHTML = `❌ Errore: ${data.message}`;
            }
        })
        .catch(error => {
            tasksLoading.style.display = 'none';
            tasksError.style.display = 'block';
            tasksError.innerHTML = `❌ Errore nella comunicazione: ${error.message}`;
            console.error('Errore:', error);
        });
}