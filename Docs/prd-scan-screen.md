# PRD: Scan Screen (v1)

## 1. Introduction / Overview

The **Scan Screen** is the main entry point of the app. It opens a live camera preview with a centered square "scan window" that continuously analyses frames and detects QR codes and barcodes on both Android and iOS.

When a code is detected, the screen invokes a callback with:
- the **kind** of code (QR or Barcode),
- the specific **format** (e.g. `QR_CODE`, `EAN_13`, `CODE_128`),
- the **decoded content** (raw text).

This v1 wires the camera, the scanning logic, the permission flow, and the visual UI built from the existing `uiComponent` design system. Persistence, history, collections, navigation on success, and richer post-scan flows are explicitly **out of scope** for v1 and will be added incrementally.

## 2. Goals

1. Provide a working camera-backed scan surface on Android and iOS that detects QR and barcodes in real time.
2. Expose detection results via a single callback contract that downstream features can subscribe to.
3. Handle the camera permission lifecycle (granted / denied / permanently denied) without crashing, and let the user re-request from the UI.
4. Be the **start destination** of the app.
5. Keep the scanning UI component reusable in isolation (no ViewModel/navigation knowledge inside it).
6. Keep the feature module API surface small — only the entry point and Koin module are public.

## 3. User Stories

- As a **user**, when I open the app I see a camera preview so I can immediately scan a code without extra taps.
- As a **user**, when the app does not yet have camera permission, I see a clear message explaining why and a button to grant access.
- As a **user**, when I previously denied camera permission, I can tap a button on the screen to retry / open settings.
- As a **user**, I can toggle the flashlight from the scan screen to scan in low light.
- As a **user**, I see a "Pick from gallery" button on the scan screen (placeholder for a future feature).
- As a **user**, when I point the camera at a QR or barcode, it is detected within the scanning box and the detection callback fires once per detection (subject to a 2-second cooldown to prevent spam).
- As a **developer**, I depend only on `:feature:scan:api` from outside the feature; the implementation is hidden behind that boundary.

## 4. Functional Requirements

### 4.1 Module structure

1. The following Gradle modules must exist:
   - `:feature:scan:api` — public entry point and Koin module only.
   - `:feature:scan:impl` — `ScanScreen`, `ScanViewModel`, view-state types, sealed interaction interface, internal Koin wiring.
   - `:core:scan` — Compose Multiplatform scanning component (camera preview + scan box overlay + detection callbacks + flashlight control). Pure UI + platform glue, **no ViewModels**, **no navigation**.
2. External modules (e.g. `:shared`) must depend **only on `:feature:scan:api`**, never on `:impl`.
3. `:feature:scan:impl` depends on `:feature:scan:api`, `:core:scan`, `:core:permission`, `:core:navigation`, and `:uiComponent`.
4. `:feature:scan:api` exposes:
   - A navigation route entry referencing `NavRoute.Scan` (already declared in `:core:navigation`).
   - A Koin module that provides `ScanViewModel` and any impl-only bindings (declared in api, defined in impl via `internal` factories called from the module).
5. The Koin module from `:feature:scan:api` is registered by the app at startup.

### 4.2 Navigation

6. `NavRoute.Scan` is the **start destination** of the app.
7. The scan screen is reached without any preceding splash, onboarding, or auth.

### 4.3 Camera + scanning component (`:core:scan`)

8. `:core:scan` exposes a single `@Composable` (working name: `CodeScanner`) with the following contract (final naming may vary, but parameters must be present):
   - `modifier: Modifier`
   - `enabled: Boolean` — when false, the camera is released and the preview is blanked.
   - `flashEnabled: Boolean` — controls torch state.
   - `cooldownMillis: Long = 2_000` — minimum time between two consecutive `onDetected` callbacks (default 2 s).
   - `onDetected: (ScannedCode) -> Unit` — invoked on each successful detection that passes the cooldown.
   - `onError: (ScanError) -> Unit` — invoked when the camera fails to start, frames cannot be analysed, or hardware is missing.
9. `ScannedCode` is a public data class in `:core:scan` containing:
   - `kind: CodeKind` — `QR` or `BARCODE`.
   - `format: CodeFormat` — the specific format (see §4.4).
   - `rawValue: String` — the decoded text payload.
10. `ScanError` is a public sealed type in `:core:scan` containing at least:
    - `CameraUnavailable` (no camera hardware / cannot be opened),
    - `AnalysisFailed(cause: Throwable?)`.
11. The component must:
    - Show a full-screen camera preview behind any overlay supplied by the caller.
    - Restrict detection to frames that intersect the centered square scan window (the caller may draw the overlay; analysis must constrain itself to the same region).
    - Detect **continuously** while `enabled = true`.
    - Apply the 2-second cooldown so the same code (or any code) cannot fire `onDetected` more than once per cooldown window.
    - Toggle torch in response to `flashEnabled` if the device supports it; silently no-op if not.
    - Release camera resources when removed from composition or when `enabled = false`.
12. The component must be **portrait-only** in v1. Landscape behavior is undefined.
13. The component must implement Android and iOS targets. iOS may remain untested by the requester but must compile and use a sensible iOS camera + Vision/AVFoundation-backed implementation. Document any iOS-specific opt-ins in code.
14. The component must **not** depend on ViewModels, navigation, persistence, or Koin. It only depends on Compose Multiplatform + platform camera/scanning APIs.

### 4.4 Supported formats

15. The `CodeFormat` enum in `:core:scan` must declare the following values in v1 (subset of what platform libraries support; mark unsupported ones as such in the platform impl rather than removing them from the public enum):
    - `QR_CODE`
    - `AZTEC`
    - `DATA_MATRIX`
    - `PDF_417`
    - `CODE_39`
    - `CODE_93`
    - `CODE_128`
    - `EAN_8`
    - `EAN_13`
    - `UPC_A`
    - `UPC_E`
    - `ITF`
    - `CODABAR`
    - `UNKNOWN`
16. `CodeKind` derives from `CodeFormat`:
    - `QR` for `QR_CODE`, `AZTEC`, `DATA_MATRIX`, `PDF_417` (2D matrix codes).
    - `BARCODE` for all 1D codes listed above.
    - `UNKNOWN` formats default to `BARCODE`.
17. **Documentation requirement**: `:core:scan` must include a `README.md` (or top-level KDoc on `CodeFormat`) listing which formats are supported on each platform and how each format maps to `CodeKind`.

### 4.5 Permission flow

18. On first entry to the scan screen, the screen must request `PermissionType.CAMERA` via `core.permission.PermissionDispatcher.requestPermission(PermissionType.CAMERA)`.
19. The screen must observe permission state via `PermissionDispatcher.getPermissionState(PermissionType.CAMERA)`.
20. The screen renders one of these UI states based on permission:
    - `GRANTED` → show the camera preview + scan UI.
    - `SHOW_RATIONALE` → show a rationale message ("Camera access is required to scan codes") + a **"Grant camera access"** button. Tapping the button calls `PermissionDispatcher.requestPermission(PermissionType.CAMERA)` again.
    - `DENIED` (permanent) → show a message explaining that camera access was denied + an **"Open Settings"** button that re-triggers `PermissionDispatcher.requestPermission(PermissionType.CAMERA)`. The existing platform handler routes to system settings after 2 denials on Android; iOS handles its own equivalent.
    - `unknown / no value yet` → show a loading state while the initial request is in flight.
    - The CTA label and message **must differ** between `SHOW_RATIONALE` ("Grant camera access") and `DENIED` ("Open Settings"), so the user understands what to expect.
21. The user must be able to retry permission from the screen any number of times.
22. Only the **camera** permission is requested by this screen.

### 4.6 Screen UI (`:feature:scan:impl`)

23. The screen uses **neobrutalism** components from `:uiComponent` for any non-camera UI (buttons, messages, icon boxes, etc.) and uses the `ProvideScanAppTheme` colors/typography.
24. Visual layout (portrait, top to bottom / overlaid):
    - Camera preview filling the screen behind the controls.
    - Centered square "scan window" overlay drawn over the preview, sized to **65% of the shortest screen edge**. The overlay must visually indicate the scanning region using the design system styling (thick black border + hard shadow consistent with `Modifier.neoBrutalStyle`).
    - The scan window must include a **scanning animation**: a horizontal line that travels vertically up and down inside the box at a steady pace (≈1.5 s per traversal), drawn in the primary accent color from `MaterialTheme.colorScheme`. The animation runs only while the camera is active (`enabled = true` and permission granted).
    - A top row with the **flashlight toggle** (neobrutal icon button), reflecting current torch state.
    - A bottom row / action area with the **"Pick from gallery"** button (placeholder; tapping it is a **no-op** in v1 — it must still render so the layout matches the eventual final UI).
    - When permission is not granted, the camera + overlay are replaced by a centered `NeoBrutalCard` with the rationale/denied message and CTA button (see §4.5).
