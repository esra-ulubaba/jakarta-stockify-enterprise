package facadeLocal;

import entity.Product;
import enums.CategoryEnum;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ProductFacadeLocal {

    void addProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Product product);
    List<Product> allProducts();
    List<Product> listByCategory(CategoryEnum category);
    List<Product> lowStockProducts();
    Product findById(Long id);
    boolean isStockCodeExists(String stockCode, Long currentId);
    List<Product> search(String searchText, CategoryEnum category);
}