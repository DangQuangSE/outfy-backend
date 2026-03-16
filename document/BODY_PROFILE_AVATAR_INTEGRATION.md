# Yêu Cầu Tích Hợp Body Profile & Avatar Generation

## 1. Cấu Hình Môi Trường

### File `.env.local`

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

---

## 2. API Response Format

```json
{
  "success": true,
  "message": "Success message",
  "data": { ... }
}
```

---

## 3. API Endpoints

### Body Profile & Avatar Generation

| Method | Endpoint                         | Mô tả                         |
| ------ | -------------------------------- | ----------------------------- |
| POST   | `/body-profiles`                 | Tạo body profile (lưu vào DB) |
| GET    | `/body-profiles/{id}`            | Lấy body profile theo ID      |
| GET    | `/body-profiles/user/{userId}`   | Lấy danh sách body profiles   |
| POST   | `/body-profiles/generate-avatar` | Sinh avatar 3D từ số đo       |

**POST /body-profiles** (lưu body profile)

Request:

```json
{
  "gender": "MALE",
  "heightCm": 175.0,
  "weightKg": 70.0,
  "chestCm": 95.0,
  "waistCm": 80.0,
  "hipCm": 95.0,
  "shoulderCm": 45.0,
  "inseamCm": 80.0
}
```

Response:

```json
{
  "success": true,
  "message": "Body profile created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "gender": "MALE",
    "heightCm": 175.0,
    "weightKg": 70.0,
    "chestCm": 95.0,
    "waistCm": 80.0,
    "hipCm": 95.0,
    "shoulderCm": 45.0,
    "inseamCm": 80.0,
    "createdAt": "2026-03-16T12:00:00",
    "updatedAt": null
  }
}
```

**POST /body-profiles/generate-avatar** (sinh avatar - không lưu DB)

Request:

```json
{
  "gender": "MALE",
  "heightCm": 175.0,
  "weightKg": 70.0,
  "chestCm": 95.0,
  "waistCm": 80.0,
  "hipCm": 95.0,
  "shoulderCm": 45.0,
  "inseamCm": 80.0
}
```

Response:

```json
{
  "success": true,
  "message": "Avatar generated successfully",
  "data": {
    "bodyType": "Athletic",
    "avatarPresetCode": "MALE_ATHELETIC_001",
    "shapeParams": {
      "chest": 95.0,
      "waist": 80.0,
      "hip": 95.0,
      "shoulder": 45.0,
      "height": 175.0,
      "weight": 70.0
    },
    "previewUrl": "https://example.com/preview.png",
    "modelUrl": "https://example.com/model.glb",
    "confidence": 0.85
  }
}
```

---

## 4. TypeScript Types

**lib/types/bodyprofile.ts** (tạo mới)

```typescript
export interface BodyProfile {
  id: number;
  userId: number;
  gender: string;
  heightCm: number;
  weightKg: number;
  chestCm: number;
  waistCm: number;
  hipCm: number;
  shoulderCm: number;
  inseamCm: number;
  createdAt: string;
  updatedAt?: string;
}

export interface GenerateAvatarRequest {
  gender: string;
  heightCm: number;
  weightKg: number;
  chestCm: number;
  waistCm: number;
  hipCm: number;
  shoulderCm: number;
  inseamCm: number;
}

export interface BodyGenerationResult {
  bodyType: string;
  avatarPresetCode: string;
  shapeParams: Record<string, number>;
  previewUrl: string;
  modelUrl: string;
  confidence: number;
}
```

---

## 5. API Functions

**lib/api/bodyprofile.ts** (tạo mới)

```typescript
import api from "./index";
import type {
  BodyProfile,
  GenerateAvatarRequest,
  BodyGenerationResult,
} from "@/lib/types/bodyprofile";

export const bodyProfileApi = {
  create: (
    data: Omit<BodyProfile, "id" | "userId" | "createdAt" | "updatedAt">,
  ) => api.post<BodyProfile>("/body-profiles", data),

  getById: (id: number) => api.get<BodyProfile>(`/body-profiles/${id}`),

  getByUserId: (userId: number) =>
    api.get<BodyProfile[]>(`/body-profiles/user/${userId}`),

  generateAvatar: (data: GenerateAvatarRequest) =>
    api.post<BodyGenerationResult>("/body-profiles/generate-avatar", data),
};
```

---

## 6. Validation Rules

| Field                                         | Required | Min | Max |
| --------------------------------------------- | -------- | --- | --- |
| gender                                        | ✓        | -   | -   |
| heightCm                                      | ✓        | 100 | 250 |
| weightKg                                      | ✓        | 30  | 200 |
| chestCm, waistCm, hipCm, shoulderCm, inseamCm | ✓        | > 0 | -   |

**Gender values**: `MALE` hoặc `FEMALE`

---

## 7. Demo Flow

1. **Nhập số đo cơ thể** (chiều cao, cân nặng, ngực, eo, hông, vai, đùi)
2. **Tạo Body Profile** → `/body-profiles` (lưu vào DB)
3. **Generate Avatar** → `/body-profiles/generate-avatar` (sinh avatar 3D)

---

## 8. Checklist

- [ ] Tạo `.env.local`
- [ ] Tạo `lib/types/bodyprofile.ts`
- [ ] Tạo `lib/api/bodyprofile.ts`
- [ ] Validate form nhập số đo
- [ ] Handle error messages
