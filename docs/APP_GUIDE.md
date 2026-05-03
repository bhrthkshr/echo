# Echo Android App Guide

This document is a beginner-friendly walkthrough of the Echo app. It explains what each part of the Android project does, which libraries are used, how files connect to each other, and what you should learn next.

Echo is currently a Kotlin Android app that:

- Shows an onboarding flow.
- Lets the user enable Android notification access.
- Shows a notification feed.
- Lets the user choose apps to sync.
- Shows a devices screen.
- Stores notifications locally with Room.
- Has placeholder networking for a future backend API.
- Has Firebase Cloud Messaging service hooks prepared for later integration.

## 1. Big Picture

Android apps are built from a few major ideas:

- **Activity**: A screen/window entry point. This app has one main activity: `MainActivity`.
- **Application**: A process-level class created before activities/services. This app uses `EchoApplication`.
- **Composable UI**: UI written with Kotlin functions using Jetpack Compose.
- **Navigation**: Moving between screens inside the app.
- **ViewModel**: Holds UI state and survives configuration changes.
- **Repository**: A class that coordinates data sources, such as database and network.
- **Room database**: A local SQLite database wrapper.
- **Service**: Background component for notification listener and Firebase messages.
- **Dependency injection**: Hilt creates and provides objects like database, API, and repository.

The simplified flow is:

```text
Android launches app
  -> EchoApplication starts Hilt
  -> MainActivity opens Compose UI
  -> EchoNavGraph decides the first screen
  -> Screens call ViewModels / repositories
  -> Repository reads Room and optionally talks to API
  -> NotificationListenerService captures notifications
  -> Repository saves notifications locally
  -> HomeScreen observes Room and updates automatically
```

## 2. Project Structure

The important folders are:

```text
app/src/main/
  AndroidManifest.xml
  java/com/example/echo/
    EchoApplication.kt
    MainActivity.kt
    data/
    di/
    service/
    ui/
  res/
    values/
    drawable/
    mipmap-*/
    xml/
```

### Root Gradle Files

#### `settings.gradle.kts`

Defines the Gradle project.

Important lines:

- `rootProject.name = "echo"` names the Gradle project.
- `include(":app")` says there is one Android module named `app`.
- `repositories { google(); mavenCentral() }` tells Gradle where to download dependencies.

#### `build.gradle.kts`

Top-level build file. It declares plugins that modules can use.

This project applies plugins in `app/build.gradle.kts`, not here directly.

#### `gradle/libs.versions.toml`

The version catalog. Instead of writing dependency versions everywhere, this file centralizes them.

Example:

```toml
room = "2.8.4"
retrofit = "3.0.0"
hilt = "2.59.2"
```

Then `app/build.gradle.kts` can say:

```kotlin
implementation(libs.room.runtime)
```

#### `gradle.properties`

Project-wide Gradle settings. This includes Kotlin style and JVM memory settings.

#### `gradlew` and `gradlew.bat`

Gradle wrapper scripts. Use these instead of a globally installed Gradle:

```bash
./gradlew assembleDebug
```

## 3. App Module Build File

File: `app/build.gradle.kts`

This is the most important build configuration for the Android app.

### Plugins

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
```

What they do:

- **Android application plugin**: Builds an APK.
- **Kotlin Compose plugin**: Enables Kotlin support for Compose.
- **Google Services plugin**: Reads `google-services.json` for Firebase.
- **Hilt plugin**: Enables dependency injection code generation.
- **KSP**: Kotlin Symbol Processing, used by Room and Hilt to generate code.

### Android Config

```kotlin
namespace = "com.example.echo"
applicationId = "com.example.echo"
minSdk = 24
targetSdk = 36
```

- **namespace**: Package used for generated Android code.
- **applicationId**: Real app ID installed on a device.
- **minSdk**: Oldest Android version supported.
- **targetSdk**: Android version the app is designed for.

### Dependencies

Main libraries:

- **Jetpack Compose**: UI toolkit.
- **Material 3**: Material Design components for Compose.
- **Lifecycle ViewModel**: State holder for screens.
- **Navigation Compose**: Screen navigation.
- **Room**: Local database.
- **Retrofit**: HTTP API client.
- **OkHttp**: Lower-level HTTP client used by Retrofit.
- **Gson converter**: Converts JSON to Kotlin objects for Retrofit.
- **Hilt**: Dependency injection.
- **Firebase Messaging**: Push notification messages.
- **Firebase AI**: Present in dependencies, but not currently used by the app code.

## 4. Android Manifest

File: `app/src/main/AndroidManifest.xml`

The manifest tells Android what the app contains and what permissions it needs.

Important permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

- `INTERNET`: Needed for API calls.
- `BIND_NOTIFICATION_LISTENER_SERVICE`: Allows the notification listener service to be registered.
- `POST_NOTIFICATIONS`: Needed on newer Android versions if the app posts notifications.

Important application declaration:

```xml
<application
    android:name=".EchoApplication"
    android:label="@string/app_name">
