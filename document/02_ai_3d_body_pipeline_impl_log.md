# AI 3D Body Pipeline - Implementation Log

## Mục tiêu

Tạo API cho phép user nhập body measurements và nhận về:

- Body type (Slim, Regular, Curvy, Broad...)
- Avatar preset code
- Shape parameters
- Model 3D URL (GLB file local)
- Preview URL

## Tech Stack

- **Backend**: Spring Boot
- **3D Files**: Local static files (GLB)
- **Storage**: Không database (in-memory mock)

---

## 📋 Plan Implement

### Phase 1: Setup cơ bản

- [x] 1.1 Thêm Cloudinary dependency (tạm thời chưa dùng)
- [x] 1.2 Tạo module bodyprofile với cấu trúc chuẩn (đã có sẵn)

### Phase 2: DTOs & Enums

- [x] 2.1 Tạo Gender enum (MALE, FEMALE) - đã có trong request
- [x] 2.2 Tạo BodyType enum (SLIM, REGULAR, CURVY, BROAD, PLUS) - đã có trong logic
- [x] 2.3 Tạo GenerateAvatarRequest DTO
- [x] 2.4 Tạo GenerateAvatarResponse DTO (thêm modelUrl) - đã có BodyGenerationResult

### Phase 3: Gateway Layer

- [x] 3.1 Tạo BodyGenerationGateway interface - đã có sẵn, thêm method mới
- [x] 3.2 Tạo MockBodyGenerationGateway implementation - đã cập nhật với rule-based logic

### Phase 4: Service Layer

- [x] 4.1 Tạo BodyProfileService - đã có sẵn, thêm method mới

### Phase 5: Controller Layer

- [x] 5.1 Tạo BodyProfileController - đã có sẵn, thêm endpoint mới
- [x] 5.2 Map endpoint: POST /api/v1/body-profiles/generate-avatar

---

## 🔄 Flow xử lý

```
User Request
    ↓
Validation (DTO)
    ↓
BodyGenerationService
    ↓
MockBodyGenerationGateway (rule-based)
    ├── Tính BMI
    ├── Tính derived metrics (waist-to-hip, etc.)
    ├── Xác định body type
    ├── Map shape parameters
    ├── Map preset code
    └── Return modelUrl + previewUrl
    ↓
Response (DTO)
```

---

## 📦 Cấu trúc module

```
modules/bodyprofile/
├── controller/
│   └── BodyGenerationController.java
├── dto/
│   ├── request/
│   │   └── GenerateAvatarRequest.java
│   └── response/
│       └── GenerateAvatarResponse.java
├── service/
│   └── BodyGenerationService.java
├── gateway/
│   ├── BodyGenerationGateway.java
│   └── MockBodyGenerationGateway.java
└── enums/
    ├── Gender.java
    └── BodyType.java
```

---

## 📝 Triển khai chi tiết

### 1. Validation Rules (Rule-based)

**BMI Calculation:**

```
BMI = weightKg / (heightM * heightM)
```

**Body Type Classification:**

| Gender | BMI Range | Body Type       |
| ------ | --------- | --------------- |
| Female | < 18.5    | Slim            |
| Female | 18.5 - 24 | Regular         |
| Female | 24 - 28   | Curvy           |
| Female | >= 28     | Curvy (or Plus) |
| Male   | < 18.5    | Slim            |
| Male   | 18.5 - 25 | Regular         |
| Male   | 25 - 30   | Broad           |
| Male   | >= 30     | Broad (or Plus) |

### 2. Model URL Mapping

| Gender | Body Type | Model URL                  |
| ------ | --------- | -------------------------- |
| Male   | Slim      | /models/slim_male.glb      |
| Male   | Regular   | /models/regular_male.glb   |
| Male   | Broad     | /models/broad_male.glb     |
| Female | Slim      | /models/slim_female.glb    |
| Female | Regular   | /models/regular_female.glb |
| Female | Curvy     | /models/curvy_female.glb   |

### 3. Shape Parameters (ví dụ)

```json
{
  "heightScale": 1.02,
  "shoulderScale": 1.0,
  "waistScale": 0.97,
  "hipScale": 1.02
}
```

---

## 📌 API Contract

### Request

```json
POST /api/v1/body-profiles/generate-avatar
{
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

### Response

```json
{
  "bodyType": "Regular",
  "avatarPresetCode": "female_regular_01",
  "modelUrl": "/models/regular_female.glb",
  "previewUrl": "/models/regular_female.glb",
  "shapeParams": {
    "heightScale": 1.02,
    "shoulderScale": 1.0,
    "waistScale": 0.97,
    "hipScale": 1.02
  },
  "confidence": 0.82
}
```

---

## 🔗 Tích hợp với FE

Frontend gọi API và nhận `modelUrl`, sau đó hiển thị bằng `<model-viewer>`:

```html
<model-viewer
  src="http://localhost:8080/models/regular_female.glb"
  alt="3D Avatar"
  auto-rotate
  camera-controls
></model-viewer>
```

---

## 📅 Timeline

| Task                  | Thời gian ước tính |
| --------------------- | ------------------ |
| Phase 1: Setup        | 30 phút            |
| Phase 2: DTOs & Enums | 1 giờ              |
| Phase 3: Gateway      | 1.5 giờ            |
| Phase 4: Service      | 1 giờ              |
| Phase 5: Controller   | 1 giờ              |
| Test & Fix            | 2 giờ              |

**Tổng: ~7 giờ (trong 2 ngày)**

---

## 🔮 Mở rộng sau này

- [ ] Thay rule-based bằng ML model thật
- [ ] Upload lên Cloudinary thay vì local
- [ ] Spin images (ảnh 360°) thay thế GLB
- [ ] Lưu vào database để user có avatar riêng

---

## 🖥️ Frontend Implementation (2026-03-15)

### Files Updated:

| File                               | Description                                           |
| ---------------------------------- | ----------------------------------------------------- |
| `lib/types/avatar.ts`              | Added `GenerateAvatarRequest`, `BodyGenerationResult` |
| `lib/utils/validators.ts`          | Added `generateAvatarSchema` with Zod                 |
| `lib/api/avatar.ts`                | Added `generateAvatar()` API call                     |
| `app/avatar/measurements/page.tsx` | Save measurements to localStorage                     |
| `app/avatar/scan/page.tsx`         | Call API, save result to localStorage                 |
| `app/avatar/result/page.tsx`       | Display 3D model with `<model-viewer>`                |
| `package.json`                     | Added `@google/model-viewer` dependency               |

### New Flow:

```
measurements → scan (call API) → result (3D model with 360° rotation)
```

### Data Storage:

- Measurements: `localStorage.getItem('outfy_measurements')`
- Avatar Result: `localStorage.getItem('outfy_avatar_result')`

### 3D Model Display:

- Uses `@google/model-viewer` web component
- Supports auto-rotate, camera-controls
- Full URL: `${API_BASE_URL}${modelUrl}` (e.g., `http://localhost:8080/models/regular_female.glb`)
