# Try-On Pipeline API Documentation

## Overview

Try-On Pipeline cho phép người dùng thử quần áo trên avatar (body model) để xem sản phẩm có phù hợp hay không.

## Flow

```
1. User chọn avatar (body type) + clothing item từ wardrobe
2. Backend tạo TryOnSession
3. Backend gọi TryOnGateway để generate kết quả
4. Trả về try-on model URL + fit score + notes
```

## Models & Templates

### Body Models (GLB Files)

| Avatar ID        | File                              | Description                   |
| ---------------- | --------------------------------- | ----------------------------- |
| `slim_male`      | `/models/body/slim_male.glb`      | Slim male body type           |
| `slim_female`    | `/models/body/slim_female.glb`    | Slim female body type         |
| `regular_male`   | `/models/body/regular_male.glb`   | Regular male body type        |
| `regular_female` | `/models/body/regular_female.glb` | Regular female body type      |
| `athletic_male`  | `/models/body/broad_male.glb`     | Athletic/broad male body type |
| `curvy_female`   | `/models/body/curvy_female.glb`   | Curvy female body type        |

### Garment Templates (GLB Files)

| Category        | File                                       | Description             |
| --------------- | ------------------------------------------ | ----------------------- |
| `HOODIE`        | `/models/cloth/hoodie_template.glb`        | Hoodie template         |
| `T-SHIRT`       | `/models/cloth/tshirt_template.glb`        | T-Shirt template        |
| `FEMALE_TSHIRT` | `/models/cloth/female_tshirt_template.glb` | Female T-Shirt template |
| `SHIRT`         | `/models/cloth/shirt_template.glb`         | Shirt template          |
| `JACKET`        | `/models/cloth/jacket_template.glb`        | Jacket template         |
| `PANTS`         | `/models/cloth/pants_template.glb`         | Pants template          |
| `SHORTS`        | `/models/cloth/shorts_template.glb`        | Shorts template         |
| `DRESS`         | `/models/cloth/dress_template.glb`         | Dress template          |
| `SKIRT`         | `/models/cloth/skirt_template.glb`         | Skirt template          |
| `SHORT_SKIRT`   | `/models/cloth/short_skirt_template.glb`   | Short Skirt template    |
| `CROP_TOP`      | `/models/cloth/crop_top_template.glb`      | Crop Top template       |

### Try-On Model Files (User tạo)

**Path Pattern:** `/models/try-on/body_{bodyType}_cloth_{category}.glb`

**Ví dụ:**

- `/models/try-on/body_slim_male_cloth_hoodie.glb`
- `/models/try-on/body_regular_female_cloth_dress.glb`
- `/models/try-on/body_curvy_female_cloth_pants.glb`

## API Endpoints

### 1. Create Try-On Session (từ clothing item)

```http
POST /api/v1/tryons
Content-Type: application/json

{
    "userId": 1,
    "bodyProfileId": 1,
    "clothingItemId": 1
}
```

**Response:**

```json
{
  "success": true,
  "message": "Try-on session created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "bodyProfileId": 1,
    "clothingItemId": 1,
    "avatarId": "avatar_1",
    "status": "PENDING",
    "createdAt": "2026-03-16T12:00:00"
  }
}
```

---

### 2. Create Try-On Session (từ wardrobe item)

```http
POST /api/v1/tryons/from-wardrobe
Content-Type: application/json

{
    "userId": 1,
    "wardrobeItemId": 5,
    "avatarId": "slim_male",
    "size": "M",
    "fitType": "regular"
}
```

**Parameters:**

- `userId` (Long, required) - User ID
- `wardrobeItemId` (Long, required) - Wardrobe item ID
- `avatarId` (String, required) - Body type: `slim_male`, `regular_female`, `curvy_female`, etc.
- `size` (String, optional) - Size: `XS`, `S`, `M`, `L`, `XL`
- `fitType` (String, optional) - Fit type: `slim`, `regular`, `loose`, `oversize`

**Response:**

```json
{
  "success": true,
  "message": "Try-on session created from wardrobe successfully",
  "data": {
    "id": 2,
    "userId": 1,
    "wardrobeItemId": 5,
    "avatarId": "slim_male",
    "garmentCategory": "HOODIE",
    "garmentColor": "blue",
    "requestedSize": "M",
    "fitType": "regular",
    "status": "PENDING"
  }
}
```

---

### 3. Get Try-On Session by ID

```http
GET /api/v1/tryons/{id}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "bodyProfileId": 1,
    "clothingItemId": 1,
    "avatarId": "slim_male",
    "status": "COMPLETED",
    "createdAt": "2026-03-16T12:00:00"
  }
}
```

---

### 4. Get All Try-On Sessions for User

```http
GET /api/v1/tryons/user/{userId}
```

---

### 5. Get Favorite Try-On Results

```http
GET /api/v1/tryons/user/{userId}/favorites
```

---

### 6. Generate Try-On Result

```http
POST /api/v1/tryons/{id}/generate
```

**Response:**

```json
{
  "success": true,
  "message": "Try-on result generated successfully",
  "data": {
    "previewUrl": "/models/try-on/body_slim_male_cloth_hoodie.glb",
    "fitScore": 0.87,
    "note": "Good fit! The hoodie fits well on slim male body type.",
    "appliedParams": {
      "scale": 0.95,
      "offsetX": 0,
      "offsetY": 0,
      "size": "M",
      "fitType": "regular",
      "garmentModelUrl": "/models/cloth/hoodie_template.glb",
      "garmentCategory": "HOODIE",
      "garmentColor": "blue",
      "bodyModelUrl": "/models/body/slim_male.glb",
      "bodyType": "slim_male"
    }
  }
}
```

