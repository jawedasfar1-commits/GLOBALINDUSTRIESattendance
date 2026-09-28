# GLOBAL INDUSTRIES - Labour Attendance & Site Management

A native Android application built with **Kotlin**, **Jetpack Compose**, and **Firebase Realtime Database** for tracking construction site labor attendance, overtime, daily advances, daily progress reports (DPR), and monthly wage registers across multiple construction sites.

---

## 📱 Features

- **Real-Time Cloud Synchronization:** Powered by Firebase Realtime Database with offline disk persistence.
- **Multi-Site Management:** Switch between construction sites (e.g., *Jaynagar Railway Station*) or add new sites dynamically.
- **Admin Security Mode (PIN `1820`):** View-Only mode by default to prevent unauthorized edits.
- **Live Summary Dashboard:** Top metrics cards showing Total Workers, Today's Attendance, Total Duty Days, Overtime Hours, and Net Payable Balance.
- **5 App Views:**
  1. 📅 **Daily View:** Mark Present (1), Half (0.5), Absent (0), Overtime hours, Daily Advances, and bulk mark daily attendance.
  2. 📝 **DPR (Daily Progress Report):** Work execution details text & Material Consumption table (*Bag, Kg, Nos, Roll, Tractor*).
  3. 📓 **Notes:** Date-specific site notes and weather conditions.
  4. 📊 **Monthly Sheet:** Dual-axis scrollable 31-day matrix table computing total duty, overtime, wage earnings, advance deductions, net balance, and site grand totals.
  5. 👷 **Workers Directory:** Manage workers with Trade designations (*Mason, Fitter, Helper, Welder, Bar Bender, Carpenter, Electrician, Supervisor*), wage rates, and opening advances.

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM + Kotlin StateFlow / callbackFlow
- **Database:** Firebase Realtime Database (Regional URL: `https://g12343-default-rtdb.asia-southeast1.firebasedatabase.app/`)
- **Build System:** Gradle (AGP 9.4.1 + Kotlin 2.0.21)

---

## 🚀 How to Build & Run

### 1. Clone Repository
```bash
git clone https://github.com/jawedasfar1/GLOBALINDUSTRIESattendance.git
cd GLOBALINDUSTRIESattendance
```

### 2. Build Debug APK
```bash
./gradlew assembleDebug
```
The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
Copyright © 2025 Global Industries. All rights reserved.
