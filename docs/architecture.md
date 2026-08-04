# MeatShop Architecture Documentation

## 1. Overview

MeatShop is a backend management system designed to support meat shop operations, including:

- Product management
- Inventory tracking
- Financial transactions
- Customer and supplier management
- Employee administration

The system is built using a **Modular Monolith Architecture** based on Spring Boot and Spring Modulith. The architecture follows clean architecture principles and domain-driven design concepts, providing clear separation between business domains while maintaining simplicity in development, testing, and deployment.

The main goal of this architecture is to create a maintainable and scalable system with clear module boundaries while keeping the flexibility to extract modules into independent services in the future if required.

## 2. Technology Stack

### Core Framework

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Main programming language |
| Spring Boot | 3.4.5 | Backend application framework |
| Spring Modulith | 1.3.4 | Modular monolith architecture management |
| Maven | - | Dependency management and build system |

### Database & Caching

| Technology | Version | Purpose |
|------------|---------|---------|
| PostgreSQL | 17 | Primary relational database |
| Redis | 7.2 | Caching and session-related storage |
| Flyway | - | Database migration management |

### Security

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Security | - | Authentication and authorization |
| JJWT | 0.12.6 | JWT token generation and validation |

Security features include:
- JWT-based authentication
- Access and refresh token mechanism
- Role-Based Access Control (RBAC)
- Session management
- Method-level authorization

### Monitoring & Observability

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot Actuator | - | Health checks and application metrics |
| Micrometer | - | Metrics collection |
| Prometheus | - | Metrics storage and querying |
| Grafana | 10.4.2 | Metrics visualization and dashboards |
| Loki | 3.0.0 | Centralized log aggregation |
| Alloy | - | Logs and metrics collection |
| Node Exporter | - | System metrics collection |

### Development Tools

| Technology | Version | Purpose |
|------------|---------|---------|
| Lombok | - | Reduce boilerplate code |
| MapStruct | 1.5.5.Final | DTO mapping |
| Spring Boot DevTools | - | Development productivity and hot reload |

### Communication

| Technology | Purpose |
|------------|---------|
| WebSocket | Real-time communication support |

## 3. System Architecture

### Modular Monolith Design

MeatShop follows a **Modular Monolith Architecture**. The application is organized into independent business modules inside a single deployable application.

Each module:
- Owns its business logic
- Maintains clear boundaries
- Exposes only required functionality to other modules
- Avoids direct dependency on internal implementation details

This approach provides the advantages of structured architecture while avoiding the complexity of distributed microservices.

### Application Structure

```
com.Market.MeatShop

├── Config
│   └── Application configuration
│
├── Products
│   └── Product management and inventory
│
├── Parties
│   └── Customer and supplier management
│
├── Finances
│   └── Financial operations and transactions
│
├── Employees
│   └── Employee management and permissions
│
├── Security
│   └── Authentication and authorization
│
├── Shared
│   └── Shared DTOs, exceptions, and utilities
│
└── Utils
    └── General utility classes
```

## 4. Module Responsibilities

### Security Module

Responsible for application security.

**Responsibilities:**
- Authentication
- Authorization
- JWT token management
- Session handling
- Security policies

The Security module provides authentication and authorization services to other modules while remaining independent from business domains.

### Products Module

Responsible for product and inventory management.

**Responsibilities:**
- Product management
- Category management
- Inventory tracking
- Stock movement management

**Main operations:**
- Create and update products
- Manage product categories
- Track stock changes

### Parties Module

Responsible for managing external business entities.

**Includes:**
- Customers
- Suppliers

The Party domain is separated from Employee management to avoid unnecessary coupling between internal users and external business entities.

### Finances Module

Responsible for financial operations.

**Includes:**
- Income records
- Expenses
- Debts
- Financial transactions

### Employees Module

Responsible for internal employee management.

**Includes:**
- Employee information
- Employee operations
- Permission assignment

### Shared Module

Contains reusable components shared between modules.

**Includes:**
- Common DTOs
- Exception handling
- Shared utilities
- Common interfaces

## 5. Internal Module Architecture

Each module follows a layered architecture based on clean architecture principles.

```
Module

├── Presentation
│   └── Controllers
│
├── Application
│   ├── Services
│   └── DTOs
│
├── Domain
│   ├── Entities
│   ├── Business Rules
│   └── Repository Interfaces
│
└── Infrastructure
    ├── Database Access
    ├── JPA Repositories
    └── External Integrations
```

This separation keeps business logic independent from technical implementation details and improves maintainability.

