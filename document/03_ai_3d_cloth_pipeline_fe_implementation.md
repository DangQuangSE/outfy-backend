# Frontend Implementation Guide - AI 3D Cloth Pipeline

## Mục tiêu

Hướng dẫn frontend tích hợp API phân tích ảnh quần áo để nhận thông tin garment category, attributes, và template URL cho virtual try-on.

---

## 1. API Overview

| Thông tin | Giá trị |
|-----------|----------|
| **Endpoint** | `POST /api/v1/clothes/analyze-direct` |
| **Content-Type** | `application/json` |
| **Authentication** | Not required (demo) |

---

## 2. Request

### JSON Structure

```json
{
  "imageUrl": "https://example.com/my_hoodie.jpg",
  "fileName": "my_hoodie.jpg"
}
```

### Field Description

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `imageUrl` | String | ✅ Yes | URL của ảnh quần áo |
| `fileName` | String | ❌ No | Tên file (dùng để classify category) |

### Validation Rules

- `imageUrl`: Must be a valid URL
- `fileName`: Optional, nếu không có sẽ extract từ imageUrl

---

## 3. Response

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Clothing analyzed successfully",
  "data": {
    "garmentCategory": "HOODIE",
    "templateCode": "hoodie_template",
    "attributes": {
      "color": "Black",
      "sleeveType": "LONG",
      "fitType": "LOOSE",
      "hasHood": true,
      "hasZipper": true
    },
    "garmentParameters": {
      "chestWidth": 52.0,
      "bodyLength": 70.0,
      "sleeveLength": 63.0
    },
    "previewUrl": "/models/cloth/hoodie_template.glb",
    "confidence": 0.9
  }
}
```

### Error Response (4xx/5xx)

```json
{
  "success": false,
  "message": "Image URL is required",
  "data": null
}
```

---

## 4. TypeScript Types

### Request Type

```typescript
interface AnalyzeClothingRequest {
  imageUrl: string;
  fileName?: string;
}
```

### Response Type

```typescript
interface ClothingAnalysisResult {
  garmentCategory: 'TSHIRT' | 'HOODIE' | 'SHIRT' | 'PANTS' | 'SKIRT' | 'DRESS' | 'JACKET';
  templateCode: string;
  attributes: {
    color: string;
    sleeveType: string;
    fitType: string;
    [key: string]: any; // Additional attributes like hasHood, hasZipper, etc.
  };
  garmentParameters: {
    chestWidth?: number;
    bodyLength?: number;
    sleeveLength?: number;
    waistWidth?: number;
    inseamLength?: number;
    [key: string]: any;
  };
  previewUrl: string;
  confidence: number;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
```

---

## 5. API Implementation

### API Call Function

```typescript
// lib/api/cloth.ts
import { AnalyzeClothingRequest, ClothingAnalysisResult, ApiResponse } from '@/types/cloth';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export async function analyzeClothing(
  request: AnalyzeClothingRequest
): Promise<ClothingAnalysisResult> {
  const response = await fetch(`${API_BASE_URL}/api/v1/clothes/analyze-direct`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to analyze clothing');
  }

  const result: ApiResponse<ClothingAnalysisResult> = await response.json();
  return result.data;
}
```

### Validation Schema (Zod)

```typescript
// lib/utils/validators.ts
import { z } from 'zod';

export const analyzeClothingSchema = z.object({
  imageUrl: z.string().url('Invalid URL format'),
  fileName: z.string().optional(),
});

export type AnalyzeClothingInput = z.infer<typeof analyzeClothingSchema>;
```

---

## 6. UI Implementation

### Upload Page Example

```tsx
// app/cloth/upload/page.tsx
'use client';

import { useState } from 'react';
import { analyzeClothing } from '@/lib/api/cloth';
import { analyzeClothingSchema } from '@/lib/utils/validators';
import { ClothingAnalysisResult } from '@/types/cloth';

export default function ClothUploadPage() {
  const [imageUrl, setImageUrl] = useState('');
  const [fileName, setFileName] = useState('');
  const [result, setResult] = useState<ClothingAnalysisResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleAnalyze = async () => {
    setError('');
    setResult(null);

    // Validate input
    const validation = analyzeClothingSchema.safeParse({
      imageUrl,
      fileName: fileName || undefined,
    });

    if (!validation.success) {
      setError(validation.error.errors[0].message);
      return;
    }

    setLoading(true);

    try {
      const data = await analyzeClothing({
        imageUrl,
        fileName: fileName || undefined,
      });
      setResult(data);
      
      // Save to localStorage for result page
      localStorage.setItem('outfy_cloth_result', JSON.stringify(data));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Analysis failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Upload Your Cloth</h1>
      
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Image URL</label>
          <input
            type="url"
            value={imageUrl}
            onChange={(e) => setImageUrl(e.target.value)}
            placeholder="https://example.com/hoodie.jpg"
            className="w-full p-2 border rounded"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">
            File Name (optional)
          </label>
          <input
            type="text"
            value={fileName}
            onChange={(e) => setFileName(e.target.value)}
            placeholder="my_hoodie.jpg"
            className="w-full p-2 border rounded"
          />
          <p className="text-xs text-gray-500 mt-1">
            Used for category detection (e.g., hoodie, tshirt, pants)
          </p>
        </div>

        {error && (
          <div className="p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}

        <button
          onClick={handleAnalyze}
          disabled={loading || !imageUrl}
          className="w-full py-3 bg-blue-600 text-white rounded disabled:bg-gray-400"
        >
          {loading ? 'Analyzing...' : 'Analyze Cloth'}
        </button>
      </div>

      {result && (
        <div className="mt-6 p-4 bg-green-100 rounded">
          <h2 className="font-bold text-green-800">Analysis Complete!</h2>
          <p>Category: {result.garmentCategory}</p>
          <p>Template: {result.templateCode}</p>
          <p>Confidence: {(result.confidence * 100).toFixed(0)}%</p>
          <a href="/cloth/result" className="text-blue-600 underline">
            View 3D Model →
          </a>
        </div>
      )}
    </div>
  );
}
```

### Result Page with 3D Model

```tsx
// app/cloth/result/page.tsx
'use client';

import { useEffect, useState } from 'react';
import { ClothingAnalysisResult } from '@/types/cloth';

export default function ClothResultPage() {
  const [result, setResult] = useState<ClothingAnalysisResult | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const saved = localStorage.getItem('outfy_cloth_result');
    if (saved) {
      try {
        setResult(JSON.parse(saved));
      } catch {
        setError('Invalid result data');
      }
    } else {
      setError('No analysis result found');
    }
  }, []);

  if (error) {
    return <div className="p-4 text-red-600">{error}</div>;
  }

  if (!result) {
    return <div className="p-4">Loading...</div>;
  }

  const modelUrl = `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}${result.previewUrl}`;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Cloth Analysis Result</h1>

      <div className="grid md:grid-cols-2 gap-6">
        {/* 3D Model Viewer */}
        <div className="bg-gray-100 rounded-lg overflow-hidden" style={{ height: '400px' }}>
          <model-viewer
            src={modelUrl}
            alt={`3D ${result.garmentCategory} model`}
            auto-rotate
            camera-controls
            style={{ width: '100%', height: '100%' }}
          />
        </div>

        {/* Analysis Details */}
        <div className="space-y-4">
          <div className="p-4 bg-white border rounded">
            <h2 className="font-bold text-lg mb-2">Category</h2>
            <p className="text-2xl font-semibold">{result.garmentCategory}</p>
            <p className="text-gray-500">Template: {result.templateCode}</p>
          </div>

          <div className="p-4 bg-white border rounded">
            <h2 className="font-bold text-lg mb-2">Attributes</h2>
            <div className="grid grid-cols-2 gap-2">
              {Object.entries(result.attributes).map(([key, value]) => (
                <div key={key}>
                  <span className="text-gray-500 capitalize">{key}:</span>{' '}
                  <span className="font-medium">{String(value)}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="p-4 bg-white border rounded">
            <h2 className="font-bold text-lg mb-2">Garment Parameters</h2>
            <div className="grid grid-cols-2 gap-2">
              {Object.entries(result.garmentParameters).map(([key, value]) => (
                <div key={key}>
                  <span className="text-gray-500 capitalize">{key}:</span>{' '}
                  <span className="font-medium">{value}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="p-4 bg-blue-50 border border-blue-200 rounded">
            <h2 className="font-bold text-lg mb-2">Confidence</h2>
            <div className="w-full bg-gray-200 rounded-full h-4">
              <div
                className="bg-blue-600 h-4 rounded-full"
                style={{ width: `${result.confidence * 100}%` }}
              />
            </div>
            <p className="text-center mt-1">{(result.confidence * 100).toFixed(0)}%</p>
          </div>
        </div>
      </div>

      <div className="mt-6 flex gap-4">
        <a href="/cloth/upload" className="px-4 py-2 bg-gray-200 rounded">
          ← Upload Another
        </a>
        <a href="/tryon" className="px-4 py-2 bg-blue-600 text-white rounded">
          Try On →
        </a>
      </div>
    </div>
  );
}
```

---

## 7. Usage Tips

### File Naming Convention

Để có kết quả classify tốt nhất, nên đặt tên file rõ ràng:

| Tên file | Category detect được |
|----------|---------------------|
| `my_hoodie.jpg` | HOODIE |
| `black_tshirt.png` | TSHIRT |
| `blue_jeans.jpg` | PANTS |
| `summer_dress.jpg` | DRESS |
| `winter_jacket.png` | JACKET |
| `mini_skirt.jpg` | SKIRT |
| `formal_shirt.jpg` | SHIRT |

### Confidence Score

- **90%+**: Rất confident, filename rõ ràng
- **70-90%**: Confident trung bình
- **<70%**: Low confidence, có thể không đúng category

### Integration with Try-On

Sau khi có kết quả, có thể dùng `templateCode` hoặc `previewUrl` để:
1. Hiển thị 3D model của cloth
2. Kết hợp với body avatar để tạo try-on

---

## 8. Error Handling

| Error Code | Description | Solution |
|------------|-------------|----------|
| 400 | Invalid URL | Check imageUrl format |
| 500 | Server error | Retry later |
| Network Error | No connection | Check internet |

---

## 9. Testing

### Test Cases

```bash
# Test with hoodie
curl -X POST http://localhost:8080/api/v1/clothes/analyze-direct \
  -H "Content-Type: application/json" \
  -d '{"imageUrl": "https://example.com/hoodie.jpg", "fileName": "my_hoodie.jpg"}'

# Test with tshirt
curl -X POST http://localhost:8080/api/v1/clothes/analyze-direct \
  -H "Content-Type: application/json" \
  -d '{"imageUrl": "https://example.com/tshirt.jpg", "fileName": "white_tshirt.png"}'

# Test with pants
curl -X POST http://localhost:8080/api/v1/clothes/analyze-direct \
  -H "Content-Type: application/json" \
  -d '{"imageUrl": "https://example.com/blue_jeans.jpg", "fileName": "blue_jeans.jpg"}'
```

---

## 10. Package Dependencies

```bash
npm install @google/model-viewer zod
```

---

## 11. Data Storage

| Data | Storage | Key |
|------|---------|-----|
| Analysis Result | localStorage | `outfy_cloth_result` |
| Upload History | localStorage | `outfy_cloth_history` (optional) |

---

## 12. Flow Summary

```
User uploads/takes photo
    ↓
Enter image URL + filename
    ↓
Call POST /api/v1/clothes/analyze-direct
    ↓
Get result (category, attributes, template, confidence)
    ↓
Save to localStorage
    ↓
Display 3D model with <model-viewer>
    ↓
Option: Proceed to try-on
```

---

## 13. Future Enhancements

- [ ] Add image file upload (not just URL)
- [ ] Show multiple category suggestions
- [ ] Add manual category selection override
- [ ] Save analyzed clothes to user wardrobe
- [ ] Integration với body avatar cho full try-on

