package bean;

import entity.User;
import entity.Product;
import enums.CategoryEnum;
import facadeLocal.ProductFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named("productBean")
@ViewScoped
public class ProductBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Product product;
    private List<Product> products;
    private CategoryEnum selectedCategory;
    private String searchText;
    private boolean lastActionSuccess = false;

    @EJB
    private ProductFacadeLocal productFacade;

    @Inject
    private FacesContext facesContext;

    public void clearForm() {
        product = new Product();
        selectedCategory = null;
        searchText = null;
        products = null;
        lastActionSuccess = false;
    }

    public void saveProduct() {
        lastActionSuccess = false;
        if (productFacade.isStockCodeExists(product.getStockCode(), null)) {
            addError("Bu stok kodu zaten kullanılıyor.");
            return;
        }
        try {
            User activeUser = (User) facesContext.getExternalContext()
                    .getSessionMap().get("user");
            product.setCreatedBy(activeUser);
            productFacade.addProduct(product);
            products = null;
            product = new Product();
            lastActionSuccess = true;
            addSuccessMessage("Ürün başarıyla eklendi.");
        } catch (Exception e) {
            addError("Ürün kaydedilemedi: " + e.getMessage());
        }
    }

    public void updateProduct() {
        lastActionSuccess = false;
        if (productFacade.isStockCodeExists(product.getStockCode(), product.getId())) {
            addError("Bu stok kodu başka bir ürün tarafından kullanılıyor.");
            return;
        }
        try {
            productFacade.updateProduct(product);
            products = null;
            product = new Product();
            lastActionSuccess = true;
            addSuccessMessage("Ürün güncellendi.");
        } catch (Exception e) {
            addError("Güncelleme başarısız: " + e.getMessage());
        }
    }

    public void prepareForEdit(Product u) {
        this.product = u;
    }

    public void deleteProduct(Product u) {
        try {
            productFacade.deleteProduct(u);
            products = null;
            addSuccessMessage("Ürün silindi.");
        } catch (Exception e) {
            addError("Silme işlemi başarısız.");
        }
    }
    public void searchAndFilter() {
        lastActionSuccess = false;

        boolean inputEmpty = (searchText == null || searchText.isBlank());
        boolean categoryEmpty = (selectedCategory == null);

        if (inputEmpty && categoryEmpty) {
            addError("Lütfen arama yapabilmek için en az bir kriter (Ürün Adı/Stok Kodu veya Kategori) giriniz.");
            return;
        }
        products = null;
    }

    public void clearFilter() {
        searchText = null;
        selectedCategory = null;
        products = null;
        lastActionSuccess = false;
    }

    public void exportToExcel() throws IOException {
        List<Product> list = getProducts();

        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF');
        sb.append("Stok Kodu;Ürün Adı;Kategori;Birim Fiyat (TL);Stok Miktarı;Kritik Seviye;Durum;Oluşturma Tarihi\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        for (Product u : list) {
            sb.append(escapeCsv(u.getStockCode())).append(';');
            sb.append(escapeCsv(u.getName())).append(';');
            sb.append(escapeCsv(u.getCategory() != null ? u.getCategory().getDisplayName() : "")).append(';');
            sb.append(u.getUnitPrice() != null ? u.getUnitPrice().toPlainString() : "0").append(';');
            sb.append(u.getStockQuantity() != null ? u.getStockQuantity() : 0).append(';');
            sb.append(u.getCriticalStockLevel() != null ? u.getCriticalStockLevel() : 0).append(';');
            sb.append(u.isLowStock() ? "Düşük Stok" : "Normal").append(';');
            sb.append(u.getCreatedAt() != null ? u.getCreatedAt().format(fmt) : "").append('\n');
        }

        byte[] content = sb.toString().getBytes("UTF-8");
        String fileName = "urun-raporu-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".csv";

        ExternalContext ec = facesContext.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("application/vnd.ms-excel; charset=UTF-8");
        ec.setResponseContentLength(content.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (OutputStream os = ec.getResponseOutputStream()) {
            os.write(content);
            os.flush();
        }
        facesContext.responseComplete();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(";") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    public CategoryEnum[] getCategories() {
        return CategoryEnum.values();
    }

    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Product> getProducts() {
        if (products == null) {
            if (searchText != null && !searchText.isBlank()) {
                products = productFacade.search(searchText, selectedCategory);
            } else if (selectedCategory != null) {
                products = productFacade.listByCategory(selectedCategory);
            } else {
                products = productFacade.allProducts();
            }
        }
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public CategoryEnum getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryEnum selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public boolean isLastActionSuccess() { return lastActionSuccess; }

    private void addSuccessMessage(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addError(String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}