package com.market.warehouse.api;

import com.market.warehouse.bl.ProductStockBl;
import com.market.warehouse.dto.ProductDto;
import com.market.warehouse.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductApi {

    private final ProductStockBl productStockBl;

    @Autowired
    public ProductApi(ProductStockBl productStockBl) {
        this.productStockBl = productStockBl;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            Product savedProduct = productStockBl.createProduct(productDto);
            ProductDto resultDto = convertToDto(savedProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        Optional<Product> product = productStockBl.getProductById(id);
        if (product.isPresent()) {
            ProductDto productDto = convertToDto(product.get());
            return ResponseEntity.ok(productDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productStockBl.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductDto productDto) {
        try {
            Product updatedProduct = productStockBl.updateProduct(id, productDto);
            if (updatedProduct != null) {
                ProductDto resultDto = convertToDto(updatedProduct);
                return ResponseEntity.ok(resultDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productStockBl.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(@PathVariable Integer id, @RequestParam Integer newStock) {
        try {
            Product updatedProduct = productStockBl.updateStock(id, newStock);
            if (updatedProduct != null) {
                ProductDto resultDto = convertToDto(updatedProduct);
                return ResponseEntity.ok(resultDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockProducts() {
        List<Product> products = productStockBl.getLowStockProducts();
        List<ProductDto> productDtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productStockBl.getProductsByCategory(category);
        List<ProductDto> productDtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }



    /**
     * Converts a Product entity to ProductDto
     */
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setCost(product.getCost());
        dto.setSku(product.getSku());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setMinStockLevel(product.getMinStockLevel());
        dto.setMaxStockLevel(product.getMaxStockLevel());
        dto.setSupplier(product.getSupplier());
        dto.setBrand(product.getBrand());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setStatus(product.getStatus().name());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
