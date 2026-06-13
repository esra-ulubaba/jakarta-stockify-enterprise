package facade;

import entity.User;
import entity.SystemLog;
import facadeLocal.SystemLogFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class SystemLogFacade extends AbstractFacade implements SystemLogFacadeLocal {

    @Override
    public void saveLog(String message, User user) {
        SystemLog log = new SystemLog(message, user);
        entityManager.persist(log);
        entityManager.flush();
    }

    @Override
    public List<SystemLog> recentLogs(int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SystemLog> cq = cb.createQuery(SystemLog.class);
        Root<SystemLog> root = cq.from(SystemLog.class);
        cq.select(root).orderBy(cb.desc(root.get("timestamp")));
        TypedQuery<SystemLog> q = entityManager.createQuery(cq);
        q.setMaxResults(limit);
        return q.getResultList();
    }
}