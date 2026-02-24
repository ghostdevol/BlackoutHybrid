# BlackoutHybrid Architecture
Public Overview — Non‑Classified

BlackoutHybrid is the Android-native wrapper for the Blackout ecosystem.
It provides a minimal, hardened interface layer that communicates with the
private Blackout Wallet Engine.

This document describes the public-facing structure of the wrapper only.

---

# 1. High-Level Structure

BlackoutHybrid consists of three major layers:

1. UI Shell (Android)
2. Native Bridge Layer
3. Blackout Engine Interface (private)

Only the UI Shell and Bridge Layer exist in this repository.

---

# 2. Module Breakdown

## 2.1 UI Shell
- Android Activities / Fragments
- Minimal UI for user interaction
- No sensitive logic
- No key handling
- No storage of private data

## 2.2 Bridge Layer
- Connects UI events to the private Blackout Engine
- Enforces zero-retention discipline
- Handles ephemeral buffers
- Performs deterministic teardown after each operation

## 2.3 Private Engine (Not Included)
The following components are NOT part of this repository:
- Cryptographic routines
- Key lifecycle management
- Secure memory handling
- Mediation controllers
- Execution boundary logic
- State machine definitions

These remain private and proprietary.

---

# 3. Data Flow (Public Summary)

User Action → UI Shell → Bridge Layer → Blackout Engine → Response → Teardown

No data persists between operations.

---

# 4. Security Boundaries

The wrapper enforces:
- No telemetry
- No analytics
- No logging
- No remote calls for sensitive operations
- No key export
- No state retention

The engine enforces:
- Zero-retention memory
- Deterministic teardown
- Local-only execution
- Adversarial shutdown handling

---

# 5. Build System

BlackoutHybrid uses:
- Gradle (Android)
- Native AAPT2 pipeline
- Modular build structure

No external dependencies are required for core functionality.

---

End of public architecture overview.
