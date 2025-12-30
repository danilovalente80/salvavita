import java.sql.Connection;
import java.sql.DriverManager;

public class test {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            String url = "jdbc:oracle:thin:@(DESCRIPTION=(CONNECT_TIMEOUT=5)(TRANSPORT_CONNECT_TIMEOUT=3)(RETRY_COUNT=3)(ADDRESS_LIST=(LOAD_BALANCE=on)(FAILOVER=on)(ADDRESS=(PROTOCOL=TCP)(HOST=DBP036-scan.sogei.it)(PORT=1521)))(ADDRESS_LIST=(LOAD_BALANCE=on)(FAILOVER=on)(ADDRESS=(PROTOCOL=TCP)(HOST=DBH036-scan.sogei.it)(PORT=1521)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=DBP036_WEB_PROT)))";
            String user = "admsog_dvalente";
            String password = "Protocollo2025";
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connessione OK!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}