25. The flashlight icon must be disabled / hidden when the camera is not yet running (no permission, or pre-init state).
26. The "Pick from gallery" button must be disabled / hidden when the camera is not yet running for the same reason.
27. The screen must call its `onCodeDetected` hook (forwarded from the ViewModel) when a code is detected. For v1, the hook does nothing visible (logging is fine); it is the contract for future features.
28. When `ScanError` is reported by `:core:scan`, the screen must render an inline error message inside a `NeoBrutalCard` overlaid on the preview area, with a "Retry" CTA that re-enables scanning. The error card uses the existing error/salmon pastel from `ScanAppPastels` for visual weight.

### 4.7 MVVM contract

29. `ScanViewModel` exposes a single state stream:
    - `val viewState: StateFlow<ScanViewState>`
30. `ScanViewState` is a data class containing at minimum:
    - `permission: ScanPermissionState` (one of `Unknown`, `Granted`, `ShowRationale`, `Denied`).
    - `flashEnabled: Boolean`.
    - `lastDetection: ScannedCode?` (most recent detected code; reset/managed by VM as it sees fit; not used to render in v1 but available).
    - `error: ScanError?` (last camera/scan error to render an inline message; null when none).
31. The screen composable collects `viewState` via `collectAsStateWithLifecycle()`.
32. The screen sends user input through exactly one function:
    - `fun onInteraction(interaction: ScanInteraction)`
33. `ScanInteraction` is a `sealed interface` (in `:feature:scan:impl`) with at least these cases:
    - `ToggleFlashlight`
    - `RequestCameraPermission`
    - `OpenGalleryPicker` (placeholder; VM no-ops in v1)
    - `CodeDetected(code: ScannedCode)`
    - `ScanFailed(error: ScanError)`
    - `RetryAfterError`
    - `Resumed` / `Paused` (lifecycle hooks the screen forwards if needed for camera control)
34. The ViewModel is provided by Koin via the `:feature:scan:api` module.

### 4.8 Behavior matrix

35. The screen must remain responsive and not crash when:
    - Permission is denied repeatedly.
    - The user backgrounds and foregrounds the app (camera releases and re-acquires).
    - The device has no camera or no torch.
    - A detection fires during a cooldown (must be ignored silently).

## 5. Non-Goals (Out of Scope for v1)

- Saving scan history or any persistence of scanned codes.
- Scan collections, sequential / batch scanning workflows.
- Deep linking from URL payloads, opening URLs, copy-to-clipboard from detection.
- Navigation to a "scan result" screen.
- Haptic feedback and audio cues on detection.
- Analytics / telemetry.
- Gallery image scanning (only the placeholder button).
- Landscape support.
- Multi-camera switching, zoom controls, focus tap.
- Onboarding/splash before the scan screen.
- iOS manual QA. Implementation is required, but the requester will not validate it in v1.

## 6. Design Considerations

- The UI must adhere to the **neobrutalism** style already established in `:uiComponent` (thick black borders, hard offset shadow, pastel surfaces, uppercase labels via the existing `NeoBrutalButton`).
- Reuse:
  - `NeoBrutalButton` for the "Grant camera access" CTA and any rationale buttons.
  - `NeoBrutalIconBox` / `NeoBrutalFloatingActionButton` for flashlight and gallery controls (whichever fits visually).
  - `NeoBrutalCard` for the permission-denied / rationale message container.
  - `Modifier.neoBrutalStyle` / `neoBrutalShadow` for the scan window overlay border.
- Colors should come from `MaterialTheme.colorScheme` / `ScanAppPastels`. No hard-coded hex values in the screen.
- The scan window overlay should not block the camera preview (only its border is drawn; the interior is transparent).

## 7. Technical Considerations

### Module graph

