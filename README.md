# Spring Boot Microservices - Market System

A complete microservices architecture implementing a market system with sales, inventory, and accounting functionality, featuring distributed transactions with compensating patterns (Saga pattern) for data consistency.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Services](#services)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Database Setup](#database-setup)
- [Service Configuration](#service-configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Distributed Transactions](#distributed-transactions)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │ Discovery Service│    │   Load Balancer │
│   (Port 8080)   │    │   (Port 8761)   │    │    (Eureka)     │
└─────────┬───────┘    └─────────────────┘    └─────────────────┘
          │
          ├─────────────────┬─────────────────┬─────────────────┐
          │                 │                 │                 │
┌─────────▼───────┐ ┌───────▼───────┐ ┌───────▼───────┐ ┌───────▼───────┐
│   Warehouse     │ │     Sales     │ │  Accounting   │ │   Discovery   │
│   Service       │ │   Service     │ │   Service     │ │   Service     │
│  (Port 8081)    │ │ (Port 8082)   │ │ (Port 8083)   │ │ (Port 8761)   │
└─────────┬───────┘ └───────┬───────┘ └───────┬───────┘ └───────────────┘
          │                 │                 │
┌─────────▼───────┐ ┌───────▼───────┐ ┌───────▼───────┐
│     MySQL       │ │  PostgreSQL   │ │  PostgreSQL   │
│  (Port 13306)   │ │ (Port 15432)  │ │ (Port 15432)  │
│   warehouse     │ │     sales     │ │  accounting   │
└─────────────────┘ └───────────────┘ └───────────────┘
```

## 🛠️ Services

### 1. Discovery Service (Eureka Server)
- **Port**: 8761
- **Purpose**: Service registry and discovery
- **Technology**: Spring Cloud Netflix Eureka

### 2. API Gateway
- **Port**: 8080
- **Purpose**: Single entry point, routing, load balancing
- **Technology**: Spring Cloud Gateway (WebMVC)

### 3. Warehouse Service
- **Port**: 8081
- **Purpose**: Product inventory management
- **Database**: MySQL (Port 13306)
- **Key Features**: Product CRUD, stock management

### 4. Sales Service
- **Port**: 8082
- **Purpose**: Sales transaction processing
- **Database**: PostgreSQL (Port 15432)
- **Key Features**: Sales creation, distributed transactions, compensation logic

### 5. Accounting Service
- **Port**: 8083
- **Purpose**: Financial journal entries
- **Database**: PostgreSQL (Port 15432)
- **Key Features**: Journal entries, financial tracking

## ✨ Features

### Core Functionality
- ✅ Complete CRUD operations for products
- ✅ Sales transaction processing
- ✅ Automatic journal entry creation
- ✅ Service discovery and load balancing
- ✅ API Gateway routing

### Advanced Features
- 🔄 **Distributed Transactions**: Saga pattern implementation
- 🛡️ **Compensating Transactions**: Automatic rollback on failures
- 📊 **Data Consistency**: Cross-service transaction integrity
- 🔍 **Service Discovery**: Automatic service registration
- 🌐 **Load Balancing**: Built-in load balancing via Eureka

### Distributed Transaction Flow
```
Sale Creation Process:
1. Validate Product (Warehouse Service)
2. Create Sale Record (Sales Service)
3. Update Stock (Warehouse Service)
4. Create Journal Entry (Accounting Service)

On Failure at Any Step:
- Automatic compensation/rollback
- Data consistency maintained
- No orphaned records
```

## 📋 Prerequisites

- **Java**: 17 or higher
- **Maven**: 3.8+
- **Docker**: For databases if necessary, but your tables can be created manually using provided SQL scripts
- **Git**: For cloning the repository

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/Sergio-Al/spring-microservices.git
cd spring-microservices
```

### 2. Start Databases (optional)
```bash
docker-compose up -d
```

### 3. Start Services (in order)

#### Start Discovery Service
```bash
cd discovery-service
./mvnw spring-boot:run
```

#### Start Warehouse Service
```bash
cd warehouse
./mvnw spring-boot:run
```

#### Start Sales Service
```bash
cd sales
./mvnw spring-boot:run
```

#### Start Accounting Service
```bash
cd accounting
./mvnw spring-boot:run
```

#### Start API Gateway
```bash
cd api-gateway
./mvnw spring-boot:run
```

### 4. Verify Services
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/api/accounting/health

## 🗄️ Database Setup

### Docker Compose Services

The `docker-compose.yml` provides:

```yaml
# MySQL for Warehouse Service
warehouse-db:
  - Port: 13306
  - Database: warehouse
  - User: root
  - Password: 123456

# PostgreSQL for Sales & Accounting
pg-db:
  - Port: 15432
  - Databases: sales, accounting
  - User: postgres
  - Password: 123456
```

### Database Schemas

#### Warehouse Database (MySQL)
```sql
-- Products table
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10,2),
    cost DECIMAL(10,2),
    sku VARCHAR(50) UNIQUE,
    stock_quantity INT,
    min_stock_level INT,
    max_stock_level INT,
    supplier VARCHAR(255),
    brand VARCHAR(100),
    weight DECIMAL(5,2),
    dimensions VARCHAR(100),
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Sales Database (PostgreSQL)
```sql
-- Sales table
CREATE TABLE sale (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    sale_number VARCHAR(50) UNIQUE NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2),
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    final_amount DECIMAL(10,2),
    sale_date DATE,
    customer_id INTEGER,
    customer_name VARCHAR(255),
    salesperson VARCHAR(255),
    payment_method VARCHAR(50),
    payment_status VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Accounting Database (PostgreSQL)
```sql
-- Journal entries table
CREATE TABLE JOURNAL (
    id SERIAL PRIMARY KEY,
    journal_entry_number VARCHAR(20) UNIQUE NOT NULL,
    transaction_date DATE NOT NULL,
    posting_date DATE DEFAULT CURRENT_DATE,
    account_code VARCHAR(20) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    reference_number VARCHAR(50),
    debit_amount DECIMAL(15,2) DEFAULT 0.00,
    credit_amount DECIMAL(15,2) DEFAULT 0.00,
    balance_type CHAR(1) CHECK (balance_type IN ('D', 'C')),
    department VARCHAR(100),
    cost_center VARCHAR(50),
    project_code VARCHAR(50),
    currency_code CHAR(3) DEFAULT 'USD',
    exchange_rate DECIMAL(10,6) DEFAULT 1.000000,
    source_document VARCHAR(100),
    created_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    approval_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'posted', 'reversed')),
    reversed_by_entry VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## ⚙️ Service Configuration

### Port Configuration
| Service | Port | Purpose |
|---------|------|---------|
| Discovery Service | 8761 | Eureka Server |
| API Gateway | 8080 | Gateway Router |
| Warehouse Service | 8081 | Product Management |
| Sales Service | 8082 | Sales Processing |
| Accounting Service | 8083 | Financial Records |

### Database Connections
| Service | Database | Port | Schema |
|---------|----------|------|--------|
| Warehouse | MySQL | 13306 | warehouse |
| Sales | PostgreSQL | 15432 | sales |
| Accounting | PostgreSQL | 15432 | accounting |

## 📚 API Documentation

### Gateway Routes (Port 8080)

#### Product Management
```bash
# Get all products
GET /api/products

# Get product by ID
GET /api/products/{id}

# Create product
POST /api/products

# Update product stock
PUT /api/products/{id}/stock
```

#### Sales Management
```bash
# Get all sales
GET /api/sales

# Create sale (with distributed transaction)
POST /api/sales
Content-Type: application/json
{
  "productId": 1,
  "quantity": 2,
  "unitPrice": 100.00,
  "description": "Sale description"
}
```

#### Accounting
```bash
# Get all journal entries
GET /api/accounting/journals

# Get journal by entry number
GET /api/accounting/journals/entry/{journalEntryNumber}

# Service health check
GET /api/accounting/health
```

### Direct Service Access

#### Warehouse Service (Port 8081)
```bash
curl http://localhost:8081/api/products/1
```

#### Sales Service (Port 8082)
```bash
curl http://localhost:8082/api/sales
```

#### Accounting Service (Port 8083)
```bash
curl http://localhost:8083/api/accounting/health
```

## 🧪 Testing

### Test Successful Sale
```bash
curl -X POST http://localhost:8080/api/sales \
-H "Content-Type: application/json" \
-d '{
    "productId": 1,
    "quantity": 1,
    "unitPrice": 100.00,
    "discountPercentage": 10.0,
    "customerId": 1,
    "customerName": "Test Customer",
    "salesperson": "Sales Rep",
    "paymentMethod": "credit_card",
    "paymentStatus": "paid"
  }'
```

### Test Compensation (Rollback)
```bash
# This will fail and trigger rollback with non-existent productId
curl -X POST http://localhost:8080/api/sales \
-H "Content-Type: application/json" \
-d '{
    "productId": 999,
    "quantity": 1,
    "unitPrice": 100.00,
    "discountPercentage": 10.0,
    "customerId": 1,
    "customerName": "Test Customer",
    "salesperson": "Sales Rep",
    "paymentMethod": "credit_card",
    "paymentStatus": "paid"
  }'
