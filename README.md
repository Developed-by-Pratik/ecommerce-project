# E-Commerce Backend API – Spring Boot

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## Overview

This project is a **production-ready backend REST API** for an e-commerce system built using **Spring Boot 3.2.2** with Java 21.

The focus of this project is:
- ✅ Clean backend architecture with layered design patterns
- ✅ Real-world business rules and validation logic
- ✅ Secure API design with JWT authentication and RBAC
- ✅ Immutable order management with mutable cart state
- ✅ Pricing consistency and product lifecycle management
- ✅ Portfolio-grade application demonstrating best practices

**This is a backend-only project** - UI intentionally not included. Focus is on robust architecture and business logic.

---

## Owner & License

**Owner**: [Developed-by-Pratik](https://github.com/Developed-by-Pratik)  
**License**: MIT License - Free to use, modify, and distribute  
**Status**: Active Development

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2.2 |
| **Security** | Spring Security + JWT (JJWT) |
| **Database** | PostgreSQL 15 |
| **ORM** | Spring Data JPA (Hibernate) |
| **Caching** | Redis 7 |
| **API Documentation** | Swagger/OpenAPI 3.0 |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito |
| **Containerization** | Docker & Docker Compose |

---

## Prerequisites

Before running the application, ensure you have:

- **Java 21** or higher ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **PostgreSQL 15+** ([Download](https://www.postgresql.org/download/))
- **Redis 7+** ([Download](https://redis.io/download))
- **Git** ([Download](https://git-scm.com/))
- **Docker & Docker Compose** (Optional, for containerized setup)

---

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/Developed-by-Pratik/ecommerce-project.git
cd ecommerce-project

# 2. Copy environment template
cp .env.example .env

# 3. Start services (PostgreSQL + Redis)
docker-compose up -d

# 4. Build the application
mvn clean package

# 5. Run the application
mvn spring-boot:run
```

### Option 2: Manual Setup

```bash
# 1. Clone the repository
git clone https://github.com/Developed-by-Pratik/ecommerce-project.git
cd ecommerce-project

# 2. Set up environment variables
cp .env.example .env
# Edit .env with your database credentials and JWT secret

# 3. Ensure PostgreSQL and Redis are running
# PostgreSQL: localhost:5432
# Redis: localhost:6379

# 4. Build the application
mvn clean install

# 5. Run the application
mvn spring-boot:run

# 6. The server will start at http://localhost:8080
```

---

## Configuration

### Environment Variables

The application uses environment variables for configuration. See `.env.example` for all available options:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-super-secret-key
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Configuration File

The main configuration is in: `src/main/resources/application.properties`

Key properties:
- `spring.datasource.*` - Database connection
- `spring.redis.*` - Redis cache settings
- `spring.jpa.*` - Hibernate/JPA settings
- `jwt.secret` - JWT token secret
- `jwt.expiration` - Token expiration time (ms)

---

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Docs (JSON)
```
http://localhost:8080/v3/api-docs
```

### Health Check
```
http://localhost:8080/actuator/health
```

---

## High-Level System Flow

```
User → Register/Login (JWT) → Browse Products → Add to Cart → Checkout → Create Order → Order History
```

---

## Core Modules

### Authentication & Authorization
- **JWT-based authentication** with secure token generation
- **Role-based access control (RBAC)** for USER and ADMIN roles
- **Spring Security integration** with custom user details service
- **Secured endpoints** using @PreAuthorize annotations
- User identity derived only from JWT (no userId from client)

### Product Management
- **Product CRUD operations** for admins
- **Active flag** for temporary product availability
- **Soft delete** with `deleted` flag for lifecycle management
- Deleted/inactive products cannot be added to cart or checkout
- Product query optimization with Spring Data JPA

### Cart Management
- **One cart per user** (not multiple carts)
- Add, update, and remove items from cart
- **Prevents duplicate products** in cart (updates quantity instead)
- **Price freezing** with `priceAtAddTime` for consistency
- **Mutable and temporary** state - can be modified before checkout
- Cart stored in Redis for session management

### Checkout
- **Validation boundary** between cart and order creation
- Re-validates product availability, deletion status, and stock
- Recalculates total amount for security
- **No database mutations** during validation phase
- Safe transition from cart to order

### Order Management
- **Immutable snapshots** of completed transactions
- OrderItem stores product details at order time (not references)
- Orders **survive product deletion** or modification
- **Order history per user** with status tracking
- Initial order status: CREATED
- Supports order cancellation and tracking

---

## Sample API Endpoints

### Authentication
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}

POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### Products (Public)
```http
GET /products                    # List all products
GET /products/{id}               # Get product details
```

### Products (Admin Only)
```http
POST /admin/products             # Create product
PUT /admin/products/{id}         # Update product
DELETE /admin/products/{id}      # Soft delete product
```

### Cart (User)
```http
GET /cart                        # Get user's cart
POST /cart/items                 # Add item to cart
PUT /cart/items/{itemId}         # Update quantity
DELETE /cart/items/{itemId}      # Remove item from cart
DELETE /cart                     # Clear cart
```

### Checkout & Orders
```http
POST /checkout                   # Validate checkout (no mutations)
POST /orders                     # Create order from cart
GET /orders/my                   # Get order history
GET /orders/{id}                 # Get order details
```

---

## Key Design Decisions

### Cart vs Order
- **Cart**: Temporary, mutable state for active shopping sessions
- **Order**: Permanent, immutable snapshot of completed transactions

### Pricing Consistency
- Product price is **frozen at the time of adding to cart** using `priceAtAddTime`
- Prevents price discrepancies between cart and final order
- Checkout recalculates total for additional security

### Active vs Soft Delete
- `active = false` → Product temporarily unavailable
- `deleted = true` → Product permanently removed from lifecycle
- Both flags prevent products from being added to cart

### Security & Validation
- JWT tokens for stateless authentication
- User derived only from token (no client-provided userId)
- All endpoints validate user permissions
- Input validation on all DTOs
- Sensitive data never exposed in responses

---

## Database Schema

The application uses PostgreSQL with the following main entities:

- **Users** - Authentication and authorization
- **Products** - Product catalog with active/deleted flags
- **Carts** - User shopping carts (one per user)
- **CartItems** - Items in cart with frozen prices
- **Orders** - Immutable order records
- **OrderItems** - Order line items with snapshots

See `ARCHITECTURE.md` for detailed schema documentation.

---

## Testing

### Run All Tests
```bash
mvn test
```

### Run with Coverage Report
```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

---

## Build & Deployment

### Build JAR
```bash
mvn clean package
```

### Build Docker Image
```bash
docker build -t ecommerce-api:1.0.0 .
```

### Run Docker Container
```bash
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://postgres:5432/ecommerce_db \
  -e JWT_SECRET=your-secret \
  ecommerce-api:1.0.0
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/ecommerce/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # Business logic and validation
│   │   ├── repository/        # Data access layer (JPA)
│   │   ├── entity/            # JPA entity classes
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── config/            # Spring configuration classes
│   │   ├── security/          # JWT and security filters
│   │   └── exception/         # Custom exception classes
│   └── resources/
│       └── application.properties  # Configuration
└── test/
    └── java/com/ecommerce/   # Unit and integration tests

├── docker-compose.yml         # Docker services setup
├── pom.xml                    # Maven dependencies
├── README.md                  # This file
├── CONTRIBUTING.md            # Contribution guidelines
├── ARCHITECTURE.md            # Detailed architecture docs
└── CHANGELOG.md               # Version history
```

---

## Common Issues & Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
# Or kill existing process on port 8080
lsof -ti:8080 | xargs kill -9
```

### PostgreSQL Connection Issues
```bash
# Verify PostgreSQL is running
psql -U postgres -h localhost

# Check application.properties database URL
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
```

### Redis Connection Issues
```bash
# Verify Redis is running
redis-cli ping  # Should respond with PONG

# Check Redis configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Maven Build Issues
```bash
# Clean build
mvn clean install -DskipTests

# Update dependencies
mvn dependency:resolve
```

---

## Performance Features

- **Connection Pooling**: HikariCP for database connections
- **Query Optimization**: Lazy loading, efficient queries
- **Caching**: Redis for cart sessions and query results
- **Batch Processing**: Hibernate batch size configured
- **Asynchronous Processing**: Spring async support available

---

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: USER and ADMIN roles
- **SQL Injection Prevention**: Parameterized queries via JPA
- **CORS Configuration**: Configurable cross-origin requests
- **Password Encryption**: BCrypt hashing
- **Input Validation**: Validation annotations on all DTOs
- **Error Handling**: Sanitized error messages

---

## Monitoring & Observability

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Detailed Health Info
```bash
curl http://localhost:8080/actuator/health/details
```

---

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for:
- Development setup
- Code style guidelines
- Git workflow
- Testing requirements
- Commit message format

---

## Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed system architecture and design patterns
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Contribution guidelines
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and roadmap

---

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

MIT License allows you to:
- ✅ Use commercially
- ✅ Modify the code
- ✅ Distribute the software
- ✅ Use privately

The only requirement is to include a copy of the license and copyright notice.

---

## Notes

- **Payment Module**: Currently mocked - not connected to real payment gateways
- **UI**: Intentionally not included - this is a backend-only project
- **Focus**: Clean architecture, business logic, and secure API design
- **Production Ready**: Follows enterprise-level best practices
- **Portfolio Grade**: Demonstrates architectural patterns and Spring expertise

---

## Contact & Support

- **Repository**: [https://github.com/Developed-by-Pratik/ecommerce-project](https://github.com/Developed-by-Pratik/ecommerce-project)
- **Author**: [Developed-by-Pratik](https://github.com/Developed-by-Pratik)
- **Issues**: [GitHub Issues](https://github.com/Developed-by-Pratik/ecommerce-project/issues)

---

## Acknowledgments

- Spring Boot & Spring Framework community
- PostgreSQL and Redis communities
- JWT best practices from jwt.io
- Open source contributors

---

**Made with ❤️ by Developed-by-Pratik**
