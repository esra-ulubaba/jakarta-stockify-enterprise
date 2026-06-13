package facadeLocal;

import entity.StockMovement;
import entity.Product;
import enums.MovementTypeEnum;
import jakarta.ejb.Local;

import java.time.LocalDate;
import java.util.List;

@Local
public interface StockMovementFacadeLocal {

    void saveMovement(StockMovement movement);

    List<StockMovement> movementsByProduct(Product product);

    List<StockMovement> recentMovements(int limit);

    List<StockMovement> filteredMovements(MovementTypeEnum type, LocalDate startDate, LocalDate endDate);
}