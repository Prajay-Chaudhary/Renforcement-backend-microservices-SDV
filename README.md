# Microservices Authentication & API Gateway Project

## 📌 Overview
This project implements a **microservices architecture** with authentication and authorization using **Spring Boot, Spring Cloud Gateway, JWT (JSON Web Token), and Eureka Service Discovery**. The system consists of:

- **API Gateway** (`8082`): Handles authentication and request routing.
- **Auth Service** (`9003`): Manages user registration, login, and JWT token generation.
- **School Service** (`8080`): Provides school-related endpoints (protected by JWT).
- **Student Service** (`8081`): Provides student-related endpoints (protected by JWT).
- **Eureka Service Discovery** (`8761`): Registers and manages service discovery.

---

## ⚙️ Technologies Used
- **Java 23**
- **Spring Boot** (WebFlux, Security, Cloud Gateway, Data JPA)
- **Spring Cloud Gateway** (API Gateway)
- **Spring Security** (JWT Authentication)
- **PostgreSQL && MongoDB** (Database for the different Service)
- **Eureka** (Service Discovery)
- **JJWT** (JWT Authentication & Verification)
- **lombok**
- ** openfeign**
- **spring boot actuator**

---

## 📌 Architecture Diagram
```
[Client] --> [API Gateway (8082)] --> [School Service (8080)]
                          |----> [Student Service (8081)]
                          |----> [Auth Service (9003)]
                          |----> [Eureka Server (8761)]
```
---

## 🚀 Setting Up The Project
### 1️⃣ **Clone the Repository**
```sh
git clone https://github.com/Prajay-Chaudhary/school-student-microservice-SDV.git
cd microservices-auth
```

### 2️⃣ **Start Eureka Server**
```sh
cd eureka-server
mvn spring-boot:run
```

### 3️⃣ **Start API Gateway**
```sh
cd api-gateway
mvn spring-boot:run
```

### 4️⃣ **Start Auth Service**
```sh
cd auth-service
mvn spring-boot:run
```

### 5️⃣ **Start School & Student Services**
```sh
cd school-service
mvn spring-boot:run

cd student-service
mvn spring-boot:run
```

---

## 🏗️ **API Gateway Configuration** (`8082`)
Located in `application.yml`:
```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/auth/**
        - id: school-service
          uri: lb://SCHOOL-SERVICE
          predicates:
            - Path=/api/schools/**
        - id: student-service
          uri: lb://STUDENT-SERVICE
          predicates:
            - Path=/api/students/**

# Eureka Client Configuration
eureka:
  instance:
    prefer-ip-address: true
  client:
    enabled: true
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka/

# Enable Dynamic Route Discovery
spring.cloud.gateway.discovery.locator.enabled: true



#Actuator config
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
      gateway:
        enabled: true
  prometheus:
    metrics:
      export:
        enabled: true

server:
  port: 8082
```

---

## 🔐 **JWT Authentication Flow via the Gateway**
1️⃣ **User Registers** via `/api/auth/register`
2️⃣ **User Logs In** via `/api/auth/login` → Receives JWT token.
3️⃣ **User Calls Protected APIs** (e.g., `/api/schools`) with `Authorization: Bearer <token>`.
4️⃣ **API Gateway Validates JWT** before forwarding the request.

---

## 🔑 **Testing API Endpoints**
### **1️⃣ Register a New User**
```sh
curl -X POST http://localhost:8082/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username": "newuser", "password": "password123"}'
```
✅ Response: `User registered successfully!`

### **2️⃣ Login to Get JWT Token**
```sh
curl -X POST http://localhost:8082/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "newuser", "password": "password123"}'
```
✅ Response:
```json
{ "token": "eyJhbGc... (JWT Token)" }
```

### **3️⃣ Access a Protected Route (Requires JWT)**
```sh
curl -X GET http://localhost:8082/api/schools \
     -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```
✅ Response: List of schools
❌ Unauthorized (401) if token is missing/invalid.

---

## 📜 **API Gateway Security: JWT Filter**
Located in `JwtAuthFilter.java`:
```java
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "mySecretKeymySecretKeymySecretKeymySecretKey"; // 32+ characters

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Allow unauthenticated access to Auth Service (login & register)
        List<String> openEndpoints = List.of("/api/auth/login", "/api/auth/register");
        if (openEndpoints.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Token is valid, proceed with the request
            return chain.filter(exchange);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

---

## 🛠️ **Troubleshooting**
### 🔴 `404 Not Found` on API Gateway
✔ Check **Eureka Dashboard (`http://localhost:8761`)** to ensure all services are registered.
✔ Verify **routes in `application.yml`** (Check `Path` predicates).
✔ Restart the **API Gateway**.

### 🔴 JWT Validation Fails
✔ Ensure **Auth Service & API Gateway use the same `SECRET_KEY`**.
✔ Ensure **JWT is included in `Authorization: Bearer <token>` header**.

---

## 🚀 **Next Steps**
✅ Add **Docker Compose** to containerize all services.
✅ Implement **Role-Based Access Control (RBAC)** with JWT.
✅ Enhance **Logging & Monitoring** (Spring Boot Actuator, Prometheus, Grafana).

---

## 📜 **Screenshots**

<img width="1440" alt="Screenshot 2025-02-06 at 16 11 21" src="https://github.com/user-attachments/assets/081df156-9d93-4bbd-83a9-e8561b2f20ea" />

<img width="1440" alt="Screenshot 2025-02-06 at 16 10 11" src="https://github.com/user-attachments/assets/7fcc0f6a-ccec-43fb-a24e-739e89c85462" />

<img width="1440" alt="Screenshot 2025-02-06 at 16 10 25" src="https://github.com/user-attachments/assets/2aa8bf05-8a92-461a-881e-1482181fe18c" />

<img width="1440" alt="Screenshot 2025-02-06 at 16 10 31" src="https://github.com/user-attachments/assets/48b367d7-4122-4b4a-9c6e-c3b11dcab9a9" />

<img width="1440" alt="Screenshot 2025-02-06 at 16 10 47" src="https://github.com/user-attachments/assets/8aeaddfb-e9be-46ef-bec0-879e8e3f8772" />







