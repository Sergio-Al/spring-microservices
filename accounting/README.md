# 💰 Accounting Microservice - REST API Documentation

## 🏗️ **Service Overview**

The Accounting microservice manages financial journal entries following double-entry bookkeeping principles. It provides comprehensive CRUD operations for journal entries with built-in accounting validation rules to ensure data integrity and compliance with accounting standards.

### **📋 Service Configuration**
- **Service Name**: `accounting`
- **Port**: `8083`
- **Database**: PostgreSQL (Port: 15432)
- **Eureka Registry**: http://localhost:8761/eureka/
- **Base URL**: `http://localhost:8083`

---

## 🚀 **API Endpoints**

### **Base URL**: `http://localhost:8083/api/accounting`

---

## 📋 **1. Journal Entry CRUD Operations**

### **1.1 Create Journal Entry**
Creates a new journal entry with automatic accounting validation.

```http
POST /api/accounting/journals
Content-Type: application/json
```

**Request Body:**
```json
{
  "journalEntryNumber": "JE001",
  "transactionDate": "2025-01-15",
  "postingDate": "2025-01-15",
  "accountCode": "1000",
  "accountName": "Cash",
  "description": "Initial cash deposit",
  "debitAmount": 10000.00,
  "creditAmount": 0.00,
  "status": "draft",
  "createdBy": "system"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "journalEntryNumber": "JE001",
  "transactionDate": "2025-01-15",
  "postingDate": "2025-01-15",
  "accountCode": "1000",
  "accountName": "Cash",
  "description": "Initial cash deposit",
  "debitAmount": 10000.00,
  "creditAmount": 0.00,
  "status": "draft",
  "createdBy": "system",
  "createdDate": "2025-09-03T21:30:00"
}
```

**⚠️ Accounting Rules Enforced:**
- Cannot have both debit and credit amounts > 0
- Must have either debit OR credit amount > 0
- Follows double-entry bookkeeping principles

---

### **1.2 Get Journal Entry by ID**
Retrieves a specific journal entry by its ID.

```http
GET /api/accounting/journals/{id}
```

**Example:**
```bash
curl -X GET http://localhost:8083/api/accounting/journals/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "journalEntryNumber": "JE001",
  "transactionDate": "2025-01-15",
  "postingDate": "2025-01-15",
  "accountCode": "1000",
  "accountName": "Cash",
  "description": "Initial cash deposit",
  "debitAmount": 10000.00,
  "creditAmount": 0.00,
  "status": "draft",
  "createdBy": "system",
  "createdDate": "2025-09-03T21:30:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Journal entry not found"
}
```

---

### **1.3 Get All Journal Entries**
Retrieves all journal entries with pagination support.

```http
GET /api/accounting/journals?page=0&size=10&sort=transactionDate,desc
```

**Example:**
```bash
curl -X GET "http://localhost:8083/api/accounting/journals?page=0&size=5"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "journalEntryNumber": "JE001",
    "transactionDate": "2025-01-15",
    "accountCode": "1000",
    "accountName": "Cash",
    "debitAmount": 10000.00,
    "creditAmount": 0.00,
    "status": "draft"
  },
  {
    "id": 2,
    "journalEntryNumber": "JE002",
    "transactionDate": "2025-01-16",
    "accountCode": "3000",
    "accountName": "Revenue",
    "debitAmount": 0.00,
    "creditAmount": 5000.00,
    "status": "posted"
  }
]
```

**Pagination Parameters:**
- `page`: Page number (0-based, default: 0)
- `size`: Number of records per page (default: 20)
- `sort`: Sort by field (e.g., `transactionDate,desc`)

---

### **1.4 Update Journal Entry**
Updates an existing journal entry with validation.

```http
PUT /api/accounting/journals
Content-Type: application/json
```

**Request Body:**
```json
{
  "id": 1,
  "journalEntryNumber": "JE001",
  "transactionDate": "2025-01-15",
  "postingDate": "2025-01-15",
  "accountCode": "1000",
  "accountName": "Cash - Updated",
  "description": "Updated cash deposit description",
  "debitAmount": 12000.00,
  "creditAmount": 0.00,
  "status": "posted",
  "createdBy": "system"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "journalEntryNumber": "JE001",
  "accountName": "Cash - Updated",
  "description": "Updated cash deposit description",
  "debitAmount": 12000.00,
  "status": "posted",
  "createdDate": "2025-09-03T21:30:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Journal entry not found"
}
```

