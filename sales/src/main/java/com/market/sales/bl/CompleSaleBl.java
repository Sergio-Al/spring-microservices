package com.market.sales.bl;

import com.market.sales.dto.SaleDto;
import com.market.sales.entity.Sale;
import com.market.sales.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompleSaleBl {

    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Creates and persists a new Sale with full integration to warehouse and accounting services
     * Implements compensating transaction pattern for rollback on failures
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
        
        if (saleDto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        // 🎯 Track compensation data for rollback
        CompensationData compensationData = new CompensationData();
        
        try {
            // 🔍 STEP 1: Validate product exists and has sufficient stock
            ProductDto product = validateProductAndStock(saleDto.getProductId(), saleDto.getQuantity());
            compensationData.setProductValidated(true);
            compensationData.setOriginalStockQuantity(product.getStockQuantity());
            
            // 🔢 STEP 2: Generate a unique sale number if not provided
            String saleNumber = saleDto.getSaleNumber();
            if (saleNumber == null || saleNumber.isEmpty()) {
                saleNumber = generateSaleNumber();
            }
            
            // 🛍️ STEP 3: Create the sale entity
            Sale sale = createSaleEntity(saleDto, saleNumber, product);
            
            // 💾 STEP 4: Persist the sale first (to get ID for references)
            Sale savedSale = saleRepository.save(sale);
            compensationData.setSaleCreated(true);
            compensationData.setSaleId(savedSale.getId());
            compensationData.setSaleNumber(savedSale.getSaleNumber());
            
            System.out.println("✅ Step 4: Sale created with ID: " + savedSale.getId());
            
            // 📦 STEP 5: Update stock in warehouse service
            updateProductStockWithCompensation(saleDto.getProductId(), saleDto.getQuantity(), compensationData);
            
            System.out.println("✅ Step 5: Stock updated successfully");
            
            // 💰 STEP 6: Register sale in accounting journal
            registerSaleInJournalWithCompensation(savedSale, compensationData);
            
            System.out.println("✅ Step 6: Journal entry created successfully");
            System.out.println("🎉 Sale transaction completed successfully: " + savedSale.getSaleNumber());
            
            return savedSale;
            
        } catch (Exception e) {
            System.err.println("❌ Error during sale process: " + e.getMessage());
            System.err.println("🔄 Starting compensation/rollback process...");
            
            // Execute compensation/rollback
            executeCompensation(compensationData);
            
            // Re-throw the exception after compensation
            throw new RuntimeException("Sale transaction failed and was rolled back: " + e.getMessage(), e);
        }
    }

    /**
     * Validates product exists and has sufficient stock via Warehouse service
     * @param productId The product ID to validate
     * @param quantity The quantity needed
     * @return ProductDto with product information
     * @throws IllegalArgumentException if product doesn't exist or insufficient stock
     */
    private ProductDto validateProductAndStock(Integer productId, Integer quantity) {
        try {
            // � Call warehouse service to get product info using service name
            String productUrl = "http://warehouse/api/products/" + productId;
            ProductDto product = restTemplate.getForObject(productUrl, ProductDto.class);
            
            if (product == null) {
                throw new IllegalArgumentException("Product with ID " + productId + " not found");
            }
            
            // ✅ Check stock availability
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException(
                    String.format("Insufficient stock. Available: %d, Requested: %d", 
                                product.getStockQuantity(), quantity)
                );
            }
            
            return product;
            
        } catch (RestClientException e) {
            throw new RuntimeException("Error communicating with warehouse service: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error validating product stock: " + e.getMessage());
        }
    }
    
    /**
     * Updates product stock after successful sale via Warehouse service
     * @param productId The product ID
     * @param quantitySold The quantity sold (reduces stock)
     */
    private void updateProductStock(Integer productId, Integer quantitySold) {
        try {
            // � Get current product to calculate new stock using service name
            String getProductUrl = "http://warehouse/api/products/" + productId;
            ProductDto currentProduct = restTemplate.getForObject(getProductUrl, ProductDto.class);
            
            if (currentProduct != null) {
                // Calculate new stock quantity
                Integer newStockQuantity = currentProduct.getStockQuantity() - quantitySold;
                
                // 📦 Update stock via warehouse service using service name
                String updateStockUrl = "http://warehouse/api/products/" + productId + "/stock?newStock=" + newStockQuantity;
                restTemplate.put(updateStockUrl, null);
                
                System.out.println("Successfully updated stock for product " + productId + 
                                 ". New stock: " + newStockQuantity);
            }
            
        } catch (Exception e) {
            // Log error but don't fail the sale - stock update can be retried
            System.err.println("Warning: Failed to update product stock for product " + productId + 
                             ": " + e.getMessage());
        }
    }
    
    /**
     * Registers sale in accounting journal via Accounting service
     * @param sale The completed sale
     */
    private void registerSaleInJournal(Sale sale) {
        try {
            // � Create journal entry for the sale using service name
            JournalEntryDto journalEntry = createJournalEntryFromSale(sale);
            String journalUrl = "http://accounting/api/accounting/journals";
            
            restTemplate.postForObject(journalUrl, journalEntry, JournalEntryDto.class);
            
            System.out.println("Successfully registered journal entry for sale " + sale.getSaleNumber());
            
        } catch (Exception e) {
            // Log error but don't fail the sale - journal entry can be retried
            System.err.println("Warning: Failed to register sale in journal for sale " + 
                             sale.getSaleNumber() + ": " + e.getMessage());
        }
    }
    
    /**
     * Creates a journal entry DTO from a sale for accounting purposes
     * @param sale The sale to create journal entry for
     * @return JournalEntryDto for accounting service
     */
    private JournalEntryDto createJournalEntryFromSale(Sale sale) {
        JournalEntryDto journal = new JournalEntryDto();
        // Create unique journal entry number that fits 20-char limit (extract date and random part)
        String saleNumber = sale.getSaleNumber(); // Format: "SALE-20250903-B30BAD"
        String datePart = saleNumber.substring(5, 13); // "20250903"
        String randomPart = saleNumber.substring(14); // "B30BAD"
        String journalNumber = "JE" + datePart + randomPart; // "JE20250903B30BAD" (16 chars)
        journal.setJournalEntryNumber(journalNumber);
        journal.setTransactionDate(sale.getSaleDate());
        journal.setPostingDate(sale.getSaleDate());
        journal.setAccountCode("4000"); // Sales Revenue account
        journal.setAccountName("Sales Revenue");
        journal.setDescription("Sale: " + sale.getSaleNumber() + " - Product ID: " + sale.getProductId());
        
        // For sales revenue, we use credit amount (increases revenue)
        journal.setDebitAmount(BigDecimal.ZERO);
        journal.setCreditAmount(sale.getFinalAmount() != null ? sale.getFinalAmount() : sale.getTotalAmount());
        
        journal.setStatus("draft");
        journal.setCreatedBy("sales-service");
        return journal;
    }
    
    /**
     * Updates product stock after successful sale via Warehouse service with compensation tracking
     * @param productId The product ID
     * @param quantitySold The quantity sold (reduces stock)
     * @param compensationData Compensation data for rollback
     */
    private void updateProductStockWithCompensation(Integer productId, Integer quantitySold, CompensationData compensationData) {
        try {
            // 📦 Get current product to calculate new stock using service name
            String getProductUrl = "http://warehouse/api/products/" + productId;
            ProductDto currentProduct = restTemplate.getForObject(getProductUrl, ProductDto.class);
            
            if (currentProduct != null) {
                // Calculate new stock quantity
                Integer newStockQuantity = currentProduct.getStockQuantity() - quantitySold;
                
                // 📦 Update stock via warehouse service using service name
                String updateStockUrl = "http://warehouse/api/products/" + productId + "/stock?newStock=" + newStockQuantity;
                restTemplate.put(updateStockUrl, null);
                
                // Track for compensation
                compensationData.setStockUpdated(true);
                compensationData.setProductId(productId);
                compensationData.setQuantityReduced(quantitySold);
                
                System.out.println("Successfully updated stock for product " + productId + 
                                 ". New stock: " + newStockQuantity);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product stock for product " + productId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Registers sale in accounting journal via Accounting service with compensation tracking
     * @param sale The completed sale
     * @param compensationData Compensation data for rollback
     */
    private void registerSaleInJournalWithCompensation(Sale sale, CompensationData compensationData) {
        try {
            // 💰 Create journal entry for the sale using service name
            JournalEntryDto journalEntry = createJournalEntryFromSale(sale);
            String journalUrl = "http://accounting/api/accounting/journals";
            
            JournalEntryDto createdEntry = restTemplate.postForObject(journalUrl, journalEntry, JournalEntryDto.class);
            
            if (createdEntry != null) {
                // Track for compensation
                compensationData.setJournalEntryCreated(true);
                compensationData.setJournalEntryId(createdEntry.getId());
                compensationData.setJournalEntryNumber(createdEntry.getJournalEntryNumber());
            }
            
            System.out.println("Successfully registered journal entry for sale " + sale.getSaleNumber());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to register sale in journal for sale " + sale.getSaleNumber() + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executes compensation/rollback operations when transaction fails
     * @param compensationData Data needed for rollback operations
     */
    private void executeCompensation(CompensationData compensationData) {
        System.out.println("🔄 Executing compensation actions...");
        
        // Rollback in reverse order (LIFO - Last In, First Out)
        
        // 💰 STEP 6 ROLLBACK: Delete journal entry if created
        if (compensationData.isJournalEntryCreated()) {
            rollbackJournalEntry(compensationData);
        }
        
        // 📦 STEP 5 ROLLBACK: Restore stock if updated
        if (compensationData.isStockUpdated()) {
            rollbackStockUpdate(compensationData);
        }
        
        // 💾 STEP 4 ROLLBACK: Delete sale record if created
        if (compensationData.isSaleCreated()) {
            rollbackSaleCreation(compensationData);
        }
        
        System.out.println("✅ Compensation completed");
    }
    
    /**
     * Rollback journal entry creation
     */
    private void rollbackJournalEntry(CompensationData compensationData) {
        try {
            if (compensationData.getJournalEntryId() != null) {
                String deleteUrl = "http://accounting/api/accounting/journals/" + compensationData.getJournalEntryId();
                restTemplate.delete(deleteUrl);
                System.out.println("🔄 Rolled back journal entry: " + compensationData.getJournalEntryNumber());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to rollback journal entry: " + e.getMessage());
        }
    }
    
    /**
     * Rollback stock update
     */
    private void rollbackStockUpdate(CompensationData compensationData) {
        try {
            if (compensationData.getProductId() != null && compensationData.getOriginalStockQuantity() != null) {
                String restoreStockUrl = "http://warehouse/api/products/" + compensationData.getProductId() + 
                                        "/stock?newStock=" + compensationData.getOriginalStockQuantity();
                restTemplate.put(restoreStockUrl, null);
                System.out.println("🔄 Rolled back stock for product " + compensationData.getProductId() + 
                                 " to original quantity: " + compensationData.getOriginalStockQuantity());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to rollback stock update: " + e.getMessage());
        }
    }
    
    /**
     * Rollback sale creation
     */
    private void rollbackSaleCreation(CompensationData compensationData) {
        try {
            if (compensationData.getSaleId() != null) {
                saleRepository.deleteById(compensationData.getSaleId());
                System.out.println("🔄 Rolled back sale creation: " + compensationData.getSaleNumber());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to rollback sale creation: " + e.getMessage());
        }
    }
    
    /**
     * Generates a unique sale number
     * @return Generated sale number
     */
    private String generateSaleNumber() {
        return "SALE-" + LocalDate.now().toString().replace("-", "") + "-" + 
               UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    /**
     * Creates a Sale entity from SaleDto and product information
     * @param saleDto The sale DTO
     * @param saleNumber Generated sale number
     * @param product Product information from warehouse
     * @return Created Sale entity
     */
    private Sale createSaleEntity(SaleDto saleDto, String saleNumber, ProductDto product) {
        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        sale.setProductId(saleDto.getProductId());
        sale.setQuantity(saleDto.getQuantity());
        
        // Use unit price from DTO or product price as fallback
        BigDecimal unitPrice = saleDto.getUnitPrice() != null ? saleDto.getUnitPrice() : product.getPrice();
        sale.setUnitPrice(unitPrice);
        
        // Calculate amounts
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(saleDto.getQuantity()));
        sale.setTotalAmount(totalAmount);
        
        // Handle discount calculations
        BigDecimal discountPercentage = saleDto.getDiscountPercentage() != null ? 
                                      saleDto.getDiscountPercentage() : BigDecimal.ZERO;
        BigDecimal discountAmount = totalAmount.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        
        sale.setDiscountPercentage(discountPercentage);
        sale.setDiscountAmount(discountAmount);
        sale.setFinalAmount(finalAmount);
        
        // Set other fields
        sale.setSaleDate(saleDto.getSaleDate() != null ? saleDto.getSaleDate() : LocalDate.now());
        sale.setCustomerId(saleDto.getCustomerId());
        sale.setCustomerName(saleDto.getCustomerName());
        sale.setSalesperson(saleDto.getSalesperson());
        sale.setPaymentMethod(saleDto.getPaymentMethod());
        sale.setPaymentStatus(saleDto.getPaymentStatus() != null ? saleDto.getPaymentStatus() : "pending");
        sale.setNotes(saleDto.getNotes());

        return sale;
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
}

// Helper DTOs for service communication

/**
 * DTO for Product information from Warehouse service
 */
class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;
    private String sku;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String supplier;
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    private String status;
    
    // Default constructor
    public ProductDto() {}
    
    // Getters and setters
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
}

/**
 * DTO for Journal Entry communication with Accounting service
 */
class JournalEntryDto {
    private Integer id;
    private String journalEntryNumber;
    private LocalDate transactionDate;
    private LocalDate postingDate;
    private String accountCode;
    private String accountName;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String description;
    private String status;
    private String createdBy;
    
    // Default constructor
    public JournalEntryDto() {}
    
    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getJournalEntryNumber() { return journalEntryNumber; }
    public void setJournalEntryNumber(String journalEntryNumber) { this.journalEntryNumber = journalEntryNumber; }
    
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public LocalDate getPostingDate() { return postingDate; }
    public void setPostingDate(LocalDate postingDate) { this.postingDate = postingDate; }
    
    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    
    public BigDecimal getDebitAmount() { return debitAmount; }
    public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }
    
    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

/**
 * Data class to track compensation/rollback operations
 */
class CompensationData {
    // Step tracking
    private boolean productValidated = false;
    private boolean saleCreated = false;
    private boolean stockUpdated = false;
    private boolean journalEntryCreated = false;
    
    // Data for rollback
    private Integer originalStockQuantity;
    private Long saleId;
    private String saleNumber;
    private Integer productId;
    private Integer quantityReduced;
    private Integer journalEntryId;
    private String journalEntryNumber;
    
    // Default constructor
    public CompensationData() {}
    
    // Getters and setters
    public boolean isProductValidated() { return productValidated; }
    public void setProductValidated(boolean productValidated) { this.productValidated = productValidated; }
    
    public boolean isSaleCreated() { return saleCreated; }
    public void setSaleCreated(boolean saleCreated) { this.saleCreated = saleCreated; }
    
    public boolean isStockUpdated() { return stockUpdated; }
    public void setStockUpdated(boolean stockUpdated) { this.stockUpdated = stockUpdated; }
    
    public boolean isJournalEntryCreated() { return journalEntryCreated; }
    public void setJournalEntryCreated(boolean journalEntryCreated) { this.journalEntryCreated = journalEntryCreated; }
    
    public Integer getOriginalStockQuantity() { return originalStockQuantity; }
    public void setOriginalStockQuantity(Integer originalStockQuantity) { this.originalStockQuantity = originalStockQuantity; }
    
    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    
    public String getSaleNumber() { return saleNumber; }
    public void setSaleNumber(String saleNumber) { this.saleNumber = saleNumber; }
    
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public Integer getQuantityReduced() { return quantityReduced; }
    public void setQuantityReduced(Integer quantityReduced) { this.quantityReduced = quantityReduced; }
    
    public Integer getJournalEntryId() { return journalEntryId; }
    public void setJournalEntryId(Integer journalEntryId) { this.journalEntryId = journalEntryId; }
    
    public String getJournalEntryNumber() { return journalEntryNumber; }
    public void setJournalEntryNumber(String journalEntryNumber) { this.journalEntryNumber = journalEntryNumber; }
}