```

### Verify Service Registration
```bash
# Check Eureka dashboard
http://localhost:8761

# Check registered services
curl http://localhost:8761/eureka/apps
```

## 🔄 Distributed Transactions

### Saga Pattern Implementation

The system implements the **Compensating Transaction Pattern (Saga)** for distributed transaction management:

#### Transaction Steps:
1. **Product Validation**: Verify product exists and has sufficient stock
2. **Sale Creation**: Create sale record in sales database
3. **Stock Update**: Decrease product stock in warehouse
4. **Journal Entry**: Create accounting journal entry

#### Compensation Logic:
- If **Step 1 fails**: No compensation needed (no state changed)
- If **Step 2 fails**: No compensation needed (no state changed)
- If **Step 3 fails**: Delete created sale record
- If **Step 4 fails**: Restore stock + Delete sale record

#### Key Classes:
- `CompleSaleBl.java`: Main business logic with compensation
- `CompensationData.java`: Tracks transaction state for rollback
- Compensation methods: `rollbackJournalEntry()`, `rollbackStockUpdate()`, `rollbackSaleCreation()`

## 🛠️ Troubleshooting

### Common Issues

#### Services not registering with Eureka
```bash
# Check if Discovery Service is running
curl http://localhost:8761

# Verify application.properties has correct Eureka URL
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

