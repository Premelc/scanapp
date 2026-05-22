# ViewModel Design Guidelines

This document defines how ViewModels should be designed in this codebase going forward.

Reference implementation:
- `feature/scan/impl/src/commonMain/kotlin/com/domelabs/scanapp/feature/scan/impl/ScanViewModel.kt`
- `feature/scan/impl/src/commonMain/kotlin/com/domelabs/scanapp/feature/scan/impl/ScanViewState.kt`
- `feature/scan/impl/src/commonMain/kotlin/com/domelabs/scanapp/feature/scan/impl/ScanInteraction.kt`

---

## 1) Core Pattern

Each feature ViewModel should follow this pattern:

1. **Single public UI state**
   - Expose exactly one `viewState: StateFlow<FeatureViewState>`.
2. **Multiple internal mutable state flows**
   - Keep each mutable concern in its own `MutableStateFlow`.
   - Example: permission, loading, selected item, error, toggles.
3. **Combine to produce `viewState`**
   - Build `viewState` via `combine(...)`.
   - Convert to hot state with `stateIn(...)`.
4. **Single interaction entrypoint**
   - UI sends input only through `onInteraction(interaction: FeatureInteraction)`.
5. **No direct UI mutation in composables**
   - Composables emit interactions only; ViewModel owns state transitions.

---

## 2) Why This Pattern

- Keeps each state concern isolated and easy to reason about.
- Avoids overusing `copy(...)` on one giant mutable UI state.
- Makes transitions explicit and testable.
- Scales better as features grow (no giant reducer with one mutable object).
- Works cleanly with Compose + `collectAsStateWithLifecycle()`.

---

## 3) Implementation Rules

### 3.1 State

- `ViewState` should be a plain immutable data class.
- Internal mutable flows should be private.
- Use clear names:
  - `permissionState`
  - `isLoading`
  - `selectedFilter`
  - `errorState`
  - etc.

### 3.2 Interaction API

- Use a `sealed interface` for interactions.
- Interaction names should represent user/system intent, not UI widgets:
  - Good: `RequestCameraPermission`, `RetryAfterError`, `CodeDetected`.
  - Avoid: `OnButton1Clicked`.

### 3.3 Side effects

- Side effects live in ViewModel (`viewModelScope.launch { ... }`).
- Prefer dedicated functions for longer side-effect blocks.
- Keep `onInteraction` readable; delegate if branches grow.

### 3.4 Flow lifecycle

- Use:
  - `stateIn(viewModelScope, SharingStarted.WhileSubscribed(...), initialValue)`
- Prefer `WhileSubscribed` with a small stop timeout for UI state.

### 3.5 Logging / debug

- No `println` in production ViewModel code.
- If needed, use a project logger abstraction.

---

## 4) Scan Example (Applied)

`ScanViewModel` currently follows this standard:

- Internal private mutable flows:
  - permission
  - flash toggle
  - latest detection
  - error
  - scanner activity
- Public state:
  - `viewState` from `combine(...)`
- Inputs:
  - `onInteraction(ScanInteraction)`
- External stream integration:
  - camera permission updates are observed and mapped into `permissionState`.

This is the baseline pattern for new features.

---

## 5) Template for New Features

```kotlin
class FeatureViewModel(
    // dependencies
) : ViewModel() {

    private val stateA = MutableStateFlow(...)
    private val stateB = MutableStateFlow(...)
    private val errorState = MutableStateFlow<FeatureError?>(null)

    val viewState: StateFlow<FeatureViewState> = combine(
        stateA,
        stateB,
        errorState,
    ) { a, b, error ->
        FeatureViewState(
            a = a,
            b = b,
            error = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = FeatureViewState(),
    )

    fun onInteraction(interaction: FeatureInteraction) {
        when (interaction) {
            is FeatureInteraction.SomeAction -> {
                // mutate internal state flow(s)
            }
            FeatureInteraction.Refresh -> {
                viewModelScope.launch {
                    // side effect
                }
            }
        }
    }
}
```

---

## 6) Do / Don't

### Do
- Keep a single public `viewState`.
- Keep mutable pieces split by concern.
- Use one interaction entrypoint.
- Keep business logic in ViewModel, not Composable.

### Don't
- Expose multiple unrelated public state streams to UI.
- Mutate one huge `_viewState` for every tiny change by default.
- Trigger side effects directly from UI without interaction.
- Let composables contain decision logic that belongs in ViewModel.

---

## 7) Adoption Checklist (for each new ViewModel)

- [ ] `FeatureViewState` exists and is immutable.
- [ ] `FeatureInteraction` sealed interface exists.
- [ ] `onInteraction(...)` is the only UI input API.
- [ ] Internal mutable concerns are split into separate private flows.
- [ ] `viewState` is built via `combine(...)` + `stateIn(...)`.
- [ ] UI collects state using `collectAsStateWithLifecycle()`.
- [ ] No debug prints remain.

