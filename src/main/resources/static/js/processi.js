/**
 * processi.js - Logica specifica per il pannello Controllo Processi
 */

// MOSTRA MESSAGGIO (SUCCESS O ERROR)
function showProcessiMessage(message, isError = false) {
    const errorDiv = document.getElementById('processiError');
    const successDiv = document.getElementById('processiSuccess');

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

// CARICA CONTROLLO PROCESSI
function loadControlloProcessi() {
    const contentDiv = document.getElementById('processiContent');
    const loadingDiv = document.getElementById('processiLoading');

    contentDiv.style.display = 'none';
    loadingDiv.style.display = 'block';

    // UNA SOLA CHIAMATA che esegue tutte le query con una connessione
    // Risolve: ORA-02391: exceeded simultaneous SESSIONS_PER_USER limit
    fetchAPI('/salvavita/api/controllo-processi')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                let html = '<div style="display: flex; flex-direction: column; gap: 20px;">';

                // Sezione Allineamenti
                html += buildAllineamentiSection(data.allineamenti);

                // Sezione Demoni
                html += buildDemoniSection(data.demoneSogei, data.demoneEntrate);

                html += '</div>';
                contentDiv.innerHTML = html;
                contentDiv.style.display = 'block';

                showProcessiMessage('✓ Dati caricati con successo');
            } else {
                showProcessiMessage('❌ Errore: ' + (data.message || 'Errore sconosciuto'), true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            showProcessiMessage('❌ Errore: ' + error.message, true);
        });
}

// COSTRUISCI SEZIONE ALLINEAMENTI
function buildAllineamentiSection(allineamenti) {
    let html = '<div style="background: #f9f9f9; padding: 15px; border-radius: 5px;">';
    html += '<h3 style="margin-bottom: 10px;">📊 Allineamenti Processi</h3>';

    if (allineamenti && allineamenti.length > 0) {
        html += '<table style="width: 100%;"><thead><tr>';
        html += '<th>Count</th><th>Tipo</th><th>Stato</th>';
        html += '</tr></thead><tbody>';

        allineamenti.forEach(row => {
            let statusClass = '';
            let statusMsg = '';

            if (row.tipo === 'Allineamenti_in_errore' && row.count > 0) {
                statusClass = 'style="background: #ffebee; color: #c62828;"';
                statusMsg = '⚠️ ALLINEAMENTO DAEMON_SESAMO IN ERRORE';
            } else if (row.tipo === 'allineamenti_Non_Schedulati' && row.count > 0) {
                statusClass = 'style="background: #ffebee; color: #c62828;"';
                statusMsg = '⚠️ UNO O PIU ALLINEAMENTI DAEMON_SESAMO NON SCHEDULATI';
            } else {
                statusClass = 'style="background: #e8f5e9; color: #2e7d32;"';
                statusMsg = '✅ OK';
            }

            html += `<tr ${statusClass}>`;
            html += `<td><strong>${row.count}</strong></td>`;
            html += `<td>${row.tipo}</td>`;
            html += `<td>${statusMsg}</td>`;
            html += '</tr>';
        });

        html += '</tbody></table>';
    } else {
        html += '<p>Nessun dato disponibile</p>';
    }

    html += '</div>';
    return html;
}

// COSTRUISCI SEZIONE DEMONI
function buildDemoniSection(demoneSogei, demoneEntrate) {
    let html = '<div style="background: #f9f9f9; padding: 15px; border-radius: 5px;">';
    html += '<h3 style="margin-bottom: 10px;">📧 Demoni Mail Sender</h3>';
    html += '<table style="width: 100%;"><thead><tr>';
    html += '<th>Ente</th><th>Nome</th><th>Is Working</th><th>Start Working</th><th>Stop Working</th><th>Azione</th>';
    html += '</tr></thead><tbody>';

    // SOGEI
    if (demoneSogei) {
        html += buildDemoneRow(demoneSogei, 'sogei_asp');
    }

    // ENTRATE
    if (demoneEntrate) {
        html += buildDemoneRow(demoneEntrate, 'entr_asp');
    }

    html += '</tbody></table>';
    html += '</div>';
    return html;
}

