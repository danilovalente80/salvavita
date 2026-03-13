// ESEGUI OPERAZIONI P.S. RISERVATI
function executeRiservatiOperations() {
    // Chiedi il numero di giorni
    const giorni = prompt('Inserisci il numero di giorni (default: 30):', '30');
    
    if (giorni === null) {
        return; // Utente ha cancellato
    }
    
    // Valida che sia un numero
    if (isNaN(giorni) || parseInt(giorni) <= 0) {
        alert('❌ Inserisci un numero valido di giorni');
        return;
    }

    const loadingDiv = document.getElementById('riservatiLoading');
    const errorDiv = document.getElementById('riservatiError');
    const successDiv = document.getElementById('riservatiSuccess');

    loadingDiv.style.display = 'block';
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    fetchAPI(`/salvavita/api/insert-ps-riservati?giorni=${parseInt(giorni)}`, 'POST')
        .then(data => {
            loadingDiv.style.display = 'none';
            if (data.success) {
                // Mostra il modal con il riepilogo
                showTransactionModalRiservati(
                    data.totalRecords,
                    data.entities.length,
                    'P.S. Riservati',
                    giorni
                );
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

// MOSTRA MODAL CON RIEPILOGO P.S. RISERVATI
function showTransactionModalRiservati(records, entities, operation, giorni) {
    document.getElementById('modalRecords').textContent = records;
    document.getElementById('modalQueries').textContent = entities;
    document.getElementById('modalEnte').textContent = `${operation} (Giorni: ${giorni})`;
    
    // Salva i dati per il commit/rollback
    window.pendingTransaction = {
        type: 'riservati',
        records: records,
        giorni: giorni
    };
    
    // Mostra il modal
    document.getElementById('transactionModal').style.display = 'block';
}
