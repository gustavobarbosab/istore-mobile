# Harness — iStore

## Project

- **App:** iStore
- **Package:** `io.github.gustavobarbosab.istore`
- **Description:** Checkout app exercise (KMP + Compose Multiplatform), integrated with a
  BFF/API Gateway architecture backed by messaging for async payment processing.

## Module Layout

- **`shared`** — the real app. `domain`, `data`, and `ui` (screens, navigation, DI, theme) all
  live in `shared/src/commonMain/kotlin/io/github/gustavobarbosab/istore/` and are compiled for
  both Android and iOS. `App()` (`shared/.../App.kt`) is the single shared entry point:
  `IStoreApplication { IStoreTheme { AppNavigation() } }`.
- **`androidApp`** — a thin Android launcher only. Contains just `ui/MainActivity.kt`
  (`ComponentActivity` calling `setContent { App() }`), the manifest, and Android-only resources
  (launcher icon mipmaps/adaptive-icon XML, `strings.xml`). No domain/data/ui/business code
  belongs here — if you're adding a screen, use case, repository, or anything reusable, it goes in
  `shared/commonMain`, not `androidApp`.
- **`iosApp`** — the Xcode project. `iOSApp.swift` → `ContentView.swift` → `MainViewController()`
  (`shared/src/iosMain/.../MainViewController.kt`) → `App()`. This wiring already existed from the
  KMP wizard template and did not need to change — only `App()`'s *content* changed (from the demo
  screen to the real app).