// COSTRUISCI RIGA DEMONE
function buildDemoneRow(demone, schema) {
    const startWorking = formatDate(demone.dateStartWorking);
    const stopWorking = formatDate(demone.dateStopWorking);

    let html = '<tr>';
    html += `<td><strong>${demone.ente}</strong></td>`;
    html += `<td>${demone.codiNome || '-'}</td>`;
    html += `<td>${demone.isWorking !== null ? demone.isWorking : '-'}</td>`;
    html += `<td>${startWorking}</td>`;
    html += `<td>${stopWorking}</td>`;
    html += `<td><button class="btn-riavvia" onclick="riavviaDemone('${schema}', '${demone.ente}')">🔄 RIAVVIA</button></td>`;
    html += '</tr>';

    return html;
}

// RIAVVIA DEMONE
function riavviaDemone(schema, ente) {
    const loadingDiv = document.getElementById('processiLoading');
    loadingDiv.style.display = 'block';

    fetchAPI(`/salvavita/api/riavvia-demone-mail-sender?schema=${encodeURIComponent(schema)}`, 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (data.success) {
                // Mostra popup con riepilogo e pulsanti commit/rollback
                showRiavviaDemoneModal(data);
            } else {
                showProcessiMessage(`❌ Errore: ${data.message}`, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            showProcessiMessage(`❌ Errore: ${error.message}`, true);
        });
}

// MOSTRA POPUP PER RIAVVIO DEMONE
function showRiavviaDemoneModal(data) {
    // Costruisci HTML per il popup
    let modalBody = '<div style="max-height: 400px; overflow-y: auto;">';
    modalBody += '<h4 style="margin-bottom: 15px;">📊 Riepilogo Riavvio Demone</h4>';

    modalBody += '<div style="background: #f0f0f0; padding: 10px; border-radius: 5px; margin-bottom: 15px;">';
    modalBody += `<strong>Schema: </strong><span style="color: #667eea; font-weight: bold;">${data.schema}</span><br>`;
    modalBody += `<strong>Record aggiornati: </strong><span style="color: #667eea; font-weight: bold; font-size: 18px;">${data.rowsAffected}</span>`;
    modalBody += '</div>';

    modalBody += '<p style="color: #f44336; font-weight: bold;">⚠️ Seleziona COMMIT per salvare i cambiamenti o ROLLBACK per annullarli</p>';
    modalBody += '</div>';

    // Crea il modal dinamicamente
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.id = 'riavviaDemoneModal';
    modal.style.display = 'block';

    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                ⚠️ Conferma Riavvio Demone
            </div>
            <div class="modal-body">
                ${modalBody}
            </div>
            <div class="modal-footer">
                <button class="btn-commit" onclick="doCommitFromRiavviaDemone()">✅ COMMIT</button>
                <button class="btn-rollback" onclick="doRollbackFromRiavviaDemone()">❌ ROLLBACK</button>
            </div>
        </div>
    `;

    // Rimuovi modal esistente se presente
    const existingModal = document.getElementById('riavviaDemoneModal');
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

// COMMIT DA RIAVVIO DEMONE
function doCommitFromRiavviaDemone() {
    const modal = document.getElementById('riavviaDemoneModal');
    const loadingDiv = document.getElementById('processiLoading');

    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/commit-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }

            if (data.success) {
                showProcessiMessage('✅ ' + data.message);
                loadControlloProcessi();
            } else {
                showProcessiMessage('❌ Errore: ' + data.message, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }
            showProcessiMessage('❌ Errore: ' + error.message, true);
        });
}

// ROLLBACK DA RIAVVIO DEMONE
function doRollbackFromRiavviaDemone() {
    const modal = document.getElementById('riavviaDemoneModal');
    const loadingDiv = document.getElementById('processiLoading');

    loadingDiv.style.display = 'block';

    fetchAPI('/salvavita/api/rollback-transaction', 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }

            if (data.success) {
                showProcessiMessage('✅ ' + data.message);
                loadControlloProcessi();
            } else {
                showProcessiMessage('❌ Errore: ' + data.message, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            if (modal) {
                modal.remove();
            }
            showProcessiMessage('❌ Errore: ' + error.message, true);
        });
}
