# Fix Daemon Compilation Failed Error

The error `Daemon compilation failed: null` with the underlying `java.lang.IllegalArgumentException: 25.0.2` indicates that the project is being built using **JDK 25**, which is not supported by the currently configured **Kotlin 1.9.24**.

To fix this, we need to upgrade the project's build configuration to versions that are compatible with JDK 25 and the current Gradle 9.5.0 environment.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/johng/AndroidStudioProjects/ScreenLock/build.gradle.kts)
- Upgrade Android Gradle Plugin (AGP) from `8.5.2` to `9.3.1`.
- Upgrade Kotlin from `1.9.24` to `2.4.10`.
- Add the Kotlin Compose Compiler plugin.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/johng/AndroidStudioProjects/ScreenLock/app/build.gradle.kts)
- Apply the `org.jetbrains.kotlin.plugin.compose` plugin.
- Remove the deprecated `composeOptions` block (specifically `kotlinCompilerExtensionVersion`).
- Upgrade Compose BOM and other dependencies to modern stable versions.

## Verification Plan

### Automated Tests
- Run `./gradlew clean assembleDebug` to verify that the compilation error is resolved and the app builds successfully.
