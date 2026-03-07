# Job Hunter — Backend API

REST API backend cho ứng dụng tuyển dụng **Job Hunter**, xây dựng bằng **Spring Boot 3** + **Spring Security** (JWT).

---

## 🚀 Công Nghệ Sử Dụng

| Công nghệ | Phiên bản |
|-----------|-----------|
| Java | 17+ |
| Spring Boot | 3.x |
| Spring Security | OAuth2 Resource Server (JWT) |
| Spring Data JPA | Hibernate |
| MySQL | 8.x |
| Gradle | 8.7 |
| Lombok | Latest |

---

## ⚙️ Cấu Hình & Cài Đặt

### 1. Yêu Cầu

- Java 17+
- MySQL 8 đang chạy
- Gradle (hoặc dùng `./gradlew` wrapper)

### 2. Tạo Database

```sql
CREATE DATABASE jobhunter;
```

### 3. Cấu Hình `application.properties`

File: `src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/jobhunter
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JWT
jwt.base64-secret=YOUR_BASE64_SECRET
jwt.access-token-validity-in-seconds=86400
jwt.refresh-token-validity-in-seconds=8640000

# Upload file
dwchwang.upload-file.base-uri=file:///path/to/upload/folder/
```

> **Lưu ý:** Schema database sẽ tự động được tạo/cập nhật nhờ `spring.jpa.hibernate.ddl-auto=update`.

### 4. Chạy Ứng Dụng

```bash
# Cấp quyền thực thi (lần đầu)
chmod +x gradlew

# Chạy server
./gradlew bootRun
```

Server sẽ khởi động tại: **http://localhost:8080**

---

## 📁 Cấu Trúc Dự Án

```
src/main/java/vn/dwchwang/jobhunter/
├── config/                     # Cấu hình Spring (CORS, Security, JWT...)
│   ├── CorsConfig.java
│   ├── SecurityConfiguration.java
│   └── UserDetailCustom.java
├── controller/                 # REST Controllers
│   ├── AuthController.java
│   ├── CompanyController.java
│   ├── FileController.java
│   ├── JobController.java
│   ├── PermissionController.java
│   ├── ResumeController.java
│   ├── RoleController.java
│   ├── SkillController.java
│   ├── SubscriberController.java
│   └── UserController.java
├── domain/                     # JPA Entities & DTOs
│   ├── Company.java
│   ├── Job.java
│   ├── Permission.java
│   ├── Resume.java
│   ├── Role.java
│   ├── Skill.java
│   ├── Subscriber.java
│   ├── User.java
│   ├── request/
│   └── response/
├── repository/                 # Spring Data JPA Repositories
├── service/                    # Business Logic
└── util/                       # Utilities (JWT, Exception, Annotation...)
    ├── FormatRestResponse.java  # Tự động wrap response → {statusCode, message, data}
    ├── SecurityUtil.java
    └── error/GlobalException.java
```

---

## 🔑 API Endpoints

### Auth (Public — không cần token)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/v1/auth/register` | Đăng ký tài khoản mới |
| POST | `/api/v1/auth/login` | Đăng nhập, nhận `access_token` |
| GET | `/api/v1/auth/refresh` | Làm mới access token (dùng refresh token cookie) |

### Auth (Cần Bearer Token)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/auth/account` | Lấy thông tin tài khoản hiện tại |
| POST | `/api/v1/auth/logout` | Đăng xuất |

### Users

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/users` | Danh sách users (phân trang, filter) |
| GET | `/api/v1/users/{id}` | Lấy user theo ID |
| POST | `/api/v1/users` | Tạo user mới |
| PUT | `/api/v1/users` | Cập nhật user |
| DELETE | `/api/v1/users/{id}` | Xoá user |

### Companies

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/companies` | Danh sách công ty |
| GET | `/api/v1/companies/{id}` | Lấy công ty theo ID |
| POST | `/api/v1/companies` | Tạo công ty |
| PUT | `/api/v1/companies` | Cập nhật công ty |
| DELETE | `/api/v1/companies/{id}` | Xoá công ty |

### Jobs

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/jobs` | Danh sách việc làm |
| GET | `/api/v1/jobs/{id}` | Lấy việc làm theo ID |
| POST | `/api/v1/jobs` | Tạo việc làm |
| PUT | `/api/v1/jobs` | Cập nhật việc làm |
| DELETE | `/api/v1/jobs/{id}` | Xoá việc làm |

### Skills

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/skills` | Danh sách kỹ năng |
| POST | `/api/v1/skills` | Tạo kỹ năng |
| PUT | `/api/v1/skills` | Cập nhật kỹ năng |
| DELETE | `/api/v1/skills/{id}` | Xoá kỹ năng |

### Resumes

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/resumes` | Danh sách CV |
| GET | `/api/v1/resumes/{id}` | Lấy CV theo ID |
| POST | `/api/v1/resumes` | Nộp CV |
| POST | `/api/v1/resumes/by-user` | Lấy CV của user hiện tại |
| PUT | `/api/v1/resumes` | Cập nhật trạng thái CV |
| DELETE | `/api/v1/resumes/{id}` | Xoá CV |

### Roles & Permissions

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/roles` | Danh sách vai trò |
| POST | `/api/v1/roles` | Tạo vai trò |
| PUT | `/api/v1/roles` | Cập nhật vai trò |
| DELETE | `/api/v1/roles/{id}` | Xoá vai trò |
| GET | `/api/v1/permissions` | Danh sách quyền |
| POST | `/api/v1/permissions` | Tạo quyền |
| PUT | `/api/v1/permissions` | Cập nhật quyền |
| DELETE | `/api/v1/permissions/{id}` | Xoá quyền |

### Subscribers

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/v1/subscribers` | Danh sách subscriber |
| POST | `/api/v1/subscribers` | Đăng ký nhận thông báo |
| POST | `/api/v1/subscribers/skills` | Lấy skills của subscriber hiện tại |
| PUT | `/api/v1/subscribers` | Cập nhật subscriber |
| DELETE | `/api/v1/subscribers/{id}` | Xoá subscriber |

### Files

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/v1/files` | Upload file (CV, logo...) |

---

## 📋 Response Format

Tất cả response được wrap tự động bởi `FormatRestResponse`:

```json
{
  "statusCode": 200,
  "message": "fetch account",
  "data": {
    "access_token": "eyJ...",
    "user": {
      "id": 1,
      "name": "Admin",
      "email": "admin@gmail.com",
      "role": {
        "id": 1,
        "name": "SUPER_ADMIN",
        "permissions": [...]
      }
    }
  }
}
```

**Error response:**
```json
{
  "statusCode": 400,
  "message": "Email đã tồn tại",
  "error": "Exception Occurred ..."
}
```

---

## 🔐 Bảo Mật

- **JWT Access Token**: gửi qua header `Authorization: Bearer <token>`
- **Refresh Token**: lưu trong `HttpOnly Cookie` tên `refresh_token`
- **CORS**: cho phép từ `localhost:3000`, `localhost:4173`, `localhost:5173`
- Các endpoint public (không cần token):
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/register`
  - `GET /api/v1/auth/refresh`
  - `GET /storage/**`

---

## 🔗 Kết Nối Frontend

Backend mặc định chạy tại `http://localhost:8080` và frontend kết nối qua biến môi trường `VITE_BACKEND_URL` trong file `.env`.

Compatible với frontend: **[GitHub — dwchwang/jobhunter_FE](https://github.com/dwchwang/jobhunter_FE)**
