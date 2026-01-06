# FluxMall - Simple Shopping Mall Project

## Project Overview

**FluxMall** is a traditional server-side rendering shopping mall project based on **Spring Boot 2.x**.
It uses **JSP** for the view, **JdbcTemplate** for data access, and **MySQL** as the database.

The main goal is to implement **core shopping mall features (member, product, cart, order)** in a simple and clear way.
It's a great project for understanding the basic workings of Spring Boot through a classic MVC structure, rather than a modern SPA.

## Key Features

### Member
- Email/ID based registration (with real-time AJAX ID duplication check)
- Login / Logout (Session + Spring Security)
- My Page (view and edit my information, including nickname/password)

### Product
- View product list by category (with pagination)
- Search for products by keyword (product name/description)
- View product details (with real-time stock display)
- Product registration (any logged-in member can register a product -> simple seller concept)

### Cart
- Add products to cart (AJAX, quantity can be specified)
- Increase/decrease quantity / delete individual items / delete selected items (real-time reflection with AJAX)
- Real-time calculation of the total amount to be paid

### Order
- Order all items in the cart or purchase individual products directly
- Create an order form (enter shipping address)
- Real-time stock deduction upon payment (`@Transactional` guarantee)
- Order completion page
- Order history list (with pagination) and detailed view

## Technology Stack

| Category          | Technology                    |
|-------------------|-------------------------------|
| Framework         | Spring Boot 2.7.18            |
| View              | JSP (Java Server Pages)       |
| Data Access       | JdbcTemplate                  |
| Database          | MySQL                         |
| Security          | Spring Security (Form Login + CSRF) |
| Password Encryption | BCryptPasswordEncoder         |
| Dependency Management | Maven                         |
| Others            | Lombok, JSTL                  |

## Getting Started

### Prerequisites

*   Java 11 or higher
*   Maven 3.6 or higher
*   MySQL 8.0 or higher

### Installation & Execution

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/fluxmall.git
    cd fluxmall
    ```

2.  **Database Setup:**
    *   Create a database named `fluxmall` in MySQL.
    *   Update the database connection properties in `src/main/resources/application.properties`.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/fluxmall
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    ```
    *   Execute the `database/schema.sql` file to create the tables. (**Note:** This file needs to be created.)

3.  **Build the project:**
    ```bash
    ./mvnw clean package
    ```

4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will be accessible at `http://localhost:8080`.

## Project Structure (Key Packages)

```
com.fluxmall
├── config         # Security, Web MVC configuration
├── controller     # Handles HTTP requests
├── dao            # Data Access Object (using JdbcTemplate)
├── domain         # DTOs, VOs, Enums, Mappers
├── service        # Business logic
└── FluxMallApplication.java # Main application
```
