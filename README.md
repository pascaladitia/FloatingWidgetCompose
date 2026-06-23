# FloatingWidgetCompose

FloatingWidgetCompose is a lightweight Android library for rendering Jetpack Compose UI inside a system overlay window. It is useful for chat-head style widgets, quick actions, assistive floating controls, and small always-on-top Compose surfaces.

## Features

- Render any Jetpack Compose UI as a floating overlay
- Start and stop the floating widget explicitly from your app
- Request and check overlay permission with helper APIs
- Use touchable or click-through overlay behavior
- Choose wrapped content size or a full-screen drag layer
- Drag floating content with regular Compose gesture handling
- Lifecycle-aware Compose rendering inside an Android `Service`
- Safe guards for missing permission, process recreation, and service cleanup
- Supports Android API 24+

## Demo App

The sample app includes:

- Overlay permission status
- `Grant Permission`, `Start Floating`, and `Stop` buttons
- Three widget examples: draggable card, counter widget, and quick action widget
- Toggle for full-screen drag layer
- Toggle for touchable or click-through behavior

## Installation With JitPack

FloatingWidgetCompose is distributed through JitPack. Use a GitHub release tag as the dependency version, for example `v1.0.0`.

Add JitPack to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the dependency to your app module `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.pascaladitia:FloatingWidgetCompose:v1.0.0")
}
```

For local development inside this repository, the sample app uses the local module:

```kotlin
implementation(project(":floating-compose-widget"))
```

## Required Permission

System overlays require `SYSTEM_ALERT_WINDOW`.

Add this permission to your app module `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

Users must grant overlay permission manually from Android system settings. The library provides `openOverlayPermissionSettings(context)` to send users to the correct screen when possible.

Some OEM Android builds, especially Xiaomi, Oppo, and Vivo, may place overlay permission and background restrictions in custom settings screens.

## Usage

Check permission and open settings when needed:

```kotlin
if (!FloatingWidgetCompose.canDrawOverlays(context)) {
    FloatingWidgetCompose.openOverlayPermissionSettings(context)
    return
}
```

Start a basic floating widget:

```kotlin
val started = FloatingWidgetCompose.start(
    context = context,
    config = FloatingWidgetConfig(
        touchable = true,
        sizeMode = SizeMode.WRAP,
        startX = 24,
        startY = 160
    )
) {
    Surface(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Black
    ) {
        Text(
            text = "Floating Compose",
            modifier = Modifier.padding(16.dp),
            color = Color.White
        )
    }
}
```

Stop the floating widget:

```kotlin
FloatingWidgetCompose.stop(context)
```

## Draggable Example

Use `SizeMode.FULL` when you want a full-screen transparent gesture layer and draggable floating content:

```kotlin
FloatingWidgetCompose.start(
    context = context,
    config = FloatingWidgetConfig(
        touchable = true,
        sizeMode = SizeMode.FULL
    )
) {
    DraggableFloatingContent()
}
```

## Configuration

```kotlin
FloatingWidgetConfig(
    touchable = true,
    sizeMode = SizeMode.WRAP,
    startX = 0,
    startY = 200
)
```

Options:

- `touchable`: when `true`, the overlay can receive touch events; when `false`, touches pass through to apps behind it.
- `sizeMode = SizeMode.WRAP`: the overlay wraps its Compose content.
- `sizeMode = SizeMode.FULL`: the overlay covers the screen, which is useful for custom drag handling.
- `startX` and `startY`: initial overlay position passed to `WindowManager.LayoutParams`.

## Important Usage Rules

- Start the widget from an explicit user action, such as a button click.
- Do not call `FloatingWidgetCompose.start()` directly from a Composable body that can recompose.
- Do not start the widget from `@Preview`.
- Check or request overlay permission before starting the widget.
- Expect OEM-specific overlay and battery restrictions on some devices.

Wrong:

```kotlin
@Composable
fun Screen() {
    FloatingWidgetCompose.start(context, config) {
        Text("This can run again during recomposition")
    }
}
```

Correct:

```kotlin
Button(
    onClick = {
        FloatingWidgetCompose.start(context, config) {
            Text("Started by user action")
        }
    }
) {
    Text("Start Floating")
}
```

## Release Workflow

This repository includes a GitHub Actions workflow at `.github/workflows/release.yml`.

When changes are merged into `main`, the workflow:

1. Builds the library release artifact and sample debug APK.
2. Creates a Git tag from `RELEASE_VERSION`.
3. Creates a GitHub Release with `RELEASE_TITLE` and `RELEASE_NOTES`.
4. Uploads the generated AAR and sample APK as release assets.

Before merging a release commit into `main`, update these values in the workflow:

```yaml
env:
  RELEASE_VERSION: v1.1.0
  RELEASE_TITLE: FloatingWidgetCompose v1.1.0
  RELEASE_NOTES: |
    Add your release notes here.
```

JitPack can then consume the new GitHub tag:

```kotlin
implementation("com.github.pascaladitia:FloatingWidgetCompose:v1.1.0")
```

## Compatibility

- Minimum SDK: 24
- Target SDK: 36
- Kotlin: 2.0+
- Jetpack Compose: stable Compose BOM

## Known Limitations

- Overlay permission must be granted manually by the user.
- Some OEM devices can restrict overlays or background services.
- Battery optimization may stop long-running overlay behavior.
- Android may recreate the service after process death; the library stops safely when in-memory Compose content is no longer available.

## License

MIT License. See [LICENSE](LICENSE).

## Author

Pascal Aditia  
GitHub: https://github.com/pascaladitia
