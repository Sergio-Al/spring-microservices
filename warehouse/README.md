# 📦 Warehouse Microservice - REST API Documentation

## 🏗️ **Service Overview**

The Warehouse microservice manages product inventory, stock levels, and product information for the marketplace system. It provides comprehensive CRUD operations and specialized inventory management features.

### **📋 Service Configuration**
- **Service Name**: `warehouse`
- **Port**: `8081`
- **Database**: MySQL (Port: 13306)
- **Eureka Registry**: http://localhost:8761/eureka/
- **Base URL**: `http://localhost:8081`

---

## 🚀 **API Endpoints**

### **Base URL**: `http://localhost:8081/api/products`

---

## 📋 **1. Product CRUD Operations**

### **1.1 Create Product**
Creates a new product in the warehouse inventory.

```http
POST /api/products
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Laptop Gaming ROG",
  "description": "High-performance gaming laptop with RTX 4070",
  "category": "Electronics",
  "price": 1299.99,
  "cost": 899.99,
  "sku": "LAP-ROG-001",
  "stockQuantity": 25,
  "minStockLevel": 5,
  "maxStockLevel": 100,
  "supplier": "ASUS Supplier Inc.",
  "brand": "ASUS",
  "weight": 2.5,
  "dimensions": "35.7 x 25.5 x 2.1 cm",
  "status": "ACTIVE"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Laptop Gaming ROG",
  "description": "High-performance gaming laptop with RTX 4070",
  "category": "Electronics",
  "price": 1299.99,
  "cost": 899.99,
  "sku": "LAP-ROG-001",
  "stockQuantity": 25,
  "minStockLevel": 5,
  "maxStockLevel": 100,
  "supplier": "ASUS Supplier Inc.",
  "brand": "ASUS",
  "weight": 2.5,
  "dimensions": "35.7 x 25.5 x 2.1 cm",
  "status": "ACTIVE",
  "createdAt": "2025-09-03T21:30:00",
  "updatedAt": "2025-09-03T21:30:00"
}
```

---

### **1.2 Get Product by ID**
Retrieves a specific product by its ID.

```http
GET /api/products/{id}
```

**Example:**
```bash
curl -X GET http://localhost:8081/api/products/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop Gaming ROG",
  "description": "High-performance gaming laptop with RTX 4070",
  "category": "Electronics",
  "price": 1299.99,
  "cost": 899.99,
  "sku": "LAP-ROG-001",
  "stockQuantity": 25,
  "minStockLevel": 5,
  "maxStockLevel": 100,
  "supplier": "ASUS Supplier Inc.",
  "brand": "ASUS",
  "weight": 2.5,
  "dimensions": "35.7 x 25.5 x 2.1 cm",
  "status": "ACTIVE",
  "createdAt": "2025-09-03T21:30:00",
  "updatedAt": "2025-09-03T21:30:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Product not found"
}
```

---

### **1.3 Get All Products**
Retrieves all products in the warehouse inventory.

```http
GET /api/products
```

**Example:**
```bash
curl -X GET http://localhost:8081/api/products
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop Gaming ROG",
    "category": "Electronics",
    "price": 1299.99,
    "stockQuantity": 25,
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "name": "Wireless Mouse",
    "category": "Accessories",
    "price": 29.99,
    "stockQuantity": 150,
    "status": "ACTIVE"
  }
]
```

---

### **1.4 Update Product**
Updates an existing product's information.

```http
PUT /api/products/{id}
Content-Type: application/json
```

**Example:**
```bash
curl -X PUT http://localhost:8081/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Gaming ROG Updated",
    "description": "Updated high-performance gaming laptop",
    "category": "Electronics",
    "price": 1399.99,
    "cost": 999.99,
    "sku": "LAP-ROG-001",
    "stockQuantity": 30,
    "minStockLevel": 5,
    "maxStockLevel": 100,
    "supplier": "ASUS Supplier Inc.",
    "brand": "ASUS",
    "weight": 2.5,
    "dimensions": "35.7 x 25.5 x 2.1 cm",
    "status": "ACTIVE"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop Gaming ROG Updated",
  "description": "Updated high-performance gaming laptop",
  "price": 1399.99,
  "cost": 999.99,
  "stockQuantity": 30,
  "updatedAt": "2025-09-03T22:00:00"
}
```

---

### **1.5 Delete Product**
Removes a product from the inventory.

```http
DELETE /api/products/{id}
```

**Example:**
```bash
curl -X DELETE http://localhost:8081/api/products/1
```

**Response (204 No Content):**
```
(Empty response body)
```

**Response (404 Not Found):**
```json
{
  "error": "Product not found"
}
```

---

## 📊 **2. Stock Management Operations**

### **2.1 Update Stock Quantity**
Updates the stock quantity for a specific product.

```http
PUT /api/products/{id}/stock?newStock={quantity}
```

**Example:**
```bash
curl -X PUT "http://localhost:8081/api/products/1/stock?newStock=50"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop Gaming ROG",
  "stockQuantity": 50,
  "updatedAt": "2025-09-03T22:15:00"
}
```

---

### **2.2 Get Low Stock Products**
Retrieves products with stock levels below their minimum threshold.

```http
GET /api/products/low-stock
```

