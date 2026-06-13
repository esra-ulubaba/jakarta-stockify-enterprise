package facadeLocal;

import entity.User;
import entity.SystemLog;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface SystemLogFacadeLocal {
    void saveLog(String message, User user); // logKaydet -> saveLog, mesaj -> message, kullanici -> user
    List<SystemLog> recentLogs(int limit); // sonLoglar -> recentLogs
}