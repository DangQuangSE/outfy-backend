# AI Outfit Recommendation

## 1. Mục tiêu

Gợi ý quần áo cho user dựa trên:
- phong cách
- thời tiết
- bối cảnh / dịp sử dụng
- tủ đồ hiện có
- body type cơ bản

## 2. Dữ liệu đầu vào

### 2.1 User context
- gender
- bodyType
- preferredStyle
- color preference
- location
- current weather
- saved wardrobe items

### 2.2 Occasion / context
Ví dụ:
- đi học
- đi làm
- đi chơi
- thể thao
- tiệc
- du lịch

### 2.3 Weather context
Ví dụ:
- nóng
- mát
- lạnh
- mưa
- nắng mạnh

## 3. Pipeline tổng thể

```text
User context + wardrobe + weather + occasion
    ↓
Normalize inputs
    ↓
Filter wardrobe candidates
    ↓
Style matching
    ↓
Weather suitability scoring
    ↓
Occasion suitability scoring
    ↓
Body compatibility scoring
    ↓
Rank outfits
    ↓
Generate explanation
```

## 4. Các bước chi tiết

### Step 1 - Gather Inputs
Lấy:
- user profile
- body profile
- wardrobe items
- weather data
- occasion
- style request

### Step 2 - Normalize Features
Chuẩn hóa:
- color tags
- category tags
- formality level
- warmth level
- sportiness level

### Step 3 - Candidate Filtering
Loại bỏ item không phù hợp:
- không đúng thời tiết
- không đúng occasion
- không đúng size / body profile nếu có

### Step 4 - Scoring
Mỗi outfit được chấm theo:
- styleScore
- weatherScore
- occasionScore
- bodyFitScore
- colorHarmonyScore

### Step 5 - Ranking
Chọn top N outfit combinations.

### Step 6 - Explanation Generation
Có thể gọi LLM để sinh mô tả:
- vì sao bộ đồ phù hợp
- phù hợp thời tiết nào
- dịp nào phù hợp
- gợi ý phụ kiện

## 5. Response ví dụ

```json
{
  "occasion": "Casual",
  "weather": "Warm",
  "recommendations": [
    {
      "outfitId": "outfit_001",
      "items": ["black_hoodie_01", "blue_jeans_02", "white_sneaker_01"],
      "scores": {
        "styleScore": 0.90,
        "weatherScore": 0.84,
        "occasionScore": 0.88
      },
      "explanation": "Bộ này phù hợp cho đi chơi trời mát, phong cách trẻ trung và dễ mặc."
    }
  ]
}
```

## 6. Gợi ý implement nhanh cho demo

### Cách 1 - Rule-based
- weather = nóng -> ưu tiên áo mỏng, tay ngắn
- occasion = work -> ưu tiên shirt, trouser
- style = sporty -> ưu tiên hoodie, jogger, sneaker

### Cách 2 - Rule-based + LLM explanation
Rule engine chọn outfit.
LLM chỉ viết phần giải thích.

### Cách 3 - Full AI later
- embedding wardrobe items
- recommendation model
- personalized ranking
- trend-aware system

## 7. Liên hệ với Try-On
Sau khi AI gợi ý outfit:
- user chọn outfit
- hệ thống tạo try-on request
- hiển thị preview

## 8. Kết luận

Đây là một chức năng rất hợp để gọi API AI model cho nhanh.
Demo có thể dùng rule-based + AI text explanation, sau này nâng cấp dần.
