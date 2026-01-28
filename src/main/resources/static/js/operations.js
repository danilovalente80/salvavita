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
    if (!confirm('Sei sicuro di voler eseguire queste operazioni?\n\nLe query verranno eseguite senza autocommit e richiederanno COMMIT o ROLLBACK.')) {
        return;
    }

    const contentDiv = document.getElementById('operationsContent');
    const loadingDiv = document.getElementById('operationsLoading');

    contentDiv.style.display = 'none';
    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/execute-various-operations', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                // Mostra popup con report
                showOperationsReport(data);

                contentDiv.innerHTML = '<div class="empty-state"><p>✅ Operazioni completate - In attesa di Commit/Rollback</p></div>';
                contentDiv.style.display = 'block';

                showOperationsMessage(`✓ ${data.operations.length} operazioni completate - ${data.totalRecords} record interessati`);
            } else {
                contentDiv.style.display = 'block';
                showOperationsMessage('❌ Errore: ' + (data.message || 'Errore sconosciuto'), true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            contentDiv.style.display = 'block';
            showOperationsMessage('❌ Errore: ' + error.message, true);
        });
}

// MOSTRA POPUP CON REPORT DELLE OPERAZIONI
function showOperationsReport(data) {
    // Costruisci HTML per il popup
    let modalBody = '<div style="max-height: 400px; overflow-y: auto;">';
    modalBody += '<h4 style="margin-bottom: 15px;">📊 Report Operazioni</h4>';

    // Tabella con dettagli operazioni
    modalBody += '<table style="width: 100%; margin-bottom: 15px;"><thead><tr>';
    modalBody += '<th style="text-align: left;">Operazione</th>';
    modalBody += '<th style="text-align: right;">Record</th>';
    modalBody += '</tr></thead><tbody>';

    data.operations.forEach(op => {
        modalBody += '<tr>';
        modalBody += `<td>${op.label}</td>`;
        modalBody += `<td style="text-align: right; font-weight: bold; color: #667eea;">${op.recordsAffected}</td>`;
        modalBody += '</tr>';
    });

    modalBody += '</tbody></table>';

    // Totale
    modalBody += '<div style="background: #f0f0f0; padding: 10px; border-radius: 5px; margin-bottom: 15px;">';
    modalBody += `<strong>Totale record interessati: </strong>`;
    modalBody += `<span style="color: #667eea; font-weight: bold; font-size: 18px;">${data.totalRecords}</span>`;
    modalBody += '</div>';

    modalBody += '<p style="color: #f44336; font-weight: bold;">⚠️ Seleziona COMMIT per salvare i cambiamenti o ROLLBACK per annullarli</p>';
    modalBody += '</div>';

    // Crea il modal dinamicamente
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.id = 'operationsReportModal';
    modal.style.display = 'block';

    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                ⚠️ Conferma Operazioni
            </div>
            <div class="modal-body">
                ${modalBody}
            </div>
            <div class="modal-footer">
                <button class="btn-commit" onclick="doCommitFromOperations()">✅ COMMIT</button>
                <button class="btn-rollback" onclick="doRollbackFromOperations()">❌ ROLLBACK</button>
            </div>
        </div>
    `;

    // Rimuovi modal esistente se presente
    const existingModal = document.getElementById('operationsReportModal');
    if (existingModal) {
        existingModal.remove();
    }

    document.body.appendChild(modal);

    // Chiudi modal cliccando fuori
    modal.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.remove();
        }
    });
}

// COMMIT DALLE OPERAZIONI
function doCommitFromOperations() {
    const modal = document.getElementById('operationsReportModal');
    const loadingDiv = document.getElementById('operationsLoading');

    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/commit-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }

            if (data.success) {
                showOperationsMessage('✅ ' + data.message);
            } else {
                showOperationsMessage('❌ Errore: ' + data.message, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }
            showOperationsMessage('❌ Errore: ' + error.message, true);
        });
}

// ROLLBACK DALLE OPERAZIONI
function doRollbackFromOperations() {
    const modal = document.getElementById('operationsReportModal');
    const loadingDiv = document.getElementById('operationsLoading');

    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/rollback-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }

            if (data.success) {
                showOperationsMessage('✅ ' + data.message);
            } else {
                showOperationsMessage('❌ Errore: ' + data.message, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }
            showOperationsMessage('❌ Errore: ' + error.message, true);
        });
}
