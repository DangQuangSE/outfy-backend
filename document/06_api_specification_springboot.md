# API Specification for Spring Boot Backend

## 1. Nguyên tắc chung

- RESTful API
- JSON request/response
- versioning: `/api/v1/...`
- thống nhất response wrapper nếu cần
- tách endpoint user-facing và internal processing

## 2. Body Profile APIs

### POST /api/v1/body-profiles
Tạo hồ sơ body.

Request:
```json
{
  "userId": "user_001",
  "gender": "Female",
  "heightCm": 165,
  "weightKg": 54,
  "chestCm": 84,
  "waistCm": 68,
  "hipCm": 92,
  "shoulderCm": 38,
  "inseamCm": 75
}
```

### POST /api/v1/body-profiles/{profileId}/generate
Generate avatar metadata.

### GET /api/v1/body-profiles/{profileId}
Lấy profile.

### GET /api/v1/body-profiles/{profileId}/result
Lấy kết quả generate.

## 3. Clothing APIs

### POST /api/v1/clothes
Tạo item quần áo.

### POST /api/v1/clothes/{clothingId}/analyze
Phân tích ảnh quần áo.

### GET /api/v1/clothes/{clothingId}
Lấy chi tiết item.

### GET /api/v1/wardrobe
Danh sách wardrobe.

## 4. Try-On APIs

### POST /api/v1/tryon/sessions
Tạo session thử đồ.

Request:
```json
{
  "userId": "user_001",
  "bodyProfileId": "body_001",
  "clothingItemId": "cloth_001",
  "size": "M"
}
```

### GET /api/v1/tryon/sessions/{sessionId}
Lấy session.

### GET /api/v1/tryon/sessions/{sessionId}/result
Lấy result.

### POST /api/v1/tryon/sessions/{sessionId}/adjust
Chỉnh garment params và regenerate preview.

## 5. Recommendation APIs

### POST /api/v1/recommendations/outfits
Input:
- occasion
- weather
- style
- optional wardrobe filters

### GET /api/v1/recommendations/outfits/{recommendationId}
Lấy kết quả recommendation.

## 6. Internal / Integration APIs

### POST /internal/body/from-measurements
### POST /internal/clothes/analyze
### POST /internal/tryon/render
### POST /internal/recommendations/rank

## 7. Status model

Các resource xử lý nặng nên có status:
- PENDING
- PROCESSING
- SUCCESS
- FAILED

## 8. Demo-friendly design
Trong 2 ngày demo:
- endpoint vẫn giữ nguyên
- implementation có thể fake data
- sau này thay service thật mà không đổi API contract
