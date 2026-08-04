# Database Design

## Overview

MeatShop uses **PostgreSQL 17** as its primary relational database and **Flyway** for database versioning and schema
migration.

The database is designed around **business domains** rather than technical layers. Every domain owns its data and
exposes its functionality through application services instead of allowing unrestricted access to internal tables.

Although the application currently runs as a **Modular Monolith**, the database structure has been intentionally
designed to support a smooth migration toward a **Microservices Architecture** in the future.

---

# Design Principles

## Domain Ownership

Each business module owns its own database objects.

A domain is responsible for creating, updating, and maintaining its own tables without exposing its internal
implementation to other modules.

This keeps business logic isolated and greatly reduces coupling between modules.

---

## Loose Coupling

Communication between domains relies primarily on identifiers instead of shared entities.

Rather than exposing complete object graphs, modules exchange only the information required to complete a business
operation.

Whenever additional information is needed, it is retrieved through the owning service instead of directly accessing
another domain's data.

This design keeps boundaries clean and simplifies future service extraction.

---

## High Cohesion

Each domain groups together tables that represent a single business responsibility.

For example:

- Product management owns product-related data.
- Financial operations own invoices and payments.
- Security owns authentication and authorization data.

Keeping related data together improves maintainability and readability.

---

## Data Integrity

Data consistency is enforced using both application-level validation and database constraints.

The database makes extensive use of:

- Primary Keys
- Foreign Keys
- Unique Constraints
- Check Constraints
- Database Indexes

These constraints ensure that invalid or inconsistent data cannot be persisted.

---

## Scalability

Although all domains currently share a single PostgreSQL database, each domain already owns its schema boundaries.

This makes it possible to gradually migrate toward a **Database per Service** model without redesigning the entire
persistence layer.

---

# Technologies

## PostgreSQL 17

PostgreSQL was selected because it provides:

- ACID-compliant transactions
- High reliability
- Excellent performance
- Rich indexing capabilities
- Advanced SQL features
- Long-term stability

Its strong transactional guarantees make it well suited for financial and inventory management systems.

---

## Flyway

Database schema changes are managed using Flyway.

Every structural modification is stored as a versioned migration script, allowing every environment to remain
synchronized.

Migration scripts are located under:

```text
src/main/resources/db/migration
```

Migration naming convention:

```text
V{version}__description.sql
```

Example:

```text
V7__create_invoice_table.sql
```

---

# Domain Organization

The database is organized into independent business domains.

## Product Domain

Responsible for managing the product catalog and inventory structure.

### Owns

- Categories
- Products
- Product Components
- Stock Movements

### Responsibilities

- Product definitions
- Product composition
- Product categorization
- Inventory movement tracking

---

## Party Domain

Represents every business entity interacting with the system.

### Owns

- Parties
- Party Contacts

### Responsibilities

- Customers
- Suppliers
- Employees
- Future business entities

Using a unified Party model eliminates duplicated information and allows new actor types to be introduced without
changing the database structure.

---

## Financial Domain

Responsible for commercial transactions.

### Owns

- Invoices
- Invoice Components
- Cash Transactions

### Responsibilities

- Sales
- Purchases
- Invoice generation
- Payment tracking
- Financial history

---

## Employee Domain

Stores employee-specific business information.

### Responsibilities

- Employee information
- Employment status
- Salary
- Internal business data

Authentication data is intentionally **not stored inside the Employee domain**.

---

## Security Domain

Authentication and authorization are completely isolated from business logic.

### Owns

- Sessions
- Roles
- Authorities
- Party Roles
- Party Authorities

The Security module authenticates **business identities**, not specific domain entities.

Instead of depending directly on Employee objects, authentication relies on:

- Party Type
- Party Identifier

This makes the security module reusable for future domains such as:

- Customers
- Suppliers
- External APIs
- Service Accounts

without modifying the authentication infrastructure.

---

# Cross-Domain Communication

Business domains communicate using service boundaries.

Instead of exposing tables directly, a domain exposes business operations.

Whenever a module needs information owned by another domain, it requests it through the corresponding application
service.

This preserves encapsulation and prepares the system for future distributed deployment.

---

# Session Management

Authentication sessions are persisted independently from business entities.

Each session stores information such as:

- Refresh Token Hash
- Authentication State
- Device Fingerprint
- Trust Score
- Creation Time
- Last Activity
- Expiration Time

Only hashed refresh tokens are stored, reducing the impact of database leaks.

The session model also supports:

- Multiple active devices
- Session revocation
- Device tracking
- Future adaptive authentication
- Risk-based security policies

---

# Indexing Strategy

Indexes are created according to application access patterns.

Priority is given to:

- Foreign key relationships
- Authentication queries
- Search operations
- Frequently filtered columns
- Join performance

This strategy balances read performance with efficient write operations.

---

# Auditing

Most business entities include auditing information.

Common audit fields include:

- Created At
- Updated At

These timestamps support:

- Change tracking
- Debugging
- Reporting
- Future audit logging

---

# Migration Strategy

Database evolution is managed through Flyway.

Each structural modification is introduced as an independent migration, ensuring that every deployment follows the exact
same upgrade path.

This provides:

- Version consistency
- Automated deployments
- Reliable upgrades
- Easier rollback planning

---

# Future Architecture

The current database structure intentionally prepares the project for future architectural evolution.

The design supports migration toward:

- Microservices
- Database per Service
- Event-Driven Architecture
- Horizontal Scaling
- Distributed Authentication
- Saga-based workflows

Because each business domain already owns its data and communicates through clear boundaries, the migration can be
performed incrementally instead of requiring a complete redesign.

---

# Design Goals

The database has been designed with the following objectives:

- Clear business domain separation.
- High cohesion inside every module.
- Low coupling between domains.
- Strong data integrity.
- Maintainable schema evolution.
- Production-ready reliability.
- Long-term scalability.
- Smooth transition toward a Microservices Architecture.

---

# Summary

The MeatShop database is more than a collection of tables.

It is a domain-oriented persistence layer designed to reflect the application's modular architecture while remaining
scalable, maintainable, and ready for future evolution.

By separating responsibilities, enforcing strict ownership, and minimizing coupling between domains, the database
provides a solid foundation for both the current Modular Monolith implementation and the planned transition to
Microservices.