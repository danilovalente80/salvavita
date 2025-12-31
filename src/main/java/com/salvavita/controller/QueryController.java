package com.salvavita.controller;

import com.salvavita.model.ProtocolliSospesi;
import com.salvavita.service.OracleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * GET /api/health
     * Verifica la connessione al database
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            logger.info("Richiesta GET /health");
            oracleService.getConnection().close();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Connessione al database attiva");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore di connessione: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse("Database non disponibile", e.getMessage()));
        }
    }

    /**
     * POST /api/avvia-processi
     * Cancella i dati di scheduling (SENZA AUTO-COMMIT)
     * Le URL verranno lanciate solo al COMMIT
     */
    @PostMapping("/avvia-processi")
    public ResponseEntity<?> avviaProcessi() {
        try {
            logger.info("Richiesta POST /avvia-processi - Cancellazione dati (lancio URL al commit)");
            
            // Primo: Cancella i dati di scheduling
            Map<String, Object> deleteResult = oracleService.deleteSchedulingData();
            
            if (!deleteResult.containsKey("success") || !(Boolean)deleteResult.get("success")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(deleteResult);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cancellazione dati scheduling in sospeso - In attesa di Commit/Rollback");
            response.put("recordsAffected", deleteResult.get("recordsAffected"));
            response.put("queries", deleteResult.get("queries"));
            response.put("note", "Le URL verranno lanciate solo al COMMIT");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore nell'avvio dei processi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Errore nell'avvio dei processi", e.getMessage()));
        }
    }

    /**
     * POST /api/delete-protocolli
     * Cancella i protocolli in transizione per uno specifico ente
     */
    @PostMapping("/delete-protocolli")
    public ResponseEntity<?> deleteProtocolli(@RequestParam String ente) {
        try {
            logger.info("Richiesta POST /delete-protocolli per ente: {}", ente);
            oracleService.deleteProtocolliInTransizione(ente);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Protocolli in transizione eliminati per ente: " + ente);
            
            return ResponseEntity.ok(response);
            
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