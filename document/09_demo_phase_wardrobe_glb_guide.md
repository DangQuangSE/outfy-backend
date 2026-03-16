# Demo Phase Implementation Guide - Wardrobe + GLB Mock Try-On

## Mục tiêu

Tài liệu này chỉ tập trung vào **phase demo** để AI support có thể implement lại nhanh theo đúng yêu cầu hiện tại.

Phạm vi của phase demo:

- Giữ lại cloth analyze API hiện có
- Thêm flow **lưu quần áo vào wardrobe**
- Tận dụng **file `.glb` thô đã chuẩn bị sẵn** để fake dữ liệu 3D
- Chưa làm AI thật cho mesh generation
- Chưa làm cloth simulation thật
- Chưa làm pipeline 3D production hoàn chỉnh

---

# 1. Bối cảnh hiện tại

Hiện backend đã implement xong phần **analyze-direct** cho quần áo theo hướng rule-based từ `fileName`, trả về:

- `garmentCategory`
- `templateCode`
- `attributes`
- `garmentParameters`
- `previewUrl`
- `confidence`

và đang dùng local static files cho GLB mock. Đây là nền tảng đúng để đi tiếp sang flow wardrobe trong demo.

---

# 2. Mục tiêu phase tiếp theo

Cần bổ sung chức năng:

1. Tạo clothing item thuộc về user
2. Analyze clothing item theo `clothingId`
3. Cho user confirm / chỉnh metadata
4. Lưu item vào wardrobe
5. Trả danh sách wardrobe
6. Gắn `previewUrl` / `modelUrl` là file `.glb` thô đã có
7. Chuẩn bị dữ liệu để FE demo chọn item và mở viewer 3D xoay 360 độ

---

# 3. Phạm vi demo cần làm

## Bắt buộc
- Lưu clothing item vào DB
- Có status lifecycle cho clothing item
- Có API list wardrobe
- Có API confirm item
- Mỗi item có `previewUrl` hoặc `modelUrl` trỏ đến file `.glb`

## Cho phép fake
- Mesh generation
- Cloth fitting thật
- Cloth simulation thật
- Auto extract parameter quá chính xác
- Full AI garment reconstruction

## Không làm trong phase demo
- Image-to-mesh thật
- Unity runtime fitting thật
- Physics thật
- Recommendation model thật

---

# 4. Kiến trúc demo nên giữ

```text
Client
  ↓
Spring Boot Backend
  ↓
ClothingService
  ↓
MockClothingAnalysisGateway
  ↓
Local GLB files + Database
```

Trong phase demo:

- GLB là asset giả đã chuẩn bị sẵn
- `templateCode` hoặc `garmentCategory` sẽ map sang `modelUrl`
- FE chỉ cần load model `.glb` để xem 3D

---

# 5. Mô hình dữ liệu cần dùng

## 5.1 ClothingItem
Dùng để đại diện quần áo của user.

### Fields đề xuất
- `id`
- `userId`
- `name`
- `imageUrl`
- `fileName`
- `sourceType`
- `status`
- `garmentCategory`
- `templateCode`
- `modelUrl`
- `previewUrl`
- `color`
- `fitType`
- `createdAt`
- `updatedAt`

## 5.2 ClothingAnalysisResultEntity
Lưu kết quả phân tích.

### Fields đề xuất
- `id`
- `clothingItemId`
- `garmentCategory`
- `templateCode`
- `attributesJson`
- `garmentParametersJson`
- `previewUrl`
- `modelUrl`
- `confidence`
- `createdAt`

---

# 6. Status lifecycle cho demo

Tạo enum `ClothingItemStatus`:

- `CREATED`
- `ANALYZING`
- `ANALYZED`
- `CONFIRMED`
- `FAILED`

Ý nghĩa:
- `CREATED`: mới tạo item
- `ANALYZING`: đang phân tích
- `ANALYZED`: đã có kết quả từ gateway
- `CONFIRMED`: user đã xác nhận lưu vào wardrobe
- `FAILED`: phân tích thất bại

---

# 7. Flow demo chuẩn

## Flow A - Create + Analyze + Save to Wardrobe

```text
User chọn ảnh / nhập link
  ↓
POST /api/v1/clothes
  ↓
Backend tạo ClothingItem status=CREATED
  ↓
POST /api/v1/clothes/{id}/analyze
  ↓
Backend gọi MockClothingAnalysisGateway
  ↓
Gateway trả garmentCategory + templateCode + previewUrl + confidence
  ↓
Backend lưu ClothingAnalysisResultEntity
  ↓
Backend update ClothingItem status=ANALYZED
  ↓
FE hiển thị màn confirm
  ↓
User chỉnh lại name / color / fit nếu cần
  ↓
POST /api/v1/clothes/{id}/confirm
  ↓
Backend update ClothingItem status=CONFIRMED
  ↓
Item xuất hiện trong My Wardrobe
```

---

# 8. API cần implement cho demo

## 8.1 Tạo clothing item
### POST `/api/v1/clothes`

### Request
```json
{
  "userId": "user_001",
  "name": "My Black Hoodie",
  "imageUrl": "https://example.com/my_hoodie.jpg",
  "fileName": "my_hoodie.jpg",
  "sourceType": "UPLOAD"
}
```

### Response
```json
{
  "success": true,
  "message": "Clothing item created successfully",
  "data": {
    "id": "cloth_001",
    "status": "CREATED"
  }
}
```

---

## 8.2 Analyze clothing item theo id
### POST `/api/v1/clothes/{clothingId}/analyze`

### Behavior
- lấy `ClothingItem` từ DB
- set status = `ANALYZING`
- gọi `MockClothingAnalysisGateway`
- lưu `ClothingAnalysisResultEntity`
- map `modelUrl` từ local `.glb`
- update `ClothingItem`
- set status = `ANALYZED`

### Response
```json
{
  "success": true,
  "message": "Clothing analyzed successfully",
  "data": {
    "clothingItemId": "cloth_001",
    "garmentCategory": "HOODIE",
    "templateCode": "hoodie_template",
    "previewUrl": "/models/cloth/hoodie_template.glb",
    "modelUrl": "/models/cloth/hoodie_template.glb",
    "confidence": 0.9,
    "status": "ANALYZED"
  }
}
```

---

## 8.3 Confirm item để vào wardrobe
### POST `/api/v1/clothes/{clothingId}/confirm`

### Request
```json
{
  "userId": "user_001",
  "name": "Black Hoodie",
  "color": "Black",
  "fitType": "LOOSE"
}
```

### Behavior
- kiểm tra item tồn tại
- kiểm tra item đã ANALYZED chưa
- update metadata cuối cùng
- set status = `CONFIRMED`

### Response
```json
{
  "success": true,
  "message": "Clothing item confirmed and added to wardrobe",
  "data": {
    "id": "cloth_001",
    "status": "CONFIRMED"
  }
}
```

---

## 8.4 Lấy danh sách wardrobe
### GET `/api/v1/wardrobe/items?userId=user_001`

### Response
```json
{
  "success": true,
  "message": "Wardrobe retrieved successfully",
  "data": [
    {
      "id": "cloth_001",
      "name": "Black Hoodie",
      "imageUrl": "https://example.com/my_hoodie.jpg",
      "garmentCategory": "HOODIE",
      "color": "Black",
      "fitType": "LOOSE",
      "previewUrl": "/models/cloth/hoodie_template.glb",
      "modelUrl": "/models/cloth/hoodie_template.glb",
      "status": "CONFIRMED"
    }
  ]
}
```

---

## 8.5 Lấy chi tiết wardrobe item
### GET `/api/v1/wardrobe/items/{id}`

Dùng để FE mở trang chi tiết và load model 3D.

---

# 9. Mapping GLB fake data

## Nguyên tắc
Không generate model mới.
Chỉ map category hoặc template sang file `.glb` thô có sẵn.

## Ví dụ mapping
```java
private static final Map<GarmentCategory, String> MODEL_URL_MAP = Map.of(
    GarmentCategory.TSHIRT, "/models/cloth/tshirt_template.glb",
    GarmentCategory.HOODIE, "/models/cloth/hoodie_template.glb",
    GarmentCategory.SHIRT, "/models/cloth/shirt_template.glb",
    GarmentCategory.PANTS, "/models/cloth/pants_template.glb",
    GarmentCategory.SKIRT, "/models/cloth/skirt_template.glb",
    GarmentCategory.DRESS, "/models/cloth/dress_template.glb",
    GarmentCategory.JACKET, "/models/cloth/jacket_template.glb"
);
```

## Rule demo
- `previewUrl` có thể trùng `modelUrl`
- hoặc `previewUrl` là ảnh PNG, `modelUrl` là GLB
- nếu chưa có PNG, cho FE dùng thẳng `.glb`

---

# 10. Cách FE dùng dữ liệu này trong demo

Sau khi item được `CONFIRMED`, FE:

1. gọi API list wardrobe
2. hiển thị item dưới dạng card
3. khi user bấm vào item:
   - lấy `modelUrl`
   - load `.glb` bằng viewer 3D
4. cho user xoay 360 độ

Như vậy:
- backend không cần render 3D
- backend chỉ cần trả đúng `modelUrl`

---

# 11. Module cần sửa trong BE

## 11.1 Enums
Thêm:
- `ClothingItemStatus`
- `SourceType` nếu chưa có

## 11.2 DTOs mới
### Request
- `CreateClothingItemRequest`
- `ConfirmClothingItemRequest`

### Response
- `WardrobeItemResponse`
- `ClothingAnalyzeByIdResponse`

