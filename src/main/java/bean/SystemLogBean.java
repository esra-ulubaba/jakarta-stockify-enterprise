package bean;

import entity.SystemLog;
import facadeLocal.SystemLogFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("systemLogBean")
@ViewScoped
public class SystemLogBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<SystemLog> logs;

    @EJB
    private SystemLogFacadeLocal logFacade;

    public List<SystemLog> getLogs() {
        if (logs == null) {
            logs = logFacade.recentLogs(200);
        }
        return logs;
    }

    public void refresh() { // yenile -> refresh
        logs = null;
    }
}