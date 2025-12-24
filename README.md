# E-Commerce Order Management System

A production-grade microservices-based e-commerce platform built with Spring Boot, Angular, and MySQL.

## 🏗️ Architecture

This system follows a microservices architecture with the following components:

- **Frontend**: Angular 18+ (Port 4200)
- **API Gateway**: Spring Cloud Gateway (Port 8080)
- **Customer Service**: Spring Boot (Port 8083) - Authentication & Customer Management
- **Product Service**: Spring Boot (Port 8081) - Product Catalog & Categories
- **Order Service**: Spring Boot (Port 8082) - Order Processing & Management
- **Databases**: MySQL 8.0+ (separate database per service)

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed system design documentation.

## 🚀 Features

### Customer Management
- User registration and authentication with JWT (RS256)
- Password hashing with BCrypt (12 salt rounds)
- Profile management
- Multiple address support

### Product Catalog
- Product CRUD operations with pagination
- Category management
- Advanced filtering and search
- Dynamic sorting
- Optimistic locking for concurrent updates

### Order Management
- Order creation with product validation
- Order history and tracking
- Status management (PENDING → CONFIRMED → SHIPPED → DELIVERED)
- Inter-service communication with circuit breaker

### Security
- JWT-based authentication
- Role-based access control (CUSTOMER, ADMIN)
- CORS configuration
- Input validation
- Security headers

## 📋 Prerequisites

- **Java**: 21 LTS
- **Node.js**: 18+ with npm 10+
- **Maven**: 3.9+
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **MySQL**: 8.0+ (or use Docker Compose)

## 🛠️ Quick Start

### Option 1: Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/ShanmugaGanesh1999/ecommerce-order-management-system.git
cd ecommerce-order-management-system
```

2. Start all services:
```bash
docker-compose up -d
```

3. Wait for services to be healthy:
```bash
docker-compose ps
```

4. Access the application:
- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Option 2: Local Development

#### Backend Services

1. Start MySQL databases:
```bash
docker-compose up -d mysql-customer mysql-product mysql-order
```

2. Build all backend services:
```bash
cd backend
mvn clean install -DskipTests
```

3. Start each service (in separate terminals):
```bash
# Customer Service
cd backend/customer-service
mvn spring-boot:run

# Product Service
cd backend/product-service
mvn spring-boot:run

# Order Service
cd backend/order-service
mvn spring-boot:run

# API Gateway
cd backend/api-gateway-service
mvn spring-boot:run
```

#### Frontend Application

1. Install dependencies:
```bash
cd frontend/ecommerce-app
npm install
```

2. Start development server:
```bash
npm start
```

3. Access the application at http://localhost:4200

## 🧪 Testing

### Backend Tests

Run tests for all services:
```bash
cd backend
mvn test
```

Run tests for a specific service:
```bash
cd backend/customer-service
mvn test
```

Run integration tests:
```bash
cd backend
mvn verify
```

### Frontend Tests

Run unit tests:
```bash
cd frontend/ecommerce-app
npm test
```

Run E2E tests:
```bash
cd frontend/ecommerce-app
npm run e2e
```

## 📚 API Documentation

Once the services are running, access the API documentation:

- **API Gateway Swagger**: http://localhost:8080/swagger-ui.html
- **Customer Service**: http://localhost:8083/swagger-ui.html
- **Product Service**: http://localhost:8081/swagger-ui.html
- **Order Service**: http://localhost:8082/swagger-ui.html

## 🔐 Default Credentials

### Admin User
- **Email**: admin@ecommerce.com
- **Password**: Admin@123

### Test Customer
- **Email**: customer@test.com
- **Password**: Customer@123

## 🗂️ Project Structure

```
ecommerce-order-management-system/
├── backend/
│   ├── api-gateway-service/       # API Gateway (Port 8080)
│   ├── customer-service/          # Customer & Auth (Port 8083)
│   ├── product-service/           # Products & Categories (Port 8081)
│   ├── order-service/             # Orders (Port 8082)
│   └── docker-compose.yml         # Backend services compose
├── frontend/
│   └── ecommerce-app/             # Angular application (Port 4200)
├── docker-compose.yml             # Full stack compose
├── ARCHITECTURE.md                # System architecture documentation
└── README.md                      # This file
```

## 🔧 Configuration

### Environment Variables

Backend services can be configured using environment variables:

```bash
# Database
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=customer_db
MYSQL_USER=ecommerce_user
MYSQL_PASSWORD=ecommerce_password

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=2592000000

# Service URLs
PRODUCT_SERVICE_URL=http://localhost:8081
ORDER_SERVICE_URL=http://localhost:8082
CUSTOMER_SERVICE_URL=http://localhost:8083
```

### Frontend Configuration

Edit `frontend/ecommerce-app/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

## 📊 Database Schema

### Customer Database (customer_db)
- **customers**: User accounts with authentication
- **addresses**: Customer shipping addresses

### Product Database (product_db)
- **categories**: Product categories
- **products**: Product catalog with inventory

### Order Database (order_db)
- **orders**: Customer orders
- **order_items**: Individual items in orders

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed schema information.

## 🛡️ Security

- **Authentication**: JWT with RS256 algorithm
- **Password Hashing**: BCrypt with 12 salt rounds
- **Password Policy**: Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char
- **Token Expiration**: 24 hours (access), 30 days (refresh)
- **CORS**: Configured for http://localhost:4200
- **Input Validation**: Bean Validation API
- **Security Headers**: X-Content-Type-Options, X-Frame-Options, etc.

## 🐛 Troubleshooting

### Services not starting
1. Check if ports are available (8080-8083, 4200, 3306)
2. Verify Docker is running: `docker ps`
3. Check service logs: `docker-compose logs [service-name]`

### Database connection errors
1. Wait for MySQL to be ready (health check)
2. Verify database credentials in environment variables
3. Check if databases are created: `docker-compose exec mysql-customer mysql -u root -p`

### Frontend not connecting to backend
1. Verify API Gateway is running: `curl http://localhost:8080/actuator/health`
2. Check CORS configuration in API Gateway
3. Verify API URL in frontend environment configuration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **ShanmugaGanesh1999** - *Initial work*

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud documentation
- Angular documentation
- Docker documentation
- MySQL documentation