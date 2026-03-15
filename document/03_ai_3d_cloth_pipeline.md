# AI / 3D Cloth Pipeline

## 1. Mục tiêu

Biến ảnh quần áo thành dữ liệu có thể dùng cho virtual try-on.

Lưu ý:
Trong demo 2 ngày, không nên cố tạo cloth mesh quá phức tạp từ ảnh.
Nên đi theo hướng:
image -> analysis -> garment parameters -> template mapping

## 2. Pipeline tổng thể

```text
Clothing image
    ↓
Image validation
    ↓
Background removal
    ↓
Garment classification
    ↓
Attribute extraction
    ↓
Garment parameter generation
    ↓
Template mapping
    ↓
Cloth metadata / cloth asset
```

## 3. Step chi tiết

### Step 1 - Input Image
Nguồn ảnh:
- upload từ device
- chụp trực tiếp
- link từ shop

### Step 2 - Image Validation
Kiểm tra:
- độ phân giải
- độ mờ
- ánh sáng
- mức độ che khuất
- nền quá rối hay không

Nếu fail:
- yêu cầu user upload lại

### Step 3 - Background Removal
Tách quần áo khỏi nền để dễ phân tích hơn.

Output:
- foreground cloth image
- transparent PNG hoặc mask

### Step 4 - Garment Classification
Xác định category:
- T-shirt
- Hoodie
- Shirt
- Pants
- Skirt
- Dress
- Jacket

### Step 5 - Attribute Extraction
Trích xuất:
- color
- sleeve length
- fit type
- collar
- hood
- zipper / button
- pattern
- fabric hint

### Step 6 - Garment Parameter Generation
Ví dụ:
- chestWidth
- bodyLength
- sleeveLength
- hemWidth
- fitType
- garmentCategory

### Step 7 - Template Selection
Ví dụ:
- hoodie -> HoodieTemplate
- t-shirt -> TshirtTemplate
- pants -> PantsTemplate

### Step 8 - Parametric Mapping
Map parameters vào template:
- scale width
- scale length
- sleeve adjustment
- loose / slim fitting factor

### Step 9 - Output
Trả:
- garmentCategory
- extractedAttributes
- garmentParameters
- templateCode
- preview metadata

## 4. Ví dụ response phân tích

```json
{
  "garmentCategory": "Hoodie",
  "templateCode": "hoodie_template_v1",
  "attributes": {
    "color": "Black",
    "sleeveType": "Long",
    "fitType": "Loose",
    "hasHood": true,
    "hasZipper": true
  },
  "garmentParameters": {
    "chestWidth": 52,
    "bodyLength": 70,
    "sleeveLength": 63
  }
}
```

## 5. Nên fake phần nào trong demo?

Nên fake:
- garment parameters nếu cần
- template mapping
- cloth preview image
- cloth mesh generation

Có thể làm thật:
- garment classification
- color extraction
- basic attribute extraction

## 6. Phương án phát triển tiếp

Sau demo, cloth pipeline có thể nâng cấp thành:
- image + manual measurements -> precise garment params
- image to cloth segmentation + landmarks
- parametric cloth generator
- Unity import mesh thật
- cloth physics configuration

## 7. Kết luận

Demo nên tập trung vào:
image -> category -> attributes -> parameters -> template

Không nên ép làm:
image -> full cloth mesh generation chính xác cao
