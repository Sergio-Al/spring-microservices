package com.market.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO class for Product entity
 * Based on the product table schema
 */
public class ProductDto {

    private Integer id;

    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    private String category;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", message = "Cost must be greater than or equal to 0")
    private BigDecimal cost;
    
    private String sku;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Minimum stock level must be greater than or equal to 0")
    private Integer minStockLevel;
    
    @Min(value = 1, message = "Maximum stock level must be at least 1")
    private Integer maxStockLevel;
    
    private String supplier;
    
    private String brand;
    
    @DecimalMin(value = "0.0", message = "Weight must be greater than or equal to 0")
    private BigDecimal weight;
    
    private String dimensions;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Default constructor
    public ProductDto() {}
    
    // Constructor with required fields
    public ProductDto(String name, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", status='" + status + '\'' +
                '}';
    }
}
