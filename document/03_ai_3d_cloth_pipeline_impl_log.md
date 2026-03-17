# AI 3D Cloth Pipeline - Implementation Log

## Mục tiêu

Tạo API cho phép user upload ảnh quần áo và nhận về:
- Garment category (T-shirt, Hoodie, Pants, etc.)
- Template code
- Extracted attributes (color, sleeve type, fit type, etc.)
- Garment parameters (chestWidth, bodyLength, etc.)
- Preview URL từ local files
- Confidence score

## Tech Stack

- **Backend**: Spring Boot
- **3D Files**: Local static files (GLB)
- **Storage**: Không database (in-memory mock)

---

## 📋 Plan Implement

### Phase 1: Enums & Interface

- [x] 1.1 Tạo GarmentCategory enum (TSHIRT, HOODIE, SHIRT, PANTS, SKIRT, DRESS, JACKET)
- [x] 1.2 Tạo SleeveType enum (SHORT, LONG, NONE)
- [x] 1.3 Tạo FitType enum (SLIM, REGULAR, LOOSE)
- [x] 1.4 Tạo IClothingAnalysisService interface
- [x] 1.5 Cập nhật ClothingService implements interface

### Phase 2: Gateway Layer

- [x] 2.1 Cập nhật ClothingAnalysisGateway interface - thêm method analyzeFromImage
- [x] 2.2 Cập nhật MockClothingAnalysisGateway với rule-based logic và local file mapping

### Phase 3: DTOs

- [x] 3.1 Tạo AnalyzeClothingRequest DTO
- [x] 3.2 Cập nhật ClothingAnalysisResult - thêm confidence field

### Phase 4: Controller

- [x] 4.1 Cập nhật ClothingController - thêm endpoint analyze-direct
- [x] 4.2 Map endpoint: POST /api/v1/clothes/analyze-direct

---

## 🔄 Flow xử lý

```
User Request (imageUrl + fileName)
    ↓
Validation (DTO)
    ↓
ClothingAnalysisService
    ↓
MockClothingAnalysisGateway (rule-based)
    ├── Extract filename from URL
    ├── Classify garment category từ filename
    ├── Determine sleeve type
    ├── Determine fit type
    ├── Map template code
    ├── Get template URL từ local files
    ├── Get default attributes
    ├── Get default garment parameters
    └── Calculate confidence
    ↓
Response (DTO)
```

---

## 📦 Cấu trúc module

```
modules/clothing/
├── controller/
│   └── ClothingController.java
├── dto/
│   ├── request/
│   │   ├── CreateClothingRequest.java
│   │   └── AnalyzeClothingRequest.java    ← NEW
│   └── response/
│       ├── ClothingItemResponse.java
│       └── ClothingAnalysisResult.java    ← Updated with confidence
├── entity/
│   ├── ClothingItem.java
│   └── ClothingAnalysisResultEntity.java
├── interfaces/                             ← NEW
│   └── IClothingAnalysisService.java
├── enums/                                  ← NEW
│   ├── GarmentCategory.java
│   ├── SleeveType.java
│   └── FitType.java
├── mapper/
│   └── ClothingMapper.java
├── repository/
│   ├── ClothingItemRepository.java
│   └── ClothingAnalysisResultRepository.java
└── service/
    └── ClothingService.java               ← Updated implements interface
```

---

## 📝 Triển khai chi tiết

### 1. Garment Classification (Rule-based)

| Filename Pattern | Category | Template Code |
|-----------------|----------|---------------|
| hoodie, hood | HOODIE | hoodie_template |
| tshirt, t-shirt, tee | TSHIRT | tshirt_template |
| shirt, blouse | SHIRT | shirt_template |
| pants, jean, trouser, jeans | PANTS | pants_template |
| skirt | SKIRT | skirt_template |
| dress, gown | DRESS | dress_template |
| jacket, coat, blazer | JACKET | jacket_template |

### 2. Template URL Mapping (Local Files)

```java
private static final Map<GarmentCategory, String> TEMPLATE_URL_MAP = Map.of(
    GarmentCategory.TSHIRT, "/models/cloth/tshirt_template.glb",
    GarmentCategory.HOODIE, "/models/cloth/hoodie_template.glb",
    GarmentCategory.SHIRT, "/models/cloth/shirt_template.glb",
    GarmentCategory.PANTS, "/models/cloth/pants_template.glb",
    GarmentCategory.SKIRT, "/models/cloth/skirt_template.glb",
    GarmentCategory.DRESS, "/models/cloth/dress_template.glb",
    GarmentCategory.JACKET, "/models/cloth/jacket_template.glb"
);
```

### 3. Default Attributes by Category

| Category | Color | Sleeve | Fit | Special |
|----------|-------|--------|-----|---------|
| Hoodie | Black | Long | Loose | hasHood=true, hasZipper=true |
| T-shirt | White | Short | Regular | hasHood=false |
| Shirt | Blue | Short | Regular | hasCollar=true |
| Pants | Blue | N/A | Regular | waistType=Regular |
| Skirt | Black | N/A | Regular | length=Mini |
| Dress | Red | Long | Regular | length=Midi |
| Jacket | Brown | Long | Regular | hasZipper=true |

### 4. Confidence Calculation

```java
double baseConfidence = 0.7;
if (filename has clear category indicator) {
    baseConfidence += 0.2;
} else {
    baseConfidence += 0.05;
}
return Math.min(baseConfidence, 0.95);
```

---

## 📌 API Contract

### Request

```json
POST /api/v1/clothes/analyze-direct
{
  "imageUrl": "https://example.com/my_hoodie.jpg",
  "fileName": "my_hoodie.jpg"
}
```

### Response

```json
{
  "success": true,
  "message": "Clothing analyzed successfully",
  "data": {
    "garmentCategory": "HOODIE",
    "templateCode": "hoodie_template",
    "attributes": {
      "color": "Black",
      "sleeveType": "LONG",
      "fitType": "LOOSE",
      "hasHood": true,
      "hasZipper": true
    },
    "garmentParameters": {
      "chestWidth": 52.0,
      "bodyLength": 70.0,
      "sleeveLength": 63.0
    },
    "previewUrl": "/models/cloth/hoodie_template.glb",
    "confidence": 0.9
  }
}
```

---

## 📅 Timeline

| Task                  | Thời gian ước tính |
| --------------------- | ------------------ |
| Phase 1: Enums & Interface | 1 giờ           |
| Phase 2: Gateway Layer      | 1.5 giờ         |
| Phase 3: DTOs               | 30 phút         |
| Phase 4: Controller         | 30 phút         |
| Test & Fix                  | 1 giờ           |

**Tổng: ~4.5 giờ**

---

## 🔮 Mở rộng sau này

- [ ] Thay rule-based bằng ML model thật cho garment classification
- [ ] Thêm image validation (độ phân giải, độ mờ, ánh sáng)
- [ ] Thêm background removal
- [ ] Thêm color extraction thực từ ảnh
- [ ] Upload lên Cloudinary thay vì local
- [ ] Lưu vào database để user có clothing items riêng

---

## ✅ Kết luận

Cloth pipeline đã được triển khai tương tự như body pipeline:
- Sử dụng rule-based logic để classify garment từ filename
- Sử dụng local static files cho template URLs
- Cung cấp direct API không cần database cho demo
- Tuân thủ architecture guidelines (interface, DTO, thin controller)

