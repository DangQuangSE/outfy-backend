# Try-On Pipeline

## 1. Mục tiêu

Cho phép user chọn avatar và quần áo để xem preview thử đồ.

## 2. Tách 2 lớp flow

### User Flow
- user mở wardrobe
- user chọn quần áo
- hệ thống hiển thị kết quả thử đồ
- user chỉnh sửa nếu cần

### Engine Pipeline
- load avatar
- load garment
- fit garment
- bind skeleton
- add colliders
- run cloth simulation
- render preview

## 3. Pipeline tổng thể

```text
Select avatar + clothing
    ↓
Create try-on request
    ↓
Load avatar asset
    ↓
Load garment asset
    ↓
Fit garment to body
    ↓
Apply skeleton binding
    ↓
Set collision / cloth settings
    ↓
Run simulation
    ↓
Render result
    ↓
Return preview
```

## 4. Step chi tiết

### Step 1 - Create Try-On Request
Backend tạo `TryOnSession`.

Input:
- userId
- avatarId
- clothingItemId
- optional size / adjustment params

### Step 2 - Load Avatar
Unity service load:
- avatar preset hoặc body asset
- skeleton data

### Step 3 - Load Garment
Unity service load:
- garment template / cloth asset
- material metadata

### Step 4 - Fit Garment
Áp garment lên body:
- scale
- offset
- body clearance
- anchor points

### Step 5 - Skeleton Binding
Gắn garment theo skeleton để có thể move cùng body.

### Step 6 - Cloth Setup
Thiết lập:
- cloth component
- stiffness
- damping
- drag
- collision layers

### Step 7 - Collider Setup
Tạo collider cho:
- chest
- arms
- hips
- legs

### Step 8 - Simulation
Chạy cloth simulation để giảm xuyên mesh và tạo cảm giác tự nhiên.

### Step 9 - Render Preview
Output:
- preview image
- optional video
- metadata

### Step 10 - Save Result
Lưu vào:
- try-on results
- preview path
- applied params
- status

## 5. Demo 2 ngày nên làm gì?

Nên fake:
- Unity fitting
- cloth simulation
- render preview thật

Có thể thay bằng:
- ảnh preview mock
- overlay 2D đơn giản
- preset result theo avatar + cloth category

## 6. Response ví dụ

```json
{
  "tryOnSessionId": "session_001",
  "status": "SUCCESS",
  "previewUrl": "/mock/tryon/result_001.png",
  "fitScore": 0.82,
  "note": "Garment fits regular body preset."
}
```

## 7. Luồng chỉnh sửa

User có thể chỉnh:
- size
- fit
- sleeve
- length

Sau đó:
- cập nhật params
- regenerate try-on result
- trả preview mới

## 8. Kết luận

Demo nên giữ try-on pipeline theo kiến trúc đúng, nhưng cho phép mock phần nặng.
Điều này giúp source backend còn dùng tiếp được về sau.
