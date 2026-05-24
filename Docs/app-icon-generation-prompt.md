# Scan App — App Icon Brief (for AI image generation)

Use this document as the prompt/context when generating the **Scan App** launcher icon.

## What the app is

**Scan App** is a mobile app for **scanning QR codes and barcodes** with the device camera or from gallery images. Users scan everyday codes (URLs, payment slips, product barcodes, PDF-417, etc.), view details, copy values, share results, and browse scan history.

The icon must communicate **“scan codes”** at a glance — not documents, not generic camera, not security/fingerprint.

## Design language to reflect

The UI follows a **neo-brutalist** style:

- **Bold and friendly**, not glossy or corporate
- **High contrast**: strong black outlines/shapes on light backgrounds
- **Flat color blocks** with soft **pastel accents** — no heavy gradients, glass, or neon glow
- **Simple geometry**: rounded corners, chunky shapes, slight “sticker” / offset-shadow feel
- **Calm and utilitarian** — trustworthy everyday tool, not flashy or gaming-style

The logo should feel like it belongs on the same shelf as this UI: **clear, readable at small sizes, slightly playful but professional**.

## Recommended visual direction

**Do:**

- One simple focal symbol combining **QR + barcode** (e.g. a minimal QR grid with 2–3 horizontal barcode lines, or a viewfinder/frame around a small code pattern)
- 2–4 flat colors max
- Thick black stroke or black-filled shapes where it helps legibility
- Rounded square or squircle icon silhouette (standard app icon safe area)
- Large shapes; avoid tiny detail that disappears at 48×48 px

**Avoid:**

- Photorealistic cameras, lenses, or 3D renders
- Busy backgrounds, text, wordmarks, or “SCAN APP” lettering in the icon
- Drop shadows, bevels, metallic effects, rainbow gradients
- Overly futuristic / cyber / hacker aesthetics

## Brand colors (use these)

Primary UI palette:

| Role | Hex | Notes |
|------|-----|--------|
| Black | `#000000` | Outlines, strong shapes |
| White | `#FFFFFF` | Backgrounds, negative space |
| Pastel blue | `#A8D4FF` | Accent — good for QR/code tiles |
| Pastel mint | `#B2DFDB` | Secondary accent |
| Pastel green | `#B8E6A3` | Success / scan feedback |
| Pastel orange | `#FFCC80` | Warm accent (barcode) |

Optional subtle accent (use sparingly, not dominant):

| Role | Hex |
|------|-----|
| Bright cyan | `#00D9FF` |

Prefer **white or very light** icon background with **black + one pastel** (blue or mint). Keep it simple.

## Output requirements (critical)

1. **Format:** PNG only  
2. **Background:** **fully transparent** — no white/colored matte behind the artwork  
3. **Resolution:** **maximum practical resolution** (target **1024×1024 px** minimum; **2048×2048 px** preferred if supported)  
4. **Square canvas**, centered artwork with safe padding for Android/iOS mask cropping  
5. **Clean edges** — no semi-transparent fringe or checkerboard baked into the image  

## Example one-line prompt

> App icon for a QR and barcode scanner: neo-brutalist, flat, simple — minimal QR grid plus barcode lines inside a rounded square, thick black outlines, pastel blue `#A8D4FF` and white `#FFFFFF`, friendly and utilitarian, not flashy. PNG, **transparent background**, **2048×2048 px**, no text, no camera, no gradients.