## 11.3 Repository
### ClothingItemRepository
Cần thêm:
- `findByUserIdAndStatus(String userId, ClothingItemStatus status)`
- `findByIdAndUserId(String id, String userId)`

### ClothingAnalysisResultRepository
Cần thêm:
- `findTopByClothingItemIdOrderByCreatedAtDesc(String clothingItemId)`

## 11.4 Service methods
Trong `ClothingService` thêm:
- `createClothingItem(...)`
- `analyzeClothingItemById(...)`
- `confirmClothingItem(...)`
- `getWardrobeItems(userId)`
- `getWardrobeItemDetail(id, userId)`

---

# 12. Pseudo code service

## 12.1 Create clothing item
```java
public ClothingItemResponse createClothingItem(CreateClothingItemRequest request) {
    ClothingItem item = new ClothingItem();
    item.setUserId(request.getUserId());
    item.setName(request.getName());
    item.setImageUrl(request.getImageUrl());
    item.setFileName(request.getFileName());
    item.setSourceType(request.getSourceType());
    item.setStatus(ClothingItemStatus.CREATED);
    save(item);
    return mapToResponse(item);
}
```

## 12.2 Analyze clothing item by id
```java
public ClothingAnalysisResult analyzeClothingItemById(String clothingItemId) {
    ClothingItem item = clothingItemRepository.findById(clothingItemId)
        .orElseThrow(...);

    item.setStatus(ClothingItemStatus.ANALYZING);
    clothingItemRepository.save(item);

    AnalyzeClothingRequest request = new AnalyzeClothingRequest();
    request.setImageUrl(item.getImageUrl());
    request.setFileName(item.getFileName());

    ClothingAnalysisResult result = clothingAnalysisGateway.analyzeFromImage(request);

    item.setGarmentCategory(result.getGarmentCategory());
    item.setTemplateCode(result.getTemplateCode());
    item.setPreviewUrl(result.getPreviewUrl());
    item.setModelUrl(result.getPreviewUrl());
    item.setStatus(ClothingItemStatus.ANALYZED);
    clothingItemRepository.save(item);

    saveAnalysisResult(item, result);

    return result;
}
```

## 12.3 Confirm clothing item
```java
public WardrobeItemResponse confirmClothingItem(String clothingItemId, ConfirmClothingItemRequest request) {
    ClothingItem item = clothingItemRepository.findById(clothingItemId)
        .orElseThrow(...);

    if (item.getStatus() != ClothingItemStatus.ANALYZED) {
        throw new IllegalStateException("Item must be analyzed before confirmation");
    }

    item.setName(request.getName());
    item.setColor(request.getColor());
    item.setFitType(request.getFitType());
    item.setStatus(ClothingItemStatus.CONFIRMED);

    clothingItemRepository.save(item);

    return mapToWardrobeResponse(item);
}
```

---

# 13. Phân chia timeline cho AI support

## Phase 1 - Persistence
- update entity
- update enum
- update repository
- migration/schema

## Phase 2 - Create item API
- request DTO
- controller
- service
- test

## Phase 3 - Analyze by id
- reuse analyze-direct logic
- lưu result vào DB
- update clothing item status
- test

## Phase 4 - Confirm API
- request DTO
- service confirm
- update status
- test

## Phase 5 - Wardrobe APIs
- list wardrobe
- get detail
- test FE contract

---

# 14. Yêu cầu coding cho AI support

AI support phải tuân thủ các rule sau:

1. Không xóa endpoint `analyze-direct`
   - giữ lại để test nhanh
   - nhưng không dùng nó làm flow chính cho wardrobe

2. Không hard-code logic demo trong controller
   - controller phải mỏng
   - business nằm ở service
   - mapping GLB nằm ở service/gateway layer

3. Tái sử dụng tối đa structure hiện tại
   - giữ enum
   - giữ gateway
   - giữ DTO pattern
   - giữ thin controller

4. Phase demo chỉ cần local static files
   - không tích hợp cloud storage
   - không tích hợp Unity thật

5. Tất cả response phải sẵn sàng cho FE demo web
   - phải có `modelUrl`
   - phải có `status`
   - phải có `id`

---

# 15. Định nghĩa done cho phase demo

Phase demo được xem là hoàn thành khi:

- tạo được clothing item
- analyze theo `clothingId` thành công
- item được confirm vào wardrobe
- list wardrobe trả về item đúng theo `userId`
- mỗi item có `modelUrl` là file `.glb`
- FE có thể dùng `modelUrl` để hiển thị 3D viewer

---

# 16. Kết luận

Trong phase demo, mục tiêu không phải là build AI/3D thật hoàn chỉnh.

Mục tiêu đúng là:

- hoàn thiện lifecycle của clothing item
- lưu được item vào wardrobe
- gắn được file `.glb` giả
- cho FE dùng để hiển thị 3D và xoay 360 độ

Đây là hướng nhanh nhất, đúng kiến trúc nhất, và ít phải đập đi làm lại nhất.
