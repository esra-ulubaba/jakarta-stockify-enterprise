package facade;

import entity.SystemLog;
import entity.StockMovement;
import entity.Product;
import enums.MovementTypeEnum;
import facadeLocal.StockMovementFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class StockMovementFacade extends AbstractFacade implements StockMovementFacadeLocal {

    @Override
    public void saveMovement(StockMovement movement) {
        Product product = movement.getProduct();

        if (product == null) {
            throw new IllegalStateException("İşlem Başarısız: Ürün bilgisi bulunamadı.");
        }
        if (movement.getQuantity() == null || movement.getQuantity() <= 0) {
            throw new IllegalStateException("İşlem Başarısız: Miktar geçerli bir sayı olmalıdır.");
        }

        int currentStock = (product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
        int newQuantity;

        if (movement.getMovementType() == MovementTypeEnum.GIRIS) {
            newQuantity = currentStock + movement.getQuantity();
        } else if (movement.getMovementType() == MovementTypeEnum.CIKIS) {
            if (currentStock == 0) {
                throw new IllegalStateException(
                        "İşlem Başarısız: Yetersiz Stok Miktari! '"
                                + product.getName() + "' ürününün stok miktarı sıfırdır, çıkış yapılamaz.");
            }
            newQuantity = currentStock - movement.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalStateException(
                        "İşlem Başarısız: Yetersiz Stok Miktarı! Mevcut stok: "
                                + currentStock + ", talep edilen çıkış: " + movement.getQuantity());
            }
        } else {
            newQuantity = movement.getQuantity();
        }

        product.setStockQuantity(newQuantity);
        entityManager.merge(product);
        entityManager.persist(movement);

        String username = (movement.getUser() != null)
                ? movement.getUser().getFirstName() + " " + movement.getUser().getLastName()
                + " (" + movement.getUser().getRole().name() + ")"
                : "Bilinmeyen";
        String logMessage = username + " - \"" + product.getStockCode()
                + "\" kodlu '" + product.getName() + "' ürününden "
                + movement.getQuantity() + " adet stok "
                + (movement.getMovementType().name().equals("GIRIS") ? "girişi" : "çıkışı") + " yaptı.";
        SystemLog log = new SystemLog(logMessage, movement.getUser());
        entityManager.persist(log);

        entityManager.flush();
    }

    @Override
    public List<StockMovement> movementsByProduct(Product product) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockMovement> cq = cb.createQuery(StockMovement.class);
        Root<StockMovement> root = cq.from(StockMovement.class);
        cq.select(root)
                .where(cb.equal(root.get("product"), product))
                .orderBy(cb.desc(root.get("date")));
        TypedQuery<StockMovement> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<StockMovement> recentMovements(int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockMovement> cq = cb.createQuery(StockMovement.class);
        Root<StockMovement> root = cq.from(StockMovement.class);
        cq.select(root).orderBy(cb.desc(root.get("date")));
        TypedQuery<StockMovement> q = entityManager.createQuery(cq);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<StockMovement> filteredMovements(MovementTypeEnum type, LocalDate startDate, LocalDate endDate) { // filtreliHareketler -> filteredMovements
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockMovement> cq = cb.createQuery(StockMovement.class);
        Root<StockMovement> root = cq.from(StockMovement.class);

        List<Predicate> predicates = new ArrayList<>();

        if (type != null) {
            predicates.add(cb.equal(root.get("movementType"), type));
        }
        if (startDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), start));
        }
        if (endDate != null) {
            LocalDateTime end = endDate.plusDays(1).atStartOfDay();
            predicates.add(cb.lessThan(root.get("date"), end));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        cq.orderBy(cb.desc(root.get("date")));
        return entityManager.createQuery(cq).getResultList();
    }
}