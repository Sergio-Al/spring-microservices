package com.market.warehouse.entity;

import com.market.warehouse.converter.ProductStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Cost must have at most 10 integer digits and 2 decimal places")
    @Column(name = "cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal cost;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Size(max = 50, message = "Brand must not exceed 50 characters")
    @Column(name = "brand", length = 50)
    private String brand;

    @Size(max = 50, message = "Supplier must not exceed 50 characters")
    @Column(name = "supplier", length = 50)
    private String supplier;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Min(value = 0, message = "Minimum stock level must be non-negative")
    @Column(name = "min_stock_level")
    private Integer minStockLevel;

    @Min(value = 0, message = "Maximum stock level must be non-negative")
    @Column(name = "max_stock_level")
    private Integer maxStockLevel;

    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Weight must have at most 8 integer digits and 2 decimal places")
    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    @Column(name = "dimensions", length = 100)
    private String dimensions;

    @Convert(converter = ProductStatusConverter.class)
    @Column(name = "status")
    private ProductStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Product() {}

    // Constructor without ID for creating new products
    public Product(String name, String description, String sku, BigDecimal price, 
                   BigDecimal cost, String category, String brand, String supplier,
                   Integer stockQuantity, Integer minStockLevel, Integer maxStockLevel,
                   BigDecimal weight, String dimensions, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.cost = cost;
        this.category = category;
        this.brand = brand;
        this.supplier = supplier;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.maxStockLevel = maxStockLevel;
        this.weight = weight;
        this.dimensions = dimensions;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", cost=" + cost +
                ", category='" + category + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", status=" + status +
                '}';
    }

    // ProductStatus enum
    public enum ProductStatus {
        ACTIVE("active"),
        INACTIVE("inactive"),
        DISCONTINUED("discontinued");

        private final String value;

        ProductStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ProductStatus fromValue(String value) {
            for (ProductStatus status : ProductStatus.values()) {
                if (status.getValue().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
}
