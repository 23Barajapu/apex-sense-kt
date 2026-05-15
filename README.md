<p align="center">
  <img src="app/src/main/res/drawable/app_logo.jpg" width="140" style="border-radius: 28px" />
</p>

<h1 align="center">ApexSense</h1>

<p align="center">
  <b>🎮 Ultimate Gaming Companion for Android</b><br/>
  <sub>Real-time overlay • Crosshair customizer • Performance monitor • Game optimizer</sub>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue" />
  <img src="https://img.shields.io/badge/License-MIT-yellow" />
</p>

---

## ✨ Features

### 🎯 Custom Crosshair Overlay
Configurable on-screen crosshair that stays visible over any game.

- **8 Styles** — Cross, Dot, Circle, Gap Cross, Square, T-Shape, Chevron, X-Shape
- **Full Customization** — Size, thickness, length, rotation, alpha, color
- **Drag to Reposition** — Move anywhere on screen with touch
- **Live Preview** — See changes instantly before entering a game

### 📊 Session Monitor
Compact, draggable performance HUD that runs during gameplay.

| Metric | Source |
|--------|--------|
| CPU Usage | `/proc/stat` real-time parsing |
| RAM Usage | `ActivityManager.MemoryInfo` |
| FPS | Display refresh rate (`Display.getRefreshRate`) |
| Battery | `BatteryManager` broadcast |
| Temperature | Battery thermal sensor |
| GPU | System metrics |

### 🕹️ Game Vault
Personal game library with auto-detection and manual management.

- Auto-detects installed games via `PackageManager` category flags
- Manual add/remove with persistent `SharedPreferences` storage
- **Game Boost** — Animated launch sequence with optimization simulation
- Game count displayed on home dashboard

### ⚙️ Device Tools

| Tool | Description |
|------|-------------|
| **Sensitivity Engine** | Calculates optimal sensitivity based on screen resolution |
| **Resolution Changer** | Modify display resolution (Width × Height) |
| **Smallest Width** | Override `sw` value for UI density tuning |

### 🧭 Navigation
- **Swipe navigation** between main tabs (Profile, Home, Tools) via `HorizontalPager`
- Smooth fade + slide transitions between screens
- Synced bottom navigation bar with animated indicators

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Navigation** | Compose Navigation + HorizontalPager |
| **Backend** | Supabase (PostgREST, GoTrue) |
| **Networking** | Ktor Client |
| **Image Loading** | Coil |
| **Architecture** | MVVM (ViewModel + StateFlow) |
| **Overlay** | WindowManager + ComposeView |
| **Serialization** | kotlinx.serialization |

---

## 📁 Project Structure

```
app/src/main/java/com/apexsense/pro/
├── data/
│   ├── remote/          # Supabase client provider
│   └── repository/      # AppRepository (API + local data)
├── domain/
│   └── model/           # Data models (Game, Device, Feedback, etc.)
├── presentation/
│   ├── components/      # Reusable UI (BottomBar, Header, Footer)
│   ├── navigation/      # Screen routes, BottomNavItem, MainPagerScreen
│   ├── screens/
│   │   ├── home/        # Dashboard with hardware monitor
│   │   ├── library/     # Game Vault with grid layout
│   │   ├── profile/     # User profile
│   │   ├── splash/      # Animated splash screen
│   │   └── tools/       # Crosshair, monitor, resolution tools
│   └── theme/           # Colors, typography, theme
├── service/
│   ├── OverlayService   # Crosshair & monitor overlay rendering
│   ├── CrosshairState   # Global crosshair configuration
│   └── AppMonitorState  # Global monitor configuration
└── utils/
    └── HardwareMonitorUtils  # CPU, RAM, battery, FPS readers
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 17
- Android device/emulator (min SDK 24)

### Build & Run

```bash
# Clone
git clone https://github.com/your-username/ApexSense.git
cd ApexSense

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Permissions

ApexSense requires the following permission:

| Permission | Purpose |
|------------|---------|
| `SYSTEM_ALERT_WINDOW` | Display crosshair and monitor overlays over other apps |

The app will prompt you to grant this on first launch.

---

## 🎨 Design

- **Theme** — Premium dark mode with glassmorphism effects
- **Accent** — Orange (`#FF6B35`) throughout the interface
- **Typography** — Bold, uppercase labels with tight letter spacing
- **Cards** — Rounded corners (20-24dp) with subtle borders
- **Animations** — Fade-in from top transitions, spring-based splash

---

## 📝 License

```
MIT License

Copyright (c) 2025 Bara444

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<p align="center">
  <b>Built with 🔥 by Bara444</b><br/>
  <sub>Kotlin • Jetpack Compose • Supabase</sub>
</p>
