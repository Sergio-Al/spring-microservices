package com.market.warehouse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.market.warehouse.entity.Product;
import com.market.warehouse.entity.Product.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find product by name
    Optional<Product> findByName(String name);
    
    // Find product by sku
    Optional<Product> findBySku(String sku);
    
    // Find products with low stock (stock_quantity <= min_stock_level)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockLevel")
    List<Product> findProductsWithLowStock();
    
    // Find products by status
    List<Product> findByStatus(ProductStatus status);
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products by supplier
    List<Product> findBySupplier(String supplier);
}
