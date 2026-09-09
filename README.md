# Kotlin Multiplatform (KMP) MVI Calculator

A cross-platform mobile calculator built with **Kotlin Multiplatform (KMP)** showcasing a pure **MVI (Model-View-Intent)** unidirectional data flow architecture.

---

## 1. Project Architecture & Boundaries

```
kmpcalculatordemo/
├── sharedLogic/                              # Shared KMP Module
│   ├── src/commonMain/kotlin/com/ownagebyte/kmpcalculatordemo/
│   │   ├── model/
│   │   │   ├── MathOperation.kt              # Supported operations: ADD, SUBTRACT, MULTIPLY, DIVIDE
│   │   │   ├── ValidationResult.kt           # Validation result holder
│   │   │   └── CalculatorUiState.kt          # Immutable shared UI state
│   │   ├── mvi/
│   │   │   ├── CalculatorIntent.kt           # Explicit user actions / intents
│   │   │   ├── CalculatorStore.kt            # Read-only StateFlow contract
│   │   │   └── DefaultCalculatorStore.kt     # Atomic reducer and intent handler
│   │   └── domain/
│   │       ├── CalculatorValidator.kt        # Pure multiplatform input validation rules
│   │       └── Calculator.kt                 # Pure arithmetic engine & overflow handling
│   └── src/commonTest/kotlin/com/ownagebyte/kmpcalculatordemo/
│       └── CalculatorStoreTest.kt            # 10 comprehensive shared unit tests
├── androidApp/                               # Android Client (Native)
│   └── src/main/java/com/ownagebyte/kmpcalculatordemo/
│       ├── MainActivity.kt                   # ComponentActivity host with edge-to-edge support
│       └── ui/
│           ├── CalculatorViewModel.kt        # Retains Store across configuration changes
│           └── CalculatorScreen.kt           # Jetpack Compose UI (ExposedDropdownMenu, OutlinedTextFields)
└── iosApp/                                   # iOS Client (Native)
    └── iosApp/
        ├── ContentView.swift                 # Native SwiftUI screen (Form, Picker, Buttons)
        └── ObservableCalculatorStore.swift   # Bridges StateFlow to ObservableObject on @MainActor
```

### Architectural Boundaries & Data Flow
* **Shared Layer (`commonMain`):** Owns state definitions, mathematical operations, calculation rules, input sanitation, error generation, and intent handling. It exposes a read-only `StateFlow<CalculatorState>`.
* **Android Layer (`androidApp`):** `CalculatorViewModel` retains the `CalculatorStore` across configuration changes. `CalculatorScreen` collects `store.state` via `collectAsStateWithLifecycle()` and forwards user keystrokes, dropdown selections, and button clicks as typed `CalculatorIntent` actions.
* **iOS Layer (`iosApp`):** `ObservableCalculatorStore` listens to the shared `StateFlow` via a Kotlin/Native flow collector, routes emissions safely to SwiftUI's main thread using `@MainActor`, and tears down collection during `deinit`. Two-way bindings dispatch typed intents to the shared store without holding local state.

---

## 2. Chosen Number Type & Result Formatting Decisions

* **Number Type:** Calculations are computed using floating-point numbers (`Double`).
  * *Rationale:* Provides 53 bits of significand precision (~15–17 decimal digits), sufficient for a four-function calculator without introducing third-party multiplatform arbitrary-precision libraries.
* **Validation & Overflow Protection:**
  * Inputs are trimmed of leading and trailing whitespace.
  * Negative numbers (`-2.5`) and fractional values (`7.2`) are parsed via `toDoubleOrNull()`.
  * Division by zero is explicitly checked (`parsedSecond == 0.0`), covering both `0` and `-0`, emitting `"Cannot divide by zero"`.
  * Numerical outcomes resulting in `Infinity` or `NaN` map to the required error message: `"Result is too large"`.
* **Result Formatting:**
  * Whole numbers discard unnecessary decimal fractions (e.g., displaying `5` instead of `5.0`).
  * Fractional numbers preserve non-zero decimal output (e.g., `7 ÷ 2` formats as `3.5`).
* **Stale Output Clearance:** Any modification to either input field or the selected operation clears the previous `result` and removes the error associated with that field.

---

## 3. Third-Party Libraries Used

This repository uses **zero unnecessary third-party architectural frameworks**. The MVI flow and multiplatform concurrency are built using official platform primitives:

* **`org.jetbrains.kotlinx:kotlinx-coroutines-core`:** Provides reactive flow primitives (`StateFlow`, `MutableStateFlow`) and structured concurrency.
* **`org.jetbrains.kotlin:kotlin-test`:** Provides native assertions test control inside `commonTest`.
* **`androidx.lifecycle:lifecycle-runtime-compose` & `lifecycle-viewmodel-compose`:** Enables lifecycle-aware state observation (`collectAsStateWithLifecycle`) and ViewModel scoping on Android.
* **`androidx.compose.material3:material3`:** Native Android UI widgets (`OutlinedTextField`, `ExposedDropdownMenuBox`, `Button`).
* **Apple Foundation & SwiftUI:** Standard iOS frameworks used directly for the iOS consumer.

---

## 4. How to Run Shared Tests

The test suite located under `sharedLogic/src/commonTest` verifies production business logic without mocking or repeating algorithms. It exercises all 10 mandatory cases: addition, subtraction, multiplication with negative numbers, division with decimal outputs, empty inputs, invalid formats, missing operations, division by zero, MVI state mutations, and stale result/error clearing.

Execute tests via Gradle in your terminal:

```bash
# Run tests across all available targets
./gradlew :sharedLogic:allTests

# Or run tests specifically on the JVM test runner
./gradlew :sharedLogic:testDebugUnitTest
```

---

## 5. How to Run Applications

### Android
* Requirements: Android Studio (Ladybug or newer), JDK 17+, Android SDK (API 26+).
* Build via CLI:
  ```bash
  ./gradlew :androidApp:assembleDebug
  ```
* In Android Studio: Select **androidApp** from the run configurations dropdown and press **Run** (`Shift + F10`) to deploy to a connected device or emulator.

### iOS
* Requirements: macOS workstation with Xcode 15+ installed.
* Open `iosApp/iosApp.xcodeproj` in Xcode.
* Select an active iOS Simulator (e.g., iPhone 15 Pro, iOS 17+).
* Press **Cmd + R** to compile Kotlin/Native frameworks and launch the SwiftUI consumer.

---

## 6. Known Limitations & Environment Constraints

* **Host Machine Constraint (macOS):** Compilation of the native iOS binary target (`iosApp`) and running the iOS Simulator requires Xcode on macOS. The shared KMP logic and the complete Jetpack Compose Android client were built, inspected, and verified locally on Linux/Windows. The iOS consumer was authored using clean Swift concurrency (`ObservableObject`, `@MainActor`, `FlowCollector`) against the Kotlin/Native API specification.
* **Number Boundary Ceiling:** Calculations approaching or exceeding 1.79e+308 overflow to `Double.POSITIVE_INFINITY` and return the user-facing error `"Result is too large"` rather than calculating arbitrarily large digits.

---

## 7. Screenshots

### Android Screenshots

| Successful Calculation | Validation Errors (Multiple Fields) |
| :---: | :---: |
| <img width="367" height="776" alt="Screenshot_20260910_022638" src="https://github.com/user-attachments/assets/73d3d89f-5306-40e4-9361-b6c4c6e31a27"/> | <img width="367" height="776" alt="Screenshot_20260910_014716" src="https://github.com/user-attachments/assets/ee09a787-f358-4681-990a-058ba321d69c" /> |

