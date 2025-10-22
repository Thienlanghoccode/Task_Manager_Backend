# 🧩 Task Manager – Spring Boot Project

## 📘 Giới thiệu
**Task Manager** là project backend viết bằng **Spring Boot**, sử dụng **PostgreSQL** làm cơ sở dữ liệu.  
Dự án cho phép quản lý công việc, người dùng và phân quyền, hỗ trợ API RESTful có thể kết nối với frontend React hoặc bất kỳ client nào.

---

## ⚙️ 1. Yêu cầu hệ thống

| Công cụ | Phiên bản khuyến nghị |
|----------|------------------------|
| **Java JDK** | 17 trở lên |
| **Maven** | 3.8 trở lên |
| **PostgreSQL** | 14+ |
| **Git** | Mới nhất |
| **IDE** | IntelliJ IDEA / Eclipse / VS Code |

---

## 📦 2. Cài đặt & chạy

### 🔹 Bước 1. Clone project
```bash
git clone https://github.com/<your-username>/Task-Manager.git
cd Task-Manager
```

### 🔹 Bước 2. Chạy sql
```bash
Mở file .sql và run
```

### 🔹 Bước 3. Tạo file .env
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=task_manager
DB_USERNAME=postgres
DB_PASSWORD=your_password

SERVER_PORT=8081
JWT_SECRET=your_secret_key_here
```

### 🔹 Bước 4. Cấu hình application.properties
```bash
spring.config.import=optional:file:.env[.properties]

spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

server.port=${SERVER_PORT}
app.jwt.secret=${JWT_SECRET}
```

### 🔹 Bước 5. Build & chạy project
```bash
mvn clean install
mvn spring-boot:run
```
