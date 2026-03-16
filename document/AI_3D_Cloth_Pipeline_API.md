# AI 3D Cloth Pipeline - Backend Documentation

## Overview

This document describes the backend flow for the AI 3D Cloth Pipeline integration with the Wardrobe system.

---

## 1. Architecture Overview

### Modules Involved

| Module     | Responsibility                             |
| ---------- | ------------------------------------------ |
| `clothing` | Clothing item management, analysis         |
| `wardrobe` | Wardrobe management                        |
| `draft`    | Draft management (for future body profile) |

### Data Flow

```
User Image → Clothing Analysis → ClothingItem (ANALYZED) → Add to Wardrobe → WardrobeItem
```

---

## 2. Database Models

### 2.1 ClothingItem Entity

| Field             | Type          | Description                                     |
| ----------------- | ------------- | ----------------------------------------------- |
| `id`              | Long          | Primary key                                     |
| `userId`          | Long          | Owner ID                                        |
| `name`            | String        | Item name                                       |
| `imageUrl`        | String        | Original image URL                              |
| `fileName`        | String        | Original file name                              |
| `sourceType`      | String        | UPLOAD, etc.                                    |
| `status`          | Enum          | CREATED, ANALYZING, ANALYZED, CONFIRMED, FAILED |
| `garmentCategory` | String        | e.g., HOODIE, T-SHIRT, JACKET                   |
| `templateCode`    | String        | 3D template code                                |
| `modelUrl`        | String        | 3D model URL                                    |
| `previewUrl`      | String        | Preview image URL                               |
| `color`           | String        | Detected color                                  |
| `createdAt`       | LocalDateTime | Creation time                                   |
| `updatedAt`       | LocalDateTime | Last update time                                |

### 2.2 WardrobeItem Entity

| Field            | Type          | Description                  |
| ---------------- | ------------- | ---------------------------- |
| `id`             | Long          | Primary key                  |
| `userId`         | Long          | Owner ID                     |
| `clothingItemId` | Long          | Reference to ClothingItem    |
| `category`       | String        | Garment category             |
| `season`         | String        | SPRING, SUMMER, FALL, WINTER |
| `color`          | String        | Color                        |
| `isFavorite`     | Boolean       | Favorite flag                |
| `notes`          | String        | User notes                   |
| `createdAt`      | LocalDateTime | Creation time                |
| `updatedAt`      | LocalDateTime | Last update time             |

### 2.3 ClothingItemStatus Enum

```
CREATED → ANALYZING → ANALYZED → CONFIRMED (or FAILED)
                              ↓
                            FAILED
```

| Status      | Description                                    |
| ----------- | ---------------------------------------------- |
| `CREATED`   | Item created, waiting for analysis             |
| `ANALYZING` | AI is analyzing the image                      |
| `ANALYZED`  | Analysis completed, ready for wardrobe         |
| `CONFIRMED` | Item confirmed (legacy - not used in new flow) |
| `FAILED`    | Analysis failed                                |

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

| Field      | Required | Type   | Description        |
| ---------- | -------- | ------ | ------------------ |
| `userId`   | Yes      | Long   | User ID            |
| `imageUrl` | Yes      | String | Image URL          |
| `fileName` | No       | String | Original file name |
| `name`     | No       | String | Display name       |

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

| Field               | Type   | Description                                   |
| ------------------- | ------ | --------------------------------------------- |
| `clothingItemId`    | Long   | **Important!** Use this ID to add to wardrobe |
| `garmentCategory`   | String | Detected category                             |
| `templateCode`      | String | 3D template code                              |
| `attributes`        | Map    | Detected attributes                           |
| `garmentParameters` | Map    | Garment parameters                            |
| `previewUrl`        | String | Preview image URL                             |
| `confidence`        | Double | Analysis confidence                           |

---

### 3.2 Add to Wardrobe

Adds an analyzed clothing item to the user's wardrobe.

**Endpoint:** `POST /api/v1/wardrobe/from-clothing`

**Request Parameters:**

| Parameter        | Required | Type   | Description                     |
| ---------------- | -------- | ------ | ------------------------------- |
| `clothingItemId` | Yes      | Long   | ID from analyze-direct response |
| `userId`         | Yes      | Long   | User ID                         |
| `season`         | No       | String | SPRING, SUMMER, FALL, WINTER    |
| `notes`          | No       | String | User notes                      |

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

| Method   | Endpoint                                             | Description             |
| -------- | ---------------------------------------------------- | ----------------------- |
| `GET`    | `/api/v1/wardrobe/{id}`                              | Get wardrobe item by ID |
| `GET`    | `/api/v1/wardrobe/user/{userId}/category/{category}` | Get by category         |
| `GET`    | `/api/v1/wardrobe/user/{userId}/favorites`           | Get favorites           |
| `GET`    | `/api/v1/wardrobe/user/{userId}/season/{season}`     | Get by season           |
| `PATCH`  | `/api/v1/wardrobe/{id}/favorite`                     | Toggle favorite         |
| `PUT`    | `/api/v1/wardrobe/{id}`                              | Update wardrobe item    |
| `DELETE` | `/api/v1/wardrobe/{id}`                              | Delete wardrobe item    |

---

## 4. Complete Flow Example

### Flow 1: Direct Analysis (Skip Draft)

Use this flow when user wants to analyze immediately without saving as draft.

#### Step 1: User uploads a clothing image

Frontend sends image to storage service (e.g., Cloudinary, AWS S3) and gets an image URL.

#### Step 2: Call analyze-direct API

```javascript
// Example JavaScript/FE code
const response = await fetch("/api/v1/clothes/analyze-direct", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    userId: 1,
    imageUrl: "https://storage.example.com/images/user-upload.jpg",
    fileName: "my-clothes.jpg",
    name: "My New Hoodie",
  }),
});

const result = await response.json();
// result.data.clothingItemId = 123
// result.data.garmentCategory = 'HOODIE'
// result.data.previewUrl = '...'
```

#### Step 3: Display analysis result to user

Show the preview image and let user confirm or cancel.

#### Step 4: User confirms → Add to wardrobe

```javascript
// User clicks "Add to Wardrobe" button
const wardrobeResponse = await fetch(
  "/api/v1/wardrobe/from-clothing?clothingItemId=123&userId=1&season=SPRING&notes=For casual wear",
  { method: "POST" },
);

const wardrobeItem = await wardrobeResponse.json();
// wardrobeItem.data.id = 456
// wardrobeItem.data.category = 'HOODIE'
```

#### Step 5: (Optional) Update wardrobe item

```javascript
// Update season, notes, or toggle favorite
await fetch("/api/v1/wardrobe/456", {
  method: "PUT",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    category: "HOODIE",
    season: "FALL", // changed
    color: "blue",
    notes: "Updated notes", // changed
  }),
});

// Toggle favorite
await fetch("/api/v1/wardrobe/456/favorite", { method: "PATCH" });
```

---

### Flow 2: Save as Draft → Analyze Later → Confirm to Wardrobe

Use this flow when user wants to save the image as a draft first, then analyze and confirm later.

#### Overview

```
User Image → Create Draft (DRAFT) → Analyze Draft (ANALYZED) → Confirm to Wardrobe → WardrobeItem
```

#### Database: Draft Entity

| Field                | Type          | Description                |
| -------------------- | ------------- | -------------------------- |
| `id`                 | Long          | Primary key                |
| `userId`             | Long          | Owner ID                   |
| `draftType`          | Enum          | CLOTH (BODY for future)    |
| `sourceItemId`       | Long          | Reference to source item   |
| `name`               | String        | Draft name                 |
| `imageUrl`           | String        | Image URL                  |
| `fileName`           | String        | File name                  |
| `inputDataJson`      | String        | Input data JSON            |
| `analysisResultJson` | String        | Analysis result JSON       |
| `status`             | Enum          | DRAFT, ANALYZING, ANALYZED |
| `garmentCategory`    | String        | Detected category          |
| `templateCode`       | String        | 3D template code           |
| `modelUrl`           | String        | 3D model URL               |
| `previewUrl`         | String        | Preview image URL          |
| `color`              | String        | Detected color             |
| `createdAt`          | LocalDateTime | Creation time              |
| `updatedAt`          | LocalDateTime | Last update time           |

