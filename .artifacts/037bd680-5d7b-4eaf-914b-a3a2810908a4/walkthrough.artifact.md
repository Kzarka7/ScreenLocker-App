# Walkthrough - Double-Tap to Lock & Haptics

I have implemented the double-tap lock behavior and added haptic feedback to confirm the lock action.

## Changes Made

### Logic & Feedback
- **Double-Tap Detection**: Updated `MainActivity` to track the time between app launches. If two launches occur within **500 milliseconds**, the screen will lock. A single tap will only record the timestamp and close the app.
- **Haptic Feedback**: Added a short vibration (50ms) that triggers on a successful double-tap, so you know the lock command was sent.
- **Permissions**: Added the `VIBRATE` permission to `AndroidManifest.xml` to enable haptic feedback.

### Reliability
- The `lastTapTime` is stored in a `companion object`, ensuring it persists while the app process remains active in the background for quick consecutive taps.
- The app still calls `finish()` immediately on the first tap to keep the home screen visible for the second tap.

### Renaming
- **App Name**: The app is now officially named **Screen Locker**.
- **APK Filename**: The generated APK is now named **`screenlocker-v1.8.apk`**.

### Build & Versioning
- Updated the app to **Version 1.8** (`versionCode 9`).

## Verification Results

### Automated Tests
- Ran `./gradlew clean :app:assembleDebug` - **Passed**.

### Manual Verification Steps (For User)
1. **Standard Launch**: Tap the app icon once.
    - **Expected**: The app opens and closes instantly; the screen remains **ON**.
2. **Locking**: Tap the app icon **twice** quickly.
    - **Expected**: You will feel a brief vibration and the screen will **LOCK** immediately.
3. **Slow Taps**: Tap once, wait 1 second, then tap again.
    - **Expected**: The screen remains **ON**.