**Example:**
```bash
curl -X GET http://localhost:8081/api/products/low-stock
```

**Response (200 OK):**
```json
[
  {
    "id": 3,
    "name": "Gaming Keyboard",
    "category": "Accessories",
    "stockQuantity": 2,
    "minStockLevel": 10,
    "status": "ACTIVE"
  },
  {
    "id": 5,
    "name": "USB Cable",
    "category": "Accessories",
    "stockQuantity": 5,
    "minStockLevel": 20,
    "status": "ACTIVE"
  }
]
```

---

## 🏷️ **3. Category Operations**

### **3.1 Get Products by Category**
Retrieves all products belonging to a specific category.

```http
GET /api/products/category/{category}
```

**Example:**
```bash
curl -X GET http://localhost:8081/api/products/category/Electronics
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop Gaming ROG",
    "category": "Electronics",
    "price": 1299.99,
    "stockQuantity": 25,
    "status": "ACTIVE"
  },
  {
    "id": 4,
    "name": "Smartphone",
    "category": "Electronics",
    "price": 699.99,
    "stockQuantity": 40,
    "status": "ACTIVE"
  }
]
```

---

## 📝 **4. Request/Response Models**

### **ProductDto Fields**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `id` | Integer | No | - | Auto-generated product ID |
| `name` | String | Yes | @NotBlank | Product name |
| `description` | String | No | - | Product description |
| `category` | String | No | - | Product category |
| `price` | BigDecimal | Yes | @DecimalMin(0.0) | Selling price |
| `cost` | BigDecimal | No | @DecimalMin(0.0) | Cost price |
| `sku` | String | No | - | Stock Keeping Unit |
| `stockQuantity` | Integer | Yes | @Min(0) | Current stock level |
| `minStockLevel` | Integer | No | @Min(0) | Minimum stock threshold |
| `maxStockLevel` | Integer | No | @Min(1) | Maximum stock limit |
| `supplier` | String | No | - | Supplier information |
| `brand` | String | No | - | Product brand |
| `weight` | BigDecimal | No | @DecimalMin(0.0) | Product weight |
| `dimensions` | String | No | - | Product dimensions |
| `status` | String | No | - | Product status (ACTIVE, INACTIVE, DISCONTINUED) |
| `createdAt` | LocalDateTime | No | - | Creation timestamp |
| `updatedAt` | LocalDateTime | No | - | Last update timestamp |

---

## ⚠️ **5. Error Responses**

### **400 Bad Request**
```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "name",
      "message": "Product name is required"
    },
    {
      "field": "price",
      "message": "Price must be greater than or equal to 0"
    }
  ]
}
```

### **404 Not Found**
```json
{
  "error": "Product not found",
  "message": "Product with ID 999 does not exist"
}
```

### **500 Internal Server Error**
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred"
}
```

---

## 🧪 **6. Testing Examples**

### **Create a New Product**
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Headphones",
    "description": "Noise-cancelling wireless headphones",
    "category": "Electronics",
    "price": 199.99,
    "cost": 120.00,
    "sku": "WH-NC-001",
    "stockQuantity": 75,
    "minStockLevel": 10,
    "maxStockLevel": 200,
    "supplier": "Audio Tech Ltd.",
    "brand": "AudioMax",
    "weight": 0.25,
    "dimensions": "18 x 15 x 8 cm",
    "status": "ACTIVE"
  }'
```

### **Update Stock Level**
```bash
curl -X PUT "http://localhost:8081/api/products/1/stock?newStock=100"
```

### **Check Low Stock Products**
```bash
curl -X GET http://localhost:8081/api/products/low-stock
```

### **Filter by Category**
```bash
curl -X GET http://localhost:8081/api/products/category/Electronics
```

---

## 🔧 **7. Service Dependencies**

### **Database Configuration**
- **Type**: MySQL
- **Host**: localhost:13306
- **Database**: `warehouse`
- **Username**: `root`
- **Password**: `123456`

### **Service Discovery**
- **Eureka Server**: http://localhost:8761/eureka/
- **Service Registration**: Automatic via Spring Cloud

---

## 📚 **8. Business Rules**

1. **Stock Validation**: Stock quantity cannot be negative
2. **Price Validation**: Price and cost must be non-negative
3. **Low Stock Alert**: Products with stock below `minStockLevel` appear in low-stock endpoint
4. **Status Management**: Products can be ACTIVE, INACTIVE, or DISCONTINUED
5. **SKU Uniqueness**: Each product should have a unique SKU (if provided)

---

## 🚀 **9. Getting Started**

### **Prerequisites**
- Java 17+
- MySQL 8.0+
- Maven 3.6+
- Eureka Discovery Service running on port 8761

### **Starting the Service**
```bash
cd warehouse
./mvnw spring-boot:run
```

### **Health Check**
```bash
curl -X GET http://localhost:8081/actuator/health
```

---

## 📞 **10. Support**

For technical support or questions about the Warehouse API:
- **Service Port**: 8081
- **Health Endpoint**: `/actuator/health`
- **Eureka Dashboard**: http://localhost:8761
- **API Base URL**: http://localhost:8081/api/products

---

**Last Updated**: September 3, 2025  
**Version**: 1.0.0  
**Service**: Warehouse Microservice
