package com.salvavita.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Connection;
import java.util.*;

/**
 * Servizio Singleton per gestire le transazioni in sospeso
 * Mantiene la connessione attiva usando Session ID
 */
@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // Mappa di connessioni per sessione (usando Session ID come chiave)
    private static final Map<String, Connection> activeTransactions = Collections.synchronizedMap(new HashMap<>());

    /**
     * Ottieni il Session ID della richiesta corrente
     */
    private static String getSessionId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getSession().getId();
            }
        } catch (Exception e) {
            logger.warn("Non riesco a ottenere il Session ID, uso Thread ID come fallback");
        }
        return "thread_" + Thread.currentThread().getId();
    }

    /**
     * Salva una connessione per una transazione
     */
    public static void saveConnection(Connection conn) {
        String sessionId = getSessionId();
        activeTransactions.put(sessionId, conn);
        logger.info("Transazione salvata per sessione: {}", sessionId);
    }

    /**
     * Ottieni la connessione di una transazione
     */
    public static Connection getConnection() {
        String sessionId = getSessionId();
        Connection conn = activeTransactions.get(sessionId);
        logger.info("Ricerca transazione per sessione: {}, trovato: {}", sessionId, conn != null);
        return conn;
    }

    /**
     * Rimuovi la transazione
     */
    public static void removeConnection() {
        String sessionId = getSessionId();
        activeTransactions.remove(sessionId);
        logger.info("Transazione rimossa per sessione: {}", sessionId);
    }

    /**
     * Controlla se c'è una transazione attiva
     */
    public static boolean hasActiveTransaction() {
        return getConnection() != null;
    }

    /**
     * Debug: mostra tutte le transazioni attive
     */
    public static void debugTransactions() {
        logger.info("Transazioni attive: {}", activeTransactions.size());
        activeTransactions.forEach((k, v) -> {
            try {
                logger.info("  Sessione: {}, Connessione chiusa: {}", k, v.isClosed());
            } catch (Exception e) {
                logger.error("  Errore nel controllare la connessione", e);
            }
        });
    }
}