/**
 * operations.js - Logica specifica per il pannello INSERT/UPDATE/DELETE Varie
 */

// MOSTRA MESSAGGIO (SUCCESS O ERROR)
function showOperationsMessage(message, isError = false) {
    const errorDiv = document.getElementById('operationsError');
    const successDiv = document.getElementById('operationsSuccess');

    if (isError) {
        errorDiv.innerHTML = message;
        errorDiv.style.display = 'block';
        successDiv.style.display = 'none';
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 8000);
    } else {
        successDiv.innerHTML = message;
        successDiv.style.display = 'block';
        errorDiv.style.display = 'none';
        setTimeout(() => {
            successDiv.style.display = 'none';
        }, 5000);
    }
}

// ESEGUI OPERAZIONI VARIE
function executeVariousOperations() {
    const contentDiv = document.getElementById('operationsContent');
    const loadingDiv = document.getElementById('operationsLoading');
    const actionsDiv = document.getElementById('operationsActions');

    contentDiv.style.display = 'none';
    actionsDiv.style.display = 'none';
    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/execute-various-operations', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                // Mostra i risultati inline nel pannello
                showOperationsResultsInline(data);

                contentDiv.style.display = 'block';
                actionsDiv.style.display = 'block';

                showOperationsMessage(`✓ ${data.operations.length} operazioni completate - ${data.totalRecords} record interessati`);
            } else {
                contentDiv.innerHTML = '<div class="empty-state"><p>❌ Operazioni fallite</p></div>';
                contentDiv.style.display = 'block';
                showOperationsMessage('❌ Errore: ' + (data.message || 'Errore sconosciuto'), true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            contentDiv.innerHTML = '<div class="empty-state"><p>❌ Errore di comunicazione</p></div>';
            contentDiv.style.display = 'block';
            showOperationsMessage('❌ Errore: ' + error.message, true);
        });
}

// MOSTRA RISULTATI INLINE NEL PANNELLO
function showOperationsResultsInline(data) {
    const contentDiv = document.getElementById('operationsContent');

    let html = '<div style="padding: 15px; background: #f9f9f9; border-radius: 5px;">';
    html += '<h3 style="margin-bottom: 15px; color: #333;">📊 Report Operazioni</h3>';

    // Tabella con dettagli operazioni
    html += '<table style="width: 100%; margin-bottom: 15px;"><thead><tr>';
    html += '<th style="text-align: left; padding: 10px; background: #667eea; color: white;">Operazione</th>';
    html += '<th style="text-align: right; padding: 10px; background: #667eea; color: white;">Record Interessati</th>';
    html += '</tr></thead><tbody>';

    data.operations.forEach((op, index) => {
        const bgColor = index % 2 === 0 ? '#fff' : '#f5f5f5';
        html += `<tr style="background: ${bgColor};">`;
        html += `<td style="padding: 10px;">${op.label}</td>`;
        html += `<td style="text-align: right; padding: 10px; font-weight: bold; color: #667eea;">${op.recordsAffected}</td>`;
        html += '</tr>';
    });

    html += '</tbody></table>';

    // Totale
    html += '<div style="background: #e3f2fd; padding: 15px; border-radius: 5px; border-left: 4px solid #2196f3;">';
    html += `<strong style="font-size: 16px;">Totale record interessati: </strong>`;
    html += `<span style="color: #2196f3; font-weight: bold; font-size: 20px;">${data.totalRecords}</span>`;
    html += '</div>';

    html += '<p style="color: #f44336; font-weight: bold; margin-top: 15px; padding: 10px; background: #ffebee; border-radius: 5px;">';
    html += '⚠️ Seleziona COMMIT per salvare i cambiamenti o ROLLBACK per annullarli';
    html += '</p>';

    html += '</div>';

    contentDiv.innerHTML = html;
}

// COMMIT OPERAZIONI
function doCommitOperations() {
    const loadingDiv = document.getElementById('operationsLoading');
    const contentDiv = document.getElementById('operationsContent');
    const actionsDiv = document.getElementById('operationsActions');

    loadingDiv.style.display = 'block';
    actionsDiv.style.display = 'none';

    fetchAPI('/salvavita/api/commit-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                showOperationsMessage('✅ ' + data.message);
                contentDiv.innerHTML = '<div class="empty-state"><p>✅ Commit completato con successo</p></div>';
                contentDiv.style.display = 'block';
            } else {
                showOperationsMessage('❌ Errore: ' + data.message, true);
                actionsDiv.style.display = 'block';
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            showOperationsMessage('❌ Errore: ' + error.message, true);
            actionsDiv.style.display = 'block';
        });
}

// ROLLBACK OPERAZIONI
function doRollbackOperations() {
    const loadingDiv = document.getElementById('operationsLoading');
    const contentDiv = document.getElementById('operationsContent');
    const actionsDiv = document.getElementById('operationsActions');

    loadingDiv.style.display = 'block';
    actionsDiv.style.display = 'none';

    fetchAPI('/salvavita/api/rollback-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                showOperationsMessage('✅ ' + data.message);
                contentDiv.innerHTML = '<div class="empty-state"><p>✅ Rollback completato con successo</p></div>';
                contentDiv.style.display = 'block';
            } else {
                showOperationsMessage('❌ Errore: ' + data.message, true);
                actionsDiv.style.display = 'block';
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            showOperationsMessage('❌ Errore: ' + error.message, true);
            actionsDiv.style.display = 'block';
        });
}
