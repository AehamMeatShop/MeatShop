# API Documentation

## Overview
MeatShop exposes a RESTful API for all operations. The API follows REST principles and uses JWT-based authentication for secure access. This document provides comprehensive information about all available endpoints.

## Base URL
- **Development**: `http://localhost:8080`
- **Production**: Configured via environment variables

## Authentication
All protected endpoints require JWT authentication. Include the access token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

## Response Format
All responses follow a consistent JSON format:

```json
{
  "data": {},
  "message": "Success",
  "status": "SUCCESS"
}
```

## Error Handling
Error responses follow this format:

```json
{
  "message": "Error description",
  "status": "ERROR",
  "errors": []
}
```

### HTTP Status Codes
- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `202 Accepted`: Request accepted for processing
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## API Endpoints

### Authentication Endpoints

#### Login
**Endpoint**: `POST /auth/login`

**Description**: Authenticate user and receive JWT tokens

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

**Status**: `202 ACCEPTED`

#### Refresh Token
**Endpoint**: `POST /auth/refresh`

**Description**: Refresh access token using refresh token

**Request Body**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900
}
```

**Status**: `202 ACCEPTED`

#### Logout
**Endpoint**: `GET /auth/log-out`

**Description**: Logout user and invalidate session

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "message": "Logged out successfully"
}
```

**Status**: `202 ACCEPTED`

**Authentication**: Required

### Employee Endpoints

#### Get All Employees
**Endpoint**: `GET /employees`

