# PRD: Scan Feedback + Settings Screen

## 1. Introduction / Overview

The app should provide immediate user confirmation when a scan succeeds by playing a short vibration and a placeholder sound.  
Users must be able to control these behaviors through a dedicated Settings screen.

This solves the problem of uncertain scan completion and gives users global control over feedback preferences.

## 2. Goals

1. Provide clear confirmation on successful scan via haptics and sound.
2. Allow users to globally enable/disable vibration and sound independently.
3. Add a dedicated Settings navigation destination using a new feature module split (`api` + `impl`).
4. Update scan screen FAB layout to include Settings access while preserving History access.

## 3. User Stories

1. As a scanner user, I want a subtle vibration when a scan succeeds so I know it worked instantly.
2. As a scanner user, I want a short sound when a scan succeeds so I get audible confirmation.
3. As a user, I want to disable vibration and/or sound if I do not want that feedback.
4. As a user, I want these preferences to persist and apply globally across the app.
5. As a user, I want quick access to Settings from the scan screen.

## 4. Functional Requirements

### 4.1 Scan success feedback

1. On successful scan registration, the app must trigger optional feedback:
  - Sound (placeholder sound)
  - Vibration (small, weak, short pulse)
2. Feedback must trigger only when a scan is successfully accepted and recorded by feature logic.
3. If a scan is suppressed by dedupe rules, feedback must not trigger.
4. Feedback must run only when the scan callback is actually emitted (respecting scanner cooldown behavior).
5. Feedback failures (unsupported hardware, muted device, platform limits) must fail silently without crash or error UI.

### 4.2 Settings state and persistence

1. Two global settings must exist:
  - `soundEnabled: Boolean`
  - `vibrationEnabled: Boolean`
2. Default values on first app launch:
  - `soundEnabled = true`
  - `vibrationEnabled = true`
3. Settings must persist using DataStore.
4. Saved settings must be read and applied at runtime for scan feedback decisions.
5. Settings apply globally, not just one screen/session.

### 4.3 Settings screen

1. Add a new Settings navigation location/screen.
2. Implement Settings as a new feature split:
  - `:feature:settings:api`
    - `:feature:settings:impl`
3. `api` module must expose:
  - feature entry point composable for navigation host
    - Koin module exports required by host app
4. `impl` module must contain screen implementation and internal feature logic.
5. Settings UI for v1 must include only two toggles:
  - Sound toggle
    - Vibration toggle
6. If current platform cannot support one feedback type, its toggle must be hidden (not shown disabled).
7. Toggling a value must immediately update state and persist it.

### 4.4 Scan screen navigation and FAB layout

1. Update top scan-screen FAB row:
  - History FAB on top-left
    - Settings FAB on top-right
2. History drawer behavior remains unchanged except for placement of History FAB.
3. Tapping Settings while history drawer is open must first close history drawer, then navigate to Settings.

### 4.5 Architecture and integration

1. Settings state access must follow existing app layering/conventions (data/domain/presentation).
2. Scan feature must consume global settings state to decide whether to play sound/vibration.
3. Integration must preserve existing scan flow, dedupe logic, and cooldown behavior.
4. New feature modules must be wired through shared app navigation and DI setup.

## 5. Non-Goals (Out of Scope)

1. Custom sound picker.
2. Volume control UI.
3. Vibration intensity customization.
4. Per-code-type feedback customization.
5. Advanced feedback patterns.
6. Any settings beyond the two toggles in this PRD.

## 6. Design Considerations

1. Use existing app design system (`uiComponent`, neobrutal style) for Settings UI controls.
2. Keep controls simple and readable: one row/card per toggle with clear labels.
3. Settings screen should be a full-screen destination, not a drawer.
4. FAB iconography for History and Settings should remain visually consistent with current scan actions.

## 7. Technical Considerations

1. DataStore is the persistence source of truth for feedback settings.
2. Provide a platform abstraction for:
  - play placeholder success sound
  - trigger weak short vibration
3. Platform abstraction should no-op safely when unsupported.
4. Trigger feedback from accepted scan event path only (after dedupe/cooldown acceptance point).
5. Keep Settings feature boundary clean:
  - host depends on `:feature:settings:api`
  - implementation details stay in `:feature:settings:impl`

## 8. Success Metrics

1. On successful non-deduped scan, enabled feedback plays (sound and/or vibration).
2. On deduped scan within dedupe window, feedback does not play.
3. Toggling sound/vibration in Settings changes behavior immediately.
4. Toggled values persist after app restart.
5. Settings is reachable from scan screen via top-right FAB.
6. History remains reachable from top-left FAB.
7. If history is open and user opens settings, history closes first, then settings opens.
8. No crashes on devices lacking vibration/haptic support or where sound playback is unavailable.

## 9. Open Questions

None for v1. All scope and behavior decisions are resolved.