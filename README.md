# E-Commerce Microservices Backend

This repository contains the backend implementation for an E-Commerce application based on a Microservices architecture using the Spring Cloud ecosystem.

## 🏗 Architecture

The project is composed of the following microservices:

* **Config Service** (`config-service`): Centralized configuration server for all microservices.
* **Discovery Service** (`discovery-service`): Netflix Eureka Server for service registration and discovery.
* **Gateway Service** (`gateway-service`): Spring Cloud Gateway acting as the single entry point, routing requests to appropriate services.
* **Customer Service** (`customer-service`): Manages customer data (Entities: `Customer`).
* **Inventory Service** (`inventory-service`): Manages product inventory (Entities: `Product`).
* **Billing Service** (`billing-service`): Manages bills and product items, communicating with Customer and Inventory services via OpenFeign.

## 🛠 Technologies

* **Java**: 21
* **Spring Boot**: 3.5.7
* **Spring Cloud**: 2025.0.0
* **Database**: H2 (In-memory)
* **Build Tool**: Maven
* **Other**: Lombok, Spring Data REST, OpenFeign.

## 🚀 Getting Started

### Prerequisites
* Java Development Kit (JDK) 21
* Maven

### Installation
1.  Clone this repository.
2.  Navigate to each service directory and build the project:
    ```bash
    mvn clean install
    ```

### Running the Services
To ensure the system works correctly, start the services in the following order:

1.  **Config Service**: Port `9999`
2.  **Discovery Service**: Port `8761`
3.  **Customer Service**: Port `8081`
4.  **Inventory Service**: Port `8082`
5.  **Billing Service**: Port `8083`
6.  **Gateway Service**: Port `8888`

### 🔌 API Endpoints
All services are accessible through the Gateway Service running on `http://localhost:8888`.

* **Customers**: `GET /customer-service/api/customers`
* **Products**: `GET /inventory-service/api/products`
* **Bills**: `GET /billing-service/api/bills`

### ⚙️ Configuration
The configuration for all services is managed by the `config-service`, which fetches properties from the external repository:
`https://github.com/MokhtarLahjaily/ecom-app-config-repo`.