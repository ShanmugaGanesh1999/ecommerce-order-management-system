# Quick Start Guide - E-Commerce Order Management System

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### 1. Start the Customer Service

```bash
# Clone the repository
git clone https://github.com/ShanmugaGanesh1999/ecommerce-order-management-system.git
cd ecommerce-order-management-system

# Start MySQL database
docker-compose up -d mysql-customer

# Wait 30 seconds for MySQL to initialize, then run the service
cd backend/customer-service
mvn spring-boot:run
```

### 2. Test the APIs

Open another terminal and try these commands:

**Register a new customer:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@example.com",
    "password": "SecurePass@123",
    "phone": "+1987654321"
  }'
```

**Login to get JWT token:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "SecurePass@123"
  }'
```

Copy the `accessToken` from the response.

**Get customer profile:**
```bash
curl http://localhost:8083/api/v1/customers/1/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### 3. Explore with Swagger UI

Open your browser and navigate to:
```
http://localhost:8083/swagger-ui.html
```

You can test all APIs interactively with Swagger UI.

## 📚 Documentation

- **README.md** - Project overview and setup
- **ARCHITECTURE.md** - System design and architecture
- **DEVELOPER_GUIDE.md** - Development workflow and best practices
- **PROJECT_SUMMARY.md** - Complete implementation status
- **IMPLEMENTATION_STATUS.md** - Detailed progress tracking

## 🐳 Docker Deployment

To run everything with Docker:

```bash
# Build and start all services
docker-compose up --build

# Access services:
# - Customer Service: http://localhost:8083
# - Swagger UI: http://localhost:8083/swagger-ui.html
```

## 🎯 What's Implemented

✅ **Customer Service** - Fully functional
- User registration and authentication (JWT)
- Customer profile management
- Address management
- Password security (BCrypt)
- Input validation
- Error handling

## 🔜 What's Next

The foundation is complete! To finish the system:
1. Complete Product Service
2. Complete Order Service
3. Complete API Gateway
4. Build Angular frontend
5. Add tests

See `PROJECT_SUMMARY.md` for detailed next steps.

## 💡 Tips

- Check service health: `curl http://localhost:8083/actuator/health`
- View logs: `docker-compose logs -f customer-service`
- Stop services: `docker-compose down`
- Clean everything: `docker-compose down -v`

## 🐛 Troubleshooting

**Port already in use?**
```bash
# Change the port in application.properties or stop the conflicting service
lsof -i :8083
```

**Database connection error?**
```bash
# Make sure MySQL is running
docker-compose ps mysql-customer
# Restart if needed
docker-compose restart mysql-customer
```

**Build failed?**
```bash
# Clean and rebuild
mvn clean install -U
```

## 🆘 Need Help?

Check the comprehensive guides:
- Development workflow → `DEVELOPER_GUIDE.md`
- Architecture questions → `ARCHITECTURE.md`
- Implementation details → `PROJECT_SUMMARY.md`

Enjoy building your e-commerce system! 🛒
