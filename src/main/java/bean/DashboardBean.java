package bean;

import entity.StockMovement;
import entity.Product;
import facadeLocal.StockMovementFacadeLocal;
import facadeLocal.ProductFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Product> lowStockProducts;
    private List<StockMovement> recentMovements;
    private List<Product> allProducts;

    @EJB
    private ProductFacadeLocal productFacade;

    @EJB
    private StockMovementFacadeLocal stockMovementFacade;

    public List<Product> getLowStockProducts() {
        if (lowStockProducts == null) {
            lowStockProducts = productFacade.lowStockProducts();
        }
        return lowStockProducts;
    }

    public List<StockMovement> getRecentMovements() {
        if (recentMovements == null) {
            recentMovements = stockMovementFacade.recentMovements(10);
        }
        return recentMovements;
    }

    public int getTotalProductCount() {
        if (allProducts == null) {
            allProducts = productFacade.allProducts();
        }
        return allProducts.size();
    }

    public int getLowStockCount() { // getDusukStokSayisi -> getLowStockCount
        return getLowStockProducts().size();
    }
}