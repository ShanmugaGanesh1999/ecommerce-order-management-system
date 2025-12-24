# E-Commerce Order Management System - Architecture

## Overview
This is a production-grade microservices-based E-Commerce Order Management System built with modern technologies and best practices.

## Technology Stack

### Frontend
- **Framework**: Angular 18+
- **UI Library**: Bootstrap 5
- **Styling**: SCSS
- **Build Tool**: npm 10+
- **Port**: 4200

### Backend
- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.3+
- **API Gateway**: Spring Cloud Gateway 4.1+
- **Database**: MySQL 8.0+
- **Build Tool**: Maven 3.9+

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Security**: JWT (RS256), BCrypt password hashing

## System Architecture

```
┌─────────────────────┐
│  Angular Frontend   │ (Port 4200)
│    (Single Page)    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────┐
│   API Gateway Service       │ (Port 8080)
│  (Request Router + JWT)     │
└──────┬──────┬──────┬────────┘
       │      │      │
    ▼  ▼      ▼      ▼
┌──────────┐ ┌──────────┐ ┌──────────────┐
│ Product  │ │ Order    │ │ Customer     │
│ Service  │ │ Service  │ │ Service      │
│ (8081)   │ │ (8082)   │ │ (8083)       │
└──────────┘ └──────────┘ └──────────────┘
       │            │              │
       ▼            ▼              ▼
    MySQL        MySQL          MySQL
  product_db   order_db     customer_db
```

## Microservices

### 1. Customer Service (Port 8083)
**Responsibility**: User authentication, authorization, and customer profile management

**Database**: customer_db

**Entities**:
- Customer: id, firstName, lastName, email (unique), password (BCrypt), phone, role, createdAt, updatedAt
- Address: id, customerId, street, city, state, zipCode, country, isDefault, createdAt

**Key Features**:
- JWT token generation with RS256 algorithm
- Token expiration: 24 hours access, 30 days refresh
- BCrypt password hashing (salt rounds: 12)
- Password validation: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char

**API Endpoints**:
- POST /api/v1/auth/register - Customer registration
- POST /api/v1/auth/login - Login and return JWT
- POST /api/v1/auth/refresh - Refresh expired token
- GET /api/v1/customers/profile - Get current user profile
- PUT /api/v1/customers/profile - Update profile
- GET /api/v1/customers/{id}/addresses - Get customer addresses
- POST /api/v1/customers/{id}/addresses - Add new address
- PUT /api/v1/addresses/{id} - Update address
- DELETE /api/v1/addresses/{id} - Delete address

### 2. Product Service (Port 8081)
**Responsibility**: Product catalog and category management

**Database**: product_db

**Entities**:
- Category: id, name (unique), description, createdAt, updatedAt
- Product: id, name, description, price, stockQuantity, categoryId, imageUrl, sku (unique), isActive, createdAt, updatedAt, @Version

**Key Features**:
- Dynamic filtering with Spring Data JPA Specifications
- Multi-column sorting
- Pagination
- Optimistic locking for concurrent updates
- Soft delete for products

**API Endpoints**:
- GET /api/v1/products - List products with filters
- GET /api/v1/products/{id} - Get product details
- GET /api/v1/products/search - Search products
- GET /api/v1/products/category/{categoryId} - Products by category
- GET /api/v1/categories - List categories
- POST /api/v1/products - Create product (Admin)
- PUT /api/v1/products/{id} - Update product (Admin)
- DELETE /api/v1/products/{id} - Soft delete (Admin)

### 3. Order Service (Port 8082)
**Responsibility**: Order creation, management, and processing

**Database**: order_db

**Entities**:
- Order: id, customerId, orderDate, totalAmount, status, shippingAddress, notes, updatedAt, @Version
- OrderItem: id, orderId, productId, productName, quantity, price, subtotal

**Key Features**:
- Product availability validation via Product Service
- Order total calculation
- Order status transitions
- Circuit breaker pattern for inter-service calls
- Transaction management

**API Endpoints**:
- POST /api/v1/orders - Create new order
- GET /api/v1/orders/{id} - Get order details
- GET /api/v1/orders/customer/{customerId} - Customer's orders
- GET /api/v1/orders - List all orders (Admin)
- PUT /api/v1/orders/{id}/status - Update order status (Admin)

### 4. API Gateway Service (Port 8080)
**Responsibility**: Request routing, JWT validation, and cross-cutting concerns

