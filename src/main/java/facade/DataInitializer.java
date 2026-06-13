package facade;

import entity.User;
import entity.SystemLog;
import enums.RoleEnum;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Singleton
@Startup // Uygulama deploy edildiği an (kimse tıklamadan) çalışmasını tetikler
public class DataInitializer {

    @PersistenceContext // AbstractFacade kalıtımı olmadığı için EM'yi doğrudan enjekte ediyoruz
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<User> root = cq.from(User.class);
            cq.select(cb.count(root));

            Long userCount = entityManager.createQuery(cq).getSingleResult();

            if (userCount == 0) {
                User defaultAdmin = new User();
                defaultAdmin.setFirstName("Esra");
                defaultAdmin.setLastName("Ulubaba");
                defaultAdmin.setEmail("esra@gmail.com");
                defaultAdmin.setPassword("123456");
                defaultAdmin.setRole(RoleEnum.ADMIN);

                entityManager.persist(defaultAdmin);

                String logMessage = "Sistem ilk kez başlatıldı. Otomatik kurucu ADMIN hesabı oluşturuldu: esra@gmail.com";
                entityManager.persist(new SystemLog(logMessage, null));

                System.out.println("👉 [STARTUP INIT] Varsayılan ADMIN hesabı uygulama başında başarıyla senkronize edildi.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ [STARTUP INIT ERROR] Hata: " + e.getMessage());
        }
    }
}