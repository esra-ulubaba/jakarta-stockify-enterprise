package entity;

import enums.CategoryEnum;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "urun")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true)
    private String stockCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer criticalStockLevel;

    @Column(name = "olusturulmaTarihi", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Product() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStockCode() {
        return stockCode;
    }
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public CategoryEnum getCategory() {
        return category;
    }
    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getCriticalStockLevel() {
        return criticalStockLevel;
    }
    public void setCriticalStockLevel(Integer criticalStockLevel) {
        this.criticalStockLevel = criticalStockLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isLowStock() {
        return stockQuantity != null && criticalStockLevel != null
                && stockQuantity <= criticalStockLevel;
    }
}
