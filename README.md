#  Stock Exchange Management API

## Introduction

The Stock Exchange application is a Java-based backend application using Spring Boot 3, Maven, and Java 21. 
It manages stock exchanges and stocks, with endpoints for creating, updating, and deleting stocks, as well as adding and removing stocks from stock exchanges. 
The application supports role-based access control with USER and ADMIN roles and includes comprehensive OpenAPI documentation.

##  Technologies Used

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database**
- **Swagger/OpenAPI**
- **Lombok**

##  Getting Started

### Prerequisites

- **Java 21**
- **Maven 3.6+**

### Installation

1. **Clone the repository:**
   ```sh
    git clone [https://github.com/erdogangorkem/stock-exchange.git]
   ```

2. **Build the project:**
   ```sh
    mvn clean install
    ```
3. **Run the application:**
      ```sh
    mvn spring-boot:run
    ```

##  Testing

### Running Tests

To run the tests, use the following command:
```sh
mvn test	
 ```

## Role and Access Management
In the Stock Exchange Management API, there are two primary roles defined to manage access and permissions for different endpoints and actions within the application:

1. **USER** : limited to read-only operations. This role cannot perform any create, update or delete operations. Only allowed to http GET requests.
2. **ADMIN** : has full access to the application's functionality. This role can access all endpoints and do all operations. Allowed to Http GET, PUT, POST, DELETE and all requests.
   
The application defines the following in-memory users:
| Username  |  Password | Roles  |
| ------------ | ------------ | ------------ |
| admin  | password  |  ADMIN |
| user  | password  | USER  |

Endpoints that do not require any role have been added to the *permitAll* list. These URLs are left public to perform API tests and access the application database.

**permitAll** list: */v3/api-docs/\*\**, /swagger-ui/\*\**, /swagger-ui.html, /swagger-resources/\*\**, /webjars/\*\**, /h2-console/\*\*

## Swagger UI

### Accessing Swagger UI

1. Open your browser and navigate to `http://localhost:8080/swagger-ui.html`

### Authorizing via Swagger UI

1. Click on the `Authorize` button.
2. Enter the following credentials based on the role you want to use:

| Role  | Username | Password |
|-------|----------|----------|
| ADMIN | `admin`  | `password`|
| USER  | `user`   | `password`|

##  Database Design

The database consists of the following tables:

### Stock Table

| Column       | Type         | Description               |
|--------------|--------------|---------------------------|
| `id`         | Long         | Primary key               |
| `name`       | String       | Name of the stock         |
| `description`| String       | Description of the stock  |
| `currentPrice` | BigDecimal | Current price of the stock|
| `lastUpdate` | Timestamp    | Last update timestamp     |

### StockExchange Table

| Column       | Type         | Description                     |
|--------------|--------------|---------------------------------|
| `id`         | Long         | Primary key                     |
| `name`       | String       | Name of the stock exchange      |
| `description`| String       | Description of the stock exchange|
| `liveInMarket` | Boolean    | Indicates if live in the market |

### StockExchange_Stocks Table

| Column             | Type | Description                       |
|--------------------|------|-----------------------------------|
| `stockExchange_id` | Long | Foreign key referencing StockExchange |
| `stock_id`         | Long | Foreign key referencing Stock     |
| **Primary Key**    | Combination of `stockExchange_id` and `stock_id`|

There is also a version column in database entities. The purpose of this column is to provide java-optimistic locking with the help of the field held with @Version in the Java entity.

##  Database Scripts
When the application starts, **schema.sql** and **data.sql** are run by the application. When the application is running, it keeps the relevant information in the in-memory database. There is no need to configure any database or run an SQL script to test.

### schema.sql

```sql
CREATE TABLE stock_exchange
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    description    VARCHAR(1024),
    live_in_market BOOLEAN      NOT NULL,
    version        INT          NOT NULL
);

CREATE TABLE stock
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)   NOT NULL UNIQUE,
    description   VARCHAR(1024),
    current_price DECIMAL(15, 2) NOT NULL,
    last_update   TIMESTAMP      NOT NULL,
    version       INT            NOT NULL
);

CREATE TABLE stock_exchange_stock
(
    stock_exchange_id BIGINT NOT NULL,
    stock_id          BIGINT NOT NULL,
    PRIMARY KEY (stock_exchange_id, stock_id),
    FOREIGN KEY (stock_exchange_id) REFERENCES stock_exchange (id),
    FOREIGN KEY (stock_id) REFERENCES stock (id)
);
```
### data.sql

```sql

INSERT INTO stock_exchange (name, description, live_in_market, version)
VALUES ('A', 'A Stock Exchange', FALSE, 0),
       ('B', 'B Stock Exchange', FALSE, 0),
       ('C', 'C Stock Exchange', FALSE, 0);

INSERT INTO stock (name, description, current_price, last_update, version)
VALUES ('Tesla', 'Tesla Inc.', 100.00, CURRENT_TIMESTAMP, 0),
       ('Amazon', 'Amazon.com Inc.', 200.00, CURRENT_TIMESTAMP, 0),
       ('Facebook', 'Meta Platforms Inc.', 300.00, CURRENT_TIMESTAMP, 0),
       ('Netflix', 'Netflix Inc.', 4000.00, CURRENT_TIMESTAMP, 0),
       ('Nvidia', 'Nvidia Corporation', 5000.00, CURRENT_TIMESTAMP, 0),
       ('Apple', 'Apple Inc.', 6000.00, CURRENT_TIMESTAMP, 0),
       ('Microsoft', 'Microsoft Corporation', 700.00, CURRENT_TIMESTAMP, 0),
       ('Google', 'Alphabet Inc.', 800.00, CURRENT_TIMESTAMP, 0);
INSERT INTO stock_exchange_stock (stock_exchange_id, stock_id)
VALUES (1, 1),
       (1, 2),
       (1, 3);
```

##  H2 Console

### Accessing H2 Console

1. Open your browser and navigate to `http://localhost:8080/h2-console`.
2. Enter the following details:
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: `password`

## Endpoints Overview

### Stock Endpoints

#### 1. Create a Stock

- **Endpoint**: `POST /api/v1/stock`
- **Purpose**: Create a new stock.
- **Request Example**:
    ```json
    {
        "name": "Tesla",
        "description": "Tesla Inc.",
        "currentPrice": 100.00
    }
    ```
- **Response Example**:
    ```json
    {
        "id": 1,
        "name": "Tesla",
        "description": "Tesla Inc.",
        "currentPrice": 100.00,
        "lastUpdate": "2024-07-30T12:34:56"
    }
    ```

#### 2. Update Stock Price

- **Endpoint**: `PUT /api/v1/stock`
- **Purpose**: Update the price of an existing stock.
- **Request Example**:
    ```json
    {
        "id": 1,
        "currentPrice": 150.00
    }
    ```
- **Response Example**:
    ```json
    {
        "id": 1,
        "name": "Tesla",
        "description": "Tesla Inc.",
        "currentPrice": 150.00,
        "lastUpdate": "2024-07-30T12:34:56"
    }
    ```

#### 3. Delete a Stock

- **Endpoint**: `DELETE /api/v1/stock/{id}`
- **Purpose**: Delete a stock by its ID.
- **Request Example**:
    ```http
    DELETE /api/v1/stock/1
    ```
- **Response Example**:
    ```http
    HTTP/1.1 204 No Content
    ```

### Stock Exchange Endpoints

#### 1. Get Stock Exchange

- **Endpoint**: `GET /api/v1/stock-exchange/{name}`
- **Purpose**: Retrieve a StockExchange by its name.
- **Request Example**:
    ```http
    GET /api/v1/stock-exchange/A
    ```
- **Response Example**:
    ```json
    {
        "id": 1,
        "name": "A",
        "description": "A Stock Exchange",
        "liveInMarket": true,
        "stocks": [
            {
                "id": 1,
                "name": "Tesla",
                "description": "Tesla Inc.",
                "currentPrice": 150.00,
                "lastUpdate": "2024-07-30T12:34:56"
            }
        ]
    }
    ```

#### 2. Add Stock to Stock Exchange

- **Endpoint**: `POST /api/v1/stock-exchange/{name}`
- **Purpose**: Add a stock to a StockExchange.
- **Request Example**:
    ```http
    POST /api/v1/stock-exchange/A?stockId=2
    ```
- **Response Example**:
    ```json
    {
        "id": 1,
        "name": "A",
        "description": "A Stock Exchange",
        "liveInMarket": true,
        "stocks": [
            {
                "id": 1,
                "name": "Tesla",
                "description": "Tesla Inc.",
                "currentPrice": 150.00,
                "lastUpdate": "2024-07-30T12:34:56"
            },
            {
                "id": 2,
                "name": "Amazon",
                "description": "Amazon.com Inc.",
                "currentPrice": 200.00,
                "lastUpdate": "2024-07-30T12:34:56"
            }
        ]
    }
    ```

#### 3. Remove Stock from Stock Exchange

- **Endpoint**: `DELETE /api/v1/stock-exchange/{name}`
- **Purpose**: Remove a stock from a StockExchange.
- **Request Example**:
    ```http
    DELETE /api/v1/stock-exchange/A?stockId=1
    ```
- **Response Example**:
    ```json
    {
        "id": 1,
        "name": "A",
        "description": "A Stock Exchange",
        "liveInMarket": true,
        "stocks": [
            {
                "id": 2,
                "name": "Amazon",
                "description": "Amazon.com Inc.",
                "currentPrice": 200.00,
                "lastUpdate": "2024-07-30T12:34:56"
            }
        ]
    }
    ```

### Summary

- **Stock Endpoints**: Manage stocks.
- **Stock Exchange Endpoints**: Manage stock exchanges and their relationships with stocks.

