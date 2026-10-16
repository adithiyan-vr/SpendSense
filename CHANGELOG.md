# Changelog

All notable changes to SpendSense are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

Nothing yet. Planned work is listed in the README roadmap.

## [1.1.0] - 2026-10-16

Version code 2. Update 1: income, reports, charts and security.

### Added
- **Income tracking:** add, edit, delete and undo income entries with a source, date and note.
- 14 income sources, ordered for the chosen profile (Student, Working professional, Other), plus custom sources.
- Optional opening balance, **available balance**, net, savings rate, spend rate and days-left estimate.
- Income insights (spending pace, savings-rate improvement, student pocket-money reminder).
- **Custom date range** analysis with presets and a Material 3 date range picker (up to 5 years).
- **PDF (A4) and CSV report export** with Save and Share. Notes are excluded by default.
- Donut chart by category, line chart (income vs expense), chart type switcher, All/Need/Waste filter, mini Need vs Waste donut on Home, and "View as table" for every chart.
- Compact Indian amount formatting (K, L, Cr).
- 45 expense categories in 9 groups (up from 14), with a grouped, searchable category picker and a Recent row.
- 4-step onboarding including profile and opening balance.
- Center "+" speed dial (Add expense / Add income) and launcher shortcuts.
- History tabs (All, Expenses, Income) and daily Spent and Received totals.
- Hide-amounts toggle on Home.
- **Privacy and security:** SQLCipher AES-256 encrypted database with a Keystore-protected Tink-wrapped key, PIN and biometric app lock with persistent lockout, auto-lock timeout, screen protection (`FLAG_SECURE`), private notifications option, encrypted `.ssbackup` export and import, secure wipe, Privacy and Security settings screen, and `PRIVACY.md`.
- New brand identity: vector logo, adaptive launcher icon with Android 13+ themed icon, notification icon, splash screen and animated startup loader.
- Pure black (AMOLED) theme and optional Material You dynamic color.

### Changed
- Home redesigned around the available balance.
- New teal (Need), orange (Waste) and charcoal color palette.
- Database schema upgraded from version 1 to 2 (categories gain `default_key` and `group_key`; new `income_sources` and `incomes` tables).
- JSON backup schema upgraded to version 2 (still reads version 1 backups).
- Android Auto Backup is now disabled; cleartext network traffic is blocked.
- Release APK is larger (about 24.9 MB) because of the encryption libraries.

### Fixed
- Theme switching did not apply the chosen theme; it now has a single source of truth with no wrong-theme flash at startup.
- Home refresh could leave duplicate background collections running.
- Export and share performed file work on the main thread.
- Weekly summary now respects the configured week start day.
- Picking today's date in the Add screen no longer resets the time to midnight.
- Soft-deleted income is purged together with expenses after 30 days.
- Delete all data now also resets income sources, profile and opening balance.
- Sharing a report explicitly grants read access to the receiving app.

### Security
- The existing plaintext database is converted to an encrypted database on first launch, with row-count verification and rollback protection.
- Still no INTERNET permission, no analytics or tracking SDKs, and no logging of amounts, notes or category names.

## [1.0.0] - 2026-10-12

Version code 1. First release.

### Added
- Expense logging with Need or Waste type, categories, notes, payment method and Undo.
- 14 default categories and custom categories.
- History with search and filters; Home dashboard.
- Weekly and monthly analysis with stacked bar chart, table view, top categories, peak day, average per day, Clean Days and insights.
- Monthly budget with optional Waste limit and warnings at 80% and 100%.
- Daily, weekly and monthly reminders; backup reminder.
- CSV export, JSON backup and restore (Merge or Replace); delete all data.
- Settings for currency symbol, week start day and theme.
- Offline-only design with no INTERNET permission; accessibility support; Android 13+ themed icon.
- R8 and resource shrinking (about 2 MB release APK).

### Known issues
- The theme selector did not apply the chosen theme reliably (fixed in 1.1.0).

[Unreleased]: ../../compare/v1.1.0...HEAD
[1.1.0]: ../../compare/v1.0.0...v1.1.0
[1.0.0]: ../../releases/tag/v1.0.0
