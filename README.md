# Quiz Portal Backend

A comprehensive REST API backend for a Quiz/Exam portal platform built with Spring Boot 4.0. This application provides complete CRUD operations for managing quizzes, categories, questions, users, and user authentication with JWT-based security.

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Architecture & Mechanisms](#architecture--mechanisms)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Data Seeding](#data-seeding)
- [Security](#security)
- [Maven Commands](#maven-commands)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)

## Project Overview

The Quiz Portal Backend is a full-featured examination and quiz management system that allows:

- **User Management**: Register, authenticate, and manage user profiles
- **Quiz Management**: Create, update, and manage quizzes with multiple states (draft/active)
- **Question Management**: Add multiple-choice questions to quizzes with answer evaluation
- **Category Management**: Organize quizzes into categories for better navigation
- **Authentication**: JWT-based authentication with role-based access control
- **Evaluation**: Automatic quiz evaluation and scoring system

## Tech Stack

### Core Framework
- **Spring Boot**: 4.0.0 - Latest Spring Boot with Spring Framework 7.0.1
- **Java**: 25 (Latest with preview features enabled)
- **Maven**: 3.9.11 - Build automation tool

### Database
- **PostgreSQL**: 14+ - Primary relational database
- **Hibernate ORM**: JPA implementation with automatic DDL generation
- **H2 Database**: In-memory database for testing

### Security
- **Spring Security**: 7.0.0 - Authentication and authorization
- **JWT (JSON Web Tokens)**: JJWT 0.9.0 - Token-based authentication
- **BCrypt**: Password encoding with Spring Security's BCryptPasswordEncoder

### API Documentation
- **SpringDoc OpenAPI**: 2.8.5 - Automatic OpenAPI/Swagger documentation
- **Swagger UI**: Interactive API exploration and testing


## Architecture & Mechanisms

### **Spring Security Configuration**

#### Authentication Mechanism
- **Type**: Stateless JWT-based authentication
- **Session Policy**: `SessionCreationPolicy.STATELESS` - No server-side sessions
- **Token Validity**: 600 minutes (10 hours)
- **Token Secret**: Configured in `JwtUtils` component

#### JWT Flow
```
1. User Login (POST /generate-token)
   ├─ Validate credentials
   ├─ Generate JWT token
   └─ Return token to client

2. Client includes JWT in requests
   └─ Header: "Authorization: Bearer <token>"

3. JwtAuthenticationFilter processes request
   ├─ Extract JWT from header
   ├─ Validate token signature & expiration
   ├─ Load user details from database
   └─ Set authentication in SecurityContext

4. Authorization checks
   ├─ Method-level security via @PreAuthorize
   └─ Role-based access control (ADMIN, USER)
```

### **Request/Response Processing**

#### DTO Pattern
- **Input Validation**: Jakarta validation annotations (`@NotNull`, `@NotEmpty`, `@Valid`)
- **DTOs**: Separate data transfer objects for API contracts
- **Mappers**: Entity-to-DTO conversion using mapper classes
- **Error Handling**: Global exception handler with standardized error responses

#### Exception Handling
```
GlobalExceptionHandler catches:
├─ Validation exceptions (400 Bad Request)
├─ Entity not found (404 Not Found)
├─ Authentication failures (401 Unauthorized)
├─ Authorization failures (403 Forbidden)
└─ System errors (500 Internal Server Error)
```

#### Key Services
- **AuthService**: Token generation, credential validation
- **UserService**: User CRUD, role management
- **QuizService**: Quiz management, active quiz filtering
- **QuestionService**: Question CRUD, quiz evaluation
- **CategoryService**: Category management

### **API Documentation (Swagger/OpenAPI)**

#### Technology: SpringDoc OpenAPI 2.8.5
- **Auto-generation**: Controllers are scanned automatically
- **Specification**: OpenAPI 3.0 compliant
#### Access URLs
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

## Prerequisites

### System Requirements
- **Java**: JDK 25 or higher (with preview features)
- **Maven**: 3.9.0 or higher
- **PostgreSQL**: 12.0 or higher

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd quiz-portal-backend-code
```

### 2. Configure Database

#### Option A: Local PostgreSQL (Development)
Create the database and user:
```bash
psql -U postgres

postgres=# CREATE DATABASE exam;
```

#### Option B: Environment Variables (Production)
```bash
export DB_URL=jdbc:postgresql://your-host:5432/your-db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

## Running the Application

### Option 1: Using Maven (Development)
```bash
# Run with default profile (dev)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=production"

```

### Environment Variables
```bash
DB_URL=jdbc:postgresql://localhost:5432/exam
DB_USERNAME=exam_user
DB_PASSWORD=exam_password
```

## Data Seeding

The application automatically seeds initial data on startup for testing and demonstration purposes. This is handled by two mechanisms:

### 1. **SQL Data Initialization (data-postgresql.sql)**

Located in `src/main/resources/data-postgresql.sql`, this file is automatically executed by Spring Boot and includes:

### 2. **JSON-Based Quiz Data Seeding (DefaultQuizDataSeeder)**

Located in `src/main/java/com/quiz/config/seeder/DefaultQuizDataSeeder.java`, this Spring component:

#### Implementation Details
- **Type**: Spring `CommandLineRunner` component
- **Execution**: Runs automatically on application startup
- **Transactional**: All operations wrapped in `@Transactional` for data consistency

#### What Gets Seeded
1. **Categories** (2 default):
   - SQL - "Structured Query Language quizzes"
   - Java - "Core Java programming quizzes"

2. **Quizzes** (2 default, both active):
   - **SQL Fundamentals Quiz** (SQL Category)
     - Max Marks: 100
     - Min Questions: 30
   
   - **Java Fundamentals Quiz** (Java Category)
     - Max Marks: 100
     - Min Questions: 30

3. **Questions** (60+ total):
   - **SQL Questions**: 30+ multiple-choice questions from `seed/sql-questions.json`
   - **Java Questions**: 30+ multiple-choice questions from `seed/java-questions.json`

## Contributing

1. Create feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit pull request

## License

This project is licensed under the Apache License 2.0 - see LICENSE file for details.

## Support

For issues, questions, or suggestions:
- Open GitHub issues
- Contact support@quizportal.com
- Check documentation at `/swagger-ui/index.html`

---

**Last Updated**: March 6, 2026  
**Version**: 0.0.1-SNAPSHOT  
**Spring Boot Version**: 4.0.0  
**Java Version**: 25
