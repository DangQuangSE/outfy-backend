# OUTFY Frontend - Coding Rules & Conventions

## 1. Mục đích

Tài liệu này định nghĩa các quy tắc về cấu trúc và code cho OUTFY frontend (Next.js 16 + React 19).

## 2. Cấu trúc Project

```
outfy_demo/
├── app/                    # Next.js App Router
│   ├── avatar/            # Feature: Avatar
│   │   ├── measurements/  # Nhập số đo
│   │   ├── scan/          # Quét/đang xử lý
│   │   └── result/        # Kết quả hiển thị 3D
│   ├── profile/
│   └── ...
├── lib/                   # Shared code
│   ├── api/               # API calls (axios)
│   ├── types/             # TypeScript types
│   ├── utils/             # Validators, helpers
│   └── store/             # Zustand stores
├── components/            # Reusable UI components
└── public/               # Static files
    ├── images/
    └── models/            # 3D models (GLB)
```

## 3. Quy tắc Code

### 3.1 API Integration

- **Tất cả API calls** phải qua `lib/api/`
- Sử dụng **axios** với interceptors cho auth
- Export API functions từ file riêng: `lib/api/avatar.ts`

```typescript
// ✅ Đúng
import { avatarApi } from "@/lib/api/avatar";
const result = await avatarApi.generateAvatar(data);

// ❌ Sai
import api from "@/lib/api";
await api.post("/body-profiles/generate-avatar", data);
```

### 3.2 TypeScript Types

- Đặt types trong `lib/types/`
- Tên file theo domain: `avatar.ts`, `user.ts`, `clothing.ts`
- Dùng TypeScript interfaces cho API responses

```typescript
// ✅ Đúng
export interface BodyGenerationResult {
  bodyType: string;
  modelUrl: string;
  previewUrl: string;
  shapeParams: Record<string, number>;
  confidence: number;
}

// ❌ Sai
type Result = { ... } // Viết tắt
```

### 3.3 Validation

- Sử dụng **Zod** từ `lib/utils/validators.ts`
- Export cả schema và inferred type

```typescript
// ✅ Đúng
export const generateAvatarSchema = z.object({
  gender: z.enum(["male", "female"]),
  heightCm: z.number().min(100).max(250),
  weightKg: z.number().min(30).max(300),
  // ...
});

export type GenerateAvatarData = z.infer<typeof generateAvatarSchema>;
```

### 3.4 Page Components

- Page components trong `app/` phải là **Server Components** hoặc có `"use client"` nếu dùng hooks
- Giữ logic ngắn gọn, gọi API từ service/hook riêng
- Sử dụng **TanStack Query** (React Query) cho data fetching

```typescript
// ✅ Đúng - Tách logic ra hook hoặc service
"use client";
import { useMutation } from "@tanstack/react-query";
import { avatarApi } from "@/lib/api/avatar";

export default function Page() {
  const mutation = useMutation({
    mutationFn: avatarApi.generateAvatar
  });
  // ...
}
```

### 3.5 3D Model Display

- Sử dụng **`<model-viewer>`** web component cho GLB files
- Import từ CDN hoặc npm package `@google/model-viewer`

```typescript
// ✅ Đúng
"use client";
import "@google/model-viewer";

export function ModelViewer({ src, alt }: { src: string; alt: string }) {
  return (
    <model-viewer
      src={src}
      alt={alt}
      auto-rotate
      camera-controls
      style={{ width: "100%", height: "100%" }}
    />
  );
}
```

### 3.6 State Management

- Sử dụng **Zustand** cho global state
- Store files trong `lib/store/`

### 3.7 Naming Conventions

| Loại | Quy tắc | Ví dụ |
|------|---------|-------|
| Components | PascalCase | `AvatarResult`, `ModelViewer` |
| Functions | camelCase | `generateAvatar`, `getAvatarResult` |
| Types/Interfaces | PascalCase | `BodyGenerationResult` |
| Files | kebab-case | `avatar-api.ts`, `validators.ts` |
| API Endpoints | RESTful | `/body-profiles/generate-avatar` |

## 4. Luồng xử lý Avatar

### 4.1 Flow hiện tại

```
measurements → scan (processing) → result (static image)
```

### 4.2 Flow mới (với 3D)

```
measurements → scan (call API) → result (3D model)
```

### 4.3 Data Flow

1. **measurements/page.tsx**: User nhập số đo → Submit form
2. **scan/page.tsx**: Gọi API `generate-avatar` → Lưu result vào state/context → Chuyển sang result
3. **result/page.tsx**: Hiển thị `<model-viewer>` với `modelUrl` từ API

## 5. API Contract

### Request: POST /api/v1/body-profiles/generate-avatar

```typescript
interface GenerateAvatarRequest {
  gender: "male" | "female";
  heightCm: number;
  weightKg: number;
  chestCm: number;
  waistCm: number;
  hipCm: number;
  shoulderCm: number;
  inseamCm: number;
}
```

### Response

```typescript
interface BodyGenerationResult {
  bodyType: "Slim" | "Regular" | "Curvy" | "Broad";
  avatarPresetCode: string;
  modelUrl: string;        // "/models/regular_female.glb"
  previewUrl: string;
  shapeParams: Record<string, number>;
  confidence: number;
}
```

## 6. Checklist trước khi push code

- [ ] Types được đặt trong `lib/types/`
- [ ] API calls được đặt trong `lib/api/`
- [ ] Validators sử dụng Zod trong `lib/utils/validators.ts`
- [ ] Page components sử dụng `"use client"` khi cần
- [ ] Không hardcode API URLs trong components
- [ ] Model-viewer được import đúng cách
- [ ] ESLint không có errors
- [ ] Build thành công

## 7. Cấu trúc file cần tạo

```
lib/
├── api/
│   └── avatar.ts          # Thêm generateAvatar API
├── types/
│   └── avatar.ts         # Thêm BodyGenerationResult type
└── utils/
    └── validators.ts     # Thêm generateAvatarSchema

app/avatar/
├── scan/
│   └── page.tsx          # Gọi API, chuyển data sang result
└── result/
    └── page.tsx          # Hiển thị model-viewer
```

