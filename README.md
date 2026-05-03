# Echo

Echo is an Android notification-sync app written in Kotlin with Jetpack Compose.

If you are new to Kotlin and Android development, start here:

- [docs/APP_GUIDE.md](docs/APP_GUIDE.md)

That guide explains the project structure, Android lifecycle, Kotlin concepts, libraries, files, and how the app pieces connect.

## Build

From the project root:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew testDebugUnitTest assembleDebug
```

The Echo project is a production-grade Android application designed for real-time notification synchronization across multiple devices. Here is a brief analysis of its current status and architecture:
🏗️ Project Architecture & Status
The app follows a modern Clean Architecture pattern with a robust layered structure:

1. Core Services (service/):
◦ EchoNotificationListenerService: Intercepts notifications on the current device to be synced.
◦ EchoFcmService: Receives incoming synced notifications from other devices via Firebase Cloud Messaging.

2. Data Layer (data/):
◦ Local (local/): Uses Room DB (AppDatabase, NotificationDao) for persistent local storage of up to 1000 notifications.
◦ Remote (remote/): Uses Retrofit (EchoApi) for backend communication.
◦ Repository (repository/): NotificationRepository acts as the single source of truth, coordinating between local and remote data.

3. UI Layer (ui/):
◦ Built entirely with Jetpack Compose and Material 3.
◦ home/: Displays the notification feed and real-time sync status.
◦ filter/: Allows users to whitelist specific apps for synchronization.
◦ navigation/: Manages the flow between onboarding, home, and settings using EchoNavGraph.

4. Dependency Injection (di/ & Root):
◦ Uses Dagger Hilt for dependency management.
◦ EchoApplication is correctly annotated with @HiltAndroidApp.
◦ DatabaseModule and NetworkModule provide singleton instances for the DB and API.

🛠️ Tech Stack
• Language: Kotlin (JVM Toolchain 17)
• UI: Jetpack Compose (Material 3)
• DB: Room (with KSP)
• Network: Retrofit + OkHttp (with Gson)
• DI: Hilt (with KSP)
• Messaging: Firebase FCM

⚠️ Current Technical State
While the project structure is well-defined and modern, it recently faced runtime crashes related to Hilt's ViewModel injection (NoSuchMethodException for HomeViewModel). This typically indicates a mismatch in Hilt's annotation processing or missing @Inject constructors in the ViewModel. The build environment has been stabilized with Java 17 and compatible KSP/Hilt versions in libs.versions.toml.