```

This connects the manifest to `EchoApplication`.

Important activity:

```xml
<activity android:name=".MainActivity" android:exported="true">
```

The `MAIN` and `LAUNCHER` intent filter makes `MainActivity` open when the app icon is tapped.

Important services:

- `EchoNotificationListenerService`: Receives system notification events after user grants notification access.
- `EchoFcmService`: Receives Firebase Cloud Messaging events.

## 5. App Startup Lifecycle

When the app starts:

1. Android creates the app process.
2. Android creates `EchoApplication`.
3. Hilt initializes dependency injection because of `@HiltAndroidApp`.
4. Android creates `MainActivity`.
5. `MainActivity.onCreate()` runs.
6. `setContent { ... }` starts Compose UI.
7. `EchoTheme` applies colors and typography.
8. `EchoNavGraph` decides which screen to show.
9. If onboarding has not been completed, it shows onboarding.
10. After onboarding, it shows the feed.

## 6. Kotlin Concepts Used

### Classes

A class defines a type. Example:

```kotlin
class MainActivity : ComponentActivity()
```

This means `MainActivity` inherits from `ComponentActivity`.

### Data Classes

Used for plain data containers:

```kotlin
data class NotificationItem(...)
```

Kotlin automatically gives data classes useful methods like `equals`, `copy`, and `toString`.

### Functions

Kotlin functions use `fun`:

```kotlin
fun provideDatabase(context: Context): AppDatabase
```

### Suspend Functions

Functions that can run asynchronous work:

```kotlin
suspend fun syncNotifications(): Boolean
```

They must be called from a coroutine.

### Flows

A `Flow<T>` is a stream of values over time.

Room returns:

```kotlin
Flow<List<NotificationItem>>
```

When the database changes, the UI receives a new list.

### Annotations

Annotations are metadata for frameworks and compilers:

- `@Composable`: Compose UI function.
- `@HiltViewModel`: Hilt can create this ViewModel.
- `@Inject`: Hilt should provide this dependency.
- `@Entity`: Room database table.
- `@Dao`: Room database access object.
- `@GET`, `@POST`: Retrofit API endpoint methods.

## 7. Jetpack Compose Basics

Compose UI is built with functions:

```kotlin
@Composable
fun HomeScreen(...) {
    Text("Hello")
}
```

Key ideas:

- UI is declarative: you describe what the UI should look like for the current state.
- When state changes, Compose redraws the affected UI.
- Layouts are also composables: `Column`, `Row`, `Box`, `LazyColumn`.
- `Modifier` changes size, padding, background, clicks, etc.

Example:

```kotlin
Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text("Title")
    Text("Body")
}
```

## 8. Dependency Injection With Hilt

Hilt creates objects for you and connects dependencies.

Instead of manually writing:

```kotlin
val dao = database.notificationDao()
val api = retrofit.create(EchoApi::class.java)
val repo = NotificationRepository(dao, api)
```

Hilt does it through modules and `@Inject`.

Important files:

- `EchoApplication.kt`
- `DatabaseModule.kt`
- `NetworkModule.kt`
- `NotificationRepository.kt`
- `HomeViewModel.kt`

The chain is:

```text
EchoApplication enables Hilt
  -> DatabaseModule provides AppDatabase and NotificationDao
  -> NetworkModule provides Retrofit and EchoApi
  -> Hilt creates NotificationRepository
  -> Hilt creates HomeViewModel
  -> HomeScreen receives HomeViewModel with hiltViewModel()
```

## 9. Data Flow

The current app uses a local-first model:

```text
NotificationListenerService
  -> creates NotificationItem
  -> NotificationRepository.sendNotification()
  -> inserts into Room database
  -> tries backend API, but failure is ignored for now
  -> HomeViewModel exposes Room Flow
  -> HomeScreen observes Flow
  -> UI updates
```

Startup sync:

```text
HomeViewModel init
  -> repository.syncNotifications()
  -> tries GET /notifications
  -> if backend fails, app stays usable in local-only mode
```

## 10. File-by-File Guide

### `app/src/main/java/com/example/echo/EchoApplication.kt`

```kotlin
@HiltAndroidApp
class EchoApplication : Application()
```

This is the app-level class. Android creates it before activities and services.

`@HiltAndroidApp` tells Hilt to generate dependency injection components for the whole app.

You usually keep this file small. Later, app-wide initialization can go here, but avoid putting heavy startup work here.

### `app/src/main/java/com/example/echo/MainActivity.kt`

This is the main app entry screen.

Important pieces:

- `@AndroidEntryPoint`: Allows Hilt dependencies inside this Activity and its Compose tree.
- `onCreate`: Activity lifecycle method called when the Activity is created.
- `setContent`: Starts Jetpack Compose.
- `EchoTheme`: Applies app colors and typography.
- `EchoNavGraph`: Shows the correct screen.

Lifecycle:

```text
onCreate()
  -> setContent()
  -> Compose renders UI
