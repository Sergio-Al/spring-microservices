package com.market.sales.bl;

import com.market.sales.dto.SaleDto;
import com.market.sales.entity.Sale;
import com.market.sales.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompleSaleBl {

    @Autowired
    private SaleRepository saleRepository;

    /**
     * Creates a new Sale based on SaleDto information
     * @param saleDto The sale information to create a sale for
     * @return The created Sale entity
     */
    @Transactional
    public Sale createAndSaveSale(SaleDto saleDto) {
        if (saleDto == null) {
            throw new IllegalArgumentException("SaleDto cannot be null");
        }
        
        if (saleDto.getQuantity() == null || saleDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        // Generate a unique sale number if not provided
        String saleNumber = saleDto.getSaleNumber();
        if (saleNumber == null || saleNumber.isEmpty()) {
            saleNumber = "SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        
        // Create the sale
        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        sale.setProductId(saleDto.getProductId());
        sale.setQuantity(saleDto.getQuantity());
        sale.setUnitPrice(saleDto.getUnitPrice());
        
        // Calculate total amount
        if (saleDto.getUnitPrice() != null && saleDto.getQuantity() != null) {
            BigDecimal totalAmount = saleDto.getUnitPrice().multiply(BigDecimal.valueOf(saleDto.getQuantity()));
            sale.setTotalAmount(totalAmount);
        }
        
        // Set optional fields
        sale.setDiscountPercentage(saleDto.getDiscountPercentage());
        sale.setDiscountAmount(saleDto.getDiscountAmount());
        sale.setSaleDate(saleDto.getSaleDate() != null ? saleDto.getSaleDate() : LocalDate.now());
        sale.setCustomerId(saleDto.getCustomerId());
        sale.setCustomerName(saleDto.getCustomerName());
        sale.setSalesperson(saleDto.getSalesperson());
        sale.setPaymentMethod(saleDto.getPaymentMethod());
        sale.setPaymentStatus(saleDto.getPaymentStatus() != null ? saleDto.getPaymentStatus() : "pending");
        sale.setNotes(saleDto.getNotes());

        // Persist the sale
        return saleRepository.save(sale);
    }

    /**
     * Finds a sale by its ID
     * @param id The sale ID
     * @return The Sale entity if found, null otherwise
     */
    public Sale getSaleById(Long id) {
        Optional<Sale> saleOpt = saleRepository.findById(id);
        return saleOpt.orElse(null);
    }

    /**
     * Finds a sale by its sale number
     * @param saleNumber The unique sale number
     * @return The Sale entity if found, null otherwise
     */
    public Sale findBySaleNumber(String saleNumber) {
        return saleRepository.findBySaleNumber(saleNumber).orElse(null);
    }

    /**
     * Gets all sales
     * @return List of all sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Gets sales by customer ID
     * @param customerId The customer ID
     * @return List of sales for the customer
     */
    public List<Sale> getSalesByCustomerId(Integer customerId) {
        return saleRepository.findByCustomerId(customerId);
    }

    /**
     * Gets sales by product ID
     * @param productId The product ID
     * @return List of sales for the product
     */
    public List<Sale> getSalesByProductId(Integer productId) {
        return saleRepository.findByProductId(productId);
    }

    // TODO: Integration with other microservices
    // The following methods would require integration with warehouse and accounting services
    // They are commented out as they have dependencies to other modules
    
    /*
    // Integration with Warehouse Service - would require RestTemplate/WebClient
    private void validateProductAndStock(Integer productId, Integer quantity) {
        // Call warehouse service to validate product exists and has sufficient stock
        // RestTemplate or WebClient call to warehouse microservice
    }
    
    // Integration with Accounting Service - would require RestTemplate/WebClient  
    private void registerSaleInJournal(Sale sale) {
        // Call accounting service to register journal entries
        // RestTemplate or WebClient call to accounting microservice
    }
    */
}
