# Patient Management System

A Spring Boot microservices application for managing patient records and billing, featuring gRPC inter-service
communication, RESTful APIs, and containerized deployment.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![gRPC](https://img.shields.io/badge/gRPC-1.79.0-blue.svg)](https://grpc.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)

## Features

### Patient Service

- **Complete CRUD Operations** - RESTful API for patient record management
- **Email Uniqueness** - Enforced at both database and service layers
- **Smart Validation** - Validation groups for create vs. update operations
- **UUID Primary Keys** - Auto-generated unique identifiers
- **gRPC Client Integration** - Communicates with Billing Service for account creation
- **Flexible Database Support** - H2 for development, PostgreSQL for production
- **OpenAPI Documentation** - Interactive Swagger UI for API exploration
- **DTO Pattern** - Request/Response separation with validation constraints

### Billing Service

- **gRPC Server** - High-performance RPC service for billing operations
- **Protocol Buffers** - Strongly-typed service contracts
- **Billing Account Creation** - Automated account provisioning via gRPC

### API Gateway

- **Single Entry Point** - Unified routing for all microservices
- **Dynamic Routing** - Routes `/api/patients/**` to Patient Service and `/api-docs/patients` to OpenAPI documentation
- **Spring Cloud Gateway** - Modern, non-blocking API gateway
- **Port 4004** - Centralized access point

### Analytics Service

- **Event-Driven Architecture** - Kafka consumer for real-time analytics
- **Protobuf Events** - Strongly-typed `PatientEvent` messages
- **Patient Topic Subscription** - Consumes `patient` topic for analytics processing
- **Scalable Processing** - Decoupled from core services via message broker

### Auth Service

- **JWT Authentication** - Stateless authentication using JSON Web Tokens
- **Spring Security** - Secured endpoints with role-based access control
- **User Management** - User entity with JPA/H2 persistence
- **Login Endpoint** - POST `/auth/login` returns signed JWT token
- **Pre-loaded Users** - Sample users via `data.sql` for quick testing
- **Port 4005** - Dedicated auth service port

### Infrastructure

- **Microservices Architecture** - Independent, scalable services
- **Event-Driven Messaging** - Apache Kafka for asynchronous communication
- **Docker Support** - Multi-stage builds with optimized images
- **Inter-Service Communication** - gRPC for synchronous calls, Kafka for async events
- **Global Exception Handling** - Centralized error handling across services
- **Sample Data** - Pre-loaded test data for quick development

## Technology Stack

| Category                        | Technology                                        |
|---------------------------------|---------------------------------------------------|
| **Language**                    | Java 21 (LTS)                                     |
| **Framework**                   | Spring Boot 3.5.10                                |
| **API Gateway**                 | Spring Cloud Gateway                              |
| **Inter-Service Communication** | gRPC 1.79.0 + Protocol Buffers 4.33.3             |
| **gRPC Integration**            | grpc-spring-boot-starter 3.1.0.RELEASE (net.devh) |
| **Message Broker**              | Apache Kafka                                      |
| **Database**                    | H2 (development) / PostgreSQL (production)        |
| **ORM**                         | Spring Data JPA + Hibernate                       |
| **Build Tool**                  | Maven with protobuf-maven-plugin                  |
| **Containerization**            | Docker with multi-stage builds                    |
| **API Documentation**           | SpringDoc OpenAPI 3 (v2.8.15)                     |
| **Validation**                  | Jakarta Bean Validation                           |
| **Authentication**              | Spring Security + JJWT                            |

## Quick Start

### Prerequisites

- **Java 21** or higher ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.x** (or use included wrapper)
- **Docker** (optional, for containerized deployment)

### Option 1: Local Development (Maven)

#### 1. Clone the repository

```bash
git clone <repository-url>
cd patient-management
```

#### 2. Start Billing Service (Port 9001)

```bash
cd billing-service
./mvnw clean install
./mvnw spring-boot:run
```

#### 3. Start Kafka (Port 9092)

Open a new terminal (requires Docker or local Kafka installation):

```bash
# Using Docker
docker run -d --name kafka -p 9092:9092 -p 29092:29092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  confluentinc/cp-kafka:latest

# Or use local Kafka installation
kafka-server-start.sh config/server.properties
```

#### 4. Start Patient Service (Port 4000)

Open a new terminal:

```bash
cd patient-service
./mvnw clean install
./mvnw spring-boot:run
```

#### 5. Start Analytics Service (Port 8080)

Open a new terminal:

```bash
cd analytics-service
./mvnw clean install
./mvnw spring-boot:run
```

#### 6. Start Auth Service (Port 4005)

Open a new terminal:

```bash
cd auth-service
./mvnw clean install
./mvnw spring-boot:run
```

#### 7. Start API Gateway (Port 4004)

Open a new terminal:

```bash
cd api-gateway
./mvnw clean install
./mvnw spring-boot:run
```

**Services:**

- API Gateway: `http://localhost:4004`
- Auth Service: `http://localhost:4005`
- Patient Service REST API: `http://localhost:4000`
- Patient Service via Gateway: `http://localhost:4004/api/patients`
- Swagger UI: `http://localhost:4000/swagger-ui.html`
- Swagger via Gateway: `http://localhost:4004/api-docs/patients`
- H2 Console: `http://localhost:4000/h2-console`
- Billing Service gRPC: `localhost:9001`
- Kafka Broker: `localhost:9092`

### Option 2: Docker Deployment

#### Build and run both services:

```bash
# Build and start Billing Service
cd billing-service
docker build -t billing-service:latest .
docker run -d --name billing-service -p 4001:4001 -p 9001:9001 billing-service:latest

# Build and start Patient Service
cd ../patient-service
docker build -t patient-service:latest .
docker run -d --name patient-service -p 4000:4000 \
  -e BILLING_SERVICE_ADDRESS=billing-service \
  -e BILLING_SERVICE_GRPC_PORT=9001 \
  --link billing-service \
  patient-service:latest
```

### Accessing Services

**Patient Service:**

- REST API: `http://localhost:4000/patients`
- Swagger UI: `http://localhost:4000/swagger-ui.html`
- OpenAPI Docs: `http://localhost:4000/v3/api-docs`

**H2 Console** (Patient Service):

- URL: `http://localhost:4000/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `admin_viewer`
- Password: `password`

**Auth Service:**

- Login Endpoint: `http://localhost:4005/auth/login`
- Default Credentials: `admin` / `admin123` (from `data.sql`)

**Billing Service:**

- gRPC Server: `localhost:9001`
- HTTP Port: `4001` (reserved for future use)

## API Documentation

### Patient Service REST API

#### Get All Patients

**Endpoint:** `GET /patients`

**Response (200 OK):**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "address": "123 Main St",
    "dateOfBirth": "1990-05-15"
  }
]
```

**Example:**

```bash
curl http://localhost:4000/patients
```

#### Create a Patient

**Endpoint:** `POST /patients`

**Note:** Creating a patient automatically triggers billing account creation via gRPC call to Billing Service.

**Request Body:**

```json
{
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "address": "456 Oak Avenue",
  "dateOfBirth": "1985-03-20",
  "registeredDate": "2024-02-08"
}
```

**Response (200 OK):**

```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "address": "456 Oak Avenue",
  "dateOfBirth": "1985-03-20"
}
```

**Example:**

```bash
curl -X POST http://localhost:4000/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "address": "456 Oak Avenue",
    "dateOfBirth": "1985-03-20",
    "registeredDate": "2024-02-08"
  }'