---

### **1.5 Delete Journal Entry**
Removes a journal entry from the system.

```http
DELETE /api/accounting/journals/{id}
```

**Example:**
```bash
curl -X DELETE http://localhost:8083/api/accounting/journals/1
```

**Response (200 OK):**
```json
{
  "message": "Journal entry deleted successfully"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Journal entry not found"
}
```

---

## 🏥 **2. Health Check**

### **2.1 Service Health**
Checks if the accounting service is running and accessible.

```http
GET /api/accounting/health
```

**Example:**
```bash
curl -X GET http://localhost:8083/api/accounting/health
```

**Response (200 OK):**
```
Accounting service is running
```

---

## 📝 **3. Request/Response Models**

### **JournalDto Fields**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `id` | Integer | No | - | Auto-generated journal entry ID |
| `journalEntryNumber` | String | Yes | Unique | Journal entry reference number |
| `transactionDate` | LocalDate | Yes | - | Date when transaction occurred |
| `postingDate` | LocalDate | Yes | - | Date when entry was posted |
| `accountCode` | String | Yes | - | Account code (e.g., "1000") |
| `accountName` | String | Yes | - | Account name (e.g., "Cash") |
| `debitAmount` | BigDecimal | Conditional | ≥ 0 | Debit amount (if debit entry) |
| `creditAmount` | BigDecimal | Conditional | ≥ 0 | Credit amount (if credit entry) |
| `description` | String | Yes | - | Transaction description |
| `status` | String | Yes | - | Entry status: "draft", "posted", "reversed" |
| `createdBy` | String | Yes | - | User who created the entry |
| `createdDate` | LocalDateTime | No | - | Creation timestamp |

---

## 📊 **4. Status Values**

### **Journal Entry Status**
- **`draft`**: Entry created but not yet finalized
- **`posted`**: Entry finalized and affecting account balances
- **`reversed`**: Entry reversed/cancelled

---

## ⚖️ **5. Accounting Business Rules**

### **Double-Entry Bookkeeping Validation**

1. **Exclusive Amounts Rule**: 
   ```
   Cannot have both debitAmount > 0 AND creditAmount > 0
   ```

2. **Required Amount Rule**: 
   ```
   Must have either debitAmount > 0 OR creditAmount > 0
   ```

3. **Database Constraint**: 
   ```sql
   CHECK (NOT (debit_amount > 0 AND credit_amount > 0))
   ```

### **Example Valid Entries**

**✅ Valid Debit Entry:**
```json
{
  "debitAmount": 1000.00,
  "creditAmount": 0.00
}
```

**✅ Valid Credit Entry:**
```json
{
  "debitAmount": 0.00,
  "creditAmount": 1000.00
}
```

**❌ Invalid Entry (Both amounts):**
```json
{
  "debitAmount": 1000.00,
  "creditAmount": 500.00    // ❌ Violates business rule
}
```

---

## ⚠️ **6. Error Responses**

### **400 Bad Request - Validation Error**
```json
{
  "error": "Validation failed",
  "message": "Journal entry cannot have both debit and credit amounts. Use separate entries for each."
}
```

### **400 Bad Request - Business Rule Violation**
```json
{
  "error": "Business rule violation",
  "message": "Journal entry must have either a debit or credit amount greater than zero."
}
```

### **409 Conflict - Database Constraint**
```json
{
  "error": "Database constraint violation",
  "message": "new row for relation \"journal\" violates check constraint \"chk_not_both_debit_credit\"",
  "details": "Cannot have both debit and credit amounts in the same journal entry"
}
```

### **404 Not Found**
```json
{
  "error": "Journal entry not found",
  "message": "Journal entry with ID 999 does not exist"
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

## 🧪 **7. Testing Examples**

### **Create a Debit Entry (Cash Deposit)**
```bash
curl -X POST http://localhost:8083/api/accounting/journals \
  -H "Content-Type: application/json" \
  -d '{
    "journalEntryNumber": "JE001",
    "transactionDate": "2025-01-15",
    "postingDate": "2025-01-15",
    "accountCode": "1000",
    "accountName": "Cash",
    "description": "Initial cash deposit",
    "debitAmount": 10000.00,
    "creditAmount": 0.00,
    "status": "draft",
    "createdBy": "system"
  }'
