# Salvavita - Dashboard Monitoraggio Sistemi

Dashboard web per il monitoraggio e controllo dei protocolli e task schedulati, sviluppato con **Spring Boot** e **Oracle Database**.

## 📋 Caratteristiche

- ✅ Query in tempo reale su database Oracle
- ✅ Interfaccia web semplice e responsive
- ✅ Monitoraggio protocolli sospesi
- ✅ Visualizzazione task schedulati
- ✅ Controllo dello stato della connessione al database
- ✅ Export dati in tabelle HTML

## 🚀 Quick Start

### Prerequisiti

- **Java 17+** installato
- **Maven 3.8+** installato
- **Oracle Database** accessibile (con credenziali configurate)

### Setup

1. **Clona il repository:**
   ```bash
   git clone https://github.com/danilovalente80/salvavita.git
   cd salvavita
   ```

2. **Configura le credenziali Oracle** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@(DESCRIPTION=...)
   spring.datasource.username=TUO_UTENTE
   spring.datasource.password=TUA_PASSWORD
   ```

3. **Compila il progetto:**
   ```bash
   mvn clean install
   ```

4. **Avvia l'applicazione:**
   ```bash
   mvn spring-boot:run
   ```

   O crea il JAR eseguibile:
   ```bash
   mvn clean package
   java -jar target/salvavita-1.0.0.jar
   ```

5. **Accedi all'applicazione:**
   - Apri il browser a: `http://localhost:8080/salvavita`

## 📚 API Endpoints

### Protocolli Sospesi
```
GET /salvavita/api/protocolli-sospesi
```
Restituisce la lista di protocolli sospesi da tutti gli schemi (SOGEI, CONSIP, DEMANIO, ACN, EQUI, AAMS, ENTRATE).

**Risposta di esempio:**
```json
{
  "success": true,
  "totalRecords": 42,
  "data": [
    {
      "ente": "ENTRATE",
      "sequLongId": 123456789,
      "countRecuperiEjb": 3,
      "presaVisione": 0,
      "idTransizionePresente": 1,
      "aooUfficio": "123-ABC-Ufficio ABC-----UFFABC-Descrizione Ufficio",
      "utenteCreatore": "UTENTE123",
      "dataInserimento": "2025-11-29T10:30:45",
      "statoDocumento": 1,
      "esitoDocumento": 0,
      "idAtmos": "atmos-ecs/...",
      "iChronicleId": "CHRONICLE123",
      "errore": null,
      "nomeDocumento": "Documento 001",
      "seqDocumento": 987654321
    }
  ]
}
```

### Task Schedulati
```
GET /salvavita/api/scheduled-tasks
```
Restituisce la lista di task schedulati con prossimo run time.

**Risposta di esempio:**
```json
{
  "success": true,
  "totalRecords": 15,
  "data": [
    {
      "name": "NOTIFICHE_EJB_ENTRATE_ACCORPATE",
      "prossimoRun": "2025-11-29T15:30:00"
    }
  ]
}
```

### Health Check
```
GET /salvavita/api/health
```
Verifica la connessione al database.

**Risposta:**
```json
{
  "status": "OK",
  "message": "Connessione al database attiva"
}
```

## 🏗️ Struttura del Progetto

```
salvavita/
├── src/main/java/com/salvavita/
│   ├── SalvavitaApplication.java       # Main class
│   ├── controller/
│   │   └── QueryController.java        # REST endpoints
│   ├── service/
│   │   └── OracleService.java          # Database operations
│   ├── model/
│   │   └── ProtocolliSospesi.java      # Data model
│   └── config/
├── src/main/resources/
│   ├── application.properties          # Configuration
│   └── static/
│       └── index.html                  # Frontend
├── pom.xml                             # Maven configuration
└── README.md                           # This file
```

## 🔧 Configurazione

Modifica `src/main/resources/application.properties`:

```properties
# Oracle Connection
spring.datasource.url=jdbc:oracle:thin:@(DESCRIPTION=...)
spring.datasource.username=TUOUTENTE
spring.datasource.password=TUAPASSWORD
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver

# Server Configuration
server.port=8080
server.servlet.context-path=/salvavita

# Logging
logging.level.root=INFO
logging.level.com.salvavita=DEBUG
```

## 📦 Build e Deploy

### Creazione JAR eseguibile
```bash
mvn clean package
```
Il JAR sarà in `target/salvavita-1.0.0.jar`

### Distribuzione
1. Copia il JAR su una macchina Windows
2. Assicurati che Java sia installato
3. Esegui: `java -jar salvavita-1.0.0.jar`
4. Accedi a `http://localhost:8080/salvavita`

## 🐛 Troubleshooting

### Errore di connessione Oracle
- Verifica le credenziali in `application.properties`
- Controlla che il database sia raggiungibile
- Verificar che il driver Oracle sia installato (ojdbc11)

### Porta già in uso
Se la porta 8080 è occupata, cambia in `application.properties`:
```properties
server.port=8081
```

### Log dettagliati
Per più dettagli, cambia il logging:
```properties
logging.level.com.salvavita=TRACE
```

## 📝 Licenza

Questo progetto è per uso interno.

## 👨‍💻 Sviluppatore

Danilo Valente

## 🚀 Prossime Feature

- [ ] Esecuzione query parametriche
- [ ] Export dati in CSV/Excel
- [ ] Scheduler automatico
- [ ] Notifiche email
- [ ] Autenticazione utenti
- [ ] Storico esecuzioni
