# AI 3D Cloth Pipeline - Backend Documentation

## Overview

This document describes the backend flow for the AI 3D Cloth Pipeline integration with the Wardrobe system.

---

## 1. Architecture Overview

### Modules Involved

| Module | Responsibility |
|--------|----------------|
| `clothing` | Clothing item management, analysis |
| `wardrobe` | Wardrobe management |
| `draft` | Draft management (for future body profile) |

### Data Flow

```
User Image → Clothing Analysis → ClothingItem (ANALYZED) → Add to Wardrobe → WardrobeItem
```

---

## 2. Database Models

### 2.1 ClothingItem Entity

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `userId` | Long | Owner ID |
| `name` | String | Item name |
| `imageUrl` | String | Original image URL |
| `fileName` | String | Original file name |
| `sourceType` | String | UPLOAD, etc. |
| `status` | Enum | CREATED, ANALYZING, ANALYZED, CONFIRMED, FAILED |
| `garmentCategory` | String | e.g., HOODIE, T-SHIRT, JACKET |
| `templateCode` | String | 3D template code |
| `modelUrl` | String | 3D model URL |
| `previewUrl` | String | Preview image URL |
| `color` | String | Detected color |
| `createdAt` | LocalDateTime | Creation time |
| `updatedAt` | LocalDateTime | Last update time |

### 2.2 WardrobeItem Entity

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `userId` | Long | Owner ID |
| `clothingItemId` | Long | Reference to ClothingItem |
| `category` | String | Garment category |
| `season` | String | SPRING, SUMMER, FALL, WINTER |
| `color` | String | Color |
| `isFavorite` | Boolean | Favorite flag |
| `notes` | String | User notes |
| `createdAt` | LocalDateTime | Creation time |
| `updatedAt` | LocalDateTime | Last update time |

### 2.3 ClothingItemStatus Enum

```
CREATED → ANALYZING → ANALYZED → CONFIRMED (or FAILED)
                              ↓
                            FAILED
```

| Status | Description |
|--------|-------------|
| `CREATED` | Item created, waiting for analysis |
| `ANALYZING` | AI is analyzing the image |
| `ANALYZED` | Analysis completed, ready for wardrobe |
| `CONFIRMED` | Item confirmed (legacy - not used in new flow) |
| `FAILED` | Analysis failed |

---

## 3. API Endpoints

### 3.1 Analyze Clothing (Direct)

Analyzes a clothing image and creates a ClothingItem with ANALYZED status.

**Endpoint:** `POST /api/v1/clothes/analyze-direct`

**Request:**
```json
{
  "userId": 1,
  "imageUrl": "https://storage.example.com/images/cloth1.jpg",
  "fileName": "my-hoodie.jpg",
  "name": "My Blue Hoodie"
}
```

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `userId` | Yes | Long | User ID |
| `imageUrl` | Yes | String | Image URL |
| `fileName` | No | String | Original file name |
| `name` | No | String | Display name |

**Response (Success):**
```json
{
  "success": true,
  "message": "Clothing analyzed successfully",
  "data": {
    "clothingItemId": 123,
    "garmentCategory": "HOODIE",
    "templateCode": "TMPL-HOODIE-001",
    "attributes": {
      "color": "blue",
      "style": "casual",
      "pattern": "solid"
    },
    "garmentParameters": {
      "neckline": "hooded",
      "sleeveType": "long-sleeve",
      "fit": "regular"
    },
    "previewUrl": "https://storage.example.com/preview/123.png",
    "confidence": 0.92
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `clothingItemId` | Long | **Important!** Use this ID to add to wardrobe |
| `garmentCategory` | String | Detected category |
| `templateCode` | String | 3D template code |
| `attributes` | Map | Detected attributes |
| `garmentParameters` | Map | Garment parameters |
| `previewUrl` | String | Preview image URL |
| `confidence` | Double | Analysis confidence |

---

### 3.2 Add to Wardrobe

Adds an analyzed clothing item to the user's wardrobe.

**Endpoint:** `POST /api/v1/wardrobe/from-clothing`

**Request Parameters:**

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `clothingItemId` | Yes | Long | ID from analyze-direct response |
| `userId` | Yes | Long | User ID |
| `season` | No | String | SPRING, SUMMER, FALL, WINTER |
| `notes` | No | String | User notes |

**Example:**
```
POST /api/v1/wardrobe/from-clothing?clothingItemId=123&userId=1&season=SPRING&notes=For%20casual%20outings
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Added to wardrobe successfully",
  "data": {
    "id": 456,
    "userId": 1,
    "clothingItemId": 123,
    "category": "HOODIE",
    "season": "SPRING",
    "color": "blue",
    "isFavorite": false,
    "notes": "For casual outings",
    "imageUrl": "https://storage.example.com/images/cloth1.jpg",
    "createdAt": "2026-03-16T18:30:00",
    "updatedAt": "2026-03-16T18:30:00"
  }
}
```

---

### 3.3 Get User Wardrobe

Retrieves all wardrobe items for a user.

**Endpoint:** `GET /api/v1/wardrobe/user/{userId}`

**Response (Success):**
```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 456,
      "userId": 1,
      "clothingItemId": 123,
      "category": "HOODIE",
      "season": "SPRING",
      "color": "blue",
      "isFavorite": false,
      "notes": "For casual outings",
      "imageUrl": "https://storage.example.com/images/cloth1.jpg",
      "createdAt": "2026-03-16T18:30:00",
      "updatedAt": "2026-03-16T18:30:00"
    }
  ]
}
```

---

### 3.4 Other Wardrobe Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/wardrobe/{id}` | Get wardrobe item by ID |
| `GET` | `/api/v1/wardrobe/user/{userId}/category/{category}` | Get by category |
| `GET` | `/api/v1/wardrobe/user/{userId}/favorites` | Get favorites |
| `GET` | `/api/v1/wardrobe/user/{userId}/season/{season}` | Get by season |
| `PATCH` | `/api/v1/wardrobe/{id}/favorite` | Toggle favorite |
| `PUT` | `/api/v1/wardrobe/{id}` | Update wardrobe item |
| `DELETE` | `/api/v1/wardrobe/{id}` | Delete wardrobe item |

