# AI / 3D Body Pipeline

## 1. Mục tiêu

Sinh avatar body từ dữ liệu đầu vào của user theo cách đủ nhanh để demo, nhưng vẫn có khả năng nâng cấp thành pipeline thật về sau.

## 2. Các mức triển khai

### Mức A - Measurements to Avatar Preset
Input:
- gender
- height
- weight
- chest
- waist
- hip
- shoulder
- inseam

Output:
- bodyType
- shapeParams
- avatarPresetCode
- previewUrl

Đây là hướng phù hợp nhất cho demo 2 ngày.

### Mức B - Measurements to Parametric Body
Input như trên.

Output:
- shape parameters chi tiết hơn
- body mesh metadata
- preview

### Mức C - Image to Body
Input:
- 1 hoặc nhiều ảnh người dùng

Output:
- estimated body shape
- avatar mesh / preset mapping

Mức này nên để phase sau.

## 3. Pipeline đề xuất cho demo

```text
User measurements
    ↓
Validation
    ↓
Rule-based / lightweight AI inference
    ↓
BodyType classification
    ↓
Shape parameter generation
    ↓
Avatar preset mapping
    ↓
Preview rendering
```

## 4. Step chi tiết

### Step 1 - Receive Input
Backend nhận:
- gender
- heightCm
- weightKg
- chestCm
- waistCm
- hipCm
- shoulderCm
- inseamCm

### Step 2 - Validate Input
Kiểm tra:
- chiều cao hợp lệ
- cân nặng hợp lệ
- số đo không âm
- các trường bắt buộc tồn tại

### Step 3 - Derived Metrics
Tính:
- BMI
- waist-to-hip ratio
- chest-to-waist relation
- shoulder ratio

### Step 4 - Body Type Inference
Ví dụ:
- Slim
- Regular
- Curvy
- Broad
- Plus

### Step 5 - Shape Parameter Mapping
Ví dụ:
```json
{
  "heightScale": 1.02,
  "shoulderScale": 0.98,
  "chestScale": 1.00,
  "waistScale": 0.95,
  "hipScale": 1.04,
  "legScale": 1.01
}
```

### Step 6 - Avatar Preset Mapping
Ví dụ:
- female_regular_01
- female_curvy_01
- male_slim_01
- male_broad_01

### Step 7 - Preview Rendering
Demo:
- trả ảnh mock theo preset

Production:
- gửi metadata sang Unity render service để tạo preview thật

## 5. Rule-based demo logic gợi ý

### Nữ
- BMI < 18.5 -> Slim
- 18.5 <= BMI < 24 -> Regular hoặc Curvy nếu hip-waist lớn
- 24 <= BMI < 28 -> Curvy
- BMI >= 28 -> Plus

### Nam
- BMI < 18.5 -> Slim
- 18.5 <= BMI < 25 -> Regular hoặc Broad nếu vai lớn
- 25 <= BMI < 30 -> Broad
- BMI >= 30 -> Plus

## 6. Input / Output contract

### Request
```json
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
  "shapeParams": {
    "heightScale": 1.02,
    "shoulderScale": 1.00,
    "waistScale": 0.97,
    "hipScale": 1.02
  },
  "previewUrl": "/mock/avatar/female_regular_01.png",
  "confidence": 0.82
}
```

## 7. Nâng cấp về sau

Sau demo có thể thay body inference bằng:
- regression model
- body measurement estimation model
- image-to-SMPL style pipeline
- external avatar service

Nhưng backend contract có thể giữ nguyên.

## 8. Kết luận

Demo nên đi theo:
Measurements -> BodyType -> ShapeParams -> AvatarPreset -> Preview

Đây là pipeline dễ làm, dễ demo, và tái sử dụng tốt.
