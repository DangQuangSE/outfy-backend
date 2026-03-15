
# Virtual Try-On System
## Functional Specification

This document describes the core functional flows of the Virtual Try-On system, including avatar creation, clothing upload, garment generation, try-on simulation, and garment adjustment.

---

# 1. Avatar Creation (Body Generation)

## Purpose
Allow users to create a personalized 3D avatar based on their body measurements.
This avatar will be used as the base model for all virtual try-on operations.

## User Flow

### Step 1 – Launch Application
User opens the application.

### Step 2 – Check Avatar Existence
System checks if a body avatar already exists.

Decision:
Avatar exists?

YES → Navigate to Home  
NO → Navigate to Avatar Creation

### Step 3 – Input Body Measurements
User enters body information.

Example fields:
- Gender
- Height (cm)
- Weight (kg)
- Chest circumference
- Waist circumference
- Hip circumference
- Shoulder width
- Inseam length

### Step 4 – Confirm Input
User reviews and confirms the entered measurements.

### Step 5 – Avatar Parameter Generation
System generates avatar parameters using a parametric body model.

Measurements → Body inference → Avatar parameters

Outputs:
- BodyType (Slim / Regular / Curvy / Broad)
- AvatarPreset
- Shape parameters

Example:
{
  "heightScale": 1.02,
  "shoulderScale": 1.00,
  "waistScale": 0.96,
  "hipScale": 1.04
}

### Step 6 – Avatar Rendering
Rendering engine loads the corresponding avatar preset.

Example:
female_regular_01
male_slim_02

Avatar preview is generated.

### Step 7 – Avatar Preview
User sees the generated avatar and can rotate or zoom it.

### Step 8 – Confirmation
User satisfied with avatar?

YES → Save avatar → Go to Home  
NO → Return to measurement editing

---

# 2. Clothing Upload and Analysis

## Purpose
Allow users to upload clothing items that will be converted into parametric garments for virtual try-on.

## User Flow

### Step 1 – Open Wardrobe Upload
User enters clothing upload section.

### Step 2 – Select Image Source
User selects upload method.

Options:
- Upload from device
- Take photo
- Import from online shop link

### Step 3 – Upload Image
User uploads clothing image.

### Step 4 – Image Quality Validation
System validates image quality.

Checks:
- Resolution
- Blur detection
- Lighting
- Clothing visibility
- Background complexity

Decision:
Image valid?

YES → Continue  
NO → Ask user to upload again

### Step 5 – Background Removal
AI segmentation separates clothing from background.

### Step 6 – Garment Classification
AI classifies clothing category.

Examples:
- T-shirt
- Hoodie
- Shirt
- Pants
- Dress
- Skirt

### Step 7 – Attribute Extraction
System extracts garment attributes.

Examples:
- Color
- Sleeve type
- Fit style
- Collar type
- Zipper presence
- Hood presence

Example output:
category: hoodie
color: black
sleeve: long
fit: loose
closure: zipper
hood: true

### Step 8 – Generate Garment Parameters
Extracted attributes are converted into parametric garment data.

Examples:
- Chest width
- Body length
- Sleeve length
- Fit type
- Material stiffness

### Step 9 – User Verification
User reviews detected garment information.

Information correct?

YES → Continue  
NO → User edits parameters

### Step 10 – Save to Wardrobe
Garment is stored in wardrobe database.

---

# 3. Garment 3D Model Generation

## Purpose
Convert garment parameters into a 3D garment mesh for simulation.

### Step 1 – Receive Garment Parameters
Example:
category: hoodie
chestWidth: 52
bodyLength: 70
sleeveLength: 63
fitType: loose

### Step 2 – Select Garment Template
System selects template.

Examples:
- T-shirt template
- Hoodie template
- Pants template

### Step 3 – Parametric Deformation
Template garment is modified according to parameters.

Adjustments:
- Length scaling
- Width scaling
- Sleeve adjustments

### Step 4 – Mesh Generation
System generates garment mesh including:
- Front panel
- Back panel
- Sleeves
- Optional components

### Step 5 – Mesh Validation
Checks:
- Topology validity
- Non-intersecting geometry
- UV mapping

Mesh valid?

YES → Continue  
NO → Regenerate mesh

### Step 6 – Save Garment Model
Garment mesh stored in asset storage.

---

# 4. Virtual Try-On

## Purpose
Allow users to simulate wearing clothing on their avatar.

## User Flow

### Step 1 – Open Wardrobe
User opens wardrobe.

### Step 2 – Select Clothing
User selects clothing item.

### Step 3 – Load Assets
System loads:
- Avatar model
- Garment mesh

### Step 4 – Garment Fitting
Garment aligned with avatar body.

Adjustments:
- Scaling
- Positioning
- Body clearance

### Step 5 – Skeleton Binding
Garment mesh attached to avatar skeleton.

### Step 6 – Cloth Simulation Setup
Cloth physics initialized.

### Step 7 – Collider Generation
Body colliders created:
- Chest
- Arms
- Hips
- Legs

### Step 8 – Simulation Execution
Cloth simulation begins.

### Step 9 – Render Result
Avatar wearing garment is displayed.

User can:
- Rotate avatar
- Zoom
- Change camera angle

### Step 10 – Adjustment Decision
User wants to adjust clothing?

YES → Garment Adjustment  
NO → End flow

---

# 5. Garment Adjustment

## Purpose
Allow users to modify garment parameters.

### Step 1 – Open Adjustment Panel

### Step 2 – Modify Parameters
Examples:
- Size
- Length
- Fit style
- Sleeve length

### Step 3 – Update Parameters

### Step 4 – Update Garment Mesh

### Step 5 – Refit Garment

### Step 6 – Re-run Simulation

### Step 7 – Render Updated Result

User satisfied?

YES → Save changes  
NO → Return to adjustment

---

# 6. AI Wardrobe Stylist (Future Feature)

## Purpose
Recommend outfits using AI.

Flow:

User opens wardrobe  
↓  
AI analyzes wardrobe  
↓  
User selects occasion

Examples:
- Casual
- Work
- Party
- Sport

↓  
AI suggests outfit combinations  
↓  
User can directly try-on suggested outfit

---

# 7. Key Decision Nodes

Important system decisions:

- Avatar exists?
- Image valid?
- Garment category?
- Mesh valid?
- User wants adjustment?
- User satisfied with result?
