# Brokerage Firm API

## Overview
This project provides a backend API for managing stock orders and customer accounts for a brokerage firm. The API includes functionality for placing orders, listing orders, depositing and withdrawing money, and managing customer assets.

## Technologies
- **Java 17**
- **Spring Boot 3.3.4** - Framework for building the application.
- **Spring Boot Starter Web** - For building RESTful web services.
- **Spring Boot Starter Data JPA** - For interacting with the PostgreSQL database using JPA and Hibernate.
- **Spring Boot Starter Security** - For handling security, including authentication and authorization.
- **Spring Boot Starter Validation** - For data validation using Hibernate Validator.
- **JJWT (JSON Web Tokens)** - For handling authentication and authorization via JWT.
- **PostgreSQL** - Main relational database.
- **Flyway** - For database migration management.
- **Lombok** - To reduce boilerplate code.
- **Slf4j** - Logging framework.
- **JUnit 5** - For unit testing.

## Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/brscrt/brokerage-firm-api.git
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Running Unit Tests
To run the unit tests:
```bash
mvn test
```

## Docker Setup
You can run the application in Docker:
1. Build the Docker image from scratch using the Makefile:
   ```bash
   make build-from-scratch
   ```

## Postman Collection
Use the provided Postman collection to interact with the API.
