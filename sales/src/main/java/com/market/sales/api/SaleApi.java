package com.market.sales.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.market.sales.bl.CompleSaleBl;
import com.market.sales.dto.SaleDto;
import com.market.sales.entity.Sale;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleApi {

    private CompleSaleBl compleSaleBl;

    @Autowired
    public SaleApi(CompleSaleBl compleSaleBl) {
        this.compleSaleBl = compleSaleBl;
    }

    @PostMapping
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleDto saleDto) {
        try {
            // Create and save the sale using the business logic
            Sale savedSale = compleSaleBl.createAndSaveSale(saleDto);

            // Convert Sale entity to SaleDto
            SaleDto resultDto = convertToDto(savedSale);

            return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);

        } catch (IllegalArgumentException e) {
            // Handle business logic validation errors
            ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            // Handle transaction failures with compensation
            ErrorResponse error = new ErrorResponse("TRANSACTION_FAILED", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            // Handle unexpected errors
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getSaleById(@PathVariable Long id) {
        Sale sale = compleSaleBl.getSaleById(id);
        if (sale != null) {
            SaleDto saleDto = convertToDto(sale);
            return ResponseEntity.ok(saleDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> getAllSales() {
        List<Sale> sales = compleSaleBl.getAllSales();
        List<SaleDto> saleDtos = sales.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(saleDtos);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleDto>> getSalesByCustomer(@PathVariable Integer customerId) {
        List<Sale> sales = compleSaleBl.getSalesByCustomerId(customerId);
        List<SaleDto> saleDtos = sales.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(saleDtos);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleDto>> getSalesByProduct(@PathVariable Integer productId) {
        List<Sale> sales = compleSaleBl.getSalesByProductId(productId);
        List<SaleDto> saleDtos = sales.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(saleDtos);
    }

    /**
     * Converts a Sale entity to SaleDto
     * @param sale The Sale entity to convert
     * @return SaleDto with all the sale information
     */
    private SaleDto convertToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setSaleNumber(sale.getSaleNumber());
        dto.setProductId(sale.getProductId());
        dto.setQuantity(sale.getQuantity());
        dto.setUnitPrice(sale.getUnitPrice());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setDiscountPercentage(sale.getDiscountPercentage());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setFinalAmount(sale.getFinalAmount());
        dto.setSaleDate(sale.getSaleDate());
        dto.setCustomerId(sale.getCustomerId());
        dto.setCustomerName(sale.getCustomerName());
        dto.setSalesperson(sale.getSalesperson());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setPaymentStatus(sale.getPaymentStatus());
        dto.setNotes(sale.getNotes());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setUpdatedAt(sale.getUpdatedAt());
        return dto;
    }
}

/**
 * Error response class for API error handling
 */
class ErrorResponse {
    private String error;
    private String message;
    private long timestamp;
    
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