```

#### Update a Patient

**Endpoint:** `PUT /patients/{id}`

**Request Body:**

```json
{
  "name": "Jane Smith Updated",
  "email": "jane.updated@example.com",
  "address": "789 New Street",
  "dateOfBirth": "1985-03-20"
}
```

**Note:** `registeredDate` is not required for updates.

**Response (200 OK):**

```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "name": "Jane Smith Updated",
  "email": "jane.updated@example.com",
  "address": "789 New Street",
  "dateOfBirth": "1985-03-20"
}
```

**Example:**

```bash
curl -X PUT http://localhost:4000/patients/7c9e6679-7425-40de-944b-e07fc1f90ae7 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith Updated",
    "email": "jane.updated@example.com",
    "address": "789 New Street",
    "dateOfBirth": "1985-03-20"
  }'
```

#### Delete a Patient

**Endpoint:** `DELETE /patients/{id}`

**Response:** `204 No Content` (empty body)

**Example:**

```bash
curl -X DELETE http://localhost:4000/patients/7c9e6679-7425-40de-944b-e07fc1f90ae7
```

#### Error Handling

**Patient Not Found (400 Bad Request):**

```json
{
  "message": "Patient not found"
}
```

**Email Already Exists (400 Bad Request):**

```json
{
  "message": "Email address already exists"
}
```

**Validation Error (400 Bad Request):**

```json
{
  "email": "Email should be valid",
  "name": "Name is required"
}
```

### Billing Service gRPC API

**Service:** `BillingService`

**RPC Method:** `CreateBillingAccount`

**Request (BillingRequest):**

```protobuf
message BillingRequest {
  string patientId = 1;
  string name = 2;
  string email = 3;
}
```

**Response (BillingResponse):**

```protobuf
message BillingResponse {
  string accountId = 1;
  string status = 2;
}
```

**Protocol Buffer Definition:** `src/main/proto/billing_service.proto`

**Note:** This service is called automatically by Patient Service when creating a new patient. Sample gRPC requests are
available in `grpc-requests/billing-service/`.

## Architecture

### Microservices Overview

```
  Client
    │
    ▼