#### Database Connection Issues
```bash
# Check if databases are running
docker-compose ps

# Restart databases
docker-compose down
docker-compose up -d
```

#### Port Conflicts
```bash
# Check if ports are in use
lsof -i :8080  # API Gateway
lsof -i :8761  # Discovery Service
lsof -i :8081  # Warehouse
lsof -i :8082  # Sales
lsof -i :8083  # Accounting
```

#### API Gateway Routes Not Working
```bash
# Check gateway configuration
# Ensure using correct syntax for WebMVC:
spring.cloud.gateway.mvc.routes[0].id=warehouse
spring.cloud.gateway.mvc.routes[0].uri=lb://warehouse
spring.cloud.gateway.mvc.routes[0].predicates[0]=Path=/api/products/**
```

### Service Startup Order
1. **First**: Discovery Service (8761)
2. **Second**: Databases (Docker Compose)
3. **Then**: Warehouse, Sales, Accounting services
4. **Last**: API Gateway

### Logs Location
- Service logs: Console output
- Application logs: Check each service's terminal output
- Gateway logs: `api-gateway/gateway.log` (if running in background)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

This project is only for educational purposes.

## 👥 Authors

- **Sergio** - Initial work and development

## 🔗 Related Documentation

- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)
- [Compensating Transaction Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/compensating-transaction)

---

**Last Updated**: September 4, 2025
**Version**: 1.0.0
**Spring Boot Version**: 3.5.5
**Spring Cloud Version**: 2024.0.0
