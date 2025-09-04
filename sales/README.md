# 🛒 Sales Microservice - REST API Documentation

## 🏗️ **Service Overview**

The Sales microservice manages all sales transactions, customer orders, and revenue operations for the marketplace system. It provides comprehensive sales processing with automatic calculations, discount management, and integration with inventory and accounting systems.

### **📋 Service Configuration**
- **Service Name**: `sales`
- **Port**: `8082`
- **Database**: PostgreSQL (Port: 15432)
- **Eureka Registry**: http://localhost:8761/eureka/
- **Base URL**: `http://localhost:8082`

---

## 🚀 **API Endpoints**

### **Base URL**: `http://localhost:8082/api/sales`

---

## 📋 **1. Sales Transaction Operations**

### **1.1 Create Sale**
Creates a new sales transaction with automatic calculations and validations.

```http
POST /api/sales
Content-Type: application/json
```

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2,
  "unitPrice": 1299.99,
  "discountPercentage": 10.0,
  "customerId": 101,
  "customerName": "John Doe",
  "salesperson": "Jane Smith",
  "paymentMethod": "credit_card",
  "paymentStatus": "paid",
  "notes": "Bulk purchase discount applied"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "saleNumber": "SALE-20250903-001",
  "productId": 1,
  "quantity": 2,
  "unitPrice": 1299.99,
  "totalAmount": 2599.98,
  "discountPercentage": 10.0,
  "discountAmount": 259.998,
  "finalAmount": 2339.982,
  "saleDate": "2025-09-03",
  "customerId": 101,
  "customerName": "John Doe",
  "salesperson": "Jane Smith",
  "paymentMethod": "credit_card",
  "paymentStatus": "paid",
  "notes": "Bulk purchase discount applied",
  "createdAt": "2025-09-03T21:30:00",
  "updatedAt": "2025-09-03T21:30:00"
}
```

**🧮 Automatic Calculations:**
- `totalAmount` = `quantity` × `unitPrice`
- `discountAmount` = `totalAmount` × (`discountPercentage` / 100)
- `finalAmount` = `totalAmount` - `discountAmount`
- `saleNumber` = Auto-generated unique identifier
- `saleDate` = Current date

---

### **1.2 Get Sale by ID**
Retrieves a specific sale transaction by its ID.

```http
GET /api/sales/{id}
```

**Example:**
```bash
curl -X GET http://localhost:8082/api/sales/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "saleNumber": "SALE-20250903-001",
  "productId": 1,
  "quantity": 2,
  "unitPrice": 1299.99,
  "totalAmount": 2599.98,
  "discountPercentage": 10.0,
  "discountAmount": 259.998,
  "finalAmount": 2339.982,
  "saleDate": "2025-09-03",
  "customerId": 101,
  "customerName": "John Doe",
  "salesperson": "Jane Smith",
  "paymentMethod": "credit_card",
  "paymentStatus": "paid",
  "notes": "Bulk purchase discount applied",
  "createdAt": "2025-09-03T21:30:00",
  "updatedAt": "2025-09-03T21:30:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Sale not found"
}
```

---

### **1.3 Get All Sales**
Retrieves all sales transactions in the system.

```http
GET /api/sales
```

**Example:**
```bash
curl -X GET http://localhost:8082/api/sales
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "saleNumber": "SALE-20250903-001",
    "productId": 1,
    "quantity": 2,
    "unitPrice": 1299.99,
    "finalAmount": 2339.982,
    "saleDate": "2025-09-03",
    "customerName": "John Doe",
    "paymentStatus": "paid"
  },
  {
    "id": 2,
    "saleNumber": "SALE-20250903-002",
    "productId": 2,
    "quantity": 1,
    "unitPrice": 29.99,
    "finalAmount": 29.99,
    "saleDate": "2025-09-03",
    "customerName": "Jane Smith",
    "paymentStatus": "pending"
  }
]
```

---

## 🎯 **2. Customer-Based Queries**

### **2.1 Get Sales by Customer**
Retrieves all sales for a specific customer.

```http
GET /api/sales/customer/{customerId}
```

**Example:**
```bash
curl -X GET http://localhost:8082/api/sales/customer/101
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "saleNumber": "SALE-20250903-001",
    "productId": 1,
    "quantity": 2,
    "finalAmount": 2339.982,
    "saleDate": "2025-09-03",
    "customerId": 101,
    "customerName": "John Doe",
    "paymentStatus": "paid"
  },
  {
    "id": 3,
    "saleNumber": "SALE-20250904-001",
    "productId": 3,
    "quantity": 1,
    "finalAmount": 149.99,
    "saleDate": "2025-09-04",
    "customerId": 101,
    "customerName": "John Doe",
    "paymentStatus": "pending"
  }
]
```

---

## 📦 **3. Product-Based Queries**

### **3.1 Get Sales by Product**
Retrieves all sales for a specific product.

```http
GET /api/sales/product/{productId}
```

**Example:**
```bash
curl -X GET http://localhost:8082/api/sales/product/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "saleNumber": "SALE-20250903-001",
    "productId": 1,
    "quantity": 2,
    "unitPrice": 1299.99,
    "finalAmount": 2339.982,
    "customerId": 101,
    "customerName": "John Doe",
    "saleDate": "2025-09-03"
  },
  {
    "id": 4,
    "saleNumber": "SALE-20250904-002",
    "productId": 1,
    "quantity": 1,
    "unitPrice": 1299.99,
    "finalAmount": 1299.99,
    "customerId": 102,
    "customerName": "Alice Johnson",
    "saleDate": "2025-09-04"
  }
]
```

---

## 📝 **4. Request/Response Models**

### **SaleDto Fields**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `id` | Long | No | - | Auto-generated sale ID |
| `saleNumber` | String | No | - | Auto-generated unique sale number |
| `productId` | Integer | Yes | @NotNull | Product being sold |
| `quantity` | Integer | Yes | @Min(1) | Quantity sold |
| `unitPrice` | BigDecimal | Yes | @DecimalMin(0.0) | Price per unit |
| `totalAmount` | BigDecimal | No | - | Auto-calculated: quantity × unitPrice |
| `discountPercentage` | BigDecimal | No | 0-100 | Discount percentage (0-100%) |
| `discountAmount` | BigDecimal | No | - | Auto-calculated discount amount |
| `finalAmount` | BigDecimal | No | - | Auto-calculated final amount |
| `saleDate` | LocalDate | No | - | Auto-set to current date |
| `customerId` | Integer | No | - | Customer identifier |
| `customerName` | String | No | - | Customer name |
| `salesperson` | String | No | - | Salesperson name |
| `paymentMethod` | String | No | - | Payment method used |
| `paymentStatus` | String | No | Enum | "pending", "paid", "partial", "cancelled" |
| `notes` | String | No | - | Additional notes |
| `createdAt` | LocalDateTime | No | - | Creation timestamp |
| `updatedAt` | LocalDateTime | No | - | Last update timestamp |

---

## 💳 **5. Payment Status Values**

### **Valid Payment Status Options**
- **`pending`**: Payment not yet received
- **`paid`**: Payment completed successfully
- **`partial`**: Partial payment received
- **`cancelled`**: Sale cancelled/refunded

---

## 🧮 **6. Business Logic & Calculations**

### **Automatic Calculations**

1. **Total Amount Calculation**:
   ```
   totalAmount = quantity × unitPrice
   ```

2. **Discount Amount Calculation**:
   ```
   discountAmount = totalAmount × (discountPercentage / 100)
   ```

3. **Final Amount Calculation**:
   ```
   finalAmount = totalAmount - discountAmount
   ```

4. **Sale Number Generation**:
   ```
   Format: SALE-YYYYMMDD-XXX
   Example: SALE-20250903-001
   ```

### **Example Calculation**
```json
Input:
{
  "quantity": 3,
  "unitPrice": 100.00,
  "discountPercentage": 15.0
}