```

### `app/src/main/java/com/example/echo/ui/navigation/NavGraph.kt`

This file controls screen navigation.

It uses `NavHost` from Navigation Compose.

Current routes:

- `onboarding`
- `feed`
- `apps`
- `devices`
- `settings`

It also stores onboarding completion in `SharedPreferences`:

```kotlin
context.getSharedPreferences("echo", Context.MODE_PRIVATE)
```

Despite the preference file name, the visible app name is Echo. The preference name is just an internal storage key.

Flow:

```text
If has_onboarded == false -> show OnboardingScreen
If has_onboarded == true -> show Feed
Bottom nav taps -> navigate to Feed / Apps / Devices / Settings
```

### `app/src/main/java/com/example/echo/ui/onboarding/OnboardingScreen.kt`

This is the first-run onboarding screen.

It explains setup requirements:

- Notification access.
- Battery optimization.
- Privacy/local-first note.

It uses Android intents:

```kotlin
Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
```

These open Android Settings screens.

When the user taps `Get Started`, `NavGraph` marks onboarding complete and opens the feed.

### `app/src/main/java/com/example/echo/ui/home/HomeScreen.kt`

This is the notification feed.

It gets a `HomeViewModel`:

```kotlin
viewModel: HomeViewModel = hiltViewModel()
```

Then it observes:

```kotlin
val notifications by viewModel.notifications.collectAsState(initial = emptyList())
val syncStatus by viewModel.syncStatus.collectAsState()
```

Meaning:

- `notifications` updates whenever Room emits a new list.
- `syncStatus` tells the UI whether sync is active or local-only.

Important composables in this file:

- `HomeScreen`: Main feed screen.
- `FilterChips`: Horizontal chips like All, Gmail, Slack.
- `NotificationCard`: Shows one saved notification.
- `SetupCard`: First-run useful actions when no notifications exist.
- `SampleFeedPreview`: Design/sample cards shown before real notifications exist.

### `app/src/main/java/com/example/echo/ui/home/HomeViewModel.kt`

This ViewModel connects UI to the repository.

```kotlin
class HomeViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel()
```

Hilt injects `NotificationRepository`.

It exposes:

```kotlin
val notifications = repository.allNotifications
```

It also starts sync in `init`:

```kotlin
viewModelScope.launch {
    _syncStatus.value = SyncStatus.Syncing
    _syncStatus.value = if (repository.syncNotifications()) SyncStatus.Active else SyncStatus.Paused
}
```

`viewModelScope` is a coroutine scope tied to the ViewModel lifecycle. When the ViewModel is destroyed, its coroutines are canceled.

### `app/src/main/java/com/example/echo/ui/filter/FilterScreen.kt`

This is the Apps filter screen.

It is currently UI-only. It uses in-memory state:

```kotlin
val selected = remember { mutableStateMapOf<String, Boolean>() }
val query = remember { mutableStateOf("") }
```

Concepts:

- `remember`: Keeps state during recomposition.
- `mutableStateOf`: Compose observes changes and updates UI.
- `Switch`: Material 3 toggle component.
- `LazyColumn`: Efficient scrolling list.

Future improvement: save these selected apps in DataStore or Room, then make `EchoNotificationListenerService` respect the choices.

### `app/src/main/java/com/example/echo/ui/devices/DevicesScreen.kt`

This is the Devices screen.

It currently shows sample devices:

- This Android Phone.
- MacBook Air.
- Tablet.
- Office PC.

It is UI-only for now.

Future improvement: load real devices from the backend through `EchoApi.getDevices()`.

### `app/src/main/java/com/example/echo/ui/components/EchoScaffold.kt`

Shared UI components live here.

Important pieces:

- `MainTab`: Enum for bottom navigation tabs.
- `EchoScaffold`: Shared scaffold with top bar and bottom nav.
- `EchoTopBar`: Branded top bar, now displaying Echo.
- `AppGlyph`: Small rounded icon-like letter block.
- `TonalCard`: Shared card style.
- `StatusDot`: Small colored status indicator.

Even though the file/class names still include `Echo`, this is just an internal component name from the imported design. The visible product name is Echo.

### `app/src/main/java/com/example/echo/ui/theme/Color.kt`

Defines app color constants.

Examples:

```kotlin
val Primary = Color(0xFF1F108E)
val Surface = Color(0xFFFCF8FF)
val Error = Color(0xFFBA1A1A)
```

These come from the Stitch design system.

### `app/src/main/java/com/example/echo/ui/theme/Theme.kt`

Creates Material 3 color schemes:

- `LightColorScheme`
- `DarkColorScheme`

Then:

```kotlin
MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
)
```

All screens inside `EchoTheme` can use:

```kotlin
MaterialTheme.colorScheme.primary
MaterialTheme.typography.titleLarge
```

### `app/src/main/java/com/example/echo/ui/theme/Type.kt`

Defines typography styles.

Examples:

- `headlineLarge`
- `headlineMedium`
- `titleLarge`
- `bodyLarge`
- `bodyMedium`
- `labelLarge`
- `labelSmall`

This gives the UI consistent text sizing and weight.

## 11. Data Layer Files

### `app/src/main/java/com/example/echo/data/model/NotificationItem.kt`

This is the notification data model.

```kotlin
@Entity(tableName = "notifications")
data class NotificationItem(...)
```

`@Entity` means Room creates a table called `notifications`.

Important fields:

- `id`: Primary key. Uses Android notification key.
- `userId`: Placeholder until auth exists.
- `sourceDeviceId`: Placeholder until real device identity exists.
- `appPackage`: Android package name, such as `com.google.android.gm`.
- `appName`: Human readable app name.
- `title`: Notification title.
- `message`: Notification body.
- `timestamp`: When the notification was posted.
- `createdAt`: When Echo stored it.
- `expiresAt`: When it should be deleted.
- `hash`: Deduplication helper.

### `app/src/main/java/com/example/echo/data/local/NotificationDao.kt`

DAO means Data Access Object.

Room uses DAO interfaces to generate database code.

Methods:

```kotlin
@Query("SELECT * FROM notifications ORDER BY timestamp DESC")
fun getAllNotifications(): Flow<List<NotificationItem>>
```

Returns all notifications as a `Flow`. UI updates automatically when the table changes.

```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertNotification(notification: NotificationItem)
```

Inserts or replaces a notification.

```kotlin
@Query("DELETE FROM notifications WHERE id = :id")
suspend fun deleteNotification(id: String)
```

Deletes one notification.

```kotlin
@Query("DELETE FROM notifications WHERE expiresAt < :currentTime")
suspend fun deleteExpiredNotifications(currentTime: Long)
```

Deletes old notifications.

### `app/src/main/java/com/example/echo/data/local/AppDatabase.kt`

Defines the Room database:

```kotlin
@Database(entities = [NotificationItem::class], version = 1)
abstract class AppDatabase : RoomDatabase()
```

This says:

- The database has one table: `NotificationItem`.
- Current schema version is `1`.
- It exposes `notificationDao()`.

If you later change `NotificationItem`, you may need a Room migration.

### `app/src/main/java/com/example/echo/data/remote/EchoApi.kt`

Retrofit API interface.

Methods:

```kotlin
@POST("/notifications")
suspend fun sendNotification(@Body notification: NotificationItem): ApiResponse<Unit>
```

Sends a notification to the backend.

```kotlin
@GET("/notifications")
suspend fun getNotifications(@Query("since") since: Long): List<NotificationItem>
```

Gets notifications after a timestamp.

```kotlin
@POST("/devices/register")
suspend fun registerDevice(...)
```

Will register this device with the backend.

```kotlin
@GET("/devices")
suspend fun getDevices(): List<DeviceItem>
```

Will load synced devices from backend.

Right now, the app is stable without the backend because repository methods catch failures.

### `app/src/main/java/com/example/echo/data/repository/NotificationRepository.kt`

Repository coordinates database and network.

Constructor:

```kotlin
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val echoApi: EchoApi
)
```

Hilt provides both dependencies.

Important property:

```kotlin
val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
```

The UI observes this.

`syncNotifications()`:

- Calls backend.
- Inserts returned notifications into Room.
- Returns `true` if successful.
- Returns `false` if API fails.

`sendNotification()`:

- Inserts locally first.
- Tries to send to backend.
- If backend fails, keeps local copy.

This is why the app can run without API integration.

## 12. Dependency Injection Files

### `app/src/main/java/com/example/echo/di/DatabaseModule.kt`

Provides Room database dependencies to Hilt.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
```