- **Platform-specific code** only exists where the platform genuinely differs: the Ktor HTTP
  engine (`shared/src/androidMain` uses OkHttp, `shared/src/iosMain` uses Darwin — wired via Gradle
  dependencies, no `expect`/`actual` needed since `HttpClient { }` in commonMain auto-picks
  whichever engine is on the target's classpath).
- **Do NOT** put a screen, use case, repository, mapper, or any other reusable class in
  `androidApp` — it won't be visible to iOS. `androidApp` may only contain
  Android-entry-point/manifest/resource concerns.

## SDK & Versions

| Key        | Value  |
|------------|--------|
| minSdk     | 24     |
| targetSdk  | 36     |
| compileSdk | 36     |
| Kotlin     | 2.4.10 |
| AGP        | 9.0.1  |

## Architecture

**Pattern:** Clean Architecture (domain / data / ui) with MVI on every screen.

**Layers:**

- `domain` — pure Kotlin, no Android/Compose dependencies.
    - `model/` — domain models (e.g. `Product`, `Order`, `OrderStatus`, `Payment`).
    - `repository/` — repository **interfaces** only.
    - `usecase/` — one class per use case, exposing a single `suspend operator fun invoke(...)`.
- `data` — implements the domain's repository interfaces.
    - `remote/` — `*RemoteDataSource`, calling the API Gateway via the shared Ktor `HttpClient`
      (see Networking & Data below). Endpoints that don't exist in a real gateway on purpose
      (e.g. the simulated payment worker) stay mocked with `delay` and are documented as such.
    - `local/` — `*LocalDataSource`, in-memory cache (plain `MutableList`/nullable var held in a
      Koin `single`, no TTL/eviction — good enough for the skeleton).
    - `repository/` — `*RepositoryImpl`, orchestrates local cache + remote data source
      (cache-first strategy: return cached value if present, otherwise fetch remote and populate
      the cache).
- `ui` — screens, ViewModels, mappers, navigation, all Compose Multiplatform.

**MVI contract — every screen defines an `*Arch.kt` file with:**

- `sealed class *UiState` — all possible UI states (e.g. `Loading`, `Ready`, `Error`).
- `sealed class *SideEffect` — one-shot events emitted to the UI (e.g. navigation).
- `sealed class *Event` — user intent dispatched to the ViewModel.
- `class *Mvi : MviDelegateImpl<UiState, SideEffect>` — the screen's delegate instance.

**MVI base classes (`io.github.gustavobarbosab.istore.common`):**

- `MviDelegate<State, Effect>` — interface exposing `state: StateFlow`, `sideEffect: SharedFlow`,
  `onState()`, `onSideEffect()`.
- `MviDelegateImpl<State, Effect>` — abstract class implementing `MviDelegate`; extended by each
  screen's `*Mvi` class.
- `MviEventHandler<Event>` — interface enforcing `onEvent()` on the ViewModel.

**ViewModel pattern:**

```kotlin
class MyViewModel(
    private val myUseCase: MyUseCase,
    private val myUiModelMapper: MyUiModelMapper,
    private val myMvi: MyMvi,
) : ViewModel(),
    MviDelegate<MyUiState, MySideEffect> by myMvi,
    MviEventHandler<MyEvent> {

    override fun onEvent(event: MyEvent) {
        // Implementation here
    }
}
```

ViewModels depend on **use cases** (never repositories directly) and on a **mapper** (never map
inline). The ViewModel's only mapping-adjacent responsibility is choosing which sealed `UiState`
to wrap the mapped result in (e.g. `Ready` vs `Empty`) — that's a UI-flow decision, not a mapping
one.

**Navigation:** Compose Navigation with typed destinations defined in `ui/navigation/Destination.kt`.
Top-level tabs (Home/History/Profile) always navigate through the single
`NavHostController.navigateToTopLevel()` helper (clears the stack via
`popUpTo(startDestinationId) { inclusive = true }` before pushing) — never a bespoke `popUpTo` per
screen, to avoid corrupting/duplicating the back stack.

**State management:** `StateFlow` for UI state, `SharedFlow` for one-shot side effects, managed via
the `MviDelegate` / `MviDelegateImpl` pattern.

## UI

- **Framework:** Compose Multiplatform (Android + iOS).
- **Design system:** Material3 used directly for now (no custom DS layer yet — introduce
  `ui/designsystem/` if/when the app needs custom tokens or reusable styled components shared
  *across* screens; per-screen sub-composables go in `component/` instead, see below).
- **Icons:** plain `Text` glyphs (e.g. `"←"`, emoji) instead of `material-icons-core`, to avoid
  adding that dependency for a handful of icons. Revisit if icon usage grows.
- **Screen composable split:** each screen's `*ScreenContent.kt` only routes `UiState` to the
  right body (`Loading` / `Ready` / `Empty` / etc.) — it never contains the actual visual
  building blocks (cards, lists, badges). Those live as public composables in a `component/`
  subpackage of the screen package (e.g. `ui/screen/home/component/ProductCard.kt`,
  `ui/screen/home/component/ProductList.kt`). Components take plain callbacks
  (`onClick: () -> Unit`, `onProductClick: (id: String) -> Unit`) — never the screen's own
  `sealed *Event` type — so they stay decoupled and reusable outside their original screen.

## Dependency Injection

- **Framework:** Koin Multiplatform.
- **Scoping:**
    - `single` — app-wide dependencies and in-memory local data sources/repositories (cache must
      survive across screens).
    - `factory` — remote data sources, use cases, mappers, and `*Mvi` delegates.
    - `viewModelOf` — ViewModels. Screen-scoped constructor args (e.g. `productId`, `paymentId`)
      are resolved automatically via `parametersOf(...)` passed to `koinViewModel()` at the call
      site — no manual `viewModel { (id) -> ... }` lambdas needed.
- **Module structure:** One `object *Module { val module = module { ... } }` per feature/layer:
  `data/di/DataModule.kt`, `domain/di/UseCaseModule.kt`, and one per screen in
  `ui/screen/<feature>/di/`.
- **Entry point:** `IStoreApplication` (`ui/IStoreApplication.kt`) — a `@Composable` that
  bootstraps `KoinMultiplatformApplication` with all modules assembled (`AppModule`, `DataModule`,
  `UseCaseModule`, and every screen module).

## Networking & Data

- **HTTP client:** Ktor, `OkHttp` engine. One `HttpClient` singleton, provided by
  `data/remote/network/GatewayHttpClient.kt` and registered as `single<HttpClient>` in
  `AppModule` — every `*RemoteDataSource` takes it as a constructor parameter (Koin resolves it
  automatically). Do not create a second `HttpClient` instance per data source.
- **Auth:** the Gateway expects a static `X-API-Key` header. It's attached once, as a
  `defaultRequest` header, inside `GatewayHttpClient.kt` — data sources never read or pass the
  key themselves.
- **Config:** `data/remote/network/GatewayConfig.kt` exposes `baseUrl`/`apiKey`, sourced from
  `GeneratedGatewayConfig` — a Kotlin file the `generateGatewayConfig` Gradle task
  (`shared/build.gradle.kts`) writes into `commonMain` at build time, reading `gateway.baseUrl` /
  `gateway.apiKey` from the root `local.properties` (see `local.properties.example`). This is the
  multiplatform stand-in for Android's `BuildConfig` — it works the same way for both the Android
  target and the iOS/Kotlin-Native target, since it's plain generated commonMain source, not an
  Android-only mechanism. **Never hardcode the real api key**; it must only ever live in the
  gitignored `local.properties`.
- **Wire format:** each remote endpoint has a `*Dto` (`@Serializable`) in `data/remote/dto/`, plus
  a `toDomain()` extension mapping it to the matching `domain/model`. Domain models are never
  annotated `@Serializable` or used directly as request/response bodies.
- **Serialization:** `kotlinx.serialization` — used for both typed navigation routes and now the
  Ktor `ContentNegotiation` JSON codec.
- **Persistence:** none yet — local data sources are plain in-memory caches, reset on process
  death.
- **Pattern:** Repository pattern with `*LocalDataSource` and `*RemoteDataSource` per domain,
  wired through a `*RepositoryImpl` implementing the domain's repository interface.
- **Caching:** cache-first, in-memory only (see `ProductLocalDataSource`, `OrderLocalDataSource`).
  `OrderLocalDataSource` also acts as the target of a simulated background "worker" (see below).

## Error Handling

- **Repository/use case return type:** every `domain/repository/*Repository` method and every
  `domain/usecase/*UseCase` return `Ior<DataError, T>` (Arrow) instead of throwing or returning `T`
  directly. `DataError` (`domain/error/DataError.kt`) is a sealed class covering `Network`, `Http`,
  `Serialization`, and `Unknown` failures.
- **Where exceptions actually get caught:** `data/remote/network/SafeApiCall.kt`'s `safeApiCall {
  }` is the *only* place that catches Ktor/serialization exceptions and converts them into
  `DataError`. Every `*RemoteDataSource` method that hits the network wraps its call in
  `safeApiCall { ... }` and returns the resulting `Ior<DataError, T>` — repositories then just
  propagate or `.map`/fold that result (see `ProductRepositoryImpl`, `PaymentRepositoryImpl`, and
  `CheckoutUseCase` for how multiple `Ior`-returning calls get chained via `getOrNull()`/
  `leftOrNull()`). This is deliberate: repositories/use cases never write their own `try/catch` —
  duplicating that per call site is exactly what `safeApiCall` exists to avoid.
- **`Ior` vs `Both`:** `Ior` (Left/Right/**Both**) was chosen over a plain `Either` at the
  data/domain boundary in case a future case wants to report a partial success (e.g. serving stale
  cached data *and* a warning that the background refresh failed) — but nothing in this app
  produces `Both` today; every real result is `Left` or `Right`.
- **UI boundary:** ViewModels don't handle `Both` — call `.toEither()` on the `Ior` returned by the
  use case (Arrow's built-in conversion; `Both` becomes `Either.Right`, i.e. treated as success)
  and `.fold(ifLeft = ..., ifRight = ...)` from there, mapping `ifLeft` to the screen's `Error`
  `UiState` and `ifRight` to `Ready`/`Empty`. See `HomeViewModel.loadProducts()`,
  `DetailViewModel.loadProduct()`, `CheckoutViewModel`, `HistoryViewModel.loadOrders()` for the
  pattern. Every screen that loads data has a dedicated `Error` `UiState` + a retry `Event`
  (`OnRetryClicked` / reusing `OnRefresh` for History) wired to a "Retry" button in
  `*ScreenContent.kt` — none of them let a Gateway failure crash the app.

## Async & Concurrency

- **Async:** Kotlin Coroutines.
- **Scopes:** `viewModelScope` in ViewModels. `PaymentRepositoryImpl` owns a dedicated
  `CoroutineScope(SupervisorJob() + Dispatchers.Default)` to simulate the payment worker
  continuing to run in the background after `checkout()` returns (mirrors the "no polling —
  result appears later in My Orders" architecture decision).
- **Dispatchers:** not yet abstracted behind a `CoroutineDispatcherProvider` — introduce one if/when
  dispatcher injection becomes necessary for testing.

## Testing

- **Unit tests:** (TBD)
- **Instrumented tests:** (TBD)
- **UI tests:** (TBD)
- **Coverage goals:** (TBD)

## Do NOT

- Put business logic inside Composables or Screen functions.
- Put domain-model-to-UI-model mapping logic inside ViewModels — use a dedicated `*UiModelMapper`.
- Skip the `*Arch.kt` contract — every screen must define sealed `UiState`, `Event`, and
  `SideEffect`.
- Let domain models appear in `UiState`, `SideEffect`, or Composables — always map to a dedicated
  UI model first.
- Call repositories directly from ViewModels — always go through a use case.
- Launch coroutines in `GlobalScope` — use `viewModelScope` (or a repository-owned scope, only for
  genuinely fire-and-forget background work like the simulated worker).
- Block the main thread with `runBlocking`.
- Collect flows in Composables without `LaunchedEffect`.
- Use `mutableStateOf` in ViewModels — use `StateFlow`.
- Use `single` for screen-scoped dependencies (mappers, `*Mvi`, use cases) — use `factory`.
- Instantiate ViewModels manually — use `koinViewModel()`.
- Write a bespoke `popUpTo`/`saveState`/`restoreState` combination per screen for tab navigation —
  always go through `navigateToTopLevel()`.
- Define cards/lists/badges/other reusable visual sub-composables as `private fun` inside
  `*ScreenContent.kt` — extract them to a public composable in the screen's `component/`
  subpackage instead.
- Pass a screen's `sealed *Event` type as a parameter into a `component/` composable — components
  take plain lambdas so they don't depend on a specific screen's contract.
- Write a `try/catch` around an HTTP call in a `*RemoteDataSource` — go through `safeApiCall { }`
  instead, so error handling stays in one place.
- Return `T` or throw from a repository/use case method — return `Ior<DataError, T>`.
- Fold/handle `Ior.Both` inside a ViewModel — convert to `Either` via `.toEither()` first and only
  handle `ifLeft`/`ifRight`.
- Let a Gateway/network failure propagate uncaught out of a ViewModel's `viewModelScope.launch` —
  every use case call that can fail must be folded into the screen's `Error` `UiState`.
- Hardcode the API Gateway key/URL, or read it from anywhere other than `local.properties` via
  `GatewayConfig`/`GeneratedGatewayConfig`.
- Instantiate a second `HttpClient` — inject the one Koin `single` from `AppModule`.
- Serialize/deserialize a `domain/model` directly over the wire — always go through a `*Dto` +
  `toDomain()`.
- Add a new screen, use case, repository, or any other business/reusable code under `androidApp` —
  it belongs in `shared/commonMain` so iOS gets it too.
- Import `java.*` or `android.*` anywhere under `shared/src/commonMain` — it won't compile for iOS.

## Best Practices & Conventions

- **Screen package structure:** `ui/screen/<feature>/` — one package per screen.
- **File naming per screen:** `<Feature>Arch.kt`, `<Feature>Screen.kt`, `<Feature>ScreenContent.kt`,
  `<Feature>ViewModel.kt`, `mapper/<Feature>UiModelMapper.kt`, `model/<Feature>UiModel.kt`,
  `component/<Name>.kt`, `di/<Feature>Module.kt`.
- **Components:** every reusable visual sub-composable (card, list, badge, info panel) lives as
  its own public composable in `ui/screen/<feature>/component/<Name>.kt` — one file per
  composable (e.g. `component/ProductCard.kt`, `component/ProductList.kt`,
  `component/StatusBadge.kt`). `*ScreenContent.kt` composes them together per `UiState` branch
  and wires screen `Event`s to the plain callbacks components expose.
- **Domain models:** live in `domain/model/`, no suffix (e.g. `Product`, not `ProductDto`) and are
  never `@Serializable`. Wire formats live separately as `data/remote/dto/*Dto.kt` with a
  `toDomain()` mapper.
- **Repository interfaces:** `domain/repository/*Repository.kt`.
- **Use cases:** `domain/usecase/*UseCase.kt`, one per use case, single
  `suspend operator fun invoke(...)` entry point.
- **Repository implementations:** `data/repository/*RepositoryImpl.kt`.
- **Datasources:** `data/local/*LocalDataSource.kt` / `data/remote/*RemoteDataSource.kt`.
- **DI modules:** Always `object` with a `val module` property.
- **Mappers:** All domain-model-to-UI-model mapping must live in a dedicated `*UiModelMapper`
  class inside a `mapper/` subpackage of the screen package (e.g.
  `ui/screen/home/mapper/ProductUiModelMapper`). ViewModels must never contain mapping logic —
  inject the mapper as a constructor parameter and delegate to it. Register mappers as `factory`
  in the feature's DI module. A mapper maps domain model(s) to UI model(s) only — it does not
  decide which sealed `UiState` to wrap the result in.
- **Price/date formatting:** currently formatted inline inside mappers
  (`"R$ %.2f".format(...)`, safe since mappers stay Android-only-string-formatting-free by using
  plain string templates) and inside `CheckoutUseCase` via `kotlinx-datetime`
  (`Clock.System.now().toLocalDateTime(...)`, manual zero-padding) — **not** `java.text.SimpleDateFormat`,
  which doesn't exist outside the JVM and would break the iOS build since this class lives in
  commonMain. No shared `Formatter` utility yet — introduce one if formatting logic starts
  duplicating across more than a couple of mappers.
- **commonMain platform safety:** anything under `shared/src/commonMain` compiles for iOS too —
  never import `java.*` or `android.*` there. If you need a genuinely platform-specific API, use
  Kotlin `expect`/`actual` (see the Ktor HTTP engine setup in `shared/build.gradle.kts` for a case
  that turned out to *not* need `expect`/`actual`, resolved via per-source-set Gradle dependencies
  instead).
- **Code style:** No linter enforced.
