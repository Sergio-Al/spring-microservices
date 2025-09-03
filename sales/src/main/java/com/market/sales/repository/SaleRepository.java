package com.market.sales.repository;

import com.market.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    // Find sale by sale number (unique identifier)
    Optional<Sale> findBySaleNumber(String saleNumber);
    
    // Find sales by customer ID
    List<Sale> findByCustomerId(Integer customerId);
    
    // Find sales by customer name
    List<Sale> findByCustomerNameContainingIgnoreCase(String customerName);
    
    // Find sales by product ID
    List<Sale> findByProductId(Integer productId);
    
    // Find sales by salesperson
    List<Sale> findBySalespersonContainingIgnoreCase(String salesperson);
    
    // Find sales by payment status
    List<Sale> findByPaymentStatus(String paymentStatus);
    
    // Find sales by payment method
    List<Sale> findByPaymentMethod(String paymentMethod);
    
    // Find sales by date range
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find sales by specific date
    List<Sale> findBySaleDate(LocalDate saleDate);
    
    // Find sales with final amount greater than specified value
    @Query("SELECT s FROM Sale s WHERE s.finalAmount > :amount")
    List<Sale> findSalesWithFinalAmountGreaterThan(@Param("amount") BigDecimal amount);
    
    // Find sales with final amount between range
    @Query("SELECT s FROM Sale s WHERE s.finalAmount BETWEEN :minAmount AND :maxAmount")
    List<Sale> findSalesWithFinalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    // Get total sales amount by date range
    @Query("SELECT COALESCE(SUM(s.finalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get total sales count by date range
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Long getTotalSalesCountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find top sales by final amount (descending order)
    @Query("SELECT s FROM Sale s ORDER BY s.finalAmount DESC")
    List<Sale> findTopSalesByAmount();
    
    // Find sales by customer and date range
    @Query("SELECT s FROM Sale s WHERE s.customerId = :customerId AND s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findSalesByCustomerAndDateRange(@Param("customerId") Integer customerId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    // Find pending payments
    @Query("SELECT s FROM Sale s WHERE s.paymentStatus = 'pending'")
    List<Sale> findPendingPayments();
    
    // Find sales with discounts
    @Query("SELECT s FROM Sale s WHERE s.discountAmount > 0")
    List<Sale> findSalesWithDiscounts();
    
    // Get sales summary by payment status
    @Query("SELECT s.paymentStatus, COUNT(s), COALESCE(SUM(s.finalAmount), 0) FROM Sale s GROUP BY s.paymentStatus")
    List<Object[]> getSalesSummaryByPaymentStatus();
    
    // Get monthly sales report
    @Query("SELECT YEAR(s.saleDate), MONTH(s.saleDate), COUNT(s), COALESCE(SUM(s.finalAmount), 0) " +
           "FROM Sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(s.saleDate), MONTH(s.saleDate) " +
           "ORDER BY YEAR(s.saleDate), MONTH(s.saleDate)")
    List<Object[]> getMonthlySalesReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
