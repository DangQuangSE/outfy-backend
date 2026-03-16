# Try-On Pipeline Test Guide

## Tổng Quan

Hướng dẫn cách test Try-On API để lấy đúng model phù hợp với các file mock đã có.

---

## File Try-On Có Sẵn

| File                                                 | Body Type      | Cloth                   |
| ---------------------------------------------------- | -------------- | ----------------------- |
| `body_broad_male_cloth_hoodie_pants.glb`             | Broad Male     | Hoodie + Pants          |
| `body_broad_male_cloth_jacket_pants.glb`             | Broad Male     | Jacket + Pants          |
| `body_regular_female_cloth_female_tshirt_shorts.glb` | Regular Female | Female T-Shirt + Shorts |
| `body_regular_male_cloth_tshirt_pants.glb`           | Regular Male   | T-Shirt + Pants         |
| `body_slim_female_cloth_crop_top_short_skirt.glb`    | Slim Female    | Crop Top + Short Skirt  |
| `body_slim_female_cloth_dress.glb`                   | Slim Female    | Dress                   |

---

## Mapping Để Sinh URL Đúng

### Các Body Types Hỗ Trợ

| Avatar ID                      | Body Type      | Mapping                 |
| ------------------------------ | -------------- | ----------------------- |
| `slim_male`                    | Slim Male      | → `body_slim_male`      |
| `slim_female`                  | Slim Female    | → `body_slim_female`    |
| `regular_male`                 | Regular Male   | → `body_regular_male`   |
| `regular_female`               | Regular Female | → `body_regular_female` |
| `athletic_male` / `broad_male` | Broad Male     | → `body_broad_male`     |
| `curvy_female`                 | Curvy Female   | → `body_curvy_female`   |

### Các Cloth Categories Hỗ Trợ

| Category                | →   | File Suffix            |
| ----------------------- | --- | ---------------------- |
| `HOODIE`                | →   | `hoodie_pants`         |
| `JACKET`                | →   | `jacket_pants`         |
| `T-SHIRT` / `TSHIRT`    | →   | `tshirt_pants`         |
| `FEMALE_TSHIRT`         | →   | `female_tshirt_shorts` |
| `PANTS`                 | →   | `tshirt_pants`         |
| `SHORTS`                | →   | `tshirt_pants`         |
| `DRESS`                 | →   | `dress`                |
| `CROP_TOP`              | →   | `crop_top_short_skirt` |
| `SKIRT` / `SHORT_SKIRT` | →   | `short_skirt`          |

---

## Các Cặp Body + Cloth Để Test (Chính Xác)

### ✅ Các Cặp Có Sẵn File (Hoạt Động)

| #   | Avatar ID        | Category        | clothingItemId | URL Trả Về                                                          | File Có Sẵn |
| --- | ---------------- | --------------- | -------------- | ------------------------------------------------------------------- | ----------- |
| 1   | `slim_female`    | `DRESS`         | 5              | `/models/try-on/body_slim_female_cloth_dress.glb`                   | ✅          |
| 2   | `slim_female`    | `CROP_TOP`      | 9              | `/models/try-on/body_slim_female_cloth_crop_top_short_skirt.glb`    | ✅          |
| 3   | `regular_male`   | `T-SHIRT`       | 2              | `/models/try-on/body_regular_male_cloth_tshirt_pants.glb`           | ✅          |
| 4   | `regular_female` | `FEMALE_TSHIRT` | -              | `/models/try-on/body_regular_female_cloth_female_tshirt_shorts.glb` | ✅          |
| 5   | `broad_male`     | `HOODIE`        | 1              | `/models/try-on/body_broad_male_cloth_hoodie_pants.glb`             | ✅          |
| 6   | `broad_male`     | `JACKET`        | 3              | `/models/try-on/body_broad_male_cloth_jacket_pants.glb`             | ✅          |

### ❌ Các Cặp Chưa Có File (Sẽ Trả Về URL Nhưng Load Lỗi)

| #   | Avatar ID        | Category  | clothingItemId | URL Trả Về                                             |
| --- | ---------------- | --------- | -------------- | ------------------------------------------------------ |
| 1   | `slim_male`      | `HOODIE`  | 1              | `/models/try-on/body_slim_male_cloth_hoodie_pants.glb` |
| 2   | `slim_male`      | `T-SHIRT` | 2              | `/models/try-on/body_slim_male_cloth_tshirt_pants.glb` |
| 3   | `curvy_female`   | `DRESS`   | 5              | `/models/try-on/body_curvy_female_cloth_dress.glb`     |
| 4   | `regular_female` | `DRESS`   | 5              | `/models/try-on/body_regular_female_cloth_dress.glb`   |

---

## API Endpoint

```http
POST /api/v1/tryon/generate
Authorization: Bearer <token>
Content-Type: application/json

{
  "avatarId": "slim_female",
  "clothingItemId": 1,
  "size": "M",
  "fitType": "regular"
}
```

### Response

```json
{
  "success": true,
  "data": {
    "previewUrl": "/models/try-on/body_slim_female_cloth_dress.glb",
    "fitScore": 0.87,
    "note": "Good fit! The dress fits well on slim female body type.",
    "appliedParams": {
      "size": "M",
      "fitType": "regular",
      "garmentCategory": "DRESS"
    }
  }
}
```

---

## Nếu Muốn Thêm File Try-On Mới

Nếu cần thêm các cặp body + cloth khác, cần tạo file theo naming convention:

```
/models/try-on/body_{bodyType}_cloth_{category1}_{category2}.glb
```

**Ví dụ:**

- `body_slim_male_cloth_tshirt_pants.glb`
- `body_curvy_female_cloth_dress.glb`
- `body_regular_female_cloth_skirt.glb`

---

## Mapping Cloth Items (clothingItemId → Category)

Trong MockTryOnGateway có mapping:

| clothingItemId | Category    | File Suffix            |
| -------------- | ----------- | ---------------------- |
| 1              | HOODIE      | `hoodie_pants`         |
| 2              | T-SHIRT     | `tshirt_pants`         |
| 3              | JACKET      | `jacket_pants`         |
| 4              | PANTS       | `tshirt_pants`         |
| 5              | DRESS       | `dress`                |
| 6              | SHIRT       | `tshirt_pants`         |
| 7              | SKIRT       | `short_skirt`          |
| 8              | SHORTS      | `tshirt_pants`         |
| 9              | CROP_TOP    | `crop_top_short_skirt` |
| 10             | SHORT_SKIRT | `short_skirt`          |

---

## Checklist Test

- [ ] Test với `slim_female` + `DRESS` → ✅ File có sẵn
- [ ] Test với `slim_female` + `CROP_TOP` → ✅ File có sẵn
- [ ] Test với `regular_male` + `T-SHIRT` → ✅ File có sẵn
- [ ] Test với `regular_female` + `FEMALE_TSHIRT` → ✅ File có sẵn
- [ ] Test với `broad_male` + `HOODIE` → ✅ File có sẵn
- [ ] Test với `broad_male` + `JACKET` → ✅ File có sẵn
