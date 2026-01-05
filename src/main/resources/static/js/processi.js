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

    // Carica tutti i dati in parallelo
    Promise.all([
        fetchAPI('/salvavita/api/allineamenti-processi'),
        fetchAPI('/salvavita/api/demone-mail-sender/sogei'),
        fetchAPI('/salvavita/api/demone-mail-sender/entrate')
    ])
    .then(([allineamenti, demoneSogei, demoneEntrate]) => {
        loadingDiv.style.display = 'none';

        let html = '<div style="display: flex; flex-direction: column; gap: 20px;">';

        // Sezione Allineamenti
        html += buildAllineamentiSection(allineamenti);

        // Sezione Demoni
        html += buildDemoniSection(demoneSogei, demoneEntrate);

        html += '</div>';
        contentDiv.innerHTML = html;
        contentDiv.style.display = 'block';

        showProcessiMessage('✓ Dati caricati con successo');
    })
    .catch(error => {
        loadingDiv.style.display = 'none';
        showProcessiMessage('❌ Errore: ' + error.message, true);
    });
}

// COSTRUISCI SEZIONE ALLINEAMENTI
function buildAllineamentiSection(response) {
    let html = '<div style="background: #f9f9f9; padding: 15px; border-radius: 5px;">';
    html += '<h3 style="margin-bottom: 10px;">📊 Allineamenti Processi</h3>';

    if (response.success && response.data) {
        html += '<table style="width: 100%;"><thead><tr>';
        html += '<th>Count</th><th>Tipo</th><th>Stato</th>';
        html += '</tr></thead><tbody>';

        response.data.forEach(row => {
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
    if (demoneSogei.success && demoneSogei.data) {
        html += buildDemoneRow(demoneSogei.data, 'sogei_asp');
    }

    // ENTRATE
    if (demoneEntrate.success && demoneEntrate.data) {
        html += buildDemoneRow(demoneEntrate.data, 'entr_asp');
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
    if (!confirm(`Sei sicuro di voler riavviare il demone per ${ente}?`)) {
        return;
    }

    const loadingDiv = document.getElementById('processiLoading');
    loadingDiv.style.display = 'block';

    fetchAPI(`/salvavita/api/riavvia-demone-mail-sender?schema=${encodeURIComponent(schema)}`, 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (data.success) {
                showProcessiMessage(`✅ ${data.message}`);
                // Ricarica i dati
                loadControlloProcessi();
            } else {
                showProcessiMessage(`❌ Errore: ${data.message}`, true);
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            showProcessiMessage(`❌ Errore: ${error.message}`, true);
        });
}
