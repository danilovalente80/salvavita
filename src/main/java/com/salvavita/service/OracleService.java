package com.salvavita.service;

import com.salvavita.model.AllineamentoProcesso;
import com.salvavita.model.BuchiProtocollo;
import com.salvavita.model.DemoneMailSender;
import com.salvavita.model.ProtocolliSospesi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OracleService {

    private static final Logger logger = LoggerFactory.getLogger(OracleService.class);

    @Autowired
    private TransactionService transactionService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Ottiene la connessione al database Oracle
     */
    public Connection getConnection() throws Exception {
        try {
            Class.forName(dbDriver);
            logger.debug("Connessione a Oracle: {}", dbUrl);
            
            // Aggiungi timeout alla connessione
            java.util.Properties props = new java.util.Properties();
            props.setProperty("user", dbUser);
            props.setProperty("password", dbPassword);
            props.setProperty("oracle.net.CONNECT_TIMEOUT", "10000"); // 10 secondi
            props.setProperty("oracle.jdbc.ReadTimeout", "30000"); // 30 secondi
            
            Connection conn = DriverManager.getConnection(dbUrl, props);
            logger.info("Connessione effettuata con successo");
            return conn;
        } catch (Exception e) {
            logger.error("Errore nella connessione al database: {}", e.getMessage(), e);
            throw new Exception("Errore di connessione: " + e.getMessage(), e);
        }
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query PROTOCOLLI_SOSPESI
     */
    public List<ProtocolliSospesi> getProtocolliSospesi() throws Exception {
        List<ProtocolliSospesi> result = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String query = buildQueryProtocolliSospesi();
            logger.info("Esecuzione query PROTOCOLLI_SOSPESI");
            
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                ProtocolliSospesi ps = new ProtocolliSospesi();
                ps.setEnte(rs.getString("ENTE"));
                ps.setSequLongId(rs.getLong("SEQU_LONG_ID"));
                ps.setCountRecuperiEjb(rs.getInt("COUNT_RECUPERI_EJB"));
                ps.setPresaVisione(rs.getInt("PRESA_VISIONE"));
                ps.setIdTransizionePresente(rs.getInt("IDTRANSIZIONEPRESENTE"));
                ps.setAooUfficio(rs.getString("AOO_UFFICIO"));
                ps.setUtenteCreatore(rs.getString("UTENTE_CREATORE"));
                
                Timestamp ts = rs.getTimestamp("DATA_INSERIMENTO");
                if (ts != null) {
                    ps.setDataInserimento(ts.toLocalDateTime());
                }
                
                ps.setStatoDocumento(rs.getInt("STATO_DOCUMENTO"));
                ps.setEsitoDocumento(rs.getInt("ESITO_DOCUMENTO"));
                ps.setIdAtmos(rs.getString("ID_ATMOS"));
                ps.setEsitoDocumento(rs.getInt("ESITO_DOCUMENTO"));
                ps.setErrore(rs.getString("ERRORE"));
                ps.setNomeDocumento(rs.getString("NOME_DOCUMENTO"));
                ps.setSeqDocumento(rs.getLong("SEQU_LONG_ID_DOC"));
                
                result.add(ps);
            }

            logger.info("Query eseguita: {} record trovati", result.size());

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione della query PROTOCOLLI_SOSPESI: {}", e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione della query: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query SCHED_ARCIPELAGO_TASK per prossimi run
     */
    public List<Map<String, Object>> getScheduledTasks() throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String query = "SELECT aa.name, TO_DATE('01-01-1970', 'DD-MM-YYYY') + (aa.nextfiretime+3600000)/(1000*60*60*24) PROSSIMO_RUN " +
                           "FROM ejbsched_entr.sched_arcipelago_task aa " +
                           "ORDER BY 2";
            
            logger.info("Esecuzione query sched_arcipelago_task");
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", rs.getString("NAME"));
                
                Timestamp ts = rs.getTimestamp("PROSSIMO_RUN");
                if (ts != null) {
                    row.put("prossimoRun", ts.toLocalDateTime());
                } else {
                    row.put("prossimoRun", null);
                }
                
                result.add(row);
            }

            logger.info("Query eseguita: {} record trovati", result.size());

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione della query sched_arcipelago_task: {}", e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione della query: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query BUCHI DI PROTOCOLLO
     */
    public List<BuchiProtocollo> getBuchiProtocollo() throws Exception {
        List<BuchiProtocollo> result = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String query = buildQueryBuchiProtocollo();
            logger.info("Esecuzione query BUCHI DI PROTOCOLLO");

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                BuchiProtocollo bp = new BuchiProtocollo();
                bp.setEnte(rs.getString(1));
                bp.setCount(rs.getInt(2));
                bp.setErrore(rs.getString(3));
                bp.setMinId(rs.getLong(4));
                bp.setNumeroProtocollo(rs.getString(5));

                // ID_AOO può essere null
                long idAoo = rs.getLong(6);
                if (!rs.wasNull()) {
                    bp.setIdAoo(idAoo);
                }

                bp.setCodiceAoo(rs.getString(7));

                Timestamp minTs = rs.getTimestamp(8);
                if (minTs != null) {
                    bp.setMinDataIns(minTs.toLocalDateTime());
                }

                Timestamp maxTs = rs.getTimestamp(9);
                if (maxTs != null) {
                    bp.setMaxDataIns(maxTs.toLocalDateTime());
                }

                result.add(bp);
            }

            logger.info("Query eseguita: {} record trovati", result.size());

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione della query BUCHI DI PROTOCOLLO: {}", e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione della query: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue tutte le query per il controllo processi con una SINGOLA connessione
     * per evitare ORA-02391: exceeded simultaneous SESSIONS_PER_USER limit
     */
    public Map<String, Object> getControlloProcessiCompleto() throws Exception {
        Map<String, Object> result = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // UNA SOLA CONNESSIONE per tutte le query
            conn = getConnection();
            stmt = conn.createStatement();

            logger.info("Esecuzione query CONTROLLO PROCESSI COMPLETO (1 connessione)");

            // 1. Query Allineamenti
            List<AllineamentoProcesso> allineamenti = new ArrayList<>();
            String queryAllineamenti = "SELECT count(*), 'Allineamenti_in_errore' " +
                          "FROM sesamo.allineamenti al " +
                          "WHERE al.data_inizio_exe>sysdate-1 AND al.errore_exe IS NOT NULL AND al.id_demone NOT IN (9,10) " +
                          "UNION ALL " +
                          "SELECT 28-count(*), 'allineamenti_Non_Schedulati' " +
                          "FROM (SELECT DISTINCT al.id_demone, al.id_ente FROM sesamo.allineamenti al " +
                          "WHERE al.data_inizio_exe>sysdate-1 AND al.id_demone NOT IN (3,6,8,9,10))";

            rs = stmt.executeQuery(queryAllineamenti);
            while (rs.next()) {
                AllineamentoProcesso ap = new AllineamentoProcesso();
                ap.setCount(rs.getInt(1));
                ap.setTipo(rs.getString(2));
                allineamenti.add(ap);
            }
            rs.close();
            logger.info("Query allineamenti completata: {} record", allineamenti.size());

            // 2. Query Demone SOGEI
            DemoneMailSender demoneSogei = null;
            String querySogei = "SELECT 'EJBMAILSENDER SOGEI', d.codi_nome, d.is_working, d.date_start_working, d.date_stop_working " +
                          "FROM sogei_asp.d_demoni d " +
                          "WHERE codi_nome='EjbProtocolloMailSender'";

            rs = stmt.executeQuery(querySogei);
            if (rs.next()) {
                demoneSogei = new DemoneMailSender();
                demoneSogei.setEnte(rs.getString(1));
                demoneSogei.setCodiNome(rs.getString(2));
                demoneSogei.setIsWorking(rs.getInt(3));

                Timestamp tsStart = rs.getTimestamp(4);
                if (tsStart != null) {
                    demoneSogei.setDateStartWorking(tsStart.toLocalDateTime());
                }

                Timestamp tsStop = rs.getTimestamp(5);
                if (tsStop != null) {
                    demoneSogei.setDateStopWorking(tsStop.toLocalDateTime());
                }
            }
            rs.close();
            logger.info("Query demone SOGEI completata");

            // 3. Query Demone ENTRATE
            DemoneMailSender demoneEntrate = null;
            String queryEntrate = "SELECT 'EJBMAILSENDER ENTRATE', d.codi_nome, d.is_working, d.date_start_working, d.date_stop_working " +
                          "FROM entr_asp.d_demoni d " +
                          "WHERE codi_nome='EjbProtocolloMailSender'";

            rs = stmt.executeQuery(queryEntrate);
            if (rs.next()) {
                demoneEntrate = new DemoneMailSender();
                demoneEntrate.setEnte(rs.getString(1));
                demoneEntrate.setCodiNome(rs.getString(2));
                demoneEntrate.setIsWorking(rs.getInt(3));

                Timestamp tsStart = rs.getTimestamp(4);
                if (tsStart != null) {
                    demoneEntrate.setDateStartWorking(tsStart.toLocalDateTime());
                }

                Timestamp tsStop = rs.getTimestamp(5);
                if (tsStop != null) {
                    demoneEntrate.setDateStopWorking(tsStop.toLocalDateTime());
                }
            }
            rs.close();
            logger.info("Query demone ENTRATE completata");

            // Costruisci risultato
            result.put("success", true);
            result.put("allineamenti", allineamenti);
            result.put("demoneSogei", demoneSogei);
            result.put("demoneEntrate", demoneEntrate);

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione delle query CONTROLLO PROCESSI: {}", e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione delle query: " + e.getMessage(), e);
        } finally {
            // Chiudi TUTTO inclusa la connessione
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query ALLINEAMENTI PROCESSI
     */
    public List<AllineamentoProcesso> getAllineamentiProcessi() throws Exception {
        List<AllineamentoProcesso> result = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String query = "SELECT count(*), 'Allineamenti_in_errore' " +
                          "FROM sesamo.allineamenti al " +
                          "WHERE al.data_inizio_exe>sysdate-1 AND al.errore_exe IS NOT NULL AND al.id_demone NOT IN (9,10) " +
                          "UNION ALL " +
                          "SELECT 28-count(*), 'allineamenti_Non_Schedulati' " +
                          "FROM (SELECT DISTINCT al.id_demone, al.id_ente FROM sesamo.allineamenti al " +
                          "WHERE al.data_inizio_exe>sysdate-1 AND al.id_demone NOT IN (3,6,8,9,10))";

            logger.info("Esecuzione query ALLINEAMENTI PROCESSI");
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                AllineamentoProcesso ap = new AllineamentoProcesso();
                ap.setCount(rs.getInt(1));
                ap.setTipo(rs.getString(2));
                result.add(ap);
            }

            logger.info("Query eseguita: {} record trovati", result.size());

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione della query ALLINEAMENTI PROCESSI: {}", e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione della query: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query DEMONE MAIL SENDER SOGEI
     */
    public DemoneMailSender getDemoneMailSenderSogei() throws Exception {
        return getDemoneMailSender("sogei_asp", "EJBMAILSENDER SOGEI");
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue la query DEMONE MAIL SENDER ENTRATE
     */
    public DemoneMailSender getDemoneMailSenderEntrate() throws Exception {
        return getDemoneMailSender("entr_asp", "EJBMAILSENDER ENTRATE");
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Metodo helper per query demone mail sender
     */
    private DemoneMailSender getDemoneMailSender(String schema, String ente) throws Exception {
        DemoneMailSender result = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            String query = "SELECT '" + ente + "', d.codi_nome, d.is_working, d.date_start_working, d.date_stop_working " +
                          "FROM " + schema + ".d_demoni d " +
                          "WHERE codi_nome='EjbProtocolloMailSender'";

            logger.info("Esecuzione query DEMONE MAIL SENDER per {}", schema);
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                result = new DemoneMailSender();
                result.setEnte(rs.getString(1));
                result.setCodiNome(rs.getString(2));
                result.setIsWorking(rs.getInt(3));

                Timestamp tsStart = rs.getTimestamp(4);
                if (tsStart != null) {
                    result.setDateStartWorking(tsStart.toLocalDateTime());
                }

                Timestamp tsStop = rs.getTimestamp(5);
                if (tsStop != null) {
                    result.setDateStopWorking(tsStop.toLocalDateTime());
                }
            }

            logger.info("Query eseguita per {}", schema);

        } catch (Exception e) {
            logger.error("Errore nell'esecuzione della query DEMONE MAIL SENDER {}: {}", schema, e.getMessage(), e);
            throw new Exception("Errore nell'esecuzione della query: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Riavvia il demone mail sender per uno schema specifico
     */
    public Map<String, Object> riavviaDemoneMailSender(String schema) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            // Disabilita autocommit
            conn.setAutoCommit(false);

            logger.info("Riavvio demone mail sender per {} (SENZA AUTOCOMMIT)", schema);

            String updateQuery = "UPDATE " + schema + ".d_demoni d " +
                                "SET d.is_working=0 " +
                                "WHERE codi_nome='EjbProtocolloMailSender'";

            int rowsAffected = stmt.executeUpdate(updateQuery);

            logger.info("Demone mail sender riavviato per {} - {} record aggiornati - In attesa di COMMIT", schema, rowsAffected);

            // SALVA LA CONNESSIONE PER COMMIT/ROLLBACK
            TransactionService.saveConnection(conn);

            result.put("success", true);
            result.put("message", "Riavvio demone in sospeso - In attesa di Commit/Rollback");
            result.put("rowsAffected", rowsAffected);
            result.put("schema", schema);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nel riavvio del demone mail sender {}: {}", schema, e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null); // NON chiudere la connessione
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Cancella i dati dalle tabelle di scheduling (SENZA AUTO-COMMIT)
     */
    public Map<String, Object> deleteSchedulingData() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            logger.info("Inizio cancellazione dati di scheduling");

            // Disabilita autocommit
            conn.setAutoCommit(false);

            int rows1 = stmt.executeUpdate("DELETE FROM ejbsched_entr.sched_arcipelago_lmgr");
            int rows2 = stmt.executeUpdate("DELETE FROM ejbsched_entr.sched_arcipelago_lmpr");
            int rows3 = stmt.executeUpdate("DELETE FROM ejbsched_entr.sched_arcipelago_task");
            int rows4 = stmt.executeUpdate("DELETE FROM ejbsched_entr.sched_arcipelago_treg");

            int total = rows1 + rows2 + rows3 + rows4;

            logger.info("Cancellazione dati di scheduling completata - {} record interessati", total);

            // SALVA LA CONNESSIONE PER COMMIT/ROLLBACK
            TransactionService.saveConnection(conn);

            result.put("success", true);
            result.put("message", "Cancellazione dati scheduling in sospeso - In attesa di Commit/Rollback");
            result.put("recordsAffected", total);
            result.put("queries", 4);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nella cancellazione dei dati di scheduling: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null); // NON chiudere la connessione
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Cancella i protocolli in transizione per uno specifico ente (SENZA AUTOCOMMIT)
     */
    public Map<String, Object> deleteProtocolliInTransizione(String nomeEnte) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();
        int totalRecords = 0;

        try {
            com.salvavita.model.Ente ente = com.salvavita.model.Ente.fromNome(nomeEnte);
            if (ente == null) {
                throw new Exception("Ente non riconosciuto: " + nomeEnte);
            }

            String schema = ente.getSchema();
            conn = getConnection();
            stmt = conn.createStatement();

            // Disabilita autocommit
            conn.setAutoCommit(false);

            logger.info("Inizio cancellazione protocolli in transizione per ente: {} (SENZA AUTOCOMMIT)", nomeEnte);

            // Impostare lo schema corrente
            stmt.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=" + schema);

            // Query di selezione per identificare i record da eliminare
            String selectQuery = "SELECT pt.sequ_long_id FROM " + schema + ".p2_proto_temporaneo pt " +
                    "WHERE pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
                    "AND NOT EXISTS (SELECT 1 FROM " + schema + ".p2_protocollo p " +
                    "WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
                    "AND (SELECT COUNT(*) FROM " + schema + ".p2_protocollo p " +
                    "WHERE p.id_transizione=to_char(pt.sequ_long_id))>0";

            // Delete statements
            int rows1 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_protocollo_collegati pc " +
                    "WHERE pc.fk_protocollo IN (SELECT sequ_long_id FROM " + schema + ".p2_protocollo p2 " +
                    "WHERE TO_NUMBER(p2.id_transizione) IN (" + selectQuery + ") " +
                    "AND p2.numero_protocollo IS NULL AND p2.data_ins>SYSDATE-10)");

            int rows2 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_protocollo_documenti " +
                    "WHERE fk_protocollo IN (SELECT sequ_long_id FROM " + schema + ".p2_protocollo p2 " +
                    "WHERE TO_NUMBER(p2.id_transizione) IN (" + selectQuery + ") " +
                    "AND p2.numero_protocollo IS NULL AND p2.data_ins>SYSDATE-10)");

            int rows3 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_protocollo_mitt_dest " +
                    "WHERE fk_protocollo IN (SELECT sequ_long_id FROM " + schema + ".p2_protocollo p2 " +
                    "WHERE TO_NUMBER(p2.id_transizione) IN (" + selectQuery + ") " +
                    "AND p2.numero_protocollo IS NULL AND p2.data_ins>SYSDATE-10)");

            int rows4 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_chiusura_attivita_risposta " +
                    "WHERE fk_p2_proto IN (SELECT sequ_long_id FROM " + schema + ".p2_protocollo p2 " +
                    "WHERE TO_NUMBER(p2.id_transizione) IN (" + selectQuery + ") " +
                    "AND p2.numero_protocollo IS NULL AND p2.data_ins>SYSDATE-10)");

            int rows5 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_protocollo p2 " +
                    "WHERE TO_NUMBER(p2.id_transizione) IN (" + selectQuery + ") " +
                    "AND p2.numero_protocollo IS NULL AND p2.data_ins>SYSDATE-10");

            totalRecords = rows1 + rows2 + rows3 + rows4 + rows5;

            logger.info("Cancellazione protocolli in transizione per ente {} - {} record interessati - In attesa di COMMIT", nomeEnte, totalRecords);

            // SALVA LA CONNESSIONE PER COMMIT/ROLLBACK
            TransactionService.saveConnection(conn);

            result.put("success", true);
            result.put("message", "Cancellazione in sospeso - In attesa di Commit/Rollback");
            result.put("recordsAffected", totalRecords);
            result.put("queries", 5);
            result.put("ente", nomeEnte);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nella cancellazione dei protocolli: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null); // NON chiudere la connessione
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Costruisce la query PROTOCOLLI_SOSPESI con UNION di tutti gli schemi
     * NOTA: Compatibile con Java 8 (senza triple virgolette)
     */
    private String buildQueryProtocolliSospesi() {
        return "SELECT 'SOGEI' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM sogei_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM sogei_asp.d_aree_organizzative dao, sogei_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM sogei_asp.p2_proto_temporaneo pt, sogei_asp.p2_proto_tmp_documenti doc, sogei_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM sogei_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM sogei_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'CONSIP' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM consip_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM consip_asp.d_aree_organizzative dao, consip_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM consip_asp.p2_proto_temporaneo pt, consip_asp.p2_proto_tmp_documenti doc, consip_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM consip_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM consip_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'DEMANIO' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM dem_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM dem_asp.d_aree_organizzative dao, dem_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM dem_asp.p2_proto_temporaneo pt, dem_asp.p2_proto_tmp_documenti doc, dem_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM dem_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM dem_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'ACN' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM acn_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM acn_asp.d_aree_organizzative dao, acn_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM acn_asp.p2_proto_temporaneo pt, acn_asp.p2_proto_tmp_documenti doc, acn_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo(+) AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM acn_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM acn_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'EQUI' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM equi_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM equi_asp.d_aree_organizzative dao, equi_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM equi_asp.p2_proto_temporaneo pt, equi_asp.p2_proto_tmp_documenti doc, equi_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo(+) AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM equi_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM equi_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'AAMS' ENTE, pt.sequ_long_id, pt.count_recuperi_ejb, pt.presa_visione, " +
               "(SELECT count(*) FROM aams_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM aams_asp.d_aree_organizzative dao, aams_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM aams_asp.p2_proto_temporaneo pt, aams_asp.p2_proto_tmp_documenti doc, aams_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo(+) AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM aams_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM aams_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "UNION " +
               "SELECT 'ENTRATE' ENTE, pt.sequ_long_id, NVL(pt.count_recuperi_ejb,0), pt.presa_visione, " +
               "(SELECT count(*) FROM entr_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id)) IDTRANSIZIONEPRESENTE, " +
               "(SELECT dao.sequ_long_id||'-'||dao.codi_codice||'-'||dao.desc_nome||'-----'||duf.codi_ufficio||'-'||duf.desc_descrizione " +
               "FROM entr_asp.d_aree_organizzative dao, entr_asp.d_uffici duf " +
               "WHERE dao.sequ_long_id=duf.fk_aoo AND duf.codi_ufficio=pt.codice_ufficio) AOO_UFFICIO, " +
               "pt.utente_creatore, pt.data_inserimento, doc.stato_documento, doc.esito_documento, " +
               "doc.id_atmos, doc.esito_documento, a2d.errore, doc.nome_documento, doc.sequ_long_id SEQU_LONG_ID_DOC " +
               "FROM entr_asp.p2_proto_temporaneo pt, entr_asp.p2_proto_tmp_documenti doc, entr_asp.p2_callback_a2d a2d " +
               "WHERE pt.sequ_long_id=doc.fk_protocollo_temporaneo(+) AND a2d.id_richiesta(+)=doc.id_richiesta_a2d " +
               "AND pt.flag_tipo_protocollo=3 AND pt.presa_visione NOT IN (1) " +
               "AND (SELECT count(*) FROM entr_asp.p2_proto_tmp_documenti doc2 " +
               "WHERE doc2.fk_protocollo_temporaneo=pt.sequ_long_id AND doc2.esito_documento=2) = 0 " +
               "AND NOT EXISTS (SELECT 1 FROM entr_asp.p2_protocollo p WHERE p.id_transizione=to_char(pt.sequ_long_id) AND p.numero_protocollo IS NOT NULL) " +
               "AND pt.data_inserimento > TO_DATE('01/04/2026 00:00:00', 'dd/mm/yyyy hh24:mi:ss') " +
               "ORDER BY 1, 2";
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Costruisce la query BUCHI DI PROTOCOLLO con UNION di tutti gli schemi
     */
    private String buildQueryBuchiProtocollo() {
        return "SELECT 'SOGEI', count(*), substr(errore,0,200), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM sogei_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL AND p2.errore IS NOT NULL " +
               "AND p2.data_ins>to_date('04/06/2024 00:00:00','dd/mm/yyyy hh24:mi:ss') " +
               "AND (SELECT count(*) FROM sogei_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,200), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'CONSIP', count(*), substr(errore,0,200), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM consip_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL AND p2.errore IS NOT NULL " +
               "AND p2.data_ins>to_date('04/06/2024 00:00:00','dd/mm/yyyy hh24:mi:ss') " +
               "AND (SELECT count(*) FROM consip_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,200), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'DEMANIO', count(*), substr(errore,0,200), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM dem_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL AND p2.errore IS NOT NULL " +
               "AND p2.data_ins>to_date('04/06/2024 00:00:00','dd/mm/yyyy hh24:mi:ss') " +
               "AND (SELECT count(*) FROM dem_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,200), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'ACN', count(*), substr(errore,0,200), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM acn_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL " +
               "AND (SELECT count(*) FROM acn_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,200), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'AAMS', count(*), substr(errore,0,400), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM aams_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL " +
               "AND (SELECT count(*) FROM aams_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,400), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'EQUI', count(*), substr(errore,0,200), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM equi_asp.p2_protocollo p2 " +
               "WHERE p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL " +
               "AND p2.data_ins>to_date('01/01/2025 00:00:00','dd/mm/yyyy hh24:mi:ss') " +
               "AND (SELECT count(*) FROM equi_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,0,200), numero_protocollo, id_aoo, codice_aoo " +
               "UNION " +
               "SELECT 'ENTRATE', count(*), substr(errore,10,400), min(id), numero_protocollo, id_aoo, codice_aoo, min(data_ins), max(data_ins) FROM (" +
               "SELECT p2.errore, p2.sequ_long_id ID, p2.numero_protocollo, p2.id_aoo, p2.id_registro, p2.data_ins,p2.codice_aoo " +
               "FROM entr_asp.p2_protocollo p2 " +
               "WHERE p2.codice_aoo NOT IN ('DPTEST') AND p2.fk_profilo_doc_proto IS NULL AND p2.numero_protocollo IS NOT NULL " +
               "AND p2.errore IS NOT NULL AND p2.data_ins>to_date('25/11/2025 00:00:00','dd/mm/yyyy hh24:mi:ss') " +
               "AND (SELECT count(*) FROM entr_asp.d_profilo_doc_proto pdp WHERE pdp.fk_aoo=p2.id_aoo AND pdp.data_protocollo=trunc(p2.data_protocollo) AND pdp.nume_protocollo=p2.numero_protocollo AND pdp.fk_registri=p2.id_registro)=0 " +
               ") GROUP BY substr(errore,10,400), numero_protocollo, id_aoo, codice_aoo " +
               "ORDER BY 1,3,4";
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Chiude le risorse in modo sicuro
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.error("Errore nella chiusura delle risorse: {}", e.getMessage());
        }
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Disabilita la verifica SSL per chiamate HTTPS (SOLO PER DEVELOPMENT/TESTING)
     * ⚠️ ATTENZIONE: Non usare in produzione!
     */
    private static void disableSSLVerification() {
        try {
            // Crea un TrustManager che accetta tutti i certificati
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Installa il TrustManager che accetta tutti i certificati
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Crea un HostnameVerifier che accetta tutti gli hostname
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Installa il HostnameVerifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            throw new RuntimeException("Errore nella disabilitazione SSL: " + e.getMessage(), e);
        }
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Elimina un protocollo temporaneo e i suoi dati correlati (SENZA AUTO-COMMIT)
     */
    public Map<String, Object> deleteProtoTemporaneo(String nomeEnte, long sequLongId) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();

        try {
            com.salvavita.model.Ente ente = com.salvavita.model.Ente.fromNome(nomeEnte);
            if (ente == null) {
                throw new Exception("Ente non riconosciuto: " + nomeEnte);
            }

            String schema = ente.getSchema();
            conn = getConnection();
            stmt = conn.createStatement();

            logger.info("Inizio cancellazione protocollo temporaneo {} per ente: {}", sequLongId, nomeEnte);

            // Disabilita autocommit
            conn.setAutoCommit(false);

            // Impostare lo schema corrente
            stmt.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=" + schema);

            // Esegui i delete in ordine e conta i record
            int total = 0;
            
            int rows1 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_classif_all a WHERE a.fk_protocollo_temporaneo IN (" + sequLongId + ")");
            total += rows1;
            
            int rows2 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_classificazione a WHERE a.fk_protocollo_temporaneo IN (" + sequLongId + ")");
            total += rows2;
            
            int rows3 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_collegati a WHERE a.fk_proto_tmp IN (" + sequLongId + ")");
            total += rows3;
            
            int rows4 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_dettagli a WHERE a.fk_proto_tmp IN (" + sequLongId + ")");
            total += rows4;
            
            int rows5 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_documenti a WHERE a.fk_protocollo_temporaneo IN (" + sequLongId + ")");
            total += rows5;
            
            int rows6 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_tmp_mittdest a WHERE a.fk_proto_tmp IN (" + sequLongId + ")");
            total += rows6;
            
            int rows7 = stmt.executeUpdate("DELETE FROM " + schema + ".p2_proto_temporaneo a WHERE a.sequ_long_id IN (" + sequLongId + ")");
            total += rows7;

            logger.info("Cancellazione protocollo temporaneo {} - {} record interessati", sequLongId, total);

            // SALVA LA CONNESSIONE PER COMMIT/ROLLBACK (come deleteSchedulingData)
            TransactionService.saveConnection(conn);

            result.put("success", true);
            result.put("message", "Cancellazione in sospeso - In attesa di Commit/Rollback");
            result.put("recordsAffected", total);
            result.put("queries", 7);
            result.put("ente", nomeEnte);
            result.put("sequLongId", sequLongId);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nella cancellazione del protocollo temporaneo: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null); // NON chiudere la connessione
        }

        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Esegue operazioni varie INSERT/UPDATE/DELETE senza autocommit
     * Restituisce il numero di record impattati per ogni operazione
     */
    public Map<String, Object> executeVariousOperations() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> operations = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.createStatement();

            // Disabilita autocommit
            conn.setAutoCommit(false);

            logger.info("Inizio esecuzione operazioni varie (senza autocommit)");

            // Query 1: BACKUP ACN - INSERT UTENTI_UFFICI
            String query1 = "INSERT INTO sesamo.utenti_uffici_bck (id_utente,id_ufficio,data_inizio,data_fine,flg_usr_vir,flag_prot_reg,flag_prot_out,flag_default,flag_prot_in,bozza_flg) " +
                           "SELECT suu.id_utente,suu.id_ufficio,suu.data_inizio,suu.data_fine,suu.flg_usr_vir,suu.flag_prot_reg,suu.flag_prot_out,suu.flag_default,suu.flag_prot_in, " +
                           "to_number(to_char(sysdate,'yyyymmddhh24miss')) " +
                           "FROM sesamo.utenti_uffici suu, sesamo.uffici suf " +
                           "WHERE suu.id_ufficio=suf.id_ufficio AND suf.id_ente=15";

            int rows1 = stmt.executeUpdate(query1);
            Map<String, Object> op1 = new HashMap<>();
            op1.put("label", "BACKUP ACN: INSERT UTENTI_UFFICI");
            op1.put("recordsAffected", rows1);
            operations.add(op1);
            logger.info("Query 1 completata: {} record", rows1);

            // Query 2: BACKUP ACN - INSERT UTENTI_RUOLI_APP
            String query2 = "INSERT INTO sesamo.utenti_ruoli_applic_appo (id_ruolo_app,id_utente,id_ente,id_aoo,id_utente_ass,data_inizio,id_utente_fine_ass,data_fine,id_ufficio,flag_default,bozza_flg) " +
                           "SELECT id_ruolo_app,id_utente,id_ente,id_aoo,id_utente_ass,data_inizio,id_utente_fine_ass,data_fine,id_ufficio,flag_default,to_number(to_char(sysdate,'yyyymmddhh24miss')) " +
                           "FROM sesamo.utenti_ruoli_applicativi uro " +
                           "WHERE uro.id_ente=15";

            int rows2 = stmt.executeUpdate(query2);
            Map<String, Object> op2 = new HashMap<>();
            op2.put("label", "BACKUP ACN: INSERT UTENTI_RUOLI_APP");
            op2.put("recordsAffected", rows2);
            operations.add(op2);
            logger.info("Query 2 completata: {} record", rows2);

            // Query 3-9: DELETE COLLEGATI_DOC_PROTO con fk nullo per tutti gli enti
            String[][] deleteQueries = {
                {"DELETE ENTRATE: d_collegati_doc_proto", "DELETE FROM entr_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"},
                {"DELETE CONSIP: d_collegati_doc_proto", "DELETE FROM consip_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"},
                {"DELETE AAMS: d_collegati_doc_proto", "DELETE FROM aams_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"},
                {"DELETE DEMANIO: d_collegati_doc_proto", "DELETE FROM dem_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"},
                {"DELETE SOGEI: d_collegati_doc_proto", "DELETE FROM sogei_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"},
                {"DELETE EQUI: d_collegati_doc_proto", "DELETE FROM equi_asp.d_collegati_doc_proto cdp WHERE cdp.fk_profilo_doc_proto_right IN (0) OR cdp.fk_profilo_doc_proto_left IN (0)"}
            };

            int totalRecords = rows1 + rows2;

            for (String[] deleteInfo : deleteQueries) {
                String label = deleteInfo[0];
                String query = deleteInfo[1];
                try {
                    logger.info("Esecuzione DELETE: {}", label);
                    int rowsDeleted = stmt.executeUpdate(query);
                    totalRecords += rowsDeleted;
                    Map<String, Object> opDelete = new HashMap<>();
                    opDelete.put("label", label);
                    opDelete.put("recordsAffected", rowsDeleted);
                    operations.add(opDelete);
                    logger.info("✅ {} completato: {} record eliminati", label, rowsDeleted);
                } catch (Exception e) {
                    logger.error("❌ Errore in {}: {}", label, e.getMessage());
                    Map<String, Object> opDelete = new HashMap<>();
                    opDelete.put("label", label);
                    opDelete.put("recordsAffected", 0);
                    opDelete.put("error", e.getMessage());
                    operations.add(opDelete);
                }
            }

            logger.info("Tutte le operazioni completate - Totale {} record interessati", totalRecords);

            // SALVA LA CONNESSIONE PER COMMIT/ROLLBACK
            TransactionService.saveConnection(conn);

            result.put("success", true);
            result.put("message", "Operazioni in sospeso - In attesa di Commit/Rollback");
            result.put("totalRecords", totalRecords);
            result.put("operations", operations);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nell'esecuzione delle operazioni varie: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null); // NON chiudere la connessione
        }

        return result;
    }
    public Map<String, Object> commitTransaction() throws Exception {
        Map<String, Object> result = new HashMap<>();
        Connection conn = TransactionService.getConnection();
        
        try {
            if (conn == null || conn.isClosed()) {
                result.put("success", false);
                result.put("message", "Nessuna transazione in sospeso");
                return result;
            }

            logger.info("COMMIT di tutte le operazioni");
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            
            // LANCIA LE URL DOPO IL COMMIT
            TransactionService.removeConnection();

            result.put("success", true);
            result.put("message", "COMMIT eseguito con successo - Task lanciati in background");
        } catch (Exception e) {
            logger.error("Errore nel commit: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "Errore nel commit: " + e.getMessage());
            TransactionService.removeConnection();
        }
        return result;
    }
    public Map<String, Object> rollbackTransaction() throws Exception {
        Map<String, Object> result = new HashMap<>();
        Connection conn = TransactionService.getConnection();
        
        try {
            if (conn == null || conn.isClosed()) {
                result.put("success", false);
                result.put("message", "Nessuna transazione in sospeso");
                return result;
            }

            logger.info("ROLLBACK di tutte le operazioni");
            conn.rollback();
            conn.setAutoCommit(true);
            conn.close();
            TransactionService.removeConnection();

            result.put("success", true);
            result.put("message", "ROLLBACK eseguito con successo");
        } catch (Exception e) {
            logger.error("Errore nel rollback: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "Errore nel rollback: " + e.getMessage());
            TransactionService.removeConnection();
        }
        return result;
    }
            // LANCIA LE URL DOPO IL COMMIT
            launchTaskUrls();

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Lancia le URL dei task in background con delay di 5 secondi tra una e l'altra
     */

    public Map<String, Object> insertPsRiservatiOperations(int giorni) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> operations = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            // Timeout di 2 minuti (120 secondi)
            stmt.setQueryTimeout(120);
            conn.setAutoCommit(false);
            logger.info("Inizio inserimento P.S. Riservati (giorni: {}) - Query timeout: 120 secondi", giorni);

            String[][] queriesPerEnte = {
                {"P.S. Riservati DEMANIO", buildInsertPsRiservatiQuery("dem_asp", giorni)},
                {"P.S. Riservati AAMS", buildInsertPsRiservatiQuery("aams_asp", giorni)},
                {"P.S. Riservati CONSIP", buildInsertPsRiservatiQuery("consip_asp", giorni)},
                {"P.S. Riservati SOGEI", buildInsertPsRiservatiQuery("sogei_asp", giorni)},
                {"P.S. Riservati ENTRATE", buildInsertPsRiservatiQuery("entr_asp", giorni)}
            };

            int totalRecords = 0;
            for (String[] queryInfo : queriesPerEnte) {
                String label = queryInfo[0];
                String query = queryInfo[1];
                try {
                    logger.info("=== ESECUZIONE QUERY: {} ===", label);
                    logger.info("Query: {}", query);
                    int rowsInserted = stmt.executeUpdate(query);
                    totalRecords += rowsInserted;
                    Map<String, Object> op = new HashMap<>();
                    op.put("label", label);
                    op.put("recordsAffected", rowsInserted);
                    operations.add(op);
                    logger.info("✅ {} completato: {} record", label, rowsInserted);
                } catch (Exception e) {
                    logger.error("❌ Errore nell'inserimento {}: {}", label, e.getMessage(), e);
                    Map<String, Object> op = new HashMap<>();
                    op.put("label", label);
                    op.put("recordsAffected", 0);
                    op.put("error", e.getMessage());
                    operations.add(op);
                }
            }

            TransactionService.saveConnection(conn);
            result.put("success", true);
            result.put("message", "Inserimento P.S. Riservati in sospeso - In attesa di Commit/Rollback");
            result.put("totalRecords", totalRecords);
            result.put("operations", operations);
            result.put("giorni", giorni);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    logger.error("Errore nel rollback: {}", ex.getMessage());
                }
            }
            logger.error("Errore nell'inserimento P.S. Riservati: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Errore: " + e.getMessage());
        } finally {
            closeResources(null, stmt, null);
        }
        return result;
    }

    /**
    public void launchTaskUrls() {
        logger.warn("⚠️ ATTENZIONE: Verifica SSL disabilitata per chiamate HTTPS");
        disableSSLVerification();

        String[] urls = {
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=CALLBACK_FLUSSI_EJB",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=GESTIONE_DELEGHE_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_AAMS",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_ENTRATE",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=NOTIFICHE_WKF_SOGEI",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=SOSPESI_ACN",
            "https://sd20.finanze.it/arcipelago20scheduler-sched/GestioneTaskSchedulati?op=START&taskName=BUCHI_PROTOCOLLO_ACN"
        };

        logger.info("Lancio {} URL di task in background SEQUENZIALE con delay di 5 secondi", urls.length);

        new Thread(() -> {
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {
                    logger.info("🔄 Richiamando URL [{}/{}]: {}", (i+1), urls.length, url);
                    java.net.URL urlObj = new java.net.URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    int responseCode = conn.getResponseCode();
                    logger.info("✅ Risposta [{}/{}] da {}: HTTP {}", (i+1), urls.length, url, responseCode);
                    conn.disconnect();

                    if (i < urls.length - 1) {
                        logger.info("⏱️ Attesa 5 secondi prima della prossima URL...");
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    logger.error("❌ Thread interrotto durante l'attesa: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("❌ Errore nel richiamare {}: {}", url, e.getMessage());
                }
            }
            logger.info("✅ Completato lancio di tutte le {} URL", urls.length);
        }).start();
    }
     * Costruisce la query INSERT per P.S. Riservati
     * Per ENTRATE usa SYSDATE-5, per gli altri SYSDATE-60
     */
    private String buildInsertPsRiservatiQuery(String schema, int giorni) {
        // Per ENTRATE usa -5 giorni, per gli altri -60
        int daysLimit = schema.equals("entr_asp") ? 5 : 60;
        
        return "INSERT INTO " + schema + ".d_pronto_soccorso_malati " +
               "(sequ_long_id, fk_profilo_doc_proto, data_inserimento, esito, fk_aoo) " +
               "SELECT " + schema + ".s_d_pronto_soccorso_malati.NEXTVAL, " +
               "pdp.sequ_long_id, SYSDATE, 0, pdp.fk_aoo " +
               "FROM " + schema + ".d_profilo_doc_proto pdp " +
               "WHERE (pdp.flag_riservato_01=1 OR pdp.flag_presenza_dati_sensibili=1) " +
               "AND pdp.fk_ufficio_protocollo IS NOT NULL " +
               "AND pdp.data_protocollo > SYSDATE-" + daysLimit + " " +
               "AND (pdp.data_protocollo > SYSDATE-" + giorni + " " +
               "OR (SELECT COUNT(*) FROM " + schema + ".d_attivita dat " +
               "WHERE dat.fk_documento=pdp.sequ_long_id " +
               "AND dat.dttm_aggiornamento > SYSDATE-" + giorni + ") > 0)";
}
}
