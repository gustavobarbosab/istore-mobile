# iStore

A checkout app built as a learning exercise: a simple e-commerce flow (browse → detail → checkout →
confirmation → order history) used to explore a realistic mobile + backend architecture — a KMP/
Compose Multiplatform client talking to a BFF behind an API Gateway, with async payment processing
backed by a message queue and a worker (no polling on the client).

This repo contains the **mobile client**, shared between Android and iOS via Compose
Multiplatform. It talks to a real HTTP client (Ktor) pointed at an API Gateway URL you configure
locally — the actual Gateway/BFF/Payment API/queue/worker backend is documented but not yet
implemented, so requests will fail until you point `gateway.baseUrl` at something real (see
[Configuration](#configuration) below). The one deliberately-still-simulated piece is the payment
worker's async result (see [Architecture](#architecture)) — there's no polling endpoint for it by
design.

## Screens

- **Home** — product list.
- **Detail** — single product, "Buy" starts checkout.
- **Checkout** — order summary, confirms payment.
- **Confirmation** — shows "payment processing", never polls for a result.
- **My Orders (History)** — order list; this is where the payment result (approved/declined)
  actually shows up, resolved on-demand when the screen is opened.
- **Profile** — static user info.

Home / My Orders / Profile are top-level tabs (bottom navigation bar); Detail, Checkout, and
Confirmation are pushed on top without the bottom bar.

## Architecture

Clean Architecture (`domain` / `data` / `ui`) with MVI on every screen, Koin for DI, Compose
Navigation for routing. Full conventions, do's/don'ts, and file-naming rules live in
[`.sage/harness.md`](./.sage/harness.md) — read that before adding a screen or a layer. Short
version:

- **`domain`** — pure Kotlin models, repository interfaces, and use cases (`GetProductsUseCase`,
  `CheckoutUseCase`, etc.).
- **`data`** — `*RepositoryImpl` orchestrating a `*LocalDataSource` (in-memory cache) and a
  `*RemoteDataSource` that calls the API Gateway over HTTP (Ktor). `PaymentRepositoryImpl` also
  simulates the payment worker: after `checkout()` returns, a background coroutine "resolves" the
  order status a few seconds later, so opening My Orders later shows the real outcome — the
  same "no polling" behavior the real architecture is designed around (there's no real endpoint
  for this part, since a real gateway wouldn't have one either).
- **`ui`** — one package per screen (`ui/screen/<feature>/`), each with an `*Arch.kt` (sealed
  `UiState`/`Event`/`SideEffect`), a `*ViewModel.kt`, a `*ScreenContent.kt` that only routes state,
  a `mapper/*UiModelMapper.kt` (domain → UI model, never inlined in the ViewModel), and a
  `component/` subpackage for the actual reusable composables (cards, lists, badges).

Navigation between top-level tabs always goes through a single `navigateToTopLevel()` helper
(clears the back stack before pushing) instead of ad-hoc `popUpTo` calls per screen.

## Tech stack

- Kotlin Multiplatform + Compose Multiplatform — one shared UI/business codebase for Android and
  iOS (see [Project structure](#project-structure)).
- Ktor for networking (`OkHttp` engine on Android, `Darwin` on iOS), with a single `X-API-Key`
  header attached to every request.
- Koin (DI), Compose Navigation (typed routes via `kotlinx.serialization`).
- Material3, with a custom red-based `ColorScheme` (`ui/theme/`) instead of the default baseline
  purple.

## Project structure

* [/shared](./shared/src/commonMain) — the actual app. `domain`, `data`, and `ui` (screens,
  navigation, DI, theme) all live in `shared/src/commonMain` and compile for both Android and iOS
  (see [Architecture](#architecture)). `App()` is the single shared entry point.
* [/androidApp](./androidApp/src/main/kotlin) — a thin Android launcher: just `MainActivity`
  (calls `App()`) plus the manifest and Android-only resources (launcher icon). No app logic
  belongs here.
* [/iosApp](./iosApp/iosApp) — the Xcode project. `ContentView.swift` hosts `shared`'s Compose UI
  via `MainViewController()` — same `App()` Android runs, no separate iOS UI code needed.

## Configuration

The app needs an API Gateway base URL and api key, read from `local.properties` at build time (for
both Android and iOS — see `shared/build.gradle.kts`'s `generateGatewayConfig` task). Copy
`local.properties.example`'s `gateway.baseUrl` / `gateway.apiKey` keys into your own
`local.properties` (already gitignored) before building. Without a real Gateway to point at,
`fetchProducts`/`submitCheckout` will fail at runtime — that's expected until the backend exists.

## Running the apps

Use the run configurations in your IDE's toolbar, or:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open [/iosApp](./iosApp) in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
