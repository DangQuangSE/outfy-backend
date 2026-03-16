# Yêu cầu Backend - Model File Authentication

## Vấn đề

Hiện tại FE gọi API `POST /api/v1/body-profiles/generate-avatar` và nhận về URL model 3D (`.glb` file):

```json
{
  "success": true,
  "message": "...",
  "data": {
    "bodyType": "Regular",
    "modelUrl": "/models/body/regular_male.glb",
    "previewUrl": "/previews/avatar/regular_male.png",
    ...
  }
}
```

Khi FE sử dụng `<model-viewer>` để hiển thị model 3D, browser load trực tiếp file từ:
```
GET http://localhost:8080/models/body/regular_male.glb
```

**Kết quả:** 401 Unauthorized vì request này không có token.

---

## Nguyên nhân

- `model-viewer` load file trực tiếp từ browser → KHÔNG đi qua Axios interceptor
- Axios interceptor thêm `Authorization: Bearer <token>` vào header cho API calls
- Nhưng browser request trực tiếp file `.glb` không có token

---

## Giải pháp đề xuất (chọn 1)

### Option A: Loại bỏ auth cho static model files (Khuyến nghị)

Model 3D files không phải sensitive data. Đề xuất backend configure:

```java
// Spring Boot - WebMvcConfig hoặc SecurityConfig
// Cho phép public access đến /models/** và /previews/**

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve models without authentication
        registry.addResourceHandler("/models/**")
            .addResourceLocations("classpath:/static/models/");
        
        registry.addResourceHandler("/previews/**")
            .addResourceLocations("classpath:/static/previews/");
    }
}
```

Hoặc nếu dùng Spring Security:
```java
// Cho phép public access
.antMatchers("/models/**").permitAll()
.antMatchers("/previews/**").permitAll()
```

---

### Option B: Cung cấp signed URL hoặc tokenized URL

Nếu model files cần protected:

```java
// Tạo API endpoint trả về URL với token
GET /api/v1/models/{fileId}?token={jwt_token}
// Hoặc
GET /api/v1/models/{fileId}/signed-url
```

FE sẽ gọi API này trước để lấy URL hợp lệ, rồi dùng URL đó cho `model-viewer`.

---

## Yêu cầu cụ thể

1. **Serve models without auth**: Cho phép GET `/models/**` mà không cần token
2. **Serve previews without auth**: Cho phép GET `/previews/**` mà không cần token  
3. **Hoặc**: Cung cấp API mới để lấy signed URL cho model files

---

## Ảnh hưởng nếu không sửa

- FE không thể hiển thị 3D avatar
- FE không thể hiển thị 3D cloth models
- FE không thể hiển thị try-on results

---

## Liên hệ

FE Team - Outfy Demo

