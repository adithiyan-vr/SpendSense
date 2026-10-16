# SpendSense

> **Track every expense. Know what was needed. Cut what wasn't.**

SpendSense is a high-performance, offline-first, military-grade private native Android expense and cash-flow tracker designed to split every transaction into **Need (Required)** and **Waste (Non-Required / Avoidable)**.

Built with **Kotlin 2.0**, **Jetpack Compose (Material 3)**, **SQLCipher**, **Google Tink Keystore Cryptography**, and **Clean Architecture**, SpendSense guarantees 100% data sovereignty on your device with **zero internet permissions**, **zero third-party telemetry**, and exact 64-bit integer arithmetic.

---

## 🌟 What's New in v1.1.0

SpendSense v1.1.0 introduces holistic cash-flow tracking, database-at-rest encryption, biometric app lock, interactive charts, and native A4 PDF financial reporting:

- 💰 **Income & Cash Flow Ledger:** Record multi-stream incomes (Salary, Pocket Money / Allowance, Freelance, Stipend, Investments, Gifts, etc.) alongside expenses with 14 pre-seeded sources.
- 💳 **Available Balance & Savings Rate:** Live balance calculation (`Opening Balance + Income - Expenses`), Net Savings, and Savings Rate % (`Net Savings / Income × 100`) with burn-rate days-left estimation.
- 🔒 **Database-at-Rest Encryption (SQLCipher):** Transparent whole-database encryption using SQLCipher (256-bit AES) with master keys secured by Android Keystore and Google Tink AEAD.
- 🛡️ **App Lock & Biometrics:** Salted PBKDF2-HMAC-SHA256 PIN authentication with `androidx.biometric` BiometricPrompt (fingerprint / face unlock), graduated lockout delays, and root UI composition gating.
- 🗄️ **Encrypted Local Backups (`.ssbackup`):** Password-protected AES-256-GCM encrypted backup files with PBKDF2 key derivation (100,000 iterations) and live entropy strength meters.
- 📊 **Dynamic Charts Suite:** Custom Canvas Donut/Pie charts with Hare-Niemeyer 100% rounding, multi-series straight-line trend charts (Income vs Expense), and chart type switcher (`[ Pie | Line | Bar ]`).
- 📄 **Native A4 PDF & CSV Reports:** Export formatted, print-ready PDF financial summaries and RFC-4180 CSV spreadsheets via Android Storage Access Framework (SAF) and secure `FileProvider` share sheets.
- 🎨 **Pure Black AMOLED & Dynamic Color:** True pitch-black `#000000` dark mode for OLED power saving and Android 12+ wallpaper-sampled Material You dynamic themes.
- 👁️ **Screen & Shoulder-Surfing Privacy:** `FLAG_SECURE` window protection, lockscreen notification privacy (`VISIBILITY_PRIVATE`), and a quick hide-amounts toggle (`••••`) on the Home dashboard.
- 📂 **Grouped Category Architecture (Schema v2):** 45 pre-seeded categories across 9 distinct life groups with live search, collapsible headers, and recent/frequent shortcuts.

---

## 🚀 Key Features

### 1. The Need vs Waste Decision Framework
- Every expense is intentionally categorized:
  - **Need (Required):** Groceries, utilities, rent, medicine, transit, mandatory education fees.
  - **Waste (Non-Required / Avoidable):** Impulse buys, redundant subscriptions, luxury treats, avoidable late fees.
- Integrated **"Decision Test"** prompt: *"If I skipped this for a month, would my studies, health, or basic living suffer?"*
- Computes your **Waste Ratio** (`Waste Total / Total Spend × 100`) and highlights **Clean Days** (days with zero waste).

### 2. Cash Flow, Incomes & Balance Tracking
- **Multi-Stream Incomes:** Log earnings across salary, stipends, freelance payouts, pocket money, business dividends, and gifts.
- **Personalized Lifestyle Profiles:** Choose between **Student**, **Working Professional**, or **Other** to tailor source suggestions, terminology (e.g. Pocket Money vs Salary), and intelligent insight algorithms.
- **Opening Balance:** Set initial cash-in-hand or bank balance during onboarding or in Settings to maintain an exact running balance.
- **Speed Dial FAB:** Dual-action bottom bar button with animated options to quickly log an Expense or Income.