```
:androidApp / :designShowcase / iOS host
            │
            ▼
        :shared ──────────────────────────────────► :feature:scan:api
                                                          │
                                                          ▼
                                                  :feature:scan:impl
                                                          │
                          ┌───────────────────────────────┼───────────────────────────┐
                          ▼                               ▼                           ▼
                     :core:scan                    :core:permission              :uiComponent
                                                          │
                                                          ▼
                                                   :core:persistence (existing)
```

`:core:navigation` is also a dependency of both `:feature:scan:api` (for `NavRoute.Scan`) and `:feature:scan:impl`.

### KMP targets (apply the existing pattern in the repo)

All new modules (`:feature:scan:api`, `:feature:scan:impl`, `:core:scan`) must declare iOS targets **unconditionally** (`iosArm64`, `iosSimulatorArm64`) — not gated by `OperatingSystem.current().isMacOsX`. This matches `:shared`, `:uiComponent`, and the recently corrected `:core:navigation`, `:core:permission`, and `:core:persistence` modules, and avoids `appleMain` resolution failures on Windows hosts.

### Android camera/scan implementation

- Use **CameraX** (`androidx.camera.*`) for the preview and `ImageAnalysis` use case.
- Use **ML Kit Barcode Scanning** (`com.google.mlkit:barcode-scanning`) for decoding. ML Kit supports all formats listed in §4.4 and exposes a clean async API; it is the chosen library for v1.
- The torch is toggled through `Camera.cameraControl.enableTorch(...)`.
- Camera lifecycle is bound to the composition lifecycle (release on dispose, re-bind on resume).

### iOS camera/scan implementation

- Use `AVCaptureSession` with `AVCaptureMetadataOutput` for QR/barcode metadata detection (supports the same formats natively).
- Alternative: use the Vision framework if richer detection is needed; AVFoundation's metadata output is sufficient for v1.
- Torch via `AVCaptureDevice.setTorchMode`.
- Mark iOS files with `@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)` as needed.

### Permission integration

- Reuse `PermissionDispatcher`, `PermissionType.CAMERA`, `PermissionState`, and the existing `PermissionHandler` / `PermissionChecker` / `PermissionRequester` machinery from `:core:permission`. Do not introduce a parallel permission system.

### State + lifecycle

- The ViewModel must **not** hold references to Android `Context` or iOS objects. Camera control flows through the `:core:scan` composable controlled by `viewState.flashEnabled`, etc.
- The screen calls `onInteraction(ScanInteraction.Resumed/Paused)` from `LifecycleEventEffect` (or equivalent) so the VM can update internal state if needed; the actual camera release is handled inside `:core:scan` reacting to `enabled`.

### Cooldown enforcement

- The 2-second cooldown is enforced **inside `:core:scan`** (closest to the raw detections) so all consumers benefit.
- The cooldown applies to all detections, not just duplicates of the same code (per §4.3.11).
- The cooldown timer **resets** whenever the camera is released (e.g. `enabled = false`, screen backgrounded, navigation away). The first detection after the camera resumes will always fire `onDetected`.

## 8. Success Metrics

1. On Android, opening the app from cold start shows the scan screen and, after granting permission, detects a QR code and a 1D barcode (e.g. EAN-13) within a reasonable distance/lighting.
2. Denying permission shows the rationale UI, and tapping the CTA re-prompts (or routes to settings on permanent denial).
3. Toggling the flashlight changes the device torch state on devices that have one.
4. `:feature:scan:impl` is **not** referenced by any module other than `:feature:scan:api` (verified by inspecting Gradle dependencies).
5. `:core:scan` compiles for `androidMain`, `iosArm64`, and `iosSimulatorArm64` without errors. iOS runtime testing is deferred.
6. Detections fire `onDetected` at most once per 2 seconds while a code is in view.

## 9. Open Questions

_None — all v1 design questions have been resolved with the requester. Decisions recorded above:_

- Android decoder: **ML Kit Barcode Scanning**.
- Gallery placeholder tap: **no-op**.
- Permission CTA copy: **differs by state** ("Grant camera access" for `SHOW_RATIONALE`, "Open Settings" for `DENIED`).
- Scan window size: **65%** of the shortest screen edge.
- Scan window visual: **neobrutal box** + a horizontal **scanning line animation** in the accent color.
- Scan errors: rendered inline in a `NeoBrutalCard` with a Retry CTA.
- Cooldown timer: **resets** when the camera is released; first detection after resume always fires.
