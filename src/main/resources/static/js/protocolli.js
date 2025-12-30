/**
 * protocolli.js - Logica specifica per il pannello Protocolli Sospesi
 */

// CARICA PROTOCOLLI SOSPESI
function loadProtocolliSospesi() {
    const contentDiv = document.getElementById('protocolliContent');
    const loadingDiv = document.getElementById('protocolliLoading');
    const errorDiv = document.getElementById('protocolliError');
    const successDiv = document.getElementById('protocolliSuccess');

    contentDiv.style.display = 'none';
    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    fetchAPI('/salvavita/api/protocolli-sospesi')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                successDiv.textContent = `✓ Caricati ${data.totalRecords} record`;
                successDiv.style.display = 'block';

                if (data.data.length === 0) {
                    contentDiv.innerHTML = '<div class="empty-state"><p>Nessun record trovato</p></div>';
                } else {
                    contentDiv.innerHTML = buildProtocolliTable(data);
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

// COSTRUISCI TABELLA PROTOCOLLI
function buildProtocolliTable(data) {
    let html = `<div class="record-count">📊 Totale: ${data.totalRecords} record</div>`;
    html += `<div class="table-wrapper"><table>
        <thead>
            <tr>
                <th>Azione</th>
                <th>ENTE</th>
                <th>Seq ID</th>
                <th>AOO/Ufficio</th>
                <th>Recuperi</th>
                <th>Presa Visione</th>
                <th>Transizione</th>
                <th>Utente</th>
                <th>Data Inserimento</th>
                <th>Stato Doc</th>
                <th>Esito Doc</th>
                <th>Errore</th>
            </tr>
        </thead>
        <tbody>`;

    data.data.forEach(row => {
        const dataIns = formatDate(row.dataInserimento);
        
        // Se idTransizionePresente === 1, rendi il valore cliccabile e rosso
        let transizioneCell = '';
        if (row.idTransizionePresente === 1) {
            transizioneCell = `<span class="transizione-link" onclick="deleteProtocolliByEnte('${row.ente}')">
                ${row.idTransizionePresente}
            </span>`;
        } else {
            transizioneCell = row.idTransizionePresente || 0;
        }
        
        html += `<tr>
            <td><button class="btn-elimina" onclick="deleteProtoTemporaneo('${row.ente}', ${row.sequLongId})">🗑️ Elimina</button></td>
            <td><strong>${row.ente}</strong></td>
            <td>${row.sequLongId}</td>
            <td><small>${row.aooUfficio || '-'}</small></td>
            <td>${row.countRecuperiEjb || 0}</td>
            <td>${row.presaVisione || 0}</td>
            <td>${transizioneCell}</td>
            <td>${row.utenteCreatore || '-'}</td>
            <td>${dataIns}</td>
            <td>${row.statoDocumento || '-'}</td>
            <td>${row.esitoDocumento || '-'}</td>
            <td style="color: #f44336;">${row.errore || '-'}</td>
        </tr>`;
    });

    html += `</tbody></table></div>`;
    return html;
}

// AUTO-REFRESH FUNCTION
function refreshProtocolliSospesi() {
    loadProtocolliSospesi();
    // Opzionale: ricarica ogni 30 secondi
    // setInterval(loadProtocolliSospesi, 30000);
}

// ELIMINA PROTOCOLLI PER ENTE
function deleteProtocolliByEnte(ente) {
    // Chiedi conferma
    if (!confirm(`Sei sicuro di voler eliminare i protocolli in transizione per l'ente: ${ente}?`)) {
        return;
    }

    const loadingDiv = document.getElementById('protocolliLoading');
    const errorDiv = document.getElementById('protocolliError');

    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';

    fetchAPI(`/salvavita/api/delete-protocolli?ente=${encodeURIComponent(ente)}`, 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (data.success) {
                alert(`✅ ${data.message}`);
                // Ricarica i dati
                loadProtocolliSospesi();
            } else {
                errorDiv.style.display = 'block';
                errorDiv.innerHTML = `❌ Errore: ${data.message}`;
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = `❌ Errore nella comunicazione: ${error.message}`;
            console.error('Errore:', error);
        });
}

// ELIMINA PROTOCOLLO TEMPORANEO SINGOLO
function deleteProtoTemporaneo(ente, sequLongId) {
    const loadingDiv = document.getElementById('protocolliLoading');
    const errorDiv = document.getElementById('protocolliError');

    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';

    fetchAPI(`/salvavita/api/delete-proto-temporaneo?ente=${encodeURIComponent(ente)}&sequLongId=${sequLongId}`, 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (data.success) {
                // Mostra il modal con il riepilogo
                showTransactionModal(data.recordsAffected, data.queries, ente, sequLongId);
            } else {
                errorDiv.style.display = 'block';
                errorDiv.innerHTML = `❌ Errore: ${data.message}`;
            }
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = `❌ Errore nella comunicazione: ${error.message}`;
            console.error('Errore:', error);
        });
}

// MOSTRA IL MODAL CON IL RIEPILOGO
function showTransactionModal(records, queries, ente, sequLongId) {
    document.getElementById('modalRecords').textContent = records;
    document.getElementById('modalQueries').textContent = queries;
    document.getElementById('modalEnte').textContent = ente + ' (ID: ' + sequLongId + ')';
    
    // Salva i dati per il commit/rollback
    window.pendingTransaction = {
        ente: ente,
        sequLongId: sequLongId,
        records: records
    };
    
    // Mostra il modal
    document.getElementById('transactionModal').style.display = 'block';
}

// ESEGUI COMMIT
function doCommit() {
    fetchAPI('/salvavita/api/commit-transaction', 'POST')
        .then(data => {
            if (data.success) {
                alert(`✅ ${data.message}`);
                closeTransactionModal();
                // Ricarica i dati
                loadProtocolliSospesi();
            } else {
                alert(`❌ Errore: ${data.message}`);
            }
        })
        .catch(error => {
            alert(`❌ Errore: ${error.message}`);
        });
}

// ESEGUI ROLLBACK
function doRollback() {
    fetchAPI('/salvavita/api/rollback-transaction', 'POST')
        .then(data => {
            if (data.success) {
                alert(`✅ ${data.message}`);
                closeTransactionModal();
            } else {
                alert(`❌ Errore: ${data.message}`);
            }
        })
        .catch(error => {
            alert(`❌ Errore: ${error.message}`);
        });
}

// CHIUDI IL MODAL
function closeTransactionModal() {
    document.getElementById('transactionModal').style.display = 'none';
    window.pendingTransaction = null;
}