`SingletonComponent` means these dependencies live as long as the app process.

Methods:

```kotlin
@Provides
@Singleton
fun provideDatabase(...)
```

Creates one shared Room database.

```kotlin
@Provides
fun provideNotificationDao(database: AppDatabase)
```

Gets DAO from database.

### `app/src/main/java/com/example/echo/di/NetworkModule.kt`

Provides network dependencies.

```kotlin
Retrofit.Builder()
    .baseUrl("https://api.echo-sync.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

Important: Retrofit base URLs must end with `/`.

This URL is still a placeholder. API integration is a future step.

## 13. Service Files

### `app/src/main/java/com/example/echo/service/EchoNotificationListenerService.kt`

This service receives notifications posted on the Android device.

It extends:

```kotlin
NotificationListenerService
```

Important lifecycle method:

```kotlin
override fun onNotificationPosted(sbn: StatusBarNotification?)
```

Android calls this whenever a notification appears, after the user grants notification access.

The service:

1. Reads notification extras.
2. Extracts title and text.
3. Looks up app name using `PackageManager`.
4. Creates `NotificationItem`.
5. Launches a background coroutine.
6. Calls `repository.sendNotification(item)`.

It also has:

```kotlin
override fun onDestroy() {
    serviceScope.cancel()
    super.onDestroy()
}
```

This cleans up coroutines when the service is destroyed.

### `app/src/main/java/com/example/echo/service/EchoFcmService.kt`

Firebase Cloud Messaging service.

Current methods:

```kotlin
override fun onMessageReceived(message: RemoteMessage)
override fun onNewToken(token: String)
```

These are placeholders right now.

Future use:

- `onNewToken`: Send FCM token to backend.
- `onMessageReceived`: Receive synced notification payloads from other devices and save them locally.

## 14. Resources

### `app/src/main/res/values/strings.xml`

Stores user-visible strings.

Current important value:

```xml
<string name="app_name">Echo</string>
```

The manifest uses this as the launcher label.

### `app/src/main/res/values/colors.xml`

Traditional XML color resources. The Compose UI mostly uses Kotlin colors in `Color.kt`.

### `app/src/main/res/values/themes.xml`

Android XML theme used by the Activity before Compose takes over.

### `app/src/main/res/drawable/*`

Drawable assets. Some baked goods images are template leftovers and are not used by the current Echo UI.

### `app/src/main/res/mipmap-*/*`

Launcher icons for different screen densities.

### `app/src/main/res/xml/backup_rules.xml`

Defines what app data is included in Android backup.

### `app/src/main/res/xml/data_extraction_rules.xml`

Defines data extraction behavior for backup/transfer on newer Android versions.

### `app/google-services.json`

Firebase configuration file. The Google Services Gradle plugin reads it and generates Firebase resources.

## 15. How Classes and Methods Link Together

### Launch Path

```text
AndroidManifest.xml
  -> android:name=".EchoApplication"
  -> MainActivity has LAUNCHER intent