#### DraftStatus Enum

```
DRAFT → ANALYZING → ANALYZED
```

| Status      | Description                                |
| ----------- | ------------------------------------------ |
| `DRAFT`     | Draft created, waiting for analysis        |
| `ANALYZING` | AI is analyzing the image                  |
| `ANALYZED`  | Analysis completed, ready for confirmation |

---

#### Step 1: Create Draft

Create a draft to save the image without analyzing yet.

**Endpoint:** `POST /api/v1/drafts`

**Request:**

```json
{
  "userId": 1,
  "draftType": "CLOTH",
  "name": "My Hoodie Draft",
  "imageUrl": "https://storage.example.com/images/user-upload.jpg",
  "fileName": "my-hoodie.jpg"
}
```

| Field       | Required | Type   | Description             |
| ----------- | -------- | ------ | ----------------------- |
| `userId`    | Yes      | Long   | User ID                 |
| `draftType` | Yes      | String | CLOTH (BODY for future) |
| `name`      | No       | String | Draft name              |
| `imageUrl`  | Yes      | String | Image URL               |
| `fileName`  | No       | String | File name               |

**Response (Success):**

```json
{
  "success": true,
  "message": "Draft created successfully",
  "data": {
    "id": 100,
    "userId": 1,
    "draftType": "CLOTH",
    "name": "My Hoodie Draft",
    "imageUrl": "https://storage.example.com/images/user-upload.jpg",
    "fileName": "my-hoodie.jpg",
    "status": "DRAFT",
    "garmentCategory": null,
    "templateCode": null,
    "previewUrl": null,
    "color": null,
    "createdAt": "2026-03-16T18:00:00",
    "updatedAt": "2026-03-16T18:00:00"
  }
}
```

---

#### Step 2: Get User's Drafts

Retrieve all drafts for a user.

**Endpoint:** `GET /api/v1/drafts?userId={userId}&type={type}`

| Parameter | Required | Type   | Description                 |
| --------- | -------- | ------ | --------------------------- |
| `userId`  | Yes      | Long   | User ID                     |
| `type`    | No       | String | Filter by type: CLOTH, BODY |

**Example:**

```
GET /api/v1/drafts?userId=1
GET /api/v1/drafts?userId=1&type=CLOTH
```

**Response (Success):**

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 100,
      "userId": 1,
      "draftType": "CLOTH",
      "name": "My Hoodie Draft",
      "imageUrl": "https://storage.example.com/images/user-upload.jpg",
      "fileName": "my-hoodie.jpg",
      "status": "DRAFT",
      "garmentCategory": null,
      "templateCode": null,
      "previewUrl": null,
      "color": null,
      "createdAt": "2026-03-16T18:00:00",
      "updatedAt": "2026-03-16T18:00:00"
    }
  ]
}
```

---

#### Step 3: Analyze Draft

Analyze a draft to get AI analysis results.

**Endpoint:** `POST /api/v1/drafts/{id}/analyze`

**Example:**

```
POST /api/v1/drafts/100/analyze
```

**Response (Success):**

```json
{
  "success": true,
  "message": "Draft analyzed successfully",
  "data": {
    "id": 100,
    "userId": 1,
    "draftType": "CLOTH",
    "name": "My Hoodie Draft",
    "imageUrl": "https://storage.example.com/images/user-upload.jpg",
    "fileName": "my-hoodie.jpg",
    "status": "ANALYZED",
    "garmentCategory": "HOODIE",
    "templateCode": "TMPL-HOODIE-001",
    "modelUrl": "https://storage.example.com/preview/100.glb",
    "previewUrl": "https://storage.example.com/preview/100.png",
    "color": "blue",
    "analysisResultJson": "{\"garmentCategory\":\"HOODIE\",\"templateCode\":\"TMPL-HOODIE-001\",\"previewUrl\":\"...\",\"attributes\":{...},\"garmentParameters\":{...},\"confidence\":0.92}",
    "createdAt": "2026-03-16T18:00:00",
    "updatedAt": "2026-03-16T18:05:00"
  }
}
```

---

#### Step 4: Display Analysis Result to User

Show the preview image and analysis details. Let user decide:

- **Option A:** Confirm → Add to wardrobe
- **Option B:** Re-analyze → Try again
- **Option C:** Delete draft → Discard

---

#### Step 5A: Confirm to Wardrobe

After analysis, convert the analyzed draft to a wardrobe item.

**Endpoint:** `POST /api/v1/wardrobe/from-clothing`

Note: Currently, the draft analysis saves results in Draft. To add to wardrobe, use the draft's image to create a new ClothingItem:

```javascript
// After draft is analyzed, create clothing item and add to wardrobe
const response = await fetch("/api/v1/clothes/analyze-direct", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    userId: 1,
    imageUrl: draft.imageUrl,
    fileName: draft.fileName,
    name: draft.name,
  }),
});
const result = await response.json();