Calculations:
totalAmount = 3 × 100.00 = 300.00
discountAmount = 300.00 × (15.0 / 100) = 45.00
finalAmount = 300.00 - 45.00 = 255.00
```

---

## ⚠️ **7. Error Responses**

### **400 Bad Request - Validation Error**
```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "productId",
      "message": "Product ID is required"
    },
    {
      "field": "quantity",
      "message": "Quantity must be greater than 0"
    },
    {
      "field": "discountPercentage",
      "message": "Discount percentage must be between 0 and 100"
    }
  ]
}
```

### **400 Bad Request - Business Logic Error**
```json
{
  "error": "Business validation failed",
  "message": "Insufficient inventory for the requested quantity"
}
```

### **404 Not Found**
```json
{
  "error": "Sale not found",
  "message": "Sale with ID 999 does not exist"
}
```

### **500 Internal Server Error**
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred during sale processing"
}
```

---

## 🧪 **8. Testing Examples**

### **Create a Simple Sale**
```bash
curl -X POST http://localhost:8082/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 1,
    "unitPrice": 99.99,
    "customerId": 100,
    "customerName": "Test Customer",
    "salesperson": "Sales Rep",
    "paymentMethod": "cash",
    "paymentStatus": "paid"
  }'
```

### **Create a Sale with Discount**
```bash
curl -X POST http://localhost:8082/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 2,
    "quantity": 5,
    "unitPrice": 49.99,
    "discountPercentage": 20.0,
    "customerId": 101,
    "customerName": "Bulk Customer",
    "salesperson": "Senior Sales Rep",
    "paymentMethod": "credit_card",
    "paymentStatus": "paid",
    "notes": "Volume discount applied"
  }'
```