```

### **Create a Credit Entry (Revenue)**
```bash
curl -X POST http://localhost:8083/api/accounting/journals \
  -H "Content-Type: application/json" \
  -d '{
    "journalEntryNumber": "JE002",
    "transactionDate": "2025-01-16",
    "postingDate": "2025-01-16",
    "accountCode": "3000",
    "accountName": "Service Revenue",
    "description": "Revenue from consulting services",
    "debitAmount": 0.00,
    "creditAmount": 5000.00,
    "status": "posted",
    "createdBy": "system"
  }'
```

### **Get Journal Entries with Pagination**
```bash
curl -X GET "http://localhost:8083/api/accounting/journals?page=0&size=10&sort=transactionDate,desc"
```

### **Update Journal Entry Status**
```bash
curl -X PUT http://localhost:8083/api/accounting/journals \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "journalEntryNumber": "JE001",
    "transactionDate": "2025-01-15",
    "postingDate": "2025-01-15",
    "accountCode": "1000",
    "accountName": "Cash",
    "description": "Initial cash deposit",
    "debitAmount": 10000.00,
    "creditAmount": 0.00,
    "status": "posted",
    "createdBy": "system"
  }'
```

---

## 🔧 **8. Service Dependencies**

### **Database Configuration**
- **Type**: PostgreSQL
- **Host**: localhost:15432
- **Database**: `accounting`
- **Username**: `postgres`
- **Password**: `123456`

### **Service Discovery**
- **Eureka Server**: http://localhost:8761/eureka/
- **Service Registration**: Automatic via Spring Cloud

---

## 📚 **9. Accounting Concepts**

### **Double-Entry Bookkeeping**
Every financial transaction affects at least two accounts:
- **Debit**: Increases assets or decreases liabilities/equity
- **Credit**: Decreases assets or increases liabilities/equity

### **Account Types**
- **Assets (1000-1999)**: Cash, Inventory, Equipment
- **Liabilities (2000-2999)**: Accounts Payable, Loans
- **Equity (3000-3999)**: Capital, Retained Earnings
- **Revenue (4000-4999)**: Sales, Service Revenue
- **Expenses (5000-5999)**: Rent, Utilities, Salaries

### **Sample Transaction Flow**
**Cash Sale Transaction** requires TWO journal entries:
1. **Debit Entry**: Cash Account (increases asset)
2. **Credit Entry**: Revenue Account (increases revenue)

---

## 🚀 **10. Getting Started**

### **Prerequisites**
- Java 17+
- PostgreSQL 15+
- Maven 3.6+
- Eureka Discovery Service running on port 8761

### **Starting the Service**
```bash
cd accounting
./mvnw spring-boot:run
```

### **Health Check**
```bash
curl -X GET http://localhost:8083/api/accounting/health
```

### **Database Schema Validation**
The service uses `spring.jpa.hibernate.ddl-auto=validate` to ensure your entity matches the database schema exactly.

---

## 📞 **11. Support**

For technical support or questions about the Accounting API:
- **Service Port**: 8083
- **Health Endpoint**: `/api/accounting/health`
- **Eureka Dashboard**: http://localhost:8761
- **API Base URL**: http://localhost:8083/api/accounting

---

## 🔒 **12. Data Integrity**

### **Database Constraints**
- **Primary Key**: Auto-generated integer ID
- **Unique Constraint**: Journal entry numbers must be unique
- **Check Constraint**: Prevents both debit and credit amounts > 0
- **Not Null**: Required fields enforced at database level

### **Application-Level Validation**
- Business rule validation before database operations
- Comprehensive error messages for troubleshooting
- Automatic audit trail with creation timestamps

---

**Last Updated**: September 3, 2025  
**Version**: 1.0.0  
**Service**: Accounting Microservice  
**Compliance**: Double-Entry Bookkeeping Standards