**Key Features**:
- Route requests to appropriate microservices
- JWT validation filter
- CORS configuration (allow http://localhost:4200)
- Request logging
- Error handling (503, 504)
- Circuit breaker pattern

**Routing**:
- /api/v1/products/** → Product Service (8081)
- /api/v1/categories/** → Product Service (8081)
- /api/v1/orders/** → Order Service (8082)
- /api/v1/customers/** → Customer Service (8083)
- /api/v1/auth/** → Customer Service (8083)
- /api/v1/addresses/** → Customer Service (8083)

## Security Architecture

### Authentication Flow
1. User sends credentials to `/api/v1/auth/login` via API Gateway
2. Customer Service validates credentials
3. If valid, generates JWT token signed with RS256
4. Token returned to client with user information
5. Client stores token securely
6. Subsequent requests include token in Authorization header
7. API Gateway validates token before routing requests

### Authorization
- **Roles**: CUSTOMER, ADMIN
- **JWT Claims**: userId, email, role, exp, iat
- **Token Validation**: RS256 signature verification

### Password Security
- BCrypt hashing with 12 salt rounds
- Password requirements enforced at registration
- No password stored in plain text

## Database Design

### Database per Service Pattern
Each microservice has its own database to ensure:
- Service independence
- Data isolation
- Scalability
- Technology flexibility

### Customer Database (customer_db)
```sql
customers (id, first_name, last_name, email*, password, phone, role, created_at, updated_at)
addresses (id, customer_id, street, city, state, zip_code, country, is_default, created_at)
```

### Product Database (product_db)
```sql
categories (id, name*, description, created_at, updated_at)
products (id, name, description, price, stock_quantity, category_id, image_url, sku*, is_active, created_at, updated_at, version)
```

### Order Database (order_db)
```sql
orders (id, customer_id, order_date, total_amount, status, shipping_address, notes, updated_at, version)
order_items (id, order_id, product_id, product_name, quantity, price, subtotal)
```

*Unique constraint

## Communication Patterns

### Synchronous Communication
- REST APIs for client-service communication
- HTTP for inter-service communication (Order → Product)
- Circuit breaker pattern for resilience

### Data Consistency
- Optimistic locking with @Version for concurrent updates
- Transaction management within service boundaries
- Eventual consistency for cross-service operations

## Deployment

### Docker Compose
All services are containerized and orchestrated using Docker Compose:
- 3 MySQL databases (one per service)
- 4 Spring Boot microservices
- 1 Angular frontend (nginx)
- Shared network: ecommerce-network
- Volume persistence for databases

### Environment Configuration
- Database URLs and credentials via environment variables
- Service discovery through Docker network
- Configurable JWT secrets
- Port mapping for local development

## Quality Attributes

### Scalability
- Microservices can be scaled independently
- Stateless services (JWT for session management)
- Database per service pattern

### Reliability
- Circuit breaker for inter-service calls
- Health checks for all services
- Transaction management
- Optimistic locking

### Security
- JWT authentication
- Password hashing
- Input validation
- CORS configuration
- Security headers
- Rate limiting

### Maintainability
- Clear separation of concerns
- Comprehensive documentation
- Unit and integration tests (80%+ coverage)
- Consistent code style
- Logging and monitoring

## Development Guidelines

### Backend
- Follow Spring Boot best practices
- Use constructor injection for dependencies
- Implement proper exception handling
- Add comprehensive JavaDoc comments
- Write unit and integration tests
- Use SLF4J for logging

### Frontend
- Follow Angular style guide
- Use TypeScript strict mode
- Implement lazy loading for feature modules
- Write unit tests with Jasmine
- Use reactive programming with RxJS
- Follow accessibility guidelines (WCAG 2.1 AA)

### API Design
- RESTful conventions
- Proper HTTP status codes
- Pagination for list endpoints
- Filtering and sorting support
- OpenAPI/Swagger documentation
- Versioned endpoints (/api/v1/)

## Testing Strategy

### Backend (80%+ coverage)
- Unit tests with JUnit 5 + Mockito
- Integration tests with @SpringBootTest
- Controller tests with MockMvc
- Security tests for auth endpoints

### Frontend (75%+ coverage)
- Unit tests with Jasmine
- Component tests with Angular Testing Utilities
- E2E tests with Cypress

## Monitoring and Observability

### Logging
- Centralized logging with SLF4J/Logback
- Log important operations
- Include correlation IDs
- Log levels: ERROR, WARN, INFO, DEBUG

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- Liveness and readiness probes

## Future Enhancements

### Phase 2 Features
- Event-driven architecture with message queues
- Notification service (email, SMS)
- Payment gateway integration
- Inventory service
- Real-time order tracking
- Product reviews and ratings
- Advanced search with Elasticsearch
- Caching with Redis
- API rate limiting
- Distributed tracing