// Then add to wardrobe
await fetch(
  `/api/v1/wardrobe/from-clothing?clothingItemId=${result.data.clothingItemId}&userId=1&season=SPRING`,
  { method: "POST" },
);
```

---

#### Step 5B: Re-analyze Draft

If user wants to try again with different image or settings.

**Endpoint:** `POST /api/v1/drafts/{id}/reanalyze`

**Example:**

```
POST /api/v1/drafts/100/reanalyze
```

---

#### Step 5C: Delete Draft

If user decides to discard the draft.

**Endpoint:** `DELETE /api/v1/drafts/{id}?userId={userId}`

**Example:**

```
DELETE /api/v1/drafts/100?userId=1
```

---

## 5. Complete Flow Comparison

| Aspect           | Flow 1: Direct                   | Flow 2: Draft                          |
| ---------------- | -------------------------------- | -------------------------------------- |
| **Use Case**     | Quick analysis                   | Save for later                         |
| **Steps**        | 2 API calls                      | 3-4 API calls                          |
| **Persistence**  | ClothingItem created immediately | Saved as draft first                   |
| **User Control** | Limited                          | Full control                           |
| **Re-analyze**   | Not available                    | Available                              |
| **Best For**     | Quick demo, simple UX            | User wants to review before confirming |

### Flow Diagrams

#### Flow 1: Direct Analysis

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Upload   │────▶│ analyze-direct   │────▶│ from-clothing  │
│   Image    │     │ (POST)           │     │ (POST)         │
└─────────────┘     │ - Creates       │     │ - Creates      │
                    │   ClothingItem  │     │   WardrobeItem │
                    │ - Returns       │     └─────────────────┘
                    │   clothingItemId│
                    └──────────────────┘
```

#### Flow 2: Draft Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   Upload   │────▶│   Create    │────▶│   Analyze   │────▶│  from-clothing  │
│   Image    │     │   Draft     │     │   Draft     │     │  (or delete)    │
└─────────────┘     │ (POST)      │     │ (POST)      │     │ (POST)          │
                    └─────────────┘     └─────────────┘     └─────────────────┘
                         │                   │
                         │              ┌────▼────┐
                         │              │  Display│
                         │              │ Preview │
                         │              └────┬────┘
                         │                   │
                    ┌────▼────┐        ┌────▼────┐
                    │   Get   │        │Re-analyze│
                    │  Drafts │        │ (POST)   │
                    └─────────┘        └──────────┘
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

| Date       | Version | Changes                                          |
| ---------- | ------- | ------------------------------------------------ |
| 2026-03-16 | 1.0     | Initial implementation with wardrobe integration |

---

## 8. Questions?

If you have any questions about the API flow, please contact the backend team.
