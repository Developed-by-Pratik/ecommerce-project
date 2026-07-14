# E-Commerce Backend API – Spring Boot

## Overview
This project is a backend REST API for an e-commerce system built using Spring Boot.
The focus of this project is clean backend architecture, real-world business rules,
and secure API design rather than UI.

The system separates temporary cart state from immutable orders, preserves pricing
consistency, and handles product lifecycle management using active and soft-delete
concepts.

This project is designed as a portfolio-grade backend application.

---

## Tech Stack
- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL
- Maven

---

## High-Level System Flow
User → Cart → Checkout → Order → Payment (Mock)

---

## Core Modules

### Authentication & Authorization
- JWT-based authentication
- Role-based authorization (USER / ADMIN)
- Secured endpoints using Spring Security
- User identity derived only from JWT (no userId from client)

### Product Management
- Product CRUD operations
- active flag for temporary availability
- deleted flag for soft delete (lifecycle removal)
- Deleted products cannot be added to cart or checkout

### Cart Management
- One cart per user
- Add, update, and remove cart items
- Prevents duplicate products in cart
- Uses priceAtAddTime to freeze pricing
- Cart is mutable and temporary

### Checkout
- Validation boundary between cart and order
- Re-validates product availability, deletion, and stock
- Recalculates total amount
- No database mutation

### Order Management
- Orders are immutable snapshots of cart
- OrderItem does not reference Product entity
- Orders survive product deletion
- Supports order history per user
- Initial order status is CREATED

---

## Key Design Decisions

### Cart vs Order
Cart is temporary and mutable.
Order is permanent and immutable.

### Pricing Consistency
Product price is frozen at the time of adding to cart using priceAtAddTime.

### Active vs Soft Delete
active = false → temporarily unavailable  
deleted = true → permanently removed from lifecycle

---

## Sample API Endpoints

### Auth
POST /auth/register  
POST /auth/login  

### Products
GET /products  
POST /admin/products  
PUT /admin/products/{id}  
DELETE /admin/products/{id}  

### Cart
GET /cart  
POST /cart/items  
PUT /cart/items/{itemId}  
DELETE /cart/items/{itemId}  
DELETE /cart  

### Checkout & Orders
POST /checkout  
POST /orders  
GET /orders/my  
GET /orders/{id}  

---

## How to Run
1. Clone the repository
2. Configure database in application.yml
3. Run: mvn spring-boot:run
4. Test APIs using Postman or Swagger

---

## Notes
- Payment module is mocked
- UI intentionally not included
- Focus is backend architecture and business logic