**Description**: Retrieve all employees with pagination

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sort`: Sort field and direction (e.g., `id,desc`)

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "salary": 50000,
      "partyId": 1,
      "status": "ACTIVE",
      "email": "employee@example.com",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Get Employee by ID
**Endpoint**: `GET /employees/{id}`

**Description**: Retrieve specific employee by ID

**Path Parameters**:
- `id`: Employee ID

**Response**:
```json
{
  "id": 1,
  "salary": 50000,
  "partyId": 1,
  "status": "ACTIVE",
  "email": "employee@example.com",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Employee
**Endpoint**: `POST /employees`

**Description**: Create a new employee

**Request Body**:
```json
{
  "salary": 50000,
  "partyId": 1,
  "status": "ACTIVE",
  "email": "employee@example.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "id": 1,
  "salary": 50000,
  "partyId": 1,
  "status": "ACTIVE",
  "email": "employee@example.com",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Update Employee
**Endpoint**: `PUT /employees/{id}`

**Description**: Update existing employee

**Path Parameters**:
- `id`: Employee ID

**Request Body**:
```json
{
  "salary": 55000,
  "status": "ACTIVE",
  "email": "updated@example.com"
}
```

**Response**:
```json
{
  "id": 1,
  "salary": 55000,
  "partyId": 1,
  "status": "ACTIVE",
  "email": "updated@example.com",
  "updatedAt": "2024-01-02T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Delete Employee
**Endpoint**: `DELETE /employees/{id}`

**Description**: Delete employee by ID

**Path Parameters**:
- `id`: Employee ID

**Response**:
```json
{
  "message": "Employee deleted successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Product Endpoints

#### Get All Products
**Endpoint**: `GET /products`

**Description**: Retrieve all products with pagination and filtering

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sort`: Sort field and direction
- `name`: Filter by product name (partial match)
- `categoryId`: Filter by category ID
- `productType`: Filter by product type

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "productName": "Beef Steak",
      "description": "Premium beef steak",
      "categoryId": 1,
      "productType": "RAW_MEAT",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Get Product by ID
**Endpoint**: `GET /products/{id}`

**Description**: Retrieve specific product by ID

**Path Parameters**:
- `id`: Product ID

**Response**:
```json
{
  "id": 1,
  "productName": "Beef Steak",
  "description": "Premium beef steak",
  "categoryId": 1,
  "productType": "RAW_MEAT",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Product
**Endpoint**: `POST /products`

**Description**: Create a new product

**Request Body**:
```json
{
  "productName": "Beef Steak",
  "description": "Premium beef steak",
  "categoryId": 1,
  "productType": "RAW_MEAT"
}
```

**Response**:
```json
{
  "id": 1,
  "productName": "Beef Steak",
  "description": "Premium beef steak",
  "categoryId": 1,
  "productType": "RAW_MEAT",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Update Product
**Endpoint**: `PUT /products/{id}`

**Description**: Update existing product

**Path Parameters**:
- `id`: Product ID

**Request Body**:
```json
{
  "productName": "Premium Beef Steak",
  "description": "Premium quality beef steak",
  "productType": "RAW_MEAT"
}
```

**Response**:
```json
{
  "id": 1,
  "productName": "Premium Beef Steak",
  "description": "Premium quality beef steak",
  "categoryId": 1,
  "productType": "RAW_MEAT",
  "updatedAt": "2024-01-02T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Delete Product
**Endpoint**: `DELETE /products/{id}`

**Description**: Delete product by ID

**Path Parameters**:
- `id`: Product ID

**Response**:
```json
{
  "message": "Product deleted successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Category Endpoints

#### Get All Categories
**Endpoint**: `GET /categories`

**Description**: Retrieve all categories

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Raw Meat",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ]
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Get Category by ID
**Endpoint**: `GET /categories/{id}`

**Description**: Retrieve specific category by ID

**Path Parameters**:
- `id`: Category ID

**Response**:
```json
{
  "id": 1,
  "name": "Raw Meat",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Category
**Endpoint**: `POST /categories`

**Description**: Create a new category

**Request Body**:
```json
{
  "name": "Processed Meat"
}
```

**Response**:
```json
{
  "id": 2,
  "name": "Processed Meat",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Update Category
**Endpoint**: `PUT /categories/{id}`

**Description**: Update existing category

**Path Parameters**:
- `id`: Category ID

**Request Body**:
```json
{
  "name": "Premium Processed Meat"
}
```

**Response**:
```json
{
  "id": 2,
  "name": "Premium Processed Meat",
  "updatedAt": "2024-01-02T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Delete Category
**Endpoint**: `DELETE /categories/{id}`

**Description**: Delete category by ID

**Path Parameters**:
- `id`: Category ID

**Response**:
```json
{
  "message": "Category deleted successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Product Component Endpoints

#### Get Product Components
**Endpoint**: `GET /product-components`

**Description**: Retrieve product components with pagination

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `productId`: Filter by product ID

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "productId": 1,
      "componentId": 2,
      "ratioInKg": 0.5,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 20,
  "totalPages": 2,
  "currentPage": 0
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Product Component
**Endpoint**: `POST /product-components`

**Description**: Create a new product component relationship

**Request Body**:
```json
{
  "productId": 1,
  "componentId": 2,
  "ratioInKg": 0.5
}
```

**Response**:
```json
{
  "id": 1,
  "productId": 1,
  "componentId": 2,
  "ratioInKg": 0.5,
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Update Product Component
**Endpoint**: `PUT /product-components/{id}`

**Description**: Update product component

**Path Parameters**:
- `id`: Product component ID

**Request Body**:
```json
{
  "ratioInKg": 0.6
}
```

**Response**:
```json
{
  "id": 1,
  "productId": 1,
  "componentId": 2,
  "ratioInKg": 0.6,
  "updatedAt": "2024-01-02T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Delete Product Component
**Endpoint**: `DELETE /product-components/{id}`

**Description**: Delete product component

**Path Parameters**:
- `id`: Product component ID

**Response**:
```json
{
  "message": "Product component deleted successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Stock Movement Endpoints

#### Get Stock Movements
**Endpoint**: `GET /stock-movements`

**Description**: Retrieve stock movements with pagination

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `productId`: Filter by product ID
- `movementType`: Filter by movement type (IN/OUT)
- `startDate`: Filter by start date
- `endDate`: Filter by end date

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "productId": 1,
      "movementType": "IN",
      "quantity": 100.5,
      "movementDate": "2024-01-01T00:00:00",
      "notes": "Stock replenishment",
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 150,
  "totalPages": 15,
  "currentPage": 0
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Stock Movement
**Endpoint**: `POST /stock-movements`

**Description**: Create a new stock movement

**Request Body**:
```json
{
  "productId": 1,
  "movementType": "IN",
  "quantity": 100.5,
  "movementDate": "2024-01-01T00:00:00",
  "notes": "Stock replenishment"
}
```

**Response**:
```json
{
  "id": 1,
  "productId": 1,
  "movementType": "IN",
  "quantity": 100.5,
  "movementDate": "2024-01-01T00:00:00",
  "notes": "Stock replenishment",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

### Party Endpoints

#### Get All Parties
**Endpoint**: `GET /parties`

**Description**: Retrieve all parties (customers/suppliers) with pagination

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `partyType`: Filter by party type (CUSTOMER/SUPPLIER)
- `name`: Filter by party name (partial match)

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "partyName": "John Doe",
      "partyAddress": "123 Main St",
      "partyType": "CUSTOMER",
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 75,
  "totalPages": 8,
  "currentPage": 0
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Get Party by ID
**Endpoint**: `GET /parties/{id}`

**Description**: Retrieve specific party by ID

**Path Parameters**:
- `id`: Party ID

**Response**:
```json
{
  "id": 1,
  "partyName": "John Doe",
  "partyAddress": "123 Main St",
  "partyType": "CUSTOMER",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Party
**Endpoint**: `POST /parties`

**Description**: Create a new party

**Request Body**:
```json
{
  "partyName": "Jane Smith",
  "partyAddress": "456 Oak Ave",
  "partyType": "CUSTOMER"
}
```

**Response**:
```json
{
  "id": 2,
  "partyName": "Jane Smith",
  "partyAddress": "456 Oak Ave",
  "partyType": "CUSTOMER",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Update Party
**Endpoint**: `PUT /parties/{id}`

**Description**: Update existing party

**Path Parameters**:
- `id`: Party ID

**Request Body**:
```json
{
  "partyName": "Jane Smith",
  "partyAddress": "789 Pine Rd",
  "partyType": "CUSTOMER"
}
```

**Response**:
```json
{
  "id": 2,
  "partyName": "Jane Smith",
  "partyAddress": "789 Pine Rd",
  "partyType": "CUSTOMER",
  "updatedAt": "2024-01-02T00:00:00"
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Delete Party
**Endpoint**: `DELETE /parties/{id}`

**Description**: Delete party by ID

**Path Parameters**:
- `id`: Party ID

**Response**:
```json
{
  "message": "Party deleted successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Role Endpoints

#### Get All Roles
**Endpoint**: `GET /roles`

**Description**: Retrieve all roles

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "roleName": "ADMIN",
      "description": "Administrator role"
    },
    {
      "id": 2,
      "roleName": "MANAGER",
      "description": "Manager role"
    }
  ]
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Role
**Endpoint**: `POST /roles`

**Description**: Create a new role

**Request Body**:
```json
{
  "roleName": "EMPLOYEE",
  "description": "Employee role"
}
```

**Response**:
```json
{
  "id": 3,
  "roleName": "EMPLOYEE",
  "description": "Employee role",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

#### Assign Authority to Role
**Endpoint**: `POST /roles/{roleId}/authorities/{authorityId}`

**Description**: Assign authority to role

**Path Parameters**:
- `roleId`: Role ID
- `authorityId`: Authority ID

**Response**:
```json
{
  "message": "Authority assigned to role successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

### Authority Endpoints

#### Get All Authorities
**Endpoint**: `GET /authorities`

**Description**: Retrieve all authorities

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "authorityName": "READ_PRODUCTS",
      "description": "Read products"
    },
    {
      "id": 2,
      "authorityName": "WRITE_PRODUCTS",
      "description": "Write products"
    }
  ]
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Create Authority
**Endpoint**: `POST /authorities`

**Description**: Create a new authority

**Request Body**:
```json
{
  "authorityName": "DELETE_PRODUCTS",
  "description": "Delete products"
}
```

**Response**:
```json
{
  "id": 3,
  "authorityName": "DELETE_PRODUCTS",
  "description": "Delete products",
  "createdAt": "2024-01-01T00:00:00"
}
```

**Status**: `201 CREATED`

**Authentication**: Required

### Session Endpoints

#### Get User Sessions
**Endpoint**: `GET /sessions`

**Description**: Retrieve current user's sessions

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "partyType": "EMPLOYEE",
      "partyId": 1,
      "state": "ACTIVE",
      "deviceId": "device-123",
      "trustScore": 100,
      "createdAt": "2024-01-01T00:00:00",
      "lastSeenAt": "2024-01-01T12:00:00"
    }
  ]
}
```

**Status**: `200 OK`

**Authentication**: Required

#### Revoke Session
**Endpoint**: `DELETE /sessions/{id}`

**Description**: Revoke specific session

**Path Parameters**:
- `id`: Session ID

**Response**:
```json
{
  "message": "Session revoked successfully"
}
```

**Status**: `200 OK`

**Authentication**: Required

## Postman Collection

A complete Postman collection is available in the `docs` folder:
- **File**: `MeatShop.postman_collection.json`
- **Environment**: `MeatShop.postman_environment.json`

Import these files into Postman to test all endpoints with pre-configured environments.

## API Versioning

The API currently uses version 1. Future versions will be indicated in the URL path:
- Current: `/api/{endpoint}`
- Future: `/api/v1/{endpoint}`

## Rate Limiting

Rate limiting is implemented to prevent abuse:
- **Default Limit**: 100 requests per minute per user
- **Burst Limit**: 200 requests per minute per user
- **Headers**: Rate limit information is returned in response headers

## Pagination

All list endpoints support pagination:
- **page**: Page number (0-indexed)
- **size**: Number of items per page (default: 10, max: 100)
- **sort**: Sort field and direction (e.g., `id,desc`)

## Filtering and Search

Many endpoints support filtering and search:
- **Partial Match**: String fields support partial matching
- **Exact Match**: ID fields require exact match
- **Date Range**: Date fields support range filtering
- **Multiple Filters**: Combine multiple filters with AND logic

## Validation

All endpoints perform input validation:
- **Required Fields**: Marked as required in request body
- **Data Types**: Strict type checking
- **Format Validation**: Email, phone number format validation
- **Business Rules**: Business logic validation

## Error Codes

### Common Error Codes
- `AUTH_001`: Invalid credentials
- `AUTH_002`: Token expired
- `AUTH_003`: Token invalid
- `AUTH_004`: Session not found
- `VAL_001`: Validation error
- `VAL_002`: Required field missing
- `VAL_003`: Invalid data format
- `RES_001`: Resource not found
- `RES_002`: Resource already exists
- `PERM_001`: Insufficient permissions
- `SRV_001`: Internal server error

## WebSocket Endpoints

### Real-time Updates
**Endpoint**: `ws://localhost:8080/ws`

**Description**: WebSocket endpoint for real-time updates

**Authentication**: JWT token in query parameter or header

**Events**:
- `stock_update`: Stock level changes
- `invoice_created`: New invoice created
- `payment_received`: Payment received

## Manual Testing
Use the provided Postman collection for manual testing.

## Security Considerations

### HTTPS
Always use HTTPS in production environments.

### Input Sanitization
All inputs are sanitized to prevent injection attacks.

### SQL Injection Prevention
Parameterized queries prevent SQL injection.

### XSS Prevention
Output encoding prevents XSS attacks.

### CSRF Protection
CSRF tokens protect against CSRF attacks.

## Support

For API support and questions:
- **Documentation**: See `/docs` folder
- **Issues**: Create an issue in the repository
- **Contact**: Contact the development team
