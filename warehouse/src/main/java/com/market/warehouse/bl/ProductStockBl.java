package com.market.warehouse.bl;

import com.market.warehouse.dto.ProductDto;
import com.market.warehouse.entity.Product;
import com.market.warehouse.entity.Product.ProductStatus;
import com.market.warehouse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductStockBl {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCost(productDto.getCost());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setMinStockLevel(productDto.getMinStockLevel());
        product.setMaxStockLevel(productDto.getMaxStockLevel());
        product.setCategory(productDto.getCategory());
        product.setSupplier(productDto.getSupplier());
        product.setSku(productDto.getSku());
        product.setBrand(productDto.getBrand());
        product.setWeight(productDto.getWeight());
        product.setDimensions(productDto.getDimensions());
        product.setStatus(ProductStatus.valueOf(productDto.getStatus()));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Integer id, ProductDto productDto) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setCost(productDto.getCost());
            product.setStockQuantity(productDto.getStockQuantity());
            product.setMinStockLevel(productDto.getMinStockLevel());
            product.setMaxStockLevel(productDto.getMaxStockLevel());
            product.setCategory(productDto.getCategory());
            product.setSupplier(productDto.getSupplier());
            product.setSku(productDto.getSku());
            product.setBrand(productDto.getBrand());
            product.setWeight(productDto.getWeight());
            product.setDimensions(productDto.getDimensions());
            product.setStatus(ProductStatus.valueOf(productDto.getStatus()));
            product.setUpdatedAt(LocalDateTime.now());
            
            return productRepository.save(product);
        }
        return null;
    }

    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Product updateStock(Integer productId, Integer newQuantity) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setStockQuantity(newQuantity);
            product.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        }
        return null;
    }

    public Product increaseStock(Integer productId, Integer quantity) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setStockQuantity(product.getStockQuantity() + quantity);
            product.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        }
        return null;
    }

    public Product decreaseStock(Integer productId, Integer quantity) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            if (product.getStockQuantity() >= quantity) {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                product.setUpdatedAt(LocalDateTime.now());
                return productRepository.save(product);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findProductsWithLowStock();
    }

    @Transactional(readOnly = true)
    public boolean isProductInStock(Integer productId, Integer requiredQuantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStockQuantity() >= requiredQuantity;
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsBySupplier(String supplier) {
        return productRepository.findBySupplier(supplier);
    }
}
