package facade;

import entity.Product;
import enums.CategoryEnum;
import facadeLocal.ProductFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProductFacade extends AbstractFacade implements ProductFacadeLocal {

    @Override
    public void addProduct(Product product) {
        entityManager.persist(product);
        entityManager.flush();
    }

    @Override
    public Product updateProduct(Product product) {
        entityManager.merge(product);
        entityManager.flush();
        return product;
    }

    @Override
    public void deleteProduct(Product product) {
        Product merge = entityManager.merge(product);
        entityManager.remove(merge);
    }

    @Override
    public List<Product> allProducts() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).orderBy(cb.asc(root.get("name")));
        TypedQuery<Product> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> listByCategory(CategoryEnum category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.equal(root.get("category"), category));
        TypedQuery<Product> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> lowStockProducts() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        Predicate low = cb.lessThanOrEqualTo(root.get("stockQuantity"), root.get("criticalStockLevel"));
        cq.select(root).where(low).orderBy(cb.asc(root.get("stockQuantity")));
        TypedQuery<Product> q = entityManager.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public boolean isStockCodeExists(String stockCode, Long currentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        Predicate equalCode = cb.equal(root.get("stockCode"), stockCode);
        if (currentId != null) {
            Predicate other = cb.notEqual(root.get("id"), currentId);
            cq.select(cb.count(root)).where(equalCode, other);
        } else {
            cq.select(cb.count(root)).where(equalCode);
        }
        return entityManager.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public List<Product> search(String searchText, CategoryEnum category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchText != null && !searchText.isBlank()) {
            String pattern = "%" + searchText.toLowerCase() + "%";
            Predicate adPred = cb.like(cb.lower(root.get("name")), pattern);
            Predicate kodPred = cb.like(cb.lower(root.get("stockCode")), pattern);
            predicates.add(cb.or(adPred, kodPred));
        }
        if (category != null) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        cq.select(root).orderBy(cb.asc(root.get("name")));
        return entityManager.createQuery(cq).getResultList();
    }
}