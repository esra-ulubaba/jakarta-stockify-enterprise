package bean;

import entity.User;
import entity.StockMovement;
import entity.Product;
import enums.MovementTypeEnum;
import facadeLocal.StockMovementFacadeLocal;
import facadeLocal.ProductFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Named("stockMovementBean")
@ViewScoped
public class StockMovementBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private StockMovement movement;
    private List<StockMovement> movements;
    private Long selectedProductId;
    private boolean lastActionSuccess = false;

    private MovementTypeEnum filterMovementType;
    private String filterStartDateStr;
    private String filterEndDateStr;
    private boolean filterApplied = false;

    @EJB
    private StockMovementFacadeLocal stockMovementFacade;

    @EJB
    private ProductFacadeLocal productFacade;

    @Inject
    private FacesContext facesContext;

    public void saveMovement() {
        lastActionSuccess = false;
        if (selectedProductId == null) {
            addError("Lütfen bir ürün seçin.");
            return;
        }
        try {
            Product product = productFacade.findById(selectedProductId);
            if (product == null) {
                addError("Seçili ürün bulunamadı.");
                return;
            }
            User activeUser = (User) facesContext.getExternalContext()
                    .getSessionMap().get("user");

            movement.setProduct(product);
            movement.setUser(activeUser);

            stockMovementFacade.saveMovement(movement);
            movements = null;
            movement = new StockMovement();
            selectedProductId = null;
            lastActionSuccess = true;
            addSuccessMessage("Stok hareketi kaydedildi.");
        } catch (IllegalStateException ise) {
            String message = ise.getMessage();
            addError((message != null && !message.isBlank())
                    ? message
                    : "İşlem Başarısız: Stok miktarı yetersiz!");
        } catch (Exception e) {
            String message = e.getMessage();
            addError((message != null && !message.isBlank())
                    ? "Hareket kaydedilemedi: " + message
                    : "İşlem Başarısız: Stok miktarı yetersiz!");
        }
    }

    public void applyFilter() {
        boolean typeEmpty = (filterMovementType == null);
        boolean startEmpty = (filterStartDateStr == null || filterStartDateStr.isBlank());
        boolean endEmpty = (filterEndDateStr == null || filterEndDateStr.isBlank());

        if (typeEmpty && startEmpty && endEmpty) {
            this.filterApplied = false;
            addError("Lütfen filtreleme yapabilmek için en az bir kriter (Hareket Tipi veya Tarih) seçiniz.");
            return;
        }

        this.filterApplied = true;
        this.movements = null;
    }

    public void clearFilter() {
        filterMovementType = null;
        filterStartDateStr = null;
        filterEndDateStr = null;
        filterApplied = false;
        movements = null;
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public MovementTypeEnum[] getMovementTypes() {
        return MovementTypeEnum.values();
    }

    public List<Product> getAllProducts() {
        return productFacade.allProducts();
    }

    public List<StockMovement> getRecentMovements() {
        if (movements == null) {
            if (filterApplied) {
                movements = stockMovementFacade.filteredMovements(
                        filterMovementType,
                        parseDate(filterStartDateStr),
                        parseDate(filterEndDateStr)
                );
            } else {
                movements = stockMovementFacade.recentMovements(50);
            }
        }
        return movements;
    }

    public StockMovement getMovement() {
        if (movement == null) movement = new StockMovement();
        return movement;
    }

    public void exportToExcel() throws java.io.IOException {
        List<StockMovement> list = getRecentMovements();

        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF');

        sb.append("Tarih;Ürün Adı;Stok Kodu;Hareket Tipi;Miktar;Açıklama;İşlemi Yapan\n");

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (list != null) {
            for (StockMovement h : list) {
                String userName = (h.getUser() != null) ? h.getUser().getFirstName() + " " + h.getUser().getLastName() : "-";

                sb.append(h.getDate() != null ? h.getDate().format(fmt) : "").append(';');
                sb.append(escapeCsv(h.getProduct().getName())).append(';');
                sb.append(escapeCsv(h.getProduct().getStockCode())).append(';');
                sb.append(escapeCsv(h.getMovementType() != null ? h.getMovementType().getDisplayName() : "")).append(';');
                sb.append(h.getQuantity() != null ? h.getQuantity() : 0).append(';');
                sb.append(escapeCsv(h.getDescription() != null ? h.getDescription() : "-")).append(';');
                sb.append(escapeCsv(userName)).append('\n');
            }
        }

        byte[] content = sb.toString().getBytes("UTF-8");
        String fileName = "stok-hareket-raporu-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".csv";

        jakarta.faces.context.ExternalContext ec = facesContext.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("application/vnd.ms-excel; charset=UTF-8");
        ec.setResponseContentLength(content.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (java.io.OutputStream os = ec.getResponseOutputStream()) {
            os.write(content);
            os.flush();
        }
        facesContext.responseComplete();
    }
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains("\"") || value.contains(";")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value.trim();
    }

    public void setMovement(StockMovement movement) {
        this.movement = movement;
    }
    public Long getSelectedProductId() {
        return selectedProductId;
    }
    public void setSelectedProductId(Long selectedProductId) {
        this.selectedProductId = selectedProductId;
    }
    public MovementTypeEnum getFilterMovementType() {
        return filterMovementType;
    }
    public void setFilterMovementType(MovementTypeEnum filterMovementType) {
        this.filterMovementType = filterMovementType;
    }
    public String getFilterStartDateStr() {
        return filterStartDateStr;
    }
    public void setFilterStartDateStr(String filterStartDateStr) {
        this.filterStartDateStr = filterStartDateStr;
    }
    public String getFilterEndDateStr() {
        return filterEndDateStr;
    }
    public void setFilterEndDateStr(String filterEndDateStr) {
        this.filterEndDateStr = filterEndDateStr;
    }
    public boolean isFilterApplied() {
        return filterApplied;
    }

    public boolean isLastActionSuccess() {
        return lastActionSuccess;
    }

    private void addSuccessMessage(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addError(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}