# Hướng dẫn cấu hình Environment Variables

## 1. Mục đích

File `.env` dùng để lưu trữ các thông tin nhạy cảm như:
- Database credentials
- API keys
- JWT secret keys
- Các cấu hình riêng của từng môi trường

**Lưu ý:** File `.env` KHÔNG được commit lên Git (đã có trong `.gitignore`).

## 2. Cấu trúc file .env

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=outfy_db
DB_USERNAME=outfy
DB_PASSWORD=outfy123

# Application Configuration
APP_PORT=8080
APP_ENV=development

# JWT Secret Key (thay đổi giá trị này khi deploy production)
JWT_SECRET=outfy_jwt_secret_key_change_in_production_12345678

# (Optional) External Services
# AI_SERVICE_URL=http://localhost:8081
# STORAGE_URL=http://localhost:8082
```

## 3. Cách sử dụng

### Cách 1: Sử dụng trực tiếp (cho development)

Spring Boot tự động đọc các biến từ `.env` nếu bạn sử dụng plugin `dotenv`.

Thêm vào `pom.xml`:

```xml
<plugin>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-maven-plugin</artifactId>
    <version>1.0.1</version>
    <configuration>
        <path>${project.basedir}</path>
    </configuration>
</plugin>
```

### Cách 2: Export biến môi trường (khuyên dùng)

Trước khi chạy Spring Boot, export các biến:

```powershell
# PowerShell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="outfy_db"
$env:DB_USERNAME="outfy"
$env:DB_PASSWORD="outfy123"
$env:JWT_SECRET="your_jwt_secret"

# Chạy Spring Boot
cd outfy-backend
./mvnw spring-boot:run
```

### Cách 3: Sử dụng Run Configuration trong IDE

Trong IntelliJ IDEA hoặc VS Code:
1. Tạo Run Configuration mới
2. Thêm Environment Variables trong phần cấu hình
3. Chạy như bình thường

## 4. Cấu hình trong application.properties

File `application.properties` đã được cấu hình để đọc từ environment variables với giá trị mặc định:

```properties
# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:outfy_db}
spring.datasource.username=${DB_USERNAME:outfy}
spring.datasource.password=${DB_PASSWORD:outfy123}

# Server
server.port=${APP_PORT:8080}

# JWT
app.jwt.secret=${JWT_SECRET:default_jwt_secret_key}
```

## 5. Quy tắc đặt tên biến

- Sử dụng UPPER_SNAKE_CASE
- Thêm prefix để tránh xung đột: `DB_`, `APP_`, `JWT_`
- Giá trị mặc định sau dấu hai chấm `:`

## 6. Bảo mật

- KHÔNG BAO GIỜ commit file `.env` lên Git
- Sử dụng `.env.example` để template cho teammate
- Đổi JWT_SECRET khi deploy production
- Không share credentials qua chat

## 7. Troubleshooting

### Lỗi kết nối database

Kiểm tra các biến:
- `DB_HOST` - thường là `localhost` hoặc `host.docker.internal` (nếu chạy Docker)
- `DB_PORT` - mặc định PostgreSQL là `5432`
- `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`

### Lỗi không đọc được .env

Đảm bảo:
- File `.env` nằm ở thư mục gốc project
- Không có khoảng trắng thừa
- Không có dấu quote không cần thiết

