package com.salvavita.controller;

import com.salvavita.model.ProtocolliSospesi;
import com.salvavita.service.OracleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private OracleService oracleService;

    /**
     * GET /api/protocolli-sospesi
     * Restituisce lista di protocolli sospesi
     */
    @GetMapping("/protocolli-sospesi")
    public ResponseEntity<?> getProtocolliSospesi() {
        try {
            logger.info("Richiesta GET /protocolli-sospesi");
            List<ProtocolliSospesi> data = oracleService.getProtocolliSospesi();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRecords", data.size());
            response.put("data", data);
            
            logger.info("Risposta: {} record trovati", data.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * GET /api/scheduled-tasks
     * Restituisce lista di task schedulati con prossimo run
     */
    @GetMapping("/scheduled-tasks")
    public ResponseEntity<?> getScheduledTasks() {
        try {
            logger.info("Richiesta GET /scheduled-tasks");
            List<Map<String, Object>> data = oracleService.getScheduledTasks();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRecords", data.size());
            response.put("data", data);
            
            logger.info("Risposta: {} record trovati", data.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * GET /api/buchi-protocollo
     * Restituisce lista di buchi di protocollo
     */
    @GetMapping("/buchi-protocollo")
    public ResponseEntity<?> getBuchiProtocollo() {
        try {
            logger.info("Richiesta GET /buchi-protocollo");
            List<com.salvavita.model.BuchiProtocollo> data = oracleService.getBuchiProtocollo();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRecords", data.size());
            response.put("data", data);

            logger.info("Risposta: {} record trovati", data.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * GET /api/controllo-processi
     * Restituisce TUTTE le query di controllo processi con UNA SOLA connessione
     * (risolve ORA-02391: exceeded simultaneous SESSIONS_PER_USER limit)
     */
    @GetMapping("/controllo-processi")
    public ResponseEntity<?> getControlloProcessi() {
        try {
            logger.info("Richiesta GET /controllo-processi (endpoint unificato)");
            Map<String, Object> result = oracleService.getControlloProcessiCompleto();

            logger.info("Risposta: controllo processi completato");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione delle query", e.getMessage()));
        }
    }

    /**
     * GET /api/allineamenti-processi
     * Restituisce controllo allineamenti processi
     */
    @GetMapping("/allineamenti-processi")
    public ResponseEntity<?> getAllineamentiProcessi() {
        try {
            logger.info("Richiesta GET /allineamenti-processi");
            List<com.salvavita.model.AllineamentoProcesso> data = oracleService.getAllineamentiProcessi();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRecords", data.size());
            response.put("data", data);

            logger.info("Risposta: {} record trovati", data.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * GET /api/demone-mail-sender/sogei
     * Restituisce stato demone mail sender SOGEI
     */
    @GetMapping("/demone-mail-sender/sogei")
    public ResponseEntity<?> getDemoneMailSenderSogei() {
        try {
            logger.info("Richiesta GET /demone-mail-sender/sogei");
            com.salvavita.model.DemoneMailSender data = oracleService.getDemoneMailSenderSogei();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * GET /api/demone-mail-sender/entrate
     * Restituisce stato demone mail sender ENTRATE
     */
    @GetMapping("/demone-mail-sender/entrate")
    public ResponseEntity<?> getDemoneMailSenderEntrate() {
        try {
            logger.info("Richiesta GET /demone-mail-sender/entrate");
            com.salvavita.model.DemoneMailSender data = oracleService.getDemoneMailSenderEntrate();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Errore nella richiesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione della query", e.getMessage()));
        }
    }

    /**
     * POST /api/riavvia-demone-mail-sender
     * Riavvia il demone mail sender per uno schema specifico
     */
    @PostMapping("/riavvia-demone-mail-sender")
    public ResponseEntity<?> riavviaDemoneMailSender(@RequestParam String schema) {
        try {
            logger.info("Richiesta POST /riavvia-demone-mail-sender per schema: {}", schema);

            // Validazione schema
            if (!schema.equals("sogei_asp") && !schema.equals("entr_asp")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Schema non valido", "Schema deve essere 'sogei_asp' o 'entr_asp'"));
            }

            Map<String, Object> result = oracleService.riavviaDemoneMailSender(schema);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Errore nel riavvio del demone: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nel riavvio", e.getMessage()));
        }
    }

    /**
     * GET /api/health
     * Verifica la connessione al database
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            logger.info("=== HEALTH CHECK ===");
            logger.info("Richiesta GET /health");
            logger.info("Tentativo di connessione al database...");
            
            Connection conn = oracleService.getConnection();
            logger.info("✅ Connessione ottenuta!");
            logger.info("AutoCommit: {}", conn.getAutoCommit());
            logger.info("IsClosed: {}", conn.isClosed());
            
            // Test query
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
                if (rs.next()) {
                    logger.info("✅ Query test SELECT 1 FROM DUAL: SUCCESS");
                }
            }
            
            conn.close();
            logger.info("✅ Connessione chiusa correttamente");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Connessione al database attiva");
            response.put("timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            logger.info("=== HEALTH CHECK COMPLETATO ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ ERRORE DI CONNESSIONE NEL HEALTH CHECK", e);
            logger.error("Messaggio errore: {}", e.getMessage());
            logger.error("Classe eccezione: {}", e.getClass().getName());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "FAIL");
            errorResponse.put("error", e.getClass().getName());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }
    }

    /**
     * POST /api/avvia-processi
     * Cancella i dati di scheduling e lancia le URL
     */
    @PostMapping("/avvia-processi")
    public ResponseEntity<?> avviaProcessi() {
        try {
            logger.info("Richiesta POST /avvia-processi - Cancellazione dati e lancio URL");
            
            // Primo: Cancella i dati di scheduling
            Map<String, Object> deleteResult = oracleService.deleteSchedulingData();
            
            if (!deleteResult.containsKey("success") || !(Boolean)deleteResult.get("success")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(deleteResult);
            }
            
            // Secondo: Esegui il COMMIT della transazione
            Map<String, Object> commitResult = oracleService.commitTransaction();
            
            // Terzo: Lancia le URL
            oracleService.launchTaskUrls();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cancellazione dati scheduling completata - URL lanciate in background");
            response.put("recordsAffected", deleteResult.get("recordsAffected"));
            response.put("queries", deleteResult.get("queries"));
            response.put("commitResult", commitResult);
            response.put("note", "Le 7 URL sono state lanciate in background con delay di 5 secondi");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore nell'avvio dei processi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'avvio dei processi", e.getMessage()));
        }
    }
    /**
     * POST /api/delete-protocolli
     * Cancella i protocolli in transizione per uno specifico ente (SENZA AUTO-COMMIT)
     */
    @PostMapping("/delete-protocolli")
    public ResponseEntity<?> deleteProtocolli(@RequestParam String ente) {
        try {
            logger.info("Richiesta POST /delete-protocolli per ente: {}", ente);
            Map<String, Object> result = oracleService.deleteProtocolliInTransizione(ente);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Errore nell'eliminazione dei protocolli: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'eliminazione", e.getMessage()));
        }
    }

    /**
     * POST /api/delete-proto-temporaneo
     * Cancella un protocollo temporaneo e i suoi dati correlati (SENZA AUTO-COMMIT)
     */
    @PostMapping("/delete-proto-temporaneo")
    public ResponseEntity<?> deleteProtoTemporaneo(@RequestParam String ente, @RequestParam Long sequLongId) {
        try {
            logger.info("Richiesta POST /delete-proto-temporaneo per ente: {}, sequLongId: {}", ente, sequLongId);
            Map<String, Object> result = oracleService.deleteProtoTemporaneo(ente, sequLongId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore nell'eliminazione del protocollo temporaneo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'eliminazione", e.getMessage()));
        }
    }

    /**
     * POST /api/execute-various-operations
     * Esegue operazioni INSERT/UPDATE/DELETE varie senza autocommit
     */
    @PostMapping("/execute-various-operations")
    public ResponseEntity<?> executeVariousOperations() {
        try {
            logger.info("Richiesta POST /execute-various-operations");
            Map<String, Object> result = oracleService.executeVariousOperations();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore nell'esecuzione delle operazioni varie: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'esecuzione", e.getMessage()));
        }
    }

    
    /**
     * POST /api/insert-ps-riservati
     * Inserisce i dati P.S. Riservati per tutti gli enti (SENZA AUTO-COMMIT)
     */
    @PostMapping("/insert-ps-riservati")
    public ResponseEntity<?> insertPsRiservati(@RequestParam int giorni) {
        try {
            logger.info("Richiesta POST /insert-ps-riservati - Giorni: {}", giorni);
            Map<String, Object> result = oracleService.insertPsRiservatiOperations(giorni);
            if ((Boolean)result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(result);
            }
        } catch (Exception e) {
            logger.error("Errore nell'inserimento P.S. Riservati: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'inserimento", e.getMessage()));
        }
    }

    /**
     * POST /api/commit-transaction
     * Esegui il commit di tutte le operazioni in sospeso
     */
    @PostMapping("/commit-transaction")
    public ResponseEntity<?> commitTransaction() {
        try {
            logger.info("Richiesta POST /commit-transaction");
            Map<String, Object> result = oracleService.commitTransaction();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore nel commit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nel commit", e.getMessage()));
        }
    }

    /**
     * POST /api/rollback-transaction
     * Esegui il rollback di tutte le operazioni in sospeso
     */
    @PostMapping("/rollback-transaction")
    public ResponseEntity<?> rollbackTransaction() {
        try {
            logger.info("Richiesta POST /rollback-transaction");
            Map<String, Object> result = oracleService.rollbackTransaction();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore nel rollback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nel rollback", e.getMessage()));
        }
    }

    /**
     * Classe per le risposte di errore - SENZA LOMBOK
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ErrorResponse{" +
                    "error='" + error + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}