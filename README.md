# FloatingWidgetCompose

FloatingWidgetCompose is an Android library that allows you to display Jetpack Compose UI as a system-level floating overlay similar to chat heads or floating widgets. This library is designed to be lightweight, reusable, and easy to integrate into any Android application that uses Jetpack Compose.

## Features
- Display any Jetpack Compose UI as a floating overlay
- Fully customizable Composable content
- Optional touch interaction (touchable or non-touchable)
- Flexible sizing (WRAP or FULL width)
- Lifecycle-safe Compose integration inside a Service
- Supports Android API 24+

## Demo Video
Demo video (replace this link with your own video later):
https://youtu.be/YOUR_VIDEO_LINK

## Installation
Add JitPack repository to settings.gradle.kts:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add dependency to module build.gradle.kts:
```kotlin
implementation("com.github.pascaladitia:FloatingWidgetCompose:v1.0.0")
```

## Overlay Permission (Required)
This library requires Android SYSTEM_ALERT_WINDOW permission to display content above other applications.

Add permission to AndroidManifest.xml (app module):
```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

Overlay permission cannot be granted automatically. The user must enable it manually from system settings.

Typical path:
Settings → Display over other apps → Your App → Allow

Important notes:
- The app must be installed and opened at least once
- Overlay permission will not appear if the app has never been launched
- Some OEM devices (Xiaomi, Oppo, Vivo) may place this setting in a different menu

## Usage
Basic example:
```kotlin
@Composable
fun SampleFloatingWidget() {
    val context = LocalContext.current

    FloatingWidgetCompose.start(
        context = context,
        config = FloatingWidgetConfig(
            touchable = true,
            sizeMode = SizeMode.WRAP
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
}
```

Stop floating widget:
```kotlin
FloatingWidgetCompose.stop(context)
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

SizeMode:
- WRAP → Overlay wraps content size
- FULL → Overlay takes full screen width

## Important Usage Rules
- Do not call FloatingWidgetCompose.start() inside @Preview
- Overlay permission must be granted before starting the widget
- App must be launched at least once before requesting permission

Wrong usage:
```kotlin
@Preview
@Composable
fun Preview() {
    SampleFloatingWidget()
}
```

Correct usage:
```kotlin
@Preview
@Composable
fun Preview() {
    Text("Preview only")
}
```

## How It Works
The library starts an Android Service, attaches a ComposeView to WindowManager, renders your Composable content inside the overlay window, and uses a custom lifecycle owner to ensure Compose works correctly outside an Activity context.

## Compatibility
- Minimum SDK: 24
- Recommended Android version: 8.0+ (API 26+)
- Kotlin: 2.0+
- Jetpack Compose: Latest stable

## Known Limitations
- Overlay permission must be granted manually
- Some OEM devices may aggressively restrict overlays or background services
- Battery optimization may stop the overlay service

## License
MIT License

Copyright (c) 2025 Pascal Aditia Muclis

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files to deal in the Software without restriction including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and or sell copies of the Software and to permit persons to whom the Software is furnished to do so subject to the following conditions.

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED AS IS WITHOUT WARRANTY OF ANY KIND EXPRESS OR IMPLIED INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM DAMAGES OR OTHER LIABILITY WHETHER IN AN ACTION OF CONTRACT TORT OR OTHERWISE ARISING FROM OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Author
Pascal Aditia  
GitHub: https://github.com/pascaladitia
