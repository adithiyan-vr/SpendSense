# Security Policy

SpendSense handles personal financial information, so security and privacy reports are taken seriously. This project is maintained by one developer, so responses are best effort.

## Supported versions

| Version | Supported |
|---|---|
| 1.1.x | Yes |
| 1.0.x | No. v1.0.x stores its database without encryption. Please upgrade to 1.1.x |

## Reporting a vulnerability

**Please do not open a public issue for a security problem.**

Use GitHub's private reporting instead: open the repository's **Security** tab and choose **Report a vulnerability**. Include:

- the app version and Android version,
- what you found and why it matters,
- steps to reproduce it,
- any suggested fix.

**Please never include real financial data** (real amounts, notes or backup files) in a report. Use made-up test data.

You can expect an acknowledgement as soon as possible and a fix or explanation after the report has been looked into. Reporters who want credit will be thanked in the changelog.

## In scope

- Weaknesses in the encrypted database, key handling, app lock, PIN storage or lockout.
- The `.ssbackup` encrypted backup format.
- Data leaking through logs, notifications, screenshots, recents previews, exports or shared files.
- Unintended permissions, exported components or network access (the app must not have the INTERNET permission).
- Problems in the data migration from v1.0.x to v1.1.x.

## Out of scope

- Attacks that need a rooted or already malware-infected device.
- An attacker who knows the PIN and has the unlocked phone.
- Photos of the screen.
- Issues in third-party libraries that are already fixed in a newer upstream release (a note that we should upgrade is welcome).

## Design promises

- No INTERNET permission, no analytics, no ads, no crash-reporting SDKs.
- No amounts, notes or category names in logs.
- Android Auto Backup is disabled; backups are user-initiated and can be password-protected.

See [`PRIVACY.md`](PRIVACY.md) for the full privacy and security description, including its honest limits.