---

### 7. Get Try-On Result Detail

```http
GET /api/v1/tryons/{id}/result/detail
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "sessionId": 1,
    "previewUrl": "/models/try-on/body_slim_male_cloth_hoodie.glb",
    "fitScore": 0.87,
    "note": "Good fit! The hoodie fits well on slim male body type.",
    "avatarId": "slim_male",
    "avatarUrl": "/models/body/slim_male.glb",
    "garmentCategory": "HOODIE",
    "garmentColor": "blue",
    "isFavorite": false,
    "createdAt": "2026-03-16T12:00:00"
  }
}
```

---

### 8. Update Try-On Session

```http
PUT /api/v1/tryons/{id}
Content-Type: application/json

{
    "size": "L",
    "fitType": "loose",
    "sleeveLength": "long",
    "avatarId": "regular_male"
}
```

---

### 9. Regenerate Try-On Result

```http
POST /api/v1/tryons/{id}/regenerate
```

---

### 10. Toggle Favorite

```http
PATCH /api/v1/tryons/{id}/favorite
```

---

### 11. Delete Try-On Session

```http
DELETE /api/v1/tryons/{id}
```

---

## Fit Score Calculation (Mock)

| Avatar       | Fit Type | Fit Score Range |
| ------------ | -------- | --------------- |
| slim_male    | slim     | 0.80 - 0.95     |
| slim_male    | loose    | 0.70 - 0.85     |
| regular_male | regular  | 0.75 - 0.95     |
| curvy_female | loose    | 0.80 - 0.95     |
| curvy_female | slim     | 0.67 - 0.80     |

## Avatar ID Mapping

Khi upload ảnh body, sử dụng naming convention sau để map với template:

| Upload Image Name       | Avatar ID        | Body Template                     |
| ----------------------- | ---------------- | --------------------------------- |
| body_slim_male.jpg      | `slim_male`      | `/models/body/slim_male.glb`      |
| body_regular_male.jpg   | `regular_male`   | `/models/body/regular_male.glb`   |
| body_slim_female.jpg    | `slim_female`    | `/models/body/slim_female.glb`    |
| body_regular_female.jpg | `regular_female` | `/models/body/regular_female.glb` |
| body_broad_male.jpg     | `athletic_male`  | `/models/body/broad_male.glb`     |
| body_curvy_female.jpg   | `curvy_female`   | `/models/body/curvy_female.glb`   |

## Garment Category Mapping

Khi upload ảnh cloth, sử dụng mapping sau:

| Upload Image Name       | Category        | Garment Template                           |
| ----------------------- | --------------- | ------------------------------------------ |
| cloth_hoodie.jpg        | `HOODIE`        | `/models/cloth/hoodie_template.glb`        |
| cloth_tshirt.jpg        | `T-SHIRT`       | `/models/cloth/tshirt_template.glb`        |
| cloth_pants.jpg         | `PANTS`         | `/models/cloth/pants_template.glb`         |
| cloth_dress.jpg         | `DRESS`         | `/models/cloth/dress_template.glb`         |
| cloth_skirt.jpg         | `SKIRT`         | `/models/cloth/skirt_template.glb`         |
| cloth_jacket.jpg        | `JACKET`        | `/models/cloth/jacket_template.glb`        |
| cloth_shorts.jpg        | `SHORTS`        | `/models/cloth/shorts_template.glb`        |
| cloth_crop_top.jpg      | `CROP_TOP`      | `/models/cloth/crop_top_template.glb`      |
| cloth_short_skirt.jpg   | `SHORT_SKIRT`   | `/models/cloth/short_skirt_template.glb`   |
| cloth_shirt.jpg         | `SHIRT`         | `/models/cloth/shirt_template.glb`         |
| cloth_female_tshirt.jpg | `FEMALE_TSHIRT` | `/models/cloth/female_tshirt_template.glb` |

## Try-On Model Files - User cần tạo

**User lưu model vào:** `/src/main/resources/static/models/try-on/`

**Naming Convention:** `body_{bodyType}_cloth_{category}.glb`

**Danh sách file cần tạo:**

| File                                  | Body Type      | Garment |
| ------------------------------------- | -------------- | ------- |
| `body_slim_male_cloth_hoodie.glb`     | slim_male      | HOODIE  |
| `body_slim_male_cloth_tshirt.glb`     | slim_male      | T-SHIRT |
| `body_slim_male_cloth_pants.glb`      | slim_male      | PANTS   |
| `body_regular_male_cloth_hoodie.glb`  | regular_male   | HOODIE  |
| `body_regular_male_cloth_tshirt.glb`  | regular_male   | T-SHIRT |
| `body_regular_female_cloth_dress.glb` | regular_female | DRESS   |
| `body_curvy_female_cloth_dress.glb`   | curvy_female   | DRESS   |
| `body_curvy_female_cloth_pants.glb`   | curvy_female   | PANTS   |
| `body_athletic_male_cloth_jacket.glb` | athletic_male  | JACKET  |

## Error Responses

```json
{
  "success": false,
  "message": "TryOnSession not found with id: 999",
  "data": null
}
```

```json
{
  "success": false,
  "message": "Wardrobe item does not belong to user",
  "data": null
}
```
