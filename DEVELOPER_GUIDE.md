# Developer Guide - E-Commerce Order Management System

## Quick Start

### Prerequisites
Ensure you have the following installed:
- **Java 21 LTS** - [Download](https://adoptium.net/)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **MySQL 8.0+** (optional, Docker Compose will provide it)

### Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/ShanmugaGanesh1999/ecommerce-order-management-system.git
cd ecommerce-order-management-system
```

#### 2. Start Databases Only (For Local Development)
```bash
docker-compose up -d mysql-customer mysql-product mysql-order
```

Wait for databases to be ready (~30 seconds):
```bash
docker-compose ps
```

#### 3. Run Customer Service Locally
```bash
cd backend/customer-service
mvn spring-boot:run
```

The service will start on http://localhost:8083

Test the service:
```bash
curl http://localhost:8083/actuator/health
```

#### 4. Test Customer Service APIs

**Register a new customer:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "Password@123",
    "phone": "+1234567890"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8083/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "Password@123"
  }'
```

Save the `accessToken` from the response.

**Get Profile:**
```bash
curl http://localhost:8083/api/v1/customers/1/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Running All Services with Docker Compose

#### Build and Start All Services
```bash
docker-compose up --build
```

This will:
1. Build all 4 backend services
2. Start 3 MySQL databases
3. Start API Gateway
4. Start frontend (when implemented)

#### Access Services
- **API Gateway**: http://localhost:8080
- **Customer Service**: http://localhost:8083
- **Product Service**: http://localhost:8081
- **Order Service**: http://localhost:8082
- **Frontend**: http://localhost:4200

#### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f customer-service
```

#### Stop All Services
```bash
docker-compose down
```

#### Clean Up Everything (Including Data)
```bash
docker-compose down -v
```

## Project Structure

```
ecommerce-order-management-system/
├── backend/
│   ├── customer-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/ecommerce/customer/
│   │   │   │   │   ├── entity/          # JPA entities
│   │   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   │   ├── repository/      # Spring Data repositories
│   │   │   │   │   ├── service/         # Business logic
│   │   │   │   │   ├── controller/      # REST controllers
│   │   │   │   │   ├── security/        # JWT utilities
│   │   │   │   │   ├── config/          # Spring configuration
│   │   │   │   │   └── exception/       # Exception handlers
│   │   │   │   └── resources/
│   │   │   │       └── application.properties
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── product-service/
│   ├── order-service/
│   └── api-gateway-service/
├── frontend/
│   └── ecommerce-app/               # Angular application
├── docker-compose.yml
├── ARCHITECTURE.md
├── IMPLEMENTATION_STATUS.md
├── DEVELOPER_GUIDE.md
└── README.md
```

## Development Workflow

### 1. Backend Development

#### Adding a New REST Endpoint

1. **Create DTO** (if needed):
```java
@Data
@Builder
public class NewFeatureRequest {
    @NotBlank
    private String field1;
}
```

2. **Add Service Method**:
```java
@Service
@RequiredArgsConstructor
public class YourService {
    public ResponseDto newFeature(NewFeatureRequest request) {
        // Business logic
        return ResponseDto.builder().build();
    }
}
```

3. **Add Controller Endpoint**:
```java
@RestController
@RequestMapping("/api/v1/resource")
public class YourController {
    private final YourService service;
    
    @PostMapping
    public ResponseEntity<ResponseDto> create(@Valid @RequestBody NewFeatureRequest request) {
        return ResponseEntity.ok(service.newFeature(request));
    }
}
```

#### Running Tests
```bash
cd backend/customer-service
mvn test
```

#### Building the Service
```bash
mvn clean package
```

The JAR file will be in `target/customer-service-1.0.0.jar`

### 2. Database Management

#### Connecting to MySQL Container
```bash
# Customer Database
docker-compose exec mysql-customer mysql -u ecommerce_user -p
# Password: ecommerce_password

# Show databases
SHOW DATABASES;
USE customer_db;
SHOW TABLES;

# View customer records
SELECT * FROM customers;
```

#### Database Migrations
Currently using JPA auto-DDL (`spring.jpa.hibernate.ddl-auto=update`).

For production, consider using Flyway or Liquibase:

**Add Flyway to pom.xml:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**Create migration file** in `src/main/resources/db/migration/`:
```sql
-- V1__initial_schema.sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    -- ...
);
```

### 3. API Documentation

#### Access Swagger UI
Each service has its own Swagger UI:
- Customer Service: http://localhost:8083/swagger-ui.html
- Product Service: http://localhost:8081/swagger-ui.html
- Order Service: http://localhost:8082/swagger-ui.html

#### OpenAPI Spec
- Customer Service: http://localhost:8083/v3/api-docs

### 4. Debugging

#### Debug Backend Service in IntelliJ IDEA
1. Open `backend/customer-service` as a Maven project
2. Create a Run Configuration:
   - Main class: `com.ecommerce.customer.CustomerServiceApplication`
   - VM options: `-Dspring.profiles.active=dev`
3. Set breakpoints and click Debug

#### View Service Logs
```bash
# Docker logs
docker-compose logs -f customer-service

# Local logs (if running with mvn spring-boot:run)
tail -f logs/application.log
```

#### Common Issues

**Port already in use:**
```bash
# Find process using port 8083
lsof -i :8083
# Kill the process
kill -9 <PID>
```

**Database connection error:**
```bash
# Check if MySQL is running
docker-compose ps mysql-customer
# Restart MySQL
docker-compose restart mysql-customer
```

**Build fails:**
```bash
# Clean Maven cache
mvn clean
rm -rf ~/.m2/repository
```

## Environment Configuration

### Development (application.properties)
```properties
spring.jpa.hibernate.ddl-auto=update
logging.level.com.ecommerce=DEBUG
```

### Production (application-prod.properties)
```properties
spring.jpa.hibernate.ddl-auto=validate
logging.level.com.ecommerce=INFO
spring.datasource.url=jdbc:mysql://production-db:3306/customer_db
```

Run with profile:
```bash
java -jar -Dspring.profiles.active=prod customer-service-1.0.0.jar
```

## Security Best Practices

### JWT Token Management
- **Never log tokens**
- **Rotate secrets regularly**
- **Use environment variables for secrets**
- **Implement token blacklist for logout**

### Password Security
- Minimum 8 characters
- At least 1 uppercase, 1 lowercase, 1 digit, 1 special character
- BCrypt with 12 salt rounds
- Never log passwords

### API Security
- Always validate input
- Use HTTPS in production
- Implement rate limiting
- Log security events

## Testing Strategy

### Unit Tests
Test individual components in isolation:
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void testRegister() {
        // Test logic
    }
}
```

### Integration Tests
Test the complete flow:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testRegisterEndpoint() {
        RegisterRequest request = new RegisterRequest(/* ... */);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            "/api/v1/auth/register", request, AuthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

### Running Tests
```bash
# All tests
mvn test

# Specific test
mvn test -Dtest=AuthServiceTest

# Integration tests only
mvn verify
```

## Performance Optimization

### Database
- Add indexes on frequently queried columns
- Use pagination for large datasets
- Implement database connection pooling
- Use read replicas for read-heavy operations

### Caching
Consider adding Redis:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### API Performance
- Use async processing for long operations
- Implement response compression
- Use HTTP caching headers
- Monitor with Spring Boot Actuator

## Troubleshooting

### Health Check Failing
```bash
# Check service health
curl http://localhost:8083/actuator/health

# Check detailed health
curl http://localhost:8083/actuator/health?showDetails=true
```

### Out of Memory
Increase heap size:
```bash
java -Xmx512m -jar app.jar
```

### Slow Queries
Enable query logging:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

## Contributing

1. Create a feature branch
2. Implement your changes
3. Add tests
4. Update documentation
5. Submit a pull request

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JWT.io](https://jwt.io/)
- [Docker Compose](https://docs.docker.com/compose/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
