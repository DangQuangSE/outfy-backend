# Database Schema

## 1. Nguyên tắc
Schema nên đủ cho demo nhưng mở rộng được.

## 2. Bảng chính

### Users
- id
- email
- password_hash
- display_name
- created_at

### BodyProfiles
- id
- user_id
- gender
- height_cm
- weight_kg
- chest_cm
- waist_cm
- hip_cm
- shoulder_cm
- inseam_cm
- created_at
- updated_at

### BodyGenerationResults
- id
- body_profile_id
- body_type
- avatar_preset_code
- shape_params_json
- preview_url
- confidence
- created_at

### ClothingItems
- id
- user_id
- image_url
- source_type
- original_name
- created_at

### ClothingAnalysisResults
- id
- clothing_item_id
- garment_category
- attributes_json
- garment_parameters_json
- template_code
- preview_url
- created_at

### TryOnSessions
- id
- user_id
- body_profile_id
- clothing_item_id
- status
- requested_size
- created_at
- updated_at

### TryOnResults
- id
- tryon_session_id
- preview_url
- fit_score
- result_note
- applied_params_json
- created_at

### OutfitRecommendations
- id
- user_id
- occasion
- weather
- preferred_style
- result_json
- created_at

## 3. Quan hệ cơ bản
- User 1 - n BodyProfiles
- User 1 - n ClothingItems
- BodyProfile 1 - n BodyGenerationResults
- ClothingItem 1 - n ClothingAnalysisResults
- TryOnSession 1 - 1..n TryOnResults
- User 1 - n OutfitRecommendations

## 4. Gợi ý cho demo
Có thể dùng:
- PostgreSQL
- MySQL
- thậm chí H2 cho local demo

Nhưng nếu muốn dùng tiếp:
- PostgreSQL hoặc MySQL hợp lý hơn

## 5. Lưu JSON
Các trường như:
- shape_params_json
- attributes_json
- garment_parameters_json
- result_json
nên để JSON/Text để linh hoạt khi demo và nâng cấp.
