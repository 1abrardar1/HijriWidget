# Hijri Calendar Widget for Android

An Android home screen widget that displays the current **Hijri (Islamic) calendar** date alongside the Gregorian date. Tap the widget to refresh it manually.

---

## Features

- Shows current **Hijri day, month name, and year (AH)**
- Shows the Gregorian date below as a reference
- Auto-updates at **midnight** every day
- Also refreshes on date/time/timezone changes and after device reboot
- Tap anywhere on the widget to force a refresh
- Resizable (minimum 2×2 grid cells)
- Deep blue semi-transparent background — looks great on any wallpaper

---

## Project Structure

```
HijriWidget/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/hijri/widget/
│   │   ├── HijriWidgetProvider.kt   ← AppWidgetProvider (main entry point)
│   │   ├── HijriConverter.kt        ← Gregorian → Hijri conversion algorithm
│   │   ├── GregorianFormatter.kt    ← Formats Gregorian date string
│   │   └── DateChangeReceiver.kt    ← BroadcastReceiver for date changes + midnight alarm
│   └── res/
│       ├── layout/widget_layout.xml ← Widget UI layout
│       ├── xml/widget_info.xml      ← AppWidget metadata (size, update interval)
│       ├── drawable/widget_background.xml
│       └── values/strings.xml
├── build.gradle
└── settings.gradle
```

---

## How to Build & Install

### Prerequisites
- **Android Studio Hedgehog** (2023.1.1) or newer
- Android SDK 26+ (Android 8.0 Oreo)
- Kotlin 1.9+

### Steps

1. **Open in Android Studio**
   - File → Open → select the `HijriWidget` folder

2. **Sync Gradle**
   - Android Studio will prompt you to sync. Click **Sync Now**.

3. **Add a launcher icon** (required to build)
   - Right-click `res` → New → Image Asset → set any icon as `ic_launcher`
   - Or copy any PNG as `app/src/main/res/mipmap-hdpi/ic_launcher.png`

4. **Run on device or emulator**
   - Connect your Android device (USB debugging on) or start an emulator
   - Click **Run ▶** (Shift+F10)

5. **Add the widget to your home screen**
   - Long-press your home screen → Widgets → find **"Hijri Widget"** → drag it to the home screen

---

## Customization

### Change widget colors
Edit `res/drawable/widget_background.xml`:
```xml
<solid android:color="#CC1A5276" />  <!-- Change this hex color -->
```
- `#CC` prefix = 80% opacity. Change first two hex digits for transparency.
- Try `#CC1B4332` for green, `#CC4A1942` for purple.

### Change widget size
Edit `res/xml/widget_info.xml`:
```xml
android:targetCellWidth="2"   <!-- columns -->
android:targetCellHeight="2"  <!-- rows -->
```

### Add Arabic month names
`HijriConverter.kt` already has `HIJRI_MONTH_NAMES_AR`. To use them in the widget, in `widget_layout.xml` set the month TextView's `android:textDirection="rtl"` and call `HijriConverter.getMonthNameAr(hijriDate.month)` in the provider.

### Show both English and Arabic month names
Replace the month TextView in `widget_layout.xml` with two stacked TextViews, one for each language.

---

## How the Hijri Conversion Works

The conversion uses the **Kuwaiti algorithm** (widely used in Islamic apps):

1. Convert Gregorian date → **Julian Day Number (JDN)**
2. Convert JDN → **Hijri date** using tabular Islamic calendar arithmetic

This gives ±1 day accuracy. For exact moon-sighting–based dates, you'd need a live API (e.g. [Aladhan API](https://aladhan.com/islamic-calendar-api)).

---

## Permissions Used

| Permission | Why |
|---|---|
| `RECEIVE_BOOT_COMPLETED` | Re-schedule the midnight alarm after reboot |
| `SCHEDULE_EXACT_ALARM` | Ensure widget updates exactly at midnight |

---

## Extending: Live API Support (optional)

To use a verified Hijri date from the internet, add internet permission and fetch from Aladhan:

```kotlin
// In a coroutine:
val url = "https://api.aladhan.com/v1/gToH?date=${day}-${month}-${year}"
val json = URL(url).readText()
// Parse "data.hijri.day", "data.hijri.month.en", "data.hijri.year"
```

Add `<uses-permission android:name="android.permission.INTERNET" />` to the manifest.

---

## License

MIT — free to use and modify.
