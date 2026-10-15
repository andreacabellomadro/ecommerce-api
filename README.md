# E-Commerce REST API

A RESTful API built with **Spring Boot** for managing an e-commerce product catalog. This project focuses on clean code, input validation, automated testing, and API documentation.

## Features

- **Product CRUD:** Create, retrieve, update, and delete products.
- **Pagination:** Retrieve products in configurable pages.
- **Sorting:** Sort products by supported fields.
- **Dynamic filtering:** Filter products by name, price range, and stock range.
- **Input validation:** Validate request data and query parameters.
- **Exception handling:** Handle invalid requests and missing products with appropriate HTTP status codes.
- **API documentation:** Interactive API documentation with Swagger UI.
- **Automated testing:** Unit and integration tests for controllers, services, repositories, and application startup.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Validation
- PostgreSQL
- H2 Database for tests
- Maven
- JUnit and Mockito
- MockMvc
- Springdoc OpenAPI / Swagger UI

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 17 or compatible Java runtime
- PostgreSQL
- Git

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/andreacabellomadro/ecommerce-api.git
   ```

2. Navigate to the project directory:

   ```bash
   cd api
   ```

3. Configure your PostgreSQL database and update the application configuration with your database credentials.

4. Run the application:

   **Windows:**
   ```powershell
   .\mvnw spring-boot:run
   ```

   **Linux / macOS:**
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Documentation

Once the application is running, access Swagger UI to explore and test the available endpoints:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

The OpenAPI specification is available at:

[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Main Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/products` | Retrieve products with pagination, sorting, and filtering |
| GET | `/api/v1/products/{id}` | Retrieve a product by ID |
| POST | `/api/v1/products` | Create a product |
| PUT | `/api/v1/products/{id}` | Update a product |
| DELETE | `/api/v1/products/{id}` | Delete a product |

## Filtering and Pagination

The product listing endpoint supports query parameters such as:

- `page`: Page number, starting at `0`.
- `size`: Number of products per page.
- `sort`: Supported sort field and direction.
- `name`: Filter by product name.
- `minPrice` / `maxPrice`: Filter by price range.
- `minStock` / `maxStock`: Filter by stock range.

Example request:

```http
GET /api/v1/products?page=0&size=5&minPrice=10&maxPrice=100&minStock=1
```

The response includes the product list and pagination metadata, such as the current page, page size, total elements, and total pages.

## Running Tests

Run the automated test suite with Maven:

**Windows:**
```powershell
.\mvnw clean test
```

**Linux / macOS:**
```bash
./mvnw clean test
```

The tests use an H2 in-memory database, so PostgreSQL does not need to be running to execute the test suite.

## Project Status

This project is being developed as a practical backend learning project, with an emphasis on REST API design, validation, persistence, testing, and documentation.