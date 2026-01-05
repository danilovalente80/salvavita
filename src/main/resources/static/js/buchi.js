/**
 * buchi.js - Logica specifica per il pannello Buchi di Protocollo
 */

// CARICA BUCHI DI PROTOCOLLO
function loadBuchiProtocollo() {
    const contentDiv = document.getElementById('buchiContent');
    const loadingDiv = document.getElementById('buchiLoading');
    const errorDiv = document.getElementById('buchiError');
    const successDiv = document.getElementById('buchiSuccess');

    contentDiv.style.display = 'none';
    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    fetchAPI('/salvavita/api/buchi-protocollo')
        .then(data => {
            loadingDiv.style.display = 'none';

            if (data.success) {
                successDiv.textContent = `✓ Caricati ${data.totalRecords} record`;
                successDiv.style.display = 'block';

                if (data.data.length === 0) {
                    contentDiv.innerHTML = '<div class="empty-state"><p>Nessun record trovato</p></div>';
                } else {
                    contentDiv.innerHTML = buildBuchiTable(data);
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

// COSTRUISCI TABELLA BUCHI
function buildBuchiTable(data) {
    let html = `<div class="record-count">📊 Totale: ${data.totalRecords} record</div>`;
    html += `<div class="table-wrapper"><table>
        <thead>
            <tr>
                <th>ENTE</th>
                <th>Count</th>
                <th>Errore</th>
                <th>Min ID</th>
                <th>Numero Protocollo</th>
                <th>ID AOO</th>
                <th>Codice AOO</th>
                <th>Min Data Ins</th>
                <th>Max Data Ins</th>
            </tr>
        </thead>
        <tbody>`;

    data.data.forEach(row => {
        const minDataIns = formatDate(row.minDataIns);
        const maxDataIns = formatDate(row.maxDataIns);

        html += `<tr>
            <td><strong>${row.ente}</strong></td>
            <td>${row.count}</td>
            <td><small style="color: #f44336;">${row.errore || '-'}</small></td>
            <td>${row.minId || '-'}</td>
            <td>${row.numeroProtocollo || '-'}</td>
            <td>${row.idAoo || '-'}</td>
            <td>${row.codiceAoo || '-'}</td>
            <td>${minDataIns}</td>
            <td>${maxDataIns}</td>
        </tr>`;
    });

    html += `</tbody></table></div>`;
    return html;
}