---

## 4. Complete Flow Example

### Step 1: User uploads a clothing image

Frontend sends image to storage service (e.g., Cloudinary, AWS S3) and gets an image URL.

### Step 2: Call analyze-direct API

```javascript
// Example JavaScript/FE code
const response = await fetch('/api/v1/clothes/analyze-direct', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 1,
    imageUrl: 'https://storage.example.com/images/user-upload.jpg',
    fileName: 'my-clothes.jpg',
    name: 'My New Hoodie'
  })
});

const result = await response.json();
// result.data.clothingItemId = 123
// result.data.garmentCategory = 'HOODIE'
// result.data.previewUrl = '...'
```

### Step 3: Display analysis result to user

Show the preview image and let user confirm or cancel.

### Step 4: User confirms → Add to wardrobe

```javascript
// User clicks "Add to Wardrobe" button
const wardrobeResponse = await fetch(
  '/api/v1/wardrobe/from-clothing?clothingItemId=123&userId=1&season=SPRING&notes=For casual wear',
  { method: 'POST' }
);

const wardrobeItem = await wardrobeResponse.json();
// wardrobeItem.data.id = 456
// wardrobeItem.data.category = 'HOODIE'
```

### Step 5: (Optional) Update wardrobe item

```javascript
// Update season, notes, or toggle favorite
await fetch('/api/v1/wardrobe/456', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    category: 'HOODIE',
    season: 'FALL',  // changed
    color: 'blue',
    notes: 'Updated notes'  // changed
  })
});

// Toggle favorite
await fetch('/api/v1/wardrobe/456/favorite', { method: 'PATCH' });
```

---

## 5. Error Handling

### Common Error Responses

**400 - Bad Request (invalid status):**
```json
{
  "success": false,
  "message": "Item must be analyzed before confirmation",
  "data": null
}
```

**400 - Bad Request (already in wardrobe):**
```json
{
  "success": false,
  "message": "Clothing item already in wardrobe",
  "data": null
}
```

**404 - Not Found:**
```json
{
  "success": false,
  "message": "ClothingItem not found with id: 999",
  "data": null
}
```

---

## 6. Mock Data

The backend uses `MockClothingAnalysisGateway` for demo purposes. It returns:

### Garment Categories
- `HOODIE`
- `T-SHIRT`
- `JACKET`
- `PANTS`
- `SHIRT`
- `DRESS`

### Template Codes
- `TMPL-HOODIE-001`
- `TMPL-TSHIRT-001`
- `TMPL-JACKET-001`
- etc.

### Detected Attributes
```json
{
  "color": "blue",
  "style": "casual",
  "pattern": "solid"
}
```

### Garment Parameters
```json
{
  "neckline": "hooded",
  "sleeveType": "long-sleeve",
  "fit": "regular"
}
```

---

## 7. Version History

| Date | Version | Changes |
|------|---------|---------|
| 2026-03-16 | 1.0 | Initial implementation with wardrobe integration |

---

## 8. Questions?

If you have any questions about the API flow, please contact the backend team.

