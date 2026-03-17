# Tài liệu API - Clothing Module (Cập nhật)

## Model Kết Quả Trả Về

### 📦 ClothingAnalysisResult (Sau khi gọi API analyze)

| Field | Type | Mô tả |
|-------|------|-------|
| `clothingItemId` | Long | ID của clothing item |
| `garmentCategory` | String | Loại quần áo (TSHIRT, HOODIE, PANTS, etc.) |
| `templateCode` | String | Mã template (dùng để render 3D) |
| `attributes` | Map | Thuộc tính bổ sung (color, sleeveType, fitType...) |
| `garmentParameters` | Map | Parameters để render model 3D |
| `previewUrl` | String | URL ảnh preview |
| `modelUrl` | **String** | **URL đến file .glb (template 3D)** |
| `confidence` | Double | Độ tin cậy của kết quả AI (0.0 - 1.0) |

**Ví dụ response:**
```json
{
  "success": true,
  "message": "Clothing analyzed successfully",
  "data": {
    "clothingItemId": 1,
    "garmentCategory": "PANTS",
    "templateCode": "pants_template",
    "attributes": {
      "color": "Blue",
      "fitType": "Regular",
      "waistType": "Regular",
      "length": "Full"
    },
    "garmentParameters": {
      "waistWidth": 38.0,
      "inseamLength": 82.0,
      "legWidth": 22.0
    },
    "previewUrl": "/models/cloth/pants_template.glb",
    "modelUrl": "/models/cloth/pants_template.glb",
    "confidence": 0.85
  }
}
```

---

## Mapping GarmentCategory → Model URL

| GarmentCategory | File .glb |
|-----------------|-----------|
| TSHIRT | `/models/cloth/tshirt_template.glb` |
| HOODIE | `/models/cloth/hoodie_template.glb` |
| SHIRT | `/models/cloth/shirt_template.glb` |
| PANTS | `/models/cloth/pants_template.glb` |
| SHORTS | `/models/cloth/shorts_template.glb` |
| SKIRT | `/models/cloth/skirt_template.glb` |
| SHORT_SKIRT | `/models/cloth/short_skirt_template.glb` |
| DRESS | `/models/cloth/dress_template.glb` |
| JACKET | `/models/cloth/jacket_template.glb` |
| CLOTH_TOP | `/models/cloth/cloth_top_template.glb` |
| FEMALE_SHIRT | `/models/cloth/female_shirt_template.glb` |

---

## Cách FE sử dụng modelUrl

1. **Load model 3D**: FE dùng `modelUrl` để load file .glb vào Three.js/Drei
2. **Mapping với body**: Kết hợp với body model từ body profile để hiển thị try-on

```typescript
// Ví dụ sử dụng trong FE
const { modelUrl, garmentParameters } = analysisResult;

// Load model
const gltf = await loader.loadAsync(modelUrl);

// Apply parameters để customize
gltf.scene.scale.set(garmentParameters.chestWidth / 50, ...);
```