┌───────────────────────────────────────────────────────────┐
│                     API Gateway (4004)                    │
│                     Spring Cloud GW                       │
└──────┬──────────────────────┬───────────────────┬─────────┘
       │ /api/patients/**     │ /auth/**           │ /api-docs/**
       ▼                      ▼                    ▼
┌──────────────┐    ┌──────────────────┐    (pass-through
│ Patient Svc  │    │   Auth Service   │     to Patient Svc)
│  (4000)      │    │   (4005)         │
│  REST API    │    │  JWT + Security  │
│  JPA/H2/PG   │    │  JPA/H2          │
└──────┬───────┘    └──────────────────┘
       │
       ├─── gRPC ──────────────────────────────────────────┐
       │                                                    ▼
       │                                        ┌──────────────────┐
       │                                        │  Billing Service │
       │                                        │  gRPC (9001)     │
       │                                        │  HTTP (4001)     │
       │                                        └──────────────────┘
       │
       ├─── JPA ────────────────────────────────────────────┐
       │                                                    ▼
       │                                        ┌──────────────────┐
       │                                        │  H2 / PostgreSQL │
       │                                        │  (Patient DB)    │
       │                                        └──────────────────┘
       │
       └─── Kafka publish ──────────────────────────────────┐
                                                            ▼
                                                ┌──────────────────┐
                                                │  Kafka Broker    │
                                                │  (9092)          │
                                                │  topic: patient  │
                                                └────────┬─────────┘
                                                         │ consume
                                                         ▼
                                                ┌──────────────────┐
                                                │Analytics Service │
                                                │  (8080)          │
                                                │  Kafka Consumer  │
                                                └──────────────────┘
```

### Patient Service - Layered Architecture

```
Controller → Service → Repository → Entity
     ↓         ↓           ↓
    DTOs   gRPC Client  Mappers
```

### Project Structure

```
patient-management/
├── patient-service/                 # Patient management REST API + Kafka Producer
│   ├── src/main/
│   │   ├── java/dev/toganbayev/patientservice/
│   │   │   ├── controller/         # REST controllers
│   │   │   ├── service/            # Business logic
│   │   │   ├── repository/         # JPA repositories
│   │   │   ├── model/              # JPA entities
│   │   │   ├── dto/                # Request/Response DTOs
│   │   │   ├── mapper/             # Entity-DTO mappers
│   │   │   ├── grpc/               # gRPC client
│   │   │   ├── kafka/              # Kafka producer
│   │   │   ├── exception/          # Custom exceptions
│   │   │   └── validation/         # Validation groups
│   │   ├── proto/                  # Protocol buffer definitions
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql            # H2 initialization data
│   ├── Dockerfile
│   └── pom.xml
├── billing-service/                # Billing management gRPC service
│   ├── src/main/
│   │   ├── java/dev/toganbayev/billingservice/
│   │   │   └── grpc/               # gRPC service implementation
│   │   ├── proto/                  # Protocol buffer definitions
│   │   └── resources/
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── api-gateway/                    # API Gateway with Spring Cloud Gateway
│   ├── src/main/
│   │   ├── java/dev/toganbayev/apigateway/
│   │   └── resources/
│   │       └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── analytics-service/              # Analytics Service with Kafka Consumer
│   ├── src/main/
│   │   ├── java/dev/toganbayev/analyticsservice/
│   │   │   └── kafka/              # Kafka consumer and listeners
│   │   ├── proto/                  # Protocol buffer definitions
│   │   └── resources/
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── auth-service/                   # Auth Service with JWT Authentication
│   ├── src/main/
│   │   ├── java/dev/toganbayev/authservice/
│   │   │   ├── controller/         # Auth endpoints
│   │   │   ├── service/            # Authentication business logic
│   │   │   ├── config/             # Spring Security configuration
│   │   │   ├── model/              # JPA entities
│   │   │   ├── repository/         # JPA repositories
│   │   │   ├── dto/                # Request/Response DTOs
│   │   │   └── util/               # JWT utility functions
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql            # Pre-loaded users
│   ├── Dockerfile
│   └── pom.xml
├── api-requests/                   # Sample HTTP requests
│   ├── patient-service/
│   └── auth-service/
├── grpc-requests/                  # Sample gRPC requests
│   └── billing-service/
└── README.md                       # This file
```

## Testing

### Patient Service Tests

```bash
cd patient-service

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PatientServiceApplicationTests

# Package without running tests
./mvnw clean package -DskipTests
```

### Billing Service Tests

```bash
cd billing-service

# Run all tests
./mvnw test

# Package without running tests
./mvnw clean package -DskipTests
```

## Development

### Build Commands (Patient Service)

```bash
cd patient-service

# Clean build (compiles proto files automatically)
./mvnw clean install

# Skip tests
./mvnw clean package -DskipTests

# Run application
./mvnw spring-boot:run

# View dependency tree
./mvnw dependency:tree

# Check for updates
./mvnw versions:display-dependency-updates
```

### Build Commands (Billing Service)

```bash
cd billing-service

# Clean build (compiles proto files automatically)
./mvnw clean install

# Skip tests
./mvnw clean package -DskipTests

# Run application
./mvnw spring-boot:run
```

### Docker Commands

```bash
# Build Patient Service image
cd patient-service
docker build -t patient-service:latest .

# Build Billing Service image
cd billing-service
docker build -t billing-service:latest .

# View running containers
docker ps

# View logs
docker logs patient-service
docker logs billing-service

# Stop containers
docker stop patient-service billing-service

# Remove containers
docker rm patient-service billing-service
```

### Database Configuration (Patient Service)

**H2 (Local Development):**

- URL: `http://localhost:4000/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `admin_viewer`
- Password: `password`

**PostgreSQL (Production/Docker):**
Configure via environment variables:

```bash
-e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
-e SPRING_DATASOURCE_USERNAME=user
-e SPRING_DATASOURCE_PASSWORD=pass
```

**Data Initialization:**

- Sample data loaded via `src/main/resources/data.sql`
- Schema auto-generated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
- Data initialization mode: `spring.sql.init.mode=always`

### gRPC Configuration

**Billing Service (Server):**

- gRPC Port: `9001`
- Protocol: Plaintext (for internal network)
- Configuration: `grpc.server.port=9001` in `application.properties`

**Patient Service (Client):**

- Server Address: Configurable via `billing.service.address` (default: localhost)
- gRPC Port: Configurable via `billing.service.grpc.port` (default: 9001)
- Connection: ManagedChannel with plaintext

### Protocol Buffers and gRPC

**Proto File Location:**

- Billing Service: `src/main/proto/billing_service.proto` (source)
- Patient Service: `src/main/proto/billing_service.proto` (client copy)

**Code Generation:**
Proto files are automatically compiled to Java classes during Maven build via `protobuf-maven-plugin`:

```bash
# Generated classes location
target/generated-sources/protobuf/java/        # Message classes
target/generated-sources/protobuf/grpc-java/   # gRPC service stubs
```

**Modifying Proto Files:**

1. Edit `.proto` file in `billing-service/src/main/proto/`
2. Rebuild billing-service: `./mvnw clean install`
3. Copy updated proto file to patient-service (if client needs changes)
4. Rebuild patient-service: `cd ../patient-service && ./mvnw clean install`

### Code Style

- **Indentation:** 4 spaces
- **Validation Groups:** `CreatePatientValidationGroup` for create operations
- **DTOs:** Separate Request/Response DTOs with validation constraints
- **Exception Handling:** Centralized via `GlobalExceptionHandler` with `@ControllerAdvice`
- **gRPC Services:** Use `@GrpcService` annotation for server, `ManagedChannel` for client

### Key Components

**gRPC Integration:**

- **BillingServiceGrpcClient** (`patient-service/grpc/`): Synchronous blocking stub for calling Billing Service
- **BillingGrpcService** (`billing-service/grpc/`): Server-side gRPC service implementation with `@GrpcService`

**PatientMapper** (`mapper/PatientMapper.java`)

- Static utility methods for entity-DTO conversion
- Handles date string parsing (LocalDate ↔ String)

**GlobalExceptionHandler** (`exception/GlobalExceptionHandler.java`)

- Centralized error handling with `@ControllerAdvice`
- Custom exceptions: `EmailAlreadyExistsException`, `PatientNotFoundException`
- Returns structured JSON error responses

**Validation Groups** (`dto/validators/CreatePatientValidationGroup.java`)

- Distinguishes between create (requires `registeredDate`) and update operations
- Applied via `@Validated` annotation on controller methods

**Email Uniqueness**

- Database constraint on `Patient` entity
- Service-level checks via `existsByEmail()` and `existsByEmailAndIdNot()`

## API Testing

### Patient Service (REST API)

Sample HTTP requests are available in `api-requests/patient-service/`:

- `create-patient.http` - Create new patient (triggers billing account creation)
- `get-patients.http` - Get all patients
- `update-patient.http` - Update existing patient
- `delete-patient.http` - Delete patient

Use these with IntelliJ IDEA HTTP Client, VSCode REST Client, or similar tools.

### Billing Service (gRPC)

Sample gRPC requests are available in `grpc-requests/billing-service/`.

Test gRPC endpoints using tools like:

- [grpcurl](https://github.com/fullstorydev/grpcurl) - Command-line gRPC client
- [BloomRPC](https://github.com/bloomrpc/bloomrpc) - GUI client for gRPC
- [Postman](https://www.postman.com/) - Supports gRPC (v9.0+)

## Roadmap

### Completed Features ✅

- [x] Microservices architecture with gRPC communication
- [x] Patient Service REST API with CRUD operations
- [x] Billing Service gRPC server
- [x] Docker support with multi-stage builds
- [x] OpenAPI/Swagger documentation (Patient Service)
- [x] PostgreSQL support for production
- [x] Protocol Buffers for service contracts
- [x] Apache Kafka integration (Producer in Patient Service, Consumer in Analytics Service)
- [x] API Gateway with Spring Cloud Gateway
- [x] Analytics Service for event-driven analytics
- [x] Authentication and authorization with Auth Service (JWT/Spring Security)

### Planned Features

- [ ] Add pagination and sorting for patient listing
- [ ] Implement search functionality (by name, email)
- [ ] Add patient medical history tracking
- [ ] Add comprehensive unit and integration tests
- [ ] Implement billing operations (invoices, payments)
- [ ] Add service mesh (Istio/Linkerd) for production
- [ ] Implement audit logging and event sourcing
- [ ] Add metrics and monitoring (Prometheus/Grafana)
- [ ] Implement API rate limiting
- [ ] Add Docker Compose for multi-service orchestration
- [ ] Implement circuit breakers (Resilience4j)
- [ ] Add distributed tracing (Jaeger/Zipkin)

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for new functionality
4. Ensure all tests pass (`./mvnw test`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Contact

Toganbayev - [@toganbayev](https://github.com/toganbayev)

Project Link: [https://github.com/toganbayev/patient-management](https://github.com/toganbayev/patient-management)

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data persistence
- [gRPC](https://grpc.io/) - High-performance RPC framework
- [Protocol Buffers](https://protobuf.dev/) - Language-neutral data serialization
- [grpc-spring-boot-starter](https://github.com/yidongnan/grpc-spring-boot-starter) - gRPC Spring Boot integration
- [SpringDoc OpenAPI](https://springdoc.org/) - API documentation
- [H2 Database](https://www.h2database.com/) - In-memory database
- [PostgreSQL](https://www.postgresql.org/) - Production database
- [Docker](https://www.docker.com/) - Containerization platform
- [Jakarta Bean Validation](https://beanvalidation.org/) - Validation framework
