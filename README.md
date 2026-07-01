# MLL Virtual Closet

> **A dual-user Android outfit-matching app for couples.**  
> Upload your wardrobe, let your partner decide what goes together.

[![Kotlin](https://img.shields.io/badge/kotlin-1.8-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/API-33+-34A853?logo=android)](https://developer.android.com)
[![Firebase](https://img.shields.io/badge/Firebase-BaaS-FFCA28?logo=firebase)](https://firebase.google.com)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.4-4285F4?logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

---

## Overview

MLL Virtual Closet is an Android application that helps couples coordinate outfits. One user (the **View User**) browses their virtual wardrobe, while the other (the **Edit User**) curates which items match together. Each clothing item stores separate "like" counts for both users, creating a collaborative wardrobe management experience.

### Core Features

| Feature | Description |
|---|---|
| **Wardrobe Grid** | Browse all clothing items in a 2-column grid with Glide-loaded thumbnails |
| **Dual-User Model** | Two distinct user roles with independent like counters and backgrounds |
| **Outfit Matching** | Assign matching items to any clothing piece; those matches appear on the detail screen |
| **Product Detail** | View full-size images (front/back), see matching items, edit or delete entries |
| **Sorting & Filtering** | Filter by type (Shirts/Pants), sort by View User likes, Edit User likes, or random |
| **Wishlist** | Save external wishlist items with name and image URL to Firestore |
| **Upload Flow** | Upload front and optional back images to Firebase Storage with metadata dialog |
| **Firebase Backend** | Firestore for structured data, Firebase Storage for images, SharedPreferences for offline cache |

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                   ACTIVITIES                     │
│  Home  │  ProductDetails  │  MatchingDetails    │
│  Wishlist                                        │
└─────────────┬───────────────────────────────────┘
              │ observes LiveData
┌─────────────▼───────────────────────────────────┐
│                  VIEWMODELS                      │
│  HomeViewModel  │  ProductViewModel              │
│  MatchingViewModel                               │
└─────────────┬───────────────────────────────────┘
              │ delegates to
┌─────────────▼───────────────────────────────────┐
│              DataRepository (singleton)           │
│  - Firebase Firestore (CRUD)                     │
│  - Firebase Storage (upload/delete images)       │
│  - SharedPreferences (local Gson cache)          │
└─────────────────────────────────────────────────┘
```

**Pattern:** MVVM (Model-View-ViewModel) with `AndroidViewModel` and `LiveData`.  
**Data Flow:** Activities observe `LiveData<List<Cloths>>` from ViewModels → ViewModels delegate to `DataRepository` → Repository combines Firestore + Storage operations with a local `SharedPreferences` cache for offline reads.

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Kotlin | 1.8.10 |
| UI Framework | Jetpack Compose + XML Layouts | Compose BOM 2023.03 / AppCompat 1.6.1 |
| Architecture | MVVM + LiveData | Lifecycle 2.6.2 |
| Image Loading | Glide | via FirebaseUI Storage 7.2.0 |
| Database | Firebase Firestore | 24.10.0 |
| File Storage | Firebase Storage | 20.3.0 |
| Serialization | Gson | 2.10 |
| Build System | Gradle + AGP | 8.2 / 8.2.1 |
| Min / Target SDK | 33 / 34 | |

---

## Project Structure

```
.
├── build.gradle.kts                    # Root build: AGP, Kotlin, Google Services
├── settings.gradle.kts                 # Module config
├── gradle.properties                   # JVM args, AndroidX, Kotlin style
├── app/
│   ├── build.gradle.kts                # App module dependencies
│   ├── google-services.json            # Firebase config (EXCLUDED — provide your own)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/GOLD/MLL/VirtualCloset/
│       │   ├── Cloths.kt               # Data model (Serializable data class)
│       │   ├── DataRepository.kt       # Firestore + Storage + cache layer
│       │   ├── Wishlist.kt             # Wishlist Activity
│       │   ├── Home/
│       │   │   ├── Home.kt             # Main grid Activity
│       │   │   └── HomeViewModel.kt    # Product list LiveData
│       │   ├── ProductDetails/
│       │   │   ├── ProductDetails.kt   # Single item + matching list
│       │   │   └── ProductViewModel.kt # Selected product + update/delete
│       │   ├── MatchingDetails/
│       │   │   ├── MatchingDetails.kt  # Match editing screen
│       │   │   └── MatchingViewModel.kt
│       │   ├── Adapters/
│       │   │   ├── HomeAdapter.kt      # Main grid adapter
│       │   │   ├── ProductAdapter.kt   # Matching items list
│       │   │   ├── MatchingAdapter.kt  # Edit matching with checkboxes
│       │   │   └── WishlistAdapter.kt  # Wishlist items
│       │   └── ui/theme/               # Compose theme (Color, Type, Theme)
│       └── res/
│           ├── layout/                 # 12 XML layout files
│           ├── drawable/               # Custom icons & backgrounds
│           ├── menu/                   # Navigation drawer menu
│           ├── values/                 # Colors, strings, themes
│           └── mipmap-*/               # Launcher icons
```

---

## Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1+) or newer
- **JDK 17** (bundled with Android Studio)
- A **Firebase project** with Firestore and Storage enabled

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/Adam-Zborovsky/MLL_Virtual_Closet.git
cd MLL_Virtual_Closet

# 2. Create your own Firebase project at https://console.firebase.google.com
#    - Enable Firestore Database
#    - Enable Storage
#    - Download google-services.json and place it in app/

# 3. Open in Android Studio
#    File → Open → select the project root

# 4. Sync Gradle and build
./gradlew assembleDebug
```

### Firestore Collections

| Collection | Document ID | Fields |
|---|---|---|
| `Shirts` | Item name | `Name`, `ShaharLikes`, `AdamLikes`, `URL`, `BackSide`, `matching[]` |
| `Pants` | Item name | Same schema as Shirts |
| `Wishlist` | Item name | `URL` |

---

## Engineering Notes

- **Mixed UI approach:** Combines Jetpack Compose themes with traditional XML layouts and `AppCompatActivity`. The Compose setup exists in `ui/theme/` but the primary screens use XML + `RecyclerView`.
- **Dual-user hardcoding:** User names ("Shahar" / "Adam") are hardcoded in UI text and Firestore field names. A production refactor would replace these with a proper user system.
- **Serialized matching:** Matching relationships are stored as comma-delimited serialized `Cloths.toString()` strings inside a Firestore array field. This is functional but fragile — a refactor to reference document IDs would improve robustness.
- **Caching strategy:** Products are cached via Gson/SharedPreferences as JSON. The cache is updated after each Firestore read for offline use, but there is no conflict-resolution or staleness handling.
- **Image handling:** Glide loads images from Firebase Storage URLs with rounded corner transforms. Front/back image swapping is handled client-side via click toggle.
- **minSdk 33:** Targets Android 13+, which may limit device reach. Consider lowering to 24+ for broader compatibility.

---

## Security Audit Findings

| Finding | Severity | Status |
|---|---|---|
| `google-services.json` committed with Firebase API key | Medium | Key is partially redacted; file added to `.gitignore` |
| Hardcoded personal names ("Shahar", "Adam") in UI and database schema | Low | Documented as design choice; flagged for future refactor |
| No hardcoded secrets or tokens outside Firebase config | — | Clean |
| `.gitignore` was missing many Android/IDE entries | Low | Hardened |

---

## Roadmap

- [ ] Replace hardcoded user names with a generic dual-user or multi-user system
- [ ] Refactor matching from serialized strings to document references
- [ ] Add outfit categories beyond Shirts/Pants (shoes, accessories)
- [ ] Implement user authentication (Firebase Auth)
- [ ] Add swipe-to-delete, drag-to-reorder
- [ ] Lower `minSdk` for broader device support
- [ ] Add unit and UI test coverage (currently placeholder tests only)
- [ ] Migrate remaining XML layouts to Jetpack Compose

---

## License

This project is shared for learning and portfolio purposes. No explicit license is provided — contact the author for usage terms.

---

*Built with Kotlin, Firebase, and ❤️ by [Adam Zborovsky](https://github.com/Adam-Zborovsky).*