## 6. Module Interactions

Modules communicate through well-defined boundaries.

**Module Responsibilities:**
- **Security Module** provides authentication and authorization services.
- **Products Module** manages products and inventory data.
- **Employees Module** manages employees and permissions.
- **Finances Module** handles financial operations.
- **Parties Module** manages customers and suppliers.
- **Shared Module** provides common components used across modules.

## 7. Application Data Flow

### Request Processing Flow

```
HTTP Request
    ↓
Controller Layer
    ↓
Application Service Layer
    ↓
Domain Logic
    ↓
Repository Layer
    ↓
PostgreSQL Database
```

### Authentication Flow

```
Client Request
    ↓
Security Module
    ↓
JWT Validation
    ↓
Authentication Context
    ↓
Authorization Check
    ↓
Business Logic Execution
```

### Cache Flow

```
Application
    ↓
Redis
    ↓
Cached Data / Session Data
```

Redis is used to improve performance by reducing unnecessary database operations.

## 8. Database Architecture

PostgreSQL is used as the primary database because it provides:
- ACID transaction guarantees
- Strong relational modeling
- Advanced SQL capabilities
- Reliable data consistency
- Mature ecosystem

Database responsibilities are separated by business modules.

**Example:**

**Products**
- products
- categories
- stock_movements

**Finances**
- transactions
- expenses
- debts

**Parties**
- parties
- contacts

**Security**
- users
- roles
- sessions

Modules communicate through defined interfaces instead of directly accessing each other's internal data.

## 9. Security Architecture

### Authentication Flow

The authentication process:

1. User submits credentials to: `/api/auth/login`
2. Security module validates credentials.
3. Access token and refresh token are generated.
4. Tokens are returned to the client.
5. Client sends the access token with future requests.
6. Security module validates the token before allowing access.

### Authorization

The system uses:

**Role-Based Access Control (RBAC)**
- Users are assigned roles with specific authorities.

**Method-Level Security**
- Sensitive operations are protected using Spring Security annotations.
- Example: `@PreAuthorize(...)`

**Module-Level Security**
- Each module can define its own security rules while respecting the global security layer.

## 10. Performance Considerations

### Database Optimization

The system uses:

**Connection Pooling**
- HikariCP is used for efficient database connection management.

**Indexing**
- Strategic indexes are created for frequently queried columns.

**Query Optimization**
- Hibernate query optimization and SQL logging are used to monitor database performance.

### Caching Strategy

Redis is used for:
- Frequently accessed data caching
- Session storage
- Temporary authentication data

The caching strategy reduces database load and improves response time.

## 11. Monitoring and Observability

MeatShop provides observability through metrics collection and centralized logging.

### Metrics Pipeline

```
Application
    ↓
Spring Boot Actuator
    ↓
Micrometer
    ↓
Prometheus
    ↓
Grafana
```

Used for:
- Application health monitoring
- Performance tracking
- Business metrics analysis

### Logging Pipeline

```
Application Logs
    ↓
Alloy
    ↓
Loki
    ↓
Grafana
```

Structured logging helps with debugging, monitoring, and analyzing application behavior.

## 12. Architectural Decisions

### Why Modular Monolith?

A modular monolith was chosen because it provides:

- **Simpler Development**: A single deployment unit simplifies development and testing.
- **Better Performance**: Modules communicate internally without network latency.
- **Clear Business Boundaries**: Spring Modulith helps enforce module boundaries and dependencies.
- **Easier Deployment**: The system can be deployed as one application unit.
- **Future Migration**: Modules can be extracted into microservices if future requirements justify it.

### Why PostgreSQL?

PostgreSQL was selected because of:
- Strong consistency guarantees
- ACID transactions
- Advanced SQL features
- Reliable relational modeling
- Mature ecosystem

### Why Redis?

Redis was selected because of:
- Fast in-memory access
- Efficient caching
- Session management support
- Improved application performance

### Why JWT?

JWT was selected because it provides:
- Stateless authentication
- Horizontal scalability
- Standard token-based security
- Flexible authorization claims

## 13. Future Improvements

Planned improvements:
- Advanced Spring Security features
- Two-factor authentication
- Enhanced audit logging
- Kubernetes deployment
- Further observability improvements
- Extracting modules into microservices when required

## Conclusion

MeatShop architecture focuses on maintainability, scalability, and clear separation of business responsibilities. The Modular Monolith approach provides a balance between structured architecture and development simplicity while keeping the system flexible for future growth.
