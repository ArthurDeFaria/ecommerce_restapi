# 📦 E-commerce REST API

![Java](https://img.shields.io/badge/Java-21%2B-red?style=for-the-badge&logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=spring) ![Status](https://img.shields.io/badge/Status-Under%20Development-yellow?style=for-the-badge) ![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge) ![CI/CD](https://github.com/ArthurDeFaria/ecommerce_restapi/actions/workflows/build.yml/badge.svg)

## 📜 Description

This is a robust REST API developed in Java with Spring Boot, designed to serve as the complete backend for a modern E-commerce platform. It manages the entire lifecycle of a virtual store, from user registration and authentication, product catalog, shopping cart, order completion, to essential integrations with payment systems and shipping calculations.

The main objective of this project is to provide a solid, secure, tested, and scalable foundation for e-commerce applications, implementing best practices in backend development.

## ✨ Main Features

* **Authentication and Authorization:** User registration, secure login with JWT (JSON Web Tokens) and role-based access control (USER, MANAGER, ADMIN).
* **Product Management:** Complete CRUD for products, search by category and search by name.
* **Shopping Cart:** Add, remove, update quantities and clear cart per user.
* **Order Management:** Create orders from cart, list orders (general and per user).
* **Payment Integration (Mercado Pago):** Generate payment preferences, redirect to external checkout and receive status via webhook for order updates.
* **Shipping Calculation (SuperFrete):** Real-time shipping quotes based on products and destination ZIP code.
* **Stock Management:** Automatic stock deduction after payment confirmation.
* **Other Features:** Address Management, Favorites and Product Reviews.
* **Automated Tests:** Comprehensive suite of unit and integration tests.
* **Database Migrations:** Database schema management with Flyway.
* **Containerization:** Full support for Docker and Docker Compose for easy execution.
* **CI/CD:** Basic Continuous Integration pipeline with GitHub Actions for automatic build and testing.

## 🚀 Technologies Used

* **Language:** Java 21+
* **Main Framework:** Spring Boot 3.x
* **Persistence:** Spring Data JPA / Hibernate
* **Security:** Spring Security (with JWT authentication)
* **Database:** PostgreSQL 14+ (Production/Development), H2 (Testing)
* **Migrations:** Flyway
* **Build:** Maven
* **API Documentation:** SpringDoc OpenAPI (Swagger UI)
* **HTTP Requests (Client):** Spring WebFlux WebClient (for SuperFrete)
* **Containerization:** Docker, Docker Compose
* **Utilities:** Lombok

## 🛠️ Installation and Execution Guide

There are two main ways to run the project: locally or via Docker.

### Common Prerequisites

* Git
* Docker and Docker Compose (Recommended for ease)
* JDK 21+ (If running locally)
* Maven 3.8+ (If running locally)
* PostgreSQL Client (Optional, for inspecting the database)

### Option 1: Docker Execution (Recommended)

This is the simplest and fastest way, as it manages the database and application automatically.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ArthurDeFaria/ecommerce_restapi.git](https://github.com/ArthurDeFaria/ecommerce_restapi.git)
    cd ecommerce_restapi
    ```
2.  **Configure environment variables:**
    * Copy the `.env.example` file to `.env`:
        ```bash
        cp .env.example .env
        ```
    * Edit the `.env` file and fill in **all** variables with your real tokens and secrets. Make sure `DATASOURCE_USERNAME` and `DATASOURCE_PASSWORD` are set (can be `user`/`password` for Docker environment).
3.  **Run Docker Compose:**
    ```bash
    docker-compose up --build
    ```
    * On first run (or after code changes), the `--build` flag is important to build the API image.
    * Wait for the PostgreSQL image download, API image build and start of both containers. Flyway will apply migrations to the database automatically.
4.  **Access the API:** The API will be available at `http://localhost:8080`.

### Option 2: Local Execution (Requires Manual Installation)

1.  **Clone the repository:** (Same as step 1 of Docker)
2.  **Configure environment variables:** (Same as step 2 of Docker, but **make sure** the `DATASOURCE_URL` variable in `.env` points to your local or remote PostgreSQL instance correctly, including user and password if necessary, or use `DATASOURCE_USERNAME`/`DATASOURCE_PASSWORD`).
3.  **Prepare PostgreSQL Database:**
    * Make sure you have a PostgreSQL 14+ instance running.
    * Create an empty database (ex: `ecommerce_db`).
    * Configure the corresponding user and password in your `.env` file.
4.  **Run the Application with Maven Wrapper:**
    * In the terminal, at the root of the project, run:
        ```bash
        ./mvnw spring-boot:run
        ```
    * Maven will compile the code, download dependencies and start the application. Flyway will try to connect to the database configured in `.env` and apply migrations.
5.  **Access the API:** The API will be available at `http://localhost:8080`.

## ⚙️ Configuration (Environment Variables)

The application uses environment variables for sensitive configurations (tokens, passwords) and URLs. Create an `.env` file at the root of the project based on `.env.example` and fill in the following values:

* `DATASOURCE_URL`: The complete JDBC URL for your PostgreSQL database (used mainly for local execution without Docker).
* `DATASOURCE_DB_NAME`: Database name (used by Docker Compose).
* `DATASOURCE_USERNAME`: Database user.
* `DATASOURCE_PASSWORD`: Database password.
* `JWT_SECRET`: A long and secure secret key for signing JWT tokens.
* `SUPER_FRETE_TOKEN`: Your SuperFrete API token.
* `MERCADO_PAGO_TOKEN`: Your Mercado Pago access token (usually developer/sandbox).
* `MERCADO_PAGO_NOTIFICATION_URL`: The **public** URL where Mercado Pago will send webhook notifications (use `ngrok` or similar during local/Docker development).

## 🗃️ Database

* **Main DBMS:** PostgreSQL 14+
* **Flexibility:** Thanks to the use of Spring Data JPA/Hibernate, the application can be adapted to other relational databases (MySQL, MariaDB, SQL Server, Oracle) with minimal changes:
    1.  Add the corresponding JDBC driver dependency in `pom.xml`.
    2.  Update the `DATASOURCE_URL` and `spring.jpa.database-platform` in `application-dev.properties`.
    3.  Adjust Flyway migration scripts to the specific SQL syntax of the new database, if necessary.
* **Schema Management:** The database schema is managed exclusively by **Flyway**. Versioned SQL migrations are located in `src/main/resources/db/migration`. The `spring.jpa.hibernate.ddl-auto` property is **not** used for development/production environments.

## 🔒 Authentication

The API uses **JWT (JSON Web Token)** for authentication.
1.  Obtain a token through the `POST /auth/login` endpoint.
2.  To access protected endpoints, include the token in the `Authorization` header of each request:
    `Authorization: Bearer <your_jwt_token>`

## 📚 API Documentation (Swagger)

The API has interactive documentation automatically generated with SpringDoc OpenAPI (Swagger UI). After starting the application (locally or via Docker), access:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

There, you can explore all endpoints, see data models (DTOs) and even test requests directly from the browser (remember to use the "Authorize" button to add your JWT token for protected endpoints).

## 📌 API Endpoints

The table below lists the main available endpoints. Consult the Swagger UI for complete details about parameters, request/response bodies and status codes.

| Method | Endpoint                      | Description                               | Access                 | Status       |
| :----- | :---------------------------- | :---------------------------------------- | :--------------------- | :----------- |
| POST   | /auth/registro            | Creates a new user account.               | Public                 | ✅ Ready     |
| POST   | /auth/login               | Authenticates a user and returns a token. | Public                 | ✅ Ready     |
| GET    | /usuarios/info            | Gets logged-in user information.          | USER, MANAGER, ADMIN   | ✅ Ready     |
| PUT    | /usuarios/info            | Updates logged-in user information.       | USER, MANAGER, ADMIN   | ✅ Ready     |
| DELETE | /usuarios/info            | Deletes the logged-in user.               | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /usuarios/{id}            | Gets user information.                    | MANAGER, ADMIN         | ✅ Ready     |
| PUT    | /usuarios/{id}            | Updates user data.                        | MANAGER, ADMIN         | ✅ Ready     |
| DELETE | /usuarios/{id}            | Deletes any account.                      | ADMIN                  | ✅ Ready     |
| POST   | /auth/registro/adm        | Creates account with ADMIN permissions.   | ADMIN                  | ✅ Ready     |
| GET    | /usuarios                 | Lists all users.                          | ADMIN                  | ✅ Ready     |
| POST   | /enderecos/info           | Creates address for logged-in user.       | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /enderecos/info           | Lists logged-in user addresses.           | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /enderecos/info/{id}      | Gets logged-in user address.              | USER, MANAGER, ADMIN   | ✅ Ready     |
| PUT    | /enderecos/info/{id}      | Updates logged-in user address.           | USER, MANAGER, ADMIN   | ✅ Ready     |
| DELETE | /enderecos/info/{id}      | Removes logged-in user address.           | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /enderecos/usuario/{id}   | Lists user addresses.                     | MANAGER, ADMIN         | ✅ Ready     |
| POST   | /carrinho/adicionar        | Adds a product to cart.                   | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /carrinho/info             | Gets cart items.                          | USER, MANAGER, ADMIN   | ✅ Ready     |
| PUT    | /carrinho/atualizar/{itemId} | Updates item quantity.                  | USER, MANAGER, ADMIN   | ✅ Ready     |
| DELETE | /carrinho/remover/{itemId} | Removes an item from cart.               | USER, MANAGER, ADMIN   | ✅ Ready     |
| DELETE | /carrinho/limpar           | Empties user cart.                        | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /produtos                     | Lists all products.                       | Public                 | ✅ Ready     |
| GET    | /produtos/categoria/{categoria} | Lists products by category.             | Public                 | ✅ Ready     |
| GET    | /produtos/search              | Searches products by name.                | Public                 | ✅ Ready     |
| GET    | /produtos/{id}                | Gets a specific product.                  | Public                 | ✅ Ready     |
| POST   | /produtos                     | Creates a new product.                    | MANAGER, ADMIN         | ✅ Ready     |
| PUT    | /produtos                     | Updates an existing product.              | MANAGER, ADMIN         | ✅ Ready     |
| DELETE | /produtos/{id}                | Removes a product.                        | ADMIN                  | ✅ Ready     |
| POST   | /favoritos             | Adds a product to favorites.              | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /favoritos/info        | Lists user favorites.                     | USER, MANAGER, ADMIN   | ✅ Ready     |
| DELETE | /favoritos/{id}        | Removes a product from favorites.         | USER, MANAGER, ADMIN   | ✅ Ready     |
| POST   | /pedidos/finalizar           | Creates a new order from cart.            | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /pedidos/info                | Lists logged-in user orders.              | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /pedidos/{id}                | Gets order details.                       | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /pedidos                     | Lists all system orders.                  | MANAGER, ADMIN         | ✅ Ready     |
| GET    | /pagamentos/{Id}       | Gets payment status of an order.          | USER, MANAGER, ADMIN   | ✅ Ready     |
| POST   | /pagamentos/webhook    | Receives Mercado Pago notifications.      | Public (Webhook)       | ✅ Ready     |
| POST   | /envios/cotarfrete     | Quotes shipping value for a ZIP code.     | Public                 | ✅ Ready     |
| GET    | /envios/{Id}           | Checks shipping status.                   | USER, MANAGER, ADMIN   | ⏳ Planned   |
| PUT    | /envios/{Id}           | Updates shipping status.                  | MANAGER, ADMIN         | ⏳ Planned   |
| GET    | /cupons/{codigo}       | Checks coupon validity.                   | USER, MANAGER, ADMIN   | ⏳ Planned   |
| GET    | /cupons                | Lists all available coupons.              | MANAGER, ADMIN         | ⏳ Planned   |
| POST   | /cupons                | Creates a new coupon.                     | ADMIN                  | ⏳ Planned   |
| DELETE | /cupons/{id}           | Deletes a coupon.                         | ADMIN                  | ⏳ Planned   |
| POST   | /avaliacoes            | Creates a product review.                 | USER, MANAGER, ADMIN   | ✅ Ready     |
| GET    | /avaliacoes/{produtoId} | Lists product reviews.                    | Public                 | ✅ Ready     |

### Request/Response Examples

#### 1. Register New User

* **Endpoint:** `POST /auth/registro`
* **Access:** Public
* **Request (Body):**
    ```json
    {
      "nome": "João Silva",
      "email": "joao.silva@example.com",
      "senha": "senhaSegura123",
      "cpf": "12345678900",
      "dataNascimento": "10/05/1990",
      "telefone": "19912345678"
    }
    ```
* **Response (Success):** `200 OK` (Empty body)
* **Response (Error - Duplicate Email):** `400 Bad Request`

#### 2. Authenticate User (Login)

* **Endpoint:** `POST /auth/login`
* **Access:** Public
* **Request (Body):**
    ```json
    {
      "email": "joao.silva@example.com",
      "senha": "senhaSegura123"
    }
    ```
* **Response (Success):** `200 OK`
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Your JWT token
    }
    ```
* **Response (Error - Invalid Credentials):** `403 Forbidden` (Or other status, depending on security configuration)

#### 3. List Products

* **Endpoint:** `GET /produtos`
* **Access:** Public
* **Request:** N/A (No body)
* **Response (Success):** `200 OK`
    ```json
    [
      {
        "id": 1,
        "nome": "Smartphone XPTO",
        "descricao": "Great smartphone with triple camera.",
        "preco": 1999.90,
        "categoria": "Eletrônicos",
        "imagemUrl": "[http://example.com/images/sphone.jpg](http://example.com/images/sphone.jpg)",
        "avaliacoes": [
            // ... list of Review objects
        ],
        "peso": 0.180,
        "altura": 15.0,
        "largura": 7.0,
        "comprimento": 0.8
      },
      {
        "id": 2,
        "nome": "Notebook ABC",
        // ... other details
      }
      // ... other products
    ]
    ```

#### 4. Add Item to Cart

* **Endpoint:** `POST /carrinho/adicionar`
* **Access:** USER, MANAGER, ADMIN (Requires Bearer Token)
* **Request (Body):**
    ```json
    {
      "idProduto": 1, // Product ID to add
      "quantidade": 2
    }
    ```
* **Response (Success):** `200 OK` (Returns updated cart state)
    ```json
    {
      "id": 123, // Cart ID
      "itens": [
        {
          "id": 456, // Cart item ID
          "nome": "Smartphone XPTO",
          "preco": 1999.90,
          "imagem_url": "[http://example.com/images/sphone.jpg](http://example.com/images/sphone.jpg)",
          "quantidade": 2
        }
        // ... other cart items
      ]
    }
    ```

## ✅ Running Tests

To ensure code quality and stability, run the complete suite of unit and integration tests:

```bash
./mvnw clean verify
```

## 🤝 How to Contribute

Contributions are welcome! Follow these steps:

1. Fork the project.
2. Create your Feature Branch (```git checkout -b feature/NewFeature```).
3. Commit your changes (```git commit -m 'feat: Adds NewFeature'```).
4. Push to the Branch (```git push origin feature/NewFeature```).
5. Open a Pull Request.

## 📜 License

Distributed under the MIT license. See ```LICENSE.md``` for more information.

## Notes

**Owner/Grant in SQL:** I removed `OWNER TO` and `GRANT` from the Flyway script for H2 compatibility. For PostgreSQL production, it may be necessary to adjust permissions manually or add specific `GRANT` commands if the application user is not the schema owner. However, for most configurations, it will not be needed.