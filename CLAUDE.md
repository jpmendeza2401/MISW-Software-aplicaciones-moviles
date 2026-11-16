# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android app (Kotlin, single `app` module, package `com.example.aplicacionvinilos`) consuming the BackAlbums REST backend. minSdk 24, compileSdk/targetSdk 37, Java 11. Dependencies via version catalog `gradle/libs.versions.toml`. UI/docs text in Spanish.

## Setup

API URLs not committed. Copy keys from `local.properties.example` into gitignored `local.properties` (or set env `VINYLS_API_URL_LOCAL` / `VINYLS_API_URL_PUBLIC`):
- `api.url.local` — local backend (`10.0.2.2:3000` from emulator; PC LAN IP for physical device).
- `api.url.public` — deployed backend.

Build fails with `Missing API URL` (check in `app/build.gradle.kts` `taskGraph.whenReady`) if the URL for the requested flavor is empty.

## Commands

Run from repo root (Windows: `gradlew.bat`, bash: `./gradlew`).

- Build: `./gradlew assembleLocalDebug` / `assembleRemoteDebug`
- Unit tests: `./gradlew testLocalDebugUnitTest`
- Single unit test: `./gradlew testLocalDebugUnitTest --tests "com.example.aplicacionvinilos.ExampleUnitTest"`
- Instrumented tests: `./gradlew connectedLocalDebugAndroidTest`

## Architecture

MVVM, layered; each package has a `README.md` stating its rules:
`ui` (Activity/Fragment, ViewBinding + dataBinding enabled, no network/business logic) → `viewmodels` (LiveData wrapping `UiState<T>` = Idle/Loading/Success/Error, coroutines in `viewModelScope`) → `data/repository` (only data source for ViewModels; returns `Result<T>`, hides exceptions/HTTP) → `network` (Retrofit `ApiService` suspend funcs, singleton `RetrofitClient` with Gson) → `data/model`.

Backend selection = product flavors, dimension `backend`: `local` and `remote`. Each sets `BuildConfig.API_BASE_URL`, read through `config/ApiConfig.kt`. Cleartext HTTP allowed only in `localDebug` source set (`src/localDebug/res/xml/network_security_config.xml`, hosts `10.0.2.2`, `localhost`).

Current code is a sample GET (`albums/{albumId}/bands/{bandId}` → `Band`) wired through `AlbumBandRepository` and `AlbumBandViewModel`; follow that pattern for new endpoints.
