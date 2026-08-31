# Yemen Blood Bank (بنك دم اليمن)

A humanitarian, life-saving Android application designed to bridge blood donors and patients across all 22 governorates of Yemen with real-time Supabase cloud synchronization and hospital governance.

[![Android CI](https://github.com/salehbagomri/yemen_blood_bank_app_v2/actions/workflows/android-ci.yml/badge.svg)](https://github.com/salehbagomri/yemen_blood_bank_app_v2/actions/workflows/android-ci.yml)
![Version](https://img.shields.io/badge/version-2.0.0-red)
![Platform](https://img.shields.io/badge/platform-Android%20(Jetpack%20Compose)-blue)
![License](https://img.shields.io/badge/license-Proprietary-red)
![Backend](https://img.shields.io/badge/backend-Supabase-green)

---

## Overview

**Yemen Blood Bank (بنك دم اليمن)** is a modern, native Android application built entirely with **Kotlin** and **Jetpack Compose**. It aims to solve blood shortage emergencies across Yemen by providing a fast, direct, and account-free directory of voluntary blood donors categorized by governorate, sub-district, and blood type.

The platform empowers:
1. **Public Citizens**: Quickly search for compatible donors and connect directly via phone call or WhatsApp, or register as a donor in seconds without cumbersome signup forms.
2. **Hospitals & Medical Centers**: Access dedicated dashboards to oversee governorate blood stock, log donations, suspend donors for standard 6-month resting periods, and generate blood type distribution analytics.
3. **National Administrators**: Centralized control over all 22 governorates, hospitals approval, report verification, interactive home banners, and safety-guarded location management.

---

## Key Features

- **Multi-Filter Donor Search:** Instant lookup across all 8 blood types (`A+`, `A-`, `B+`, `B-`, `AB+`, `AB-`, `O+`, `O-`) with cascading governorate-to-district filters.
- **Direct Contact Shortcuts:** One-tap phone call dialing and pre-formatted WhatsApp messaging.
- **Frictionless Donor Onboarding:** Community members can register as donors without email/password barriers, with Yemeni phone carrier validation (Yemen Mobile, Sabafon, YOU, Y-Telecom).
- **Hospital Governance Dashboard:**
  - Governorate-scoped donor listing and advanced search.
  - One-tap 6-month medical suspension with automatic rest countdown.
  - Donation logging that updates donor status and eligibility dates.
  - Visual blood type ratio charts and analytics.
- **Central Super-Admin Portal:**
  - Complete nationwide CRUD for donors, hospitals, and locations.
  - Invalid/inactive donor report verification with donor matching and approval workflows.
  - Dynamic home banner slider management with drag-and-drop reordering.
  - Safe governorate & district toggle switches with usage checks before deletion.
- **Offline Resilience & Data Caching:** Location metadata cached locally using Jetpack DataStore to ensure instant screen transitions even during weak network connectivity.
- **Modern Medical Design System:** Clean, medical light aesthetics (`#F8F9FA` background with `#FFFFFF` elevated cards and `#E63946` primary accents) with full Right-to-Left (RTL) Arabic typography.

---

## Technology Stack

- **UI Toolkit:** Jetpack Compose (Material 3) with full RTL Arabic typography
- **Language:** Kotlin 2.0+ (Coroutines, StateFlow, Flow)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
- **Backend & Database:** Supabase (PostgreSQL with Row Level Security & RPC functions)
- **Networking & Serialization:** Ktor Client & Kotlinx Serialization
- **Image Loading:** Coil Compose
- **Local Persistence:** AndroidX DataStore Preferences & Encrypted SharedPreferences
- **Navigation:** Jetpack Navigation Compose

---

## Architecture & Project Structure

```
app/src/main/java/com/bagomri/yemenbloodbank/
├── core/
│   ├── constants/       # Color palette (AppColors), Arabic strings (AppStrings), typography
│   ├── datastore/       # DataStore preferences & session management
│   ├── network/         # Supabase client singleton & auth credentials
│   ├── theme/           # Material3 Light ColorScheme, AppShapes, AppTypography
│   └── util/            # DateUtils, IntentUtils, ErrorHandler, PhoneValidator
├── data/
│   ├── model/           # Donor, Hospital, Report, Banner, LocationData data classes
│   └── repository/      # DonorRepository, HospitalRepository, ReportRepository, etc.
└── ui/
    ├── components/      # BloodTypeChip, DonorCard, BannerSlider, CustomTextField, etc.
    ├── navigation/      # Screen routes & AppNavigation NavHost
    └── screens/
        ├── admin/       # Super-admin portal (Donors, Hospitals, Reports, Banners, Locations)
        ├── auth/        # Hospital & Admin login screen
        ├── awareness/   # Donation criteria, health tips, and educational guides
        ├── donor/       # SearchDonorsScreen & AddDonorScreen
        ├── home/        # HomeScreen with interactive banners & action cards
        ├── hospital/    # HospitalDashboard, SuspendedDonors, BloodTypeReports
        ├── info/        # AboutScreen & ContactScreen
        ├── onboarding/  # First-time introductory carousel
        ├── reports/     # ReportDonorScreen for flagging invalid contacts
        └── splash/      # Animated splash screen with white vector logo
```

---

## Getting Started

### Prerequisites

- **JDK 17** or higher
- **Android SDK** (API Level 24 minimum, Target API 34)
- **Android Studio** (Koala / Ladybug or newer recommended)

### Installation & Run

```bash
# Clone the repository
git clone https://github.com/salehbagomri/yemen_blood_bank_app_v2.git
cd yemen_blood_bank_app_v2

# Build and install debug APK directly on your connected device
.\gradlew installDebug
```

---

## Building

```bash
# Generate Debug APK
.\gradlew assembleDebug

# Generate Production Release Bundle (AAB)
.\gradlew bundleRelease
```

Generated outputs:
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release Bundle: `app/build/outputs/bundle/release/app-release.aab`

---

## Project Information

| Property           | Value                                                  |
| ------------------ | ------------------------------------------------------ |
| **Package Name**   | `com.bagomri.yemenbloodbank`                           |
| **Min SDK**        | `24` (Android 7.0 Nougat)                              |
| **Target SDK**     | `34` (Android 14)                                      |
| **Compile SDK**    | `34`                                                   |
| **Primary Language** | Kotlin & Jetpack Compose                             |
| **Database Engine** | Supabase (PostgreSQL 15)                              |
| **Geographic Scope** | All 22 Governorates of the Republic of Yemen          |

---

## Database Schema & Security

The application communicates with a secure Supabase PostgreSQL database governed by Row-Level Security (RLS) policies:

- `donors`: Public read for active donors, hospital-scoped donation logging and suspensions, admin CRUD.
- `hospitals`: Admin-managed hospital accounts and authentication bindings.
- `reports`: Public insert for community flag submissions, admin review and approval.
- `banners`: Public read for active banners, admin reordering via `reorder_banners` RPC.
- `locations`: Cascading governorate and district records with usage integrity safeguards.

---

## Privacy Policy & Terms

The application respects user confidentiality and handles only essential donor contact information voluntarily provided by users.
- [Privacy Policy (Arabic & English)](privacy/index.html)
- [Terms of Service](privacy/terms.html)
- [Account Deletion Guidelines](privacy/delete-account.html)

---

## License

Copyright (c) 2026 Saleh Bagomri. All rights reserved.

This repository is publicly visible for reference and transparency. It is proprietary software, and no rights are granted to use, copy, modify, or redistribute without prior written permission. See [LICENSE](LICENSE) for details.

---

## Contact & Developer

- **Developer:** Saleh Bagomri (صالح باقمري)
- **Website:** [www.bagomri.com](https://www.bagomri.com)
- **Email:** [s.bagomri@gmail.com](mailto:s.bagomri@gmail.com)
- **GitHub:** [@salehbagomri](https://github.com/salehbagomri)
