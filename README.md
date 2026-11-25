# PAPIFI Android

PAPIFI is a multi-currency fintech app for sending, receiving, holding and exchanging money across borders. This is its mobile client: Kotlin Multiplatform, Jetpack Compose, MVI and Clean Architecture in a modular Gradle project, with a pure-Kotlin domain layer.


## MVP scope

Registration and OTP, profile and KYC, wallets in NGN, USD, GBP and EUR, add money, send money (local, international and PAPIFI-to-PAPIFI), receive money, currency converter, beneficiaries, transaction history and receipts, PIN and two-factor security, notifications.

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, single activity |
| Presentation | MVI: one immutable state per screen, events in, one-off effects out |
| Dependency injection | Koin |
| Async | Coroutines and Flow |
| Build | Gradle (Kotlin DSL), version catalog, convention plugins in `build-logic` |
| Android Gradle Plugin | 9.x with built-in Kotlin |
| Design | Figma "PAPIFI UI", Nunito Sans, green `#14452F` |

## Architecture

The backend is the source of truth for balances and transaction status. The app is a thin client: it never decides a transfer succeeded, never converts currencies itself, and never talks to a payment provider directly.

```text
papifi-android/
├── app/              Application, root navigation, flavors, Koin wiring
├── core/
│   ├── model/        Money, Currency, statuses, quotes (plain Kotlin)
│   ├── common/       AppResult, AppError, idempotency keys (plain Kotlin)
│   ├── designsystem/ Theme, tokens, shared components
│   ├── ui/           MVI base, formatters, secure-screen helper
│   └── network, database, datastore, security, testing
├── feature/<name>/   domain (plain Kotlin) | data | ui
└── build-logic/      Convention plugins
```

Features: auth, kyc, wallet, addmoney, exchange, send, receive, activity, security, notifications, profile, support.

### Layer rules

1. `domain` depends only on `core:model` and `core:common`, and has no Android on its classpath.
2. `data` implements `domain`. `ui` depends on `domain` and never on `data`; `app` connects them through Koin.
3. A feature may use another feature's `domain`, never its `data` or `ui`.
4. DTOs stay in `data`. Unknown backend statuses map to a safe `Unknown` state.

`scripts/check-module-boundaries.sh` enforces these rules and runs in CI.

### Money safety

- Amounts are `Money(minorUnits: Long, currency)`: no floating point.
- One idempotency key per submission, reused on every retry.
- Quotes carry an expiry; the UI disables confirm when one expires.
- Estimated rates and executable quotes are separate types.
- Stale balances are labelled, never shown silently.
- Screens with PINs, OTPs, KYC and balances block screenshots.

## Getting started

Requirements: JDK 17 and an Android Studio release that supports AGP 9.5.

```bash
# 1. Create the Gradle wrapper (once)
gradle wrapper --gradle-version 9.6.0

# 2. Create the remaining module folders
bash scripts/bootstrap-modules.sh

# 3. Add the Nunito Sans font files (see core/designsystem/FONTS.md)

# 4. Build and test
./gradlew assembleDevDebug test

# 5. Check module boundaries
bash scripts/check-module-boundaries.sh
```

## Build variants

| Flavor | Purpose | Application ID suffix |
| --- | --- | --- |
| `dev` | Local development | `.dev` |
| `staging` | Pre-release testing | `.staging` |
| `prod` | Production | none |

API base URLs are placeholders in `app/build.gradle.kts` until the backend environments are confirmed.

## Testing

Unit tests run with `./gradlew test`. Pure-Kotlin modules use JUnit 5; Android modules use JUnit 4 with Turbine. CI also runs lint and treats compiler warnings as errors.

## Roadmap

1. Project skeleton, design system, MVI base (this scaffold)
2. Network layer, token handling, secure storage
3. Registration, OTP, login, biometric login
4. Profile and KYC
5. Wallets, converter and quotes
6. Add money, beneficiaries, send, receive
7. History, receipts, notifications
8. Hardening: certificate pinning, Play Integrity, accessibility