### 3. Military-Grade Privacy & Offline Security
- **Strictly Zero Internet Permissions:** `android.permission.INTERNET` is completely absent from the Android Manifest. No servers, no analytics, no ads, no cloud sync.
- **SQLCipher 256-bit DB Encryption:** AES-256-CBC database encryption via `net.zetetic:sqlcipher-android:4.6.1`. Raw encryption key is wrapped using Google Tink AEAD backed by Android Keystore.
- **Biometric & PIN App Lock:** Require biometric authentication or a 4–6 digit PIN to open the app. Main app navigation is completely stripped from Compose when locked.
- **Graduated Lockout Protection:** Persistent tracking across app restarts preventing brute-force attacks (5 failed attempts: 30s lockout, up to 1h for 9+).
- **Screen Protection (`FLAG_SECURE`):** Prevents screenshots and masks sensitive numbers in the Android Recent Apps task switcher.
- **Notification Privacy:** Masks transaction amounts in lock-screen summary notifications (`VISIBILITY_PRIVATE`).
- **Encrypted Local Backups (`.ssbackup`):** Portable AES-256-GCM backups with PBKDF2 key derivation (100,000 iterations). Plain JSON backups also supported with full backward compatibility.
- Read our full technical security guarantees in [PRIVACY.md](file:///c:/Users/user/Documents/Personal%20Projects/Expence%20Tracker/PRIVACY.md).

### 4. Advanced Analytics, Charts & Reporting
- **Period Flexibility:** Analyze spending across **Week**, **Month**, and **Custom Date Ranges** with 8 quick presets (*Today*, *Last 7 Days*, *Last 30 Days*, *This Month*, *Last Month*, *Last 3 Months*, *This Year*, *Custom*).
- **Custom Canvas Pie / Donut Chart:** Built using pure Compose Canvas with the Largest Remainder Method (Hare-Niemeyer) guaranteeing integer slice percentages sum to exactly 100%. Interactive tap segment hit-testing via `atan2`.
- **Trend Line Chart:** Multi-series straight-segment line chart comparing Income and Expense trajectories with Indian compact currency notation (`₹1.2K`, `₹3.4L`, `₹1.1Cr`) and interactive touch scrubbers.
- **Interactive Stacked Bar Chart:** Day-by-day and week-by-week visual breakdown of Need vs Waste.
- **Accessible Data Table Fallback:** TalkBack-optimized tabular representation for all charts.
- **Native A4 PDF Report Generator:** Multi-page PDF reports with SpendSense branding, financial metrics, Need vs Waste ratio bar, category tables, and insight highlights.
- **RFC-4180 CSV Export:** Standard spreadsheet exports with configurable personal notes filtering (defaulting to OFF for privacy).

### 5. Categories & Dual-Limit Budgets
- **45 Categorized Defaults:** Structured into 9 life groups (*Food & Drink*, *Transport*, *Education & Work*, *Housing & Bills*, *Health & Personal*, *Lifestyle & Fun*, *Money & Obligations*, *Family & Pets*, *Other*).
- **Full Category Customization:** Add, recolor, iconify, edit, and archive categories.
- **Dual-Limit Monthly Budgets:** Set an overall monthly spending ceiling plus an optional separate cap on Waste spending.
- **Proactive Warnings:** Automatic badges and progress indicators when reaching 80% and 100% thresholds.

### 6. Local Notifications & Background Maintenance
- Scheduled offline via **WorkManager**:
  - Daily evening reminder to log transactions.
  - Weekly and monthly habit digest notifications.
  - 30-day backup hygiene notification.
  - Automatic purging of soft-deleted transactions older than 30 days.

---

## 🛠️ Architecture & Tech Stack

SpendSense is engineered following official Android Architecture Guidelines and strict Clean Architecture principles:

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   Jetpack Compose · Material 3 · Navigation · ViewModels│
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                      Domain Layer                      │
│   Pure Kotlin · Use Cases · Models · AnalyticsEngine  │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                       Data Layer                       │
│  SQLCipher DB · Tink Keystore · DataStore · SAF Backup │
└────────────────────────────────────────────────────────┘
```

| Component | Technology | Description |
|---|---|---|
| **Language** | Kotlin 2.0.21 | Modern, expressive, type-safe language |
| **UI Toolkit** | Jetpack Compose (BOM 2024.10.01) | Declarative UI with Material Design 3 tokens |
| **Dependency Injection** | Dagger Hilt 2.51.1 | Inversion of control across all app layers |
| **Database Encryption** | SQLCipher 4.6.1 | Full database AES-256 encryption at rest |
| **Key Management** | Google Tink 1.15.0 + Keystore | AEAD envelope encryption for master keys & PINs |
| **Local Storage** | Room 2.6.1 (Schema v2) | SQLite relational database with KSP schema tracking |
| **Preferences** | Jetpack DataStore | Reactive, asynchronous key-value persistence |
| **Biometrics** | AndroidX Biometric 1.2.0-alpha05 | BiometricPrompt authentication wrapper |
| **PDF Engine** | Android `PdfDocument` | Native vector-rendered A4 financial report generation |
| **Background Work** | WorkManager 2.10.0 | Battery-friendly background task scheduling |
| **Serialization** | Kotlinx Serialization 1.7.3 | Multi-platform, reflection-free JSON serialization |
| **Testing** | JUnit 4, MockK, Turbine, Coroutines Test | 236 unit tests covering all domain, data, and UI logic |
| **Min SDK / Target SDK** | Android 8.0 (API 26) / Android 15 (API 35) | Wide compatibility with modern Android 15 features |

---

## 🚀 Building & Testing

### Prerequisites
- **JDK 21 LTS** (e.g. Eclipse Adoptium Temurin 21)
- **Android SDK Platform 35** and Build Tools

### Running Tests
Execute the complete unit test suite (236 tests covering domain logic, analytics, cryptographic managers, Room migrations, and ViewModels):
```bash
.\gradlew.bat testDebugUnitTest
```

### Static Analysis & Android Lint
Run static code analysis with 0 errors:
```bash
.\gradlew.bat lintDebug
```

### Building Debug APK
```bash
.\gradlew.bat assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Building Signed Release APK (R8 Optimized)
```bash
.\gradlew.bat assembleRelease
```
Outputs:
- Primary output: `app/build/outputs/apk/release/app-release.apk`
- Ready distribution copy: `release/SpendSense-v1.1.0-signed.apk`

---

## 📱 Sideloading & Installation

To install the signed release APK onto a connected Android device or emulator:
```bash
adb install -r release/SpendSense-v1.1.0-signed.apk
```

---

## 👥 Organization & Credits

| Role | Details |
|---|---|
| **Organization / Publisher** | **Supernovea** |
| **Lead Developer & Designer** | **Adithiyan V R** (UG student, CSE – Data Science) |
| **Product Concept** | Adithiyan V R |
| **Copyright** | © 2026 Supernovea. All rights reserved. |

*Built with passion by Adithiyan V R at Supernovea.*