EchoApplication
  -> @HiltAndroidApp starts DI graph

MainActivity.onCreate()
  -> EchoTheme
  -> EchoNavGraph

EchoNavGraph
  -> OnboardingScreen or HomeScreen
  -> bottom nav routes to FilterScreen / DevicesScreen / Settings
```

### Feed Data Path

```text
HomeScreen
  -> hiltViewModel<HomeViewModel>()

HomeViewModel
  -> injected NotificationRepository
  -> exposes repository.allNotifications
  -> calls repository.syncNotifications()

NotificationRepository
  -> NotificationDao.getAllNotifications()
  -> EchoApi.getNotifications()

NotificationDao
  -> Room-generated implementation
  -> SQLite notifications table
```

### Notification Capture Path

```text
User grants notification access in Android Settings
  -> Android binds EchoNotificationListenerService
  -> Notification appears
  -> onNotificationPosted()
  -> create NotificationItem
  -> repository.sendNotification()
  -> notificationDao.insertNotification()
  -> HomeScreen updates through Flow
```

### Future Push Sync Path

```text
Firebase receives message
  -> EchoFcmService.onMessageReceived()
  -> parse RemoteMessage.data
  -> create NotificationItem
  -> repository saves into Room
  -> HomeScreen updates
```

## 16. Lifecycle Concepts

### Application Lifecycle

`EchoApplication` exists for the whole process. Use it for app-wide setup only.

### Activity Lifecycle

`MainActivity` lifecycle basics:

```text
onCreate -> onStart -> onResume
onPause -> onStop -> onDestroy
```

In this app, the main work is in `onCreate`.

### Compose Lifecycle

Compose functions can re-run many times. This is called recomposition.

Rules:

- Composables should be fast.
- Do not do heavy work directly inside a composable.
- Use `remember` for UI state.
- Use ViewModel for screen/business state.

### ViewModel Lifecycle

ViewModel survives configuration changes like rotation.

It is cleared when the screen is removed permanently.

Use `viewModelScope` for async work tied to the ViewModel.

### Service Lifecycle

`EchoNotificationListenerService` is managed by Android.

It can run even when the app UI is not visible, after permission is granted.

## 17. Libraries Used

### Kotlin

Primary programming language.

Learn:

- Classes.
- Data classes.
- Null safety.
- Coroutines.
- Flows.
- Extension functions.
- Lambdas.

### Jetpack Compose

Modern Android UI toolkit.

Used for:

- Screens.
- Buttons.
- Lists.
- Navigation shell.
- Theme.

Learn:

- `@Composable`.
- `Modifier`.
- `remember`.
- State hoisting.
- `LazyColumn`.
- Material 3 components.

### Material 3

Compose implementation of Material Design.

Used for:

- `Scaffold`.
- `TopAppBar`.
- `NavigationBar`.
- `Button`.
- `Switch`.
- `Surface`.
- `Text`.

### Navigation Compose

Used for in-app navigation.

Key APIs:

- `rememberNavController()`
- `NavHost`
- `composable(route)`
- `navigate(route)`
- `popUpTo`

### Lifecycle ViewModel

Used to keep UI state separate from UI rendering.

Key APIs:

- `ViewModel`
- `viewModelScope`
- `collectAsState`

### Coroutines

Used for async work without blocking the main thread.

Key APIs:

- `suspend fun`
- `launch`
- `Dispatchers.IO`
- `SupervisorJob`

### Flow

Reactive data streams.

Room emits `Flow<List<NotificationItem>>`, and Compose observes it.

### Room

SQLite abstraction.

Used for local notification storage.

Key annotations:

- `@Entity`
- `@Dao`
- `@Database`
- `@Query`
- `@Insert`

### Retrofit

HTTP API client.

Used for future backend integration.

Key annotations:

- `@GET`
- `@POST`
- `@Body`
- `@Query`

### OkHttp

HTTP networking engine under Retrofit.

The app declares it as a dependency; Retrofit uses OkHttp internally.

### Gson Converter

Converts JSON responses to Kotlin data classes.

### Hilt

Dependency injection framework.

Key annotations:

- `@HiltAndroidApp`
- `@AndroidEntryPoint`
- `@HiltViewModel`
- `@Inject`
- `@Module`
- `@Provides`
- `@Singleton`

### KSP

Compiler tool used to generate code for Room and Hilt.

### Firebase Messaging

Used for future cross-device push notification sync.

Current file:

- `EchoFcmService.kt`

### Firebase AI

The dependency exists in Gradle but is not currently used in app code. It can be removed later if not needed.

## 18. What Is Stable Now vs Future Work

Stable now:

- App launches.
- Onboarding works.
- Navigation works.
- UI screens exist.
- Local Room database exists.
- Notification listener can create local notification records.
- App does not crash if backend is unavailable.

Still placeholder:

- Real authentication.
- Real backend base URL.
- Device registration.
- FCM receive logic.
- Persistent app filter settings.
- Real devices list from API.
- Retry queue for failed network sends.
- Proper notification deduplication.
- Tests beyond the generated sample test.

## 19. Recommended Learning Path

Learn in this order:

1. **Kotlin basics**
   - Variables, functions, classes, data classes, null safety.

2. **Android basics**
   - Manifest, Activity, Application, resources, permissions.

3. **Jetpack Compose**
   - `@Composable`, `Column`, `Row`, `LazyColumn`, `Modifier`, state.

4. **State and lifecycle**
   - ViewModel, `collectAsState`, recomposition.

5. **Coroutines and Flow**
   - `suspend`, `launch`, `viewModelScope`, `Flow`.

6. **Room**
   - Entity, DAO, database, migrations.

7. **Navigation Compose**
   - Routes, `NavHost`, bottom navigation.

8. **Hilt**
   - Dependency injection, modules, scopes.

9. **Retrofit and APIs**
   - REST APIs, JSON, error handling, auth headers.

10. **Android services and permissions**
   - Notification listener service, foreground/background limits, battery optimization.

11. **Firebase Cloud Messaging**
   - Tokens, message payloads, device registration.

12. **Testing**
   - Unit tests, repository tests, Compose UI tests.

## 20. Suggested Next Implementation Steps

Good next steps for Echo:

1. Persist app filter selections.
2. Make the notification listener skip disabled apps.
3. Add a real empty/history state for the feed.
4. Add fake/local device list data through a ViewModel.
5. Add repository unit tests.
6. Add real backend URL through build config.
7. Add auth/token support.
8. Implement FCM token registration.
9. Implement FCM message parsing.
10. Add WorkManager retry queue for failed sends.

## 21. Useful Commands

Build debug APK:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

Run unit tests:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew testDebugUnitTest
```

Build and test:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew testDebugUnitTest assembleDebug
```

## 22. Mental Model to Keep

When reading this app, keep this simple model in mind:

```text
UI asks ViewModel for state.
ViewModel asks Repository for data.
Repository reads/writes Room and talks to API.
Services can also write through Repository.
Room emits changes.
Compose redraws the screen.
```

That pattern is common in Android apps and is a good foundation to learn from.

