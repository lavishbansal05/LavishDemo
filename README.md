# LavishDemo

A modern Android portfolio holdings app that shows a list of stocks, a sticky P&L summary, pull‑to‑refresh, light/dark themes, and an online/offline banner. Built for speed, readability, and maintainability.

## Highlights
- Jetpack Compose UI with Material 3
- MVVM + Clean Architecture
- Hilt (KSP) for DI
- Room for offline cache
- Retrofit + OkHttp + Kotlinx Serialization
- Pull‑to‑Refresh, sticky P&L bar, connectivity banner
- Unit tests with MockK, Coroutines Test, Turbine

## Tech stack
- Language: Kotlin
- UI: Jetpack Compose, Material 3
- DI: Hilt (KSP; aggregation task disabled via `hilt { enableAggregatingTask = false }`)
- Data: Room (SQLite)
- Network: Retrofit, OkHttp, Kotlinx Serialization
- Concurrency: Coroutines, Flows

## Architecture (Clean + MVVM)
- presentation: Compose screens + ViewModel (state, UI logic)
- domain: use cases, domain models, repository interface
- data: repository impl, Room (DAO/entities), Retrofit (service/DTOs), mappers

Flow:
ViewModel → UseCase → Repository (cache + network) → Room/Retrofit → ViewModel → UI

## ERD (simplified)
```
+------------------+
| holdings         |
+------------------+
| id (PK)          |
| user_id          |
| symbol           |
| quantity         |
| ltp              |
| avg_price        |
| close            |
| updated_at       |
+------------------+
```

## Networking
- Base URL: `https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/`
- JSON via Kotlinx Serialization
- Retrofit service returns DTOs; mappers convert to domain models

### Long Polling
- A lightweight refresh loop in `ViewModel` that waits a fixed interval (e.g., 30s) and re-fetches holdings.
- Keeps UI updated without WebSockets complexity.
- Interval is centralized in `PollingConfig`.

## Offline-first
- On app open: try network → update cache; on failure → serve cached
- Room stores the last known portfolio for quick launch and offline viewing
- Logs added for “serving from DB” vs “fetched from network”

## Online/Offline banner
- Small sticky banner below the top bar
- Red/“No internet connection” when offline
- Green/“Back online” when connectivity returns; auto-hides after ~2.5s
- Uses `ConnectivityManager` and a Flow in a `NetworkMonitor`

## UI notes
- Sticky P&L summary at screen bottom; expandable/collapsible in place
- Pull‑to‑Refresh via Compose pull refresh API
- Light/Dark theme toggle in the top bar; colors aligned to Material 3 and tuned for legibility
- Titles and LTP values explicitly use `onSurface`/`onPrimary` to avoid low contrast in dark mode

## Calculations
- Current Value = Σ(LTP × Qty)
- Total Investment = Σ(AvgPrice × Qty)
- Total P&L = Current Value − Total Investment
- Today’s P&L = Σ((Close − LTP) × Qty)

## Testing
- Use cases, repository, and ViewModel covered with:
  - MockK, coroutines-test, Turbine
- Given/When/Then style for readability
- KSP + Hilt with aggregating task disabled to keep builds stable across environments

## Project structure (key files)
```
app/
  src/main/java/com/assignment/myportfolio/
    presentation/  -> Compose screens + ViewModel
    domain/        -> usecases, models, repository interface
    data/          -> retrofit, room, mappers, repo impl
    di/            -> Hilt modules (network, bindings)
    core/          -> NetworkMonitor, PollingConfig
  res/             -> themes, strings, layouts (Compose-first)
```

## Build & run
- Android Studio (Giraffe+), JDK 17
- Gradle 8.9, AGP 8.7.2, Kotlin 2.0.21
- Compose compiler plugin enabled
- Hilt KSP with:
```kotlin
hilt {
  enableAggregatingTask = false
}
```

## Notes
- Only one `Json` provider (in `NetworkModule`).
- Minimal third‑party deps and no unnecessary reflection.
- Ready for small feature growth: new endpoints, filters, or sorting.

## Screenshots

| Light | Dark |
|---|---|
| <img src="docs/screenshots/light.png" width="300"/> | <img src="docs/screenshots/dark.png" width="300"/> |
