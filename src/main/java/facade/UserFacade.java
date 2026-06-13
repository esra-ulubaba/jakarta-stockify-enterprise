package facade;

import entity.User;
import entity.SystemLog;
import enums.RoleEnum;
import facadeLocal.UserFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class UserFacade extends AbstractFacade implements UserFacadeLocal {

    @Override
    public void createUser(User user, User modifierUser) {
        entityManager.persist(user);
        String log = "Yeni kullanıcı hesabı oluşturuldu: "
                + user.getFirstName() + " " + user.getLastName()
                + " (" + (user.getRole() != null ? user.getRole().name() : "?") + ")";
        entityManager.persist(new SystemLog(log, modifierUser));
        entityManager.flush();
    }

    @Override
    public User updateUser(User user, User modifierUser) {
        entityManager.merge(user);
        String log = "Kullanıcı güncellendi: "
                + user.getFirstName() + " " + user.getLastName()
                + " (" + (user.getRole() != null ? user.getRole().name() : "?") + ")";
        entityManager.persist(new SystemLog(log, modifierUser));
        entityManager.flush();
        return user;
    }

    @Override
    public void deleteUser(User user, User modifierUser) {
        String log = "Kullanıcı silindi: "
                + user.getFirstName() + " " + user.getLastName();
        User merge = entityManager.merge(user);

        entityManager.createQuery("UPDATE SystemLog s SET s.user = null WHERE s.user = :u")
                .setParameter("u", merge)
                .executeUpdate();
        entityManager.remove(merge);
        entityManager.persist(new SystemLog(log, modifierUser));
        entityManager.flush();
    }

    @Override
    public List<User> userList() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).orderBy(cb.asc(root.get("firstName")));
        TypedQuery<User> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public User login(String email, String password) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(
                cb.equal(root.get("email"), email),
                cb.equal(root.get("password"), password)
        );
        cq.select(root);
        TypedQuery<User> q = entityManager.createQuery(cq);
        List<User> found = q.getResultList();
        User user = found.isEmpty() ? null : found.get(0);

        if (user != null) {
            String log = user.getFirstName() + " " + user.getLastName()
                    + " (" + user.getRole().name() + ") sisteme giriş yaptı.";
            entityManager.persist(new SystemLog(log, user));
            entityManager.flush();
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public boolean isEmailExists(String email, Long currentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        Predicate equalEmail = cb.equal(root.get("email"), email);
        if (currentId != null) {
            Predicate baskasi = cb.notEqual(root.get("id"), currentId);
            cq.select(cb.count(root)).where(equalEmail, baskasi);
        } else {
            cq.select(cb.count(root)).where(equalEmail);
        }
        return entityManager.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public void register(User user) {
        user.setRole(RoleEnum.PERSONEL);
        entityManager.persist(user);
        String log = "Yeni kullanıcı kayıt oldu: "
                + user.getFirstName() + " " + user.getLastName() + " (PERSONEL)";
        entityManager.persist(new SystemLog(log, user));
        entityManager.flush();
    }
}