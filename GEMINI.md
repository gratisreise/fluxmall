# Gemini Code Assistant Context

This document provides context for the Gemini Code Assistant to understand the **FluxMall** project.

## Project Overview

**FluxMall** is a simple shopping mall project built with **Spring Boot 2.7.18**. It follows a traditional server-side rendering (SSR) architecture, using **JSP** as the view layer and **JdbcTemplate** for data access. The project is built with **Maven** and uses **MySQL** as its database.

The main purpose of this project is to provide a clear and concise implementation of core e-commerce features, making it a good example for understanding the fundamentals of Spring Boot and classic MVC architecture.

### Key Features

*   **Member Management:** User registration, login/logout (session-based with Spring Security), and a "My Page" for profile updates.
*   **Product Catalog:** Category-based product listings with pagination, keyword search, and product detail pages.
*   **Shopping Cart:** AJAX-based cart functionality for adding, updating, and removing items.
*   **Order Management:** Checkout process with real-time stock reduction, order history, and order detail views.

## Building and Running the Project

This is a standard Maven project. The following commands can be used to build and run the project:

*   **Build:**
    ```bash
    ./mvnw clean package
    ```
*   **Run:**
    ```bash
    ./mvnw spring-boot:run
    ```

**TODO:** Document the database schema setup process. It's likely that a SQL script is needed to initialize the database schema and some initial data.

## Development Conventions

### Coding Style

*   The project uses **Lombok** to reduce boilerplate code.
*   The code follows standard Java conventions.
*   Service layers are used to encapsulate business logic, and DAO layers are used for data access.

### Testing

*   **TODO:** There are no tests in the project. It would be beneficial to add unit and integration tests for the services and controllers.

### Contribution Guidelines

*   **TODO:** No contribution guidelines are defined. It would be helpful to add a `CONTRIBUTING.md` file to describe the process for contributing to the project.
