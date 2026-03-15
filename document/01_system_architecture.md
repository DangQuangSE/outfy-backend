# System Architecture

## 1. Mục tiêu kiến trúc

Hệ thống được thiết kế để:
- hỗ trợ demo nhanh bằng web
- tái sử dụng backend cho app mobile sau này
- tách riêng business backend và 3D/AI pipeline
- cho phép thay mock service bằng service thật mà không phải viết lại toàn bộ backend

## 2. Các thành phần chính

### 2.1 Client Layer
Có thể là:
- Web demo
- Mobile app sau này
- Admin portal

Client chỉ gọi đến Core Backend API.

### 2.2 Core Backend API (Spring Boot)
Đây là hệ thống backend chính.

Chức năng:
- Authentication / Authorization
- User Profile
- Body Profile
- Wardrobe / Clothing Item
- Try-On Session
- Outfit Recommendation
- Orchestration giữa client và các service AI / Unity
- Lưu dữ liệu vào database
- Trả response cho client

### 2.3 AI Body Service
Phụ trách:
- nhận measurements hoặc ảnh
- suy luận body type
- sinh shape parameters
- map sang avatar preset hoặc body mesh metadata

### 2.4 AI Cloth Service
Phụ trách:
- phân tích ảnh quần áo
- phân loại category
- trích xuất attribute
- sinh garment parameters
- map sang cloth template / cloth metadata

### 2.5 Unity / 3D Service
Phụ trách:
- load avatar
- load garment
- fit garment
- chạy simulation
- render preview ảnh / video / metadata

### 2.6 Storage Layer
Lưu:
- preview images
- garment assets
- avatar assets
- try-on result images
- logs hoặc debug outputs

### 2.7 Database Layer
Lưu:
- user
- body profiles
- body generation results
- clothes
- cloth analysis results
- try-on sessions
- try-on results
- recommendation logs

## 3. Luồng tổng thể

```text
Client (Web/App)
    ↓
Spring Boot Backend API
    ↓
AI Body Service / AI Cloth Service / Unity 3D Service
    ↓
Database + Storage
```

## 4. Kiến trúc phù hợp cho demo trước mắt

### Demo 2 ngày
- FE demo bằng web
- BE dùng Spring Boot
- AI Body Service có thể fake hoặc rule-based
- AI Cloth Service có thể fake hoặc gọi model đơn giản
- Unity Service có thể mock preview image

### Vì sao phù hợp?
- làm nhanh
- dễ demo
- vẫn giữ được kiến trúc đúng để phát triển tiếp

## 5. Nguyên tắc để không phải viết lại source sau này

### 5.1 Không hard-code logic demo vào controller
Controller chỉ nên:
- nhận request
- gọi service
- trả response

### 5.2 Dùng interface cho external services
Ví dụ:
- BodyGenerationGateway
- ClothAnalysisGateway
- TryOnGateway
- RecommendationGateway

Demo dùng mock implementation.
Sau này thay bằng implementation thật.

### 5.3 Tách DTO ổn định
Các DTO response như:
- BodyGenerationResponse
- ClothAnalysisResponse
- TryOnResultResponse
nên giữ ổn định để FE web và mobile cùng dùng.

### 5.4 Dùng trạng thái xử lý
Ví dụ:
- PENDING
- PROCESSING
- SUCCESS
- FAILED

Điều này giúp sau này chuyển sang xử lý async dễ hơn.

## 6. Kiến trúc module gợi ý trong Spring Boot

```text
src/main/java/com/example/tryon
├── auth
├── user
├── bodyprofile
├── avatar
├── clothing
├── wardrobe
├── tryon
├── recommendation
├── common
├── infrastructure
│   ├── persistence
│   ├── clients
│   └── storage
└── config
```

## 7. Kiến trúc service interfaces

- `BodyProfileService`
- `BodyGenerationService`
- `ClothingService`
- `ClothAnalysisService`
- `TryOnService`
- `RecommendationService`

External gateways:
- `BodyAiClient`
- `ClothAiClient`
- `UnityTryOnClient`
- `WeatherClient` (nếu cần)

## 8. Kiến trúc triển khai về sau

### Giai đoạn demo
- 1 Spring Boot app
- mock AI service trong cùng source hoặc service giả
- ảnh preview fake

### Giai đoạn MVP thật
- Spring Boot API
- Python AI body service
- Python AI cloth service
- Unity render service
- object storage
- PostgreSQL/MySQL

## 9. Kết luận

Bạn hoàn toàn có thể:
- dùng Spring Boot để làm BE cho app
- demo trước bằng web
- giữ lại source Java để phát triển tiếp

Điều kiện là:
- kiến trúc phải tách lớp rõ
- mock data phải nằm ở service/gateway layer
- không nhúng logic demo cứng vào business core