### **Get Customer Purchase History**
```bash
curl -X GET http://localhost:8082/api/sales/customer/101
```

### **Get Product Sales Report**
```bash
curl -X GET http://localhost:8082/api/sales/product/1
```

---

## 🔧 **9. Service Dependencies**

### **Database Configuration**
- **Type**: PostgreSQL
- **Host**: localhost:15432
- **Database**: `sales`
- **Username**: `postgres`
- **Password**: `123456`

### **Service Discovery**
- **Eureka Server**: http://localhost:8761/eureka/
- **Service Registration**: Automatic via Spring Cloud

### **External Service Integrations**
- **Warehouse Service**: Product inventory validation
- **Accounting Service**: Revenue journal entry creation
- **Customer Service**: Customer information validation

---

## 📊 **10. Sales Analytics & Reporting**

### **Key Metrics Available**
- Sales by customer (purchase history)
- Sales by product (performance analysis)
- Revenue calculations with discounts
- Payment status tracking
- Salesperson performance data

### **Sample Analytics Queries**
```bash
# Get all sales for a specific product
curl -X GET http://localhost:8082/api/sales/product/1

# Get customer purchase history
curl -X GET http://localhost:8082/api/sales/customer/101

# Get all pending payments
curl -X GET http://localhost:8082/api/sales | jq '.[] | select(.paymentStatus == "pending")'
```

---

## 🔄 **11. Integration Workflow**

### **Typical Sales Process Flow**
1. **Sale Creation**: Customer places order via Sales API
2. **Inventory Check**: System validates product availability (Warehouse)
3. **Price Calculation**: Automatic discount and total calculations
4. **Sale Recording**: Transaction saved with unique sale number
5. **Inventory Update**: Stock levels adjusted (Warehouse)
6. **Revenue Recording**: Journal entries created (Accounting)
7. **Confirmation**: Sale confirmation returned to customer

---

## 🚀 **12. Getting Started**

### **Prerequisites**
- Java 17+
- PostgreSQL 15+
- Maven 3.6+
- Eureka Discovery Service running on port 8761

### **Starting the Service**
```bash
cd sales
./mvnw spring-boot:run
```

### **Health Check**
```bash
curl -X GET http://localhost:8082/actuator/health
```

### **Test the API**
```bash
# Create a test sale
curl -X POST http://localhost:8082/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 1,
    "unitPrice": 100.00,
    "customerId": 1,
    "customerName": "Test Customer",
    "paymentStatus": "paid"
  }'
```

---

## 📞 **13. Support**

For technical support or questions about the Sales API:
- **Service Port**: 8082
- **Health Endpoint**: `/actuator/health`
- **Eureka Dashboard**: http://localhost:8761
- **API Base URL**: http://localhost:8082/api/sales

---

## 💡 **14. Best Practices**

### **Sale Creation Guidelines**
- Always specify `productId`, `quantity`, and `unitPrice`
- Use appropriate `discountPercentage` (0-100%)
- Include customer information for tracking
- Set proper `paymentStatus` for financial tracking

### **Error Handling**
- Validate product availability before sale creation
- Handle insufficient inventory gracefully
- Provide clear error messages for validation failures

### **Performance Considerations**
- Use customer and product filters for large datasets
- Implement pagination for sales listings
- Cache frequently accessed product information

---

**Last Updated**: September 3, 2025  
**Version**: 1.0.0  
**Service**: Sales Microservice  
**Business Domain**: Sales Transaction Management
