# Hướng dẫn cài đặt PostgreSQL Database trên Docker

## 1. Mục tiêu

Hướng dẫn cài đặt và cấu hình PostgreSQL database chạy trên Docker cho dự án Outfy Backend.

## 2. Yêu cầu

- Đã cài đặt Docker Desktop
- Đã cài đặt Git (để sử dụng terminal)

## 3. Các bước cài đặt

### Bước 1: Khởi động Docker Desktop

Tìm và mở **Docker Desktop** trên máy tính.

Đợi khoảng 30-60 giây cho Docker khởi động hoàn tất.

Kiểm tra Docker đã chạy bằng lệnh:

```powershell
docker --version
```

### Bước 2: Tải image PostgreSQL

```powershell
docker pull postgres:latest
```

Lệnh này sẽ tải phiên bản PostgreSQL mới nhất từ Docker Hub.

### Bước 3: Tạo và chạy container PostgreSQL

```powershell
docker run -d --name outfy-postgres -e POSTGRES_USER=outfy -e POSTGRES_PASSWORD=outfy123 -e POSTGRES_DB=outfy_db -p 5432:5432 postgres:latest
```

**Giải thích các tham số:**

| Tham số | Ý nghĩa |
|---------|---------|
| `-d` | Chạy container ở chế độ detached (background) |
| `--name outfy-postgres` | Đặt tên cho container |
| `-e POSTGRES_USER=outfy` | Username đăng nhập database |
| `-e POSTGRES_PASSWORD=outfy123` | Mật khẩu đăng nhập |
| `-e POSTGRES_DB=outfy_db` | Tên database được tạo tự động |
| `-p 5432:5432` | Map port: máy host 5432 → container 5432 |

### Bước 4: Kiểm tra container đang chạy

```powershell
docker ps
```

Nếu thấy `outfy-postgres` đang chạy là thành công.

### Bước 5: Kết nối thử nghiệm (optional)

```powershell
docker exec -it outfy-postgres psql -U outfy -d outfy_db
```

Các lệnh hữu ích trong psql:

- `\dt` - Liệt kê các bảng
- `\q` - Thoát

## 4. Thông tin kết nối

Sau khi tạo database, thông tin kết nối như sau:

| Thuộc tính | Giá trị |
|------------|---------|
| Host | localhost |
| Port | 5432 |
| Database | outfy_db |
| Username | outfy |
| Password | outfy123 |

## 5. Cấu hình Spring Boot

File `application.properties` đã được cấu hình như sau:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/outfy_db
spring.datasource.username=outfy
spring.datasource.password=outfy123
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## 6. Các lệnh quản lý Docker thường dùng

### Xem logs của container

```powershell
docker logs outfy-postgres
```

### Dừng container

```powershell
docker stop outfy-postgres
```

### Khởi động lại container

```powershell
docker start outfy-postgres
```

### Xóa container

```powershell
docker rm -f outfy-postgres
```

### Xem danh sách tất cả container (đang chạy và đã dừng)

```powershell
docker ps -a
```

## 7. Khắc phục sự cố

### Lỗi "docker daemon is not running"

- Mở Docker Desktop và đợi khởi động hoàn tất
- Kiểm tra icon Docker ở System Tray có màu xanh không

### Lỗi "port 5432 is already in use"

- Kiểm tra xem có container nào đang dùng port 5432 không: `docker ps`
- Hoặc đổi port khác: `-p 5433:5432` và cập nhật application.properties

### Lỗi kết nối từ Spring Boot

- Đảm bảo container đang chạy: `docker ps`
- Kiểm tra thông tin kết nối trong application.properties
- Tắt firewall tạm thời nếu cần

## 8. Tài liệu tham khảo

- Docker: https://docs.docker.com/
- PostgreSQL: https://www.postgresql.org/docs/

