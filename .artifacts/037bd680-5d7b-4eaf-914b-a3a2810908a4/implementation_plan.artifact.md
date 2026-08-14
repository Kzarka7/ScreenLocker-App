# Implementation Plan - Double-Tap to Lock & Haptics

Update the app to require two consecutive taps on the app icon within a short interval (500ms) to lock the screen. This prevents accidental locks. Additionally, provide haptic feedback (vibration) to confirm the lock action.

## User Review Required

> [!NOTE]
> The first tap will open and immediately close the app to record the first "tap." The second tap (within 500ms) will trigger the vibration and lock the screen.

## Proposed Changes

### Logic & Feedback

#### [MODIFY] [MainActivity.kt](file:///C:/Users/johng/AndroidStudioProjects/ScreenLock/app/src/main/java/MainActivity.kt)
- Add a `companion object` to store the `lastTapTime` across activity launches.
- In `onCreate`, if Device Admin is active:
    - Compare `System.currentTimeMillis()` with `lastTapTime`.
    - If the difference is **less than 500ms**:
        - Trigger a short haptic vibration.
        - Call `devicePolicyManager.lockNow()`.
        - Reset `lastTapTime` to 0.
    - If the difference is **greater than 500ms**:
        - Update `lastTapTime` to the current time.
    - Always call `finish()` immediately to keep the experience seamless.
- Add a helper function to handle vibration across different Android versions.

### Manifest & Permissions

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/johng/AndroidStudioProjects/ScreenLock/app/src/main/AndroidManifest.xml)
- **[NEW]** Add `<uses-permission android:name="android.permission.VIBRATE" />`.

### Project Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/johng/AndroidStudioProjects/ScreenLock/app/build.gradle.kts)
- Bump `versionCode` to **7** and `versionName` to **"1.6"**.

## Verification Plan

### Manual Verification
1.  **Single Tap**: Tap the app icon once.
    - **Expected**: Nothing happens (the screen does not lock).
2.  **Double Tap**: Tap the app icon twice quickly.
    - **Expected**: The phone vibrates briefly and the screen locks immediately.
3.  **Slow Taps**: Tap the app icon twice, but wait 2 seconds between taps.
    - **Expected**: The screen does not lock.
