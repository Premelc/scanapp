# PRD: Scan Snackbar + Item Details (In Scan Module)

## 1. Introduction / Overview

After a successful scan, users need a clear confirmation UI and a fast way to inspect the scanned item.  
This feature adds:

1. A persistent, swipe-dismissible snackbar shown when an item is successfully scanned.
2. A snackbar action (`Details`) that opens a scan item details page.
3. A details page (inside the scan feature module) that displays all available metadata for the scanned item.

The goal is to improve scan confidence and provide immediate visibility into scanned content.

## 2. Goals

1. Notify users every time an accepted scan occurs.
2. Allow one-tap navigation from snackbar to item details.
3. Visually distinguish QR vs barcode scan notifications.
4. Ensure details page includes complete item metadata for v1.
5. Preserve continuous scanning behavior (scanner must never pause because of this feature).

## 3. User Stories

1. As a user, I want to see a clear confirmation when a scan succeeds so I know it worked.
2. As a user, I want a `Details` action on the confirmation snackbar so I can inspect the scanned item quickly.
3. As a user, I want to recognize whether I scanned a QR code or barcode from the snackbar appearance.
4. As a user, I want the details page to show all available scan fields so I can decide what to do next.

## 4. Functional Requirements

### 4.1 Snackbar trigger and lifecycle

1. Snackbar must appear only for **accepted scans** (not deduped/ignored scans).
2. Snackbar must not appear for scan failures or suppressed scan events.
3. Snackbar behavior:
   - Persistent (does not auto-dismiss on timeout).
   - Swipe-to-dismiss enabled.
   - Replaced by newest snackbar if another accepted scan occurs while current one is visible.
4. Snackbar action button text must be exactly `Details`.
5. Tapping `Details` must dismiss the snackbar and navigate to item details screen.

### 4.2 Snackbar content and visual requirements

6. Snackbar title must be:
   - `QR code successfully scanned` for QR scans.
   - `Barcode successfully scanned` for barcode scans.
7. Snackbar must include a type icon:
   - Generic QR icon for QR scans.
   - Generic barcode icon for barcode scans.
8. Snackbar must visually differentiate QR and barcode:
   - Different label/title color treatment between types.
   - No strict design-token requirement in v1.
9. Snackbar subtitle must display only `rawValue`.
10. Subtitle layout constraints:
   - Maximum of 1 line.
   - Ellipsis overflow when text exceeds available width.

### 4.3 Details page behavior

11. Details page must be implemented inside the scan feature module (for now).
12. Details page must display **all available data** related to the scanned item (at minimum):
   - Raw value.
   - Code kind (QR/BARCODE).
   - Code format.
   - Any additional scan metadata available in current model/context.
13. Navigating back from details returns to scan screen.
14. Opening details must **not** pause scanner behavior logic.

### 4.4 Scanner behavior constraint (strict)

15. This feature must not introduce scanner pausing/resuming behavior.
16. Snackbar visibility, snackbar action, and details navigation must not toggle scanner active state.
17. Any prior/legacy scanner-pause pattern is out of scope for this feature and must not be used.

### 4.5 Navigation integration

18. Add a scan-details navigation destination in scan feature routing.
19. Snackbar `Details` action must navigate to that destination with the selected scanned item payload (or stable item reference).
20. Navigation must be robust if user dismisses snackbar manually (no navigation side effect on dismiss-only).

## 5. Non-Goals (Out of Scope)

1. Save/share/export actions from details page.
2. Editing scanned item details.
3. Item collection management from details page.
4. Advanced icon design; generic icons are sufficient in v1.
5. New domain behavior beyond notification + read-only details presentation.

## 6. Design Considerations

1. Snackbar should feel distinct from generic app toasts/alerts.
2. Title and subtitle hierarchy should prioritize readability.
3. `Details` action must remain visible and tappable with long raw text values.
4. Details screen can be simple/read-only but must present complete scan metadata clearly.

## 7. Technical Considerations

1. Snackbar emission should occur from accepted-scan event path only.
2. Replace-current-snackbar behavior should be deterministic under rapid scan events.
3. If route arguments are constrained, pass serializable scan model fields needed to render details.
4. Keep implementation within scan module boundaries as requested.

## 8. Success Metrics

1. Accepted scan always produces exactly one snackbar notification.
2. Dedupe-suppressed scan produces no snackbar.
3. Snackbar shows correct title/icon/type styling for QR vs barcode.
4. Subtitle always remains one line with ellipsis for long values.
5. `Details` action opens details page and dismisses snackbar.
6. Details page shows complete item metadata fields.
7. Scanner behavior remains continuous (no pause/resume changes introduced by this feature).

## 9. Open Questions

None for v1. Current scope and behavior are fully specified.
