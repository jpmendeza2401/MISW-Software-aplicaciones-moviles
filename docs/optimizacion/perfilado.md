# Perfilado de recursos por historia de usuario

Medir **antes** (rama `dev`) y **después** (`feature/micro-optimizaciones`) en **al menos 3 dispositivos físicos distintos**
con `medir.sh` (arranque, memoria, frames) y/o Android Studio Profiler (CPU, Memory, Network).
Usar el build `remoteRelease` o `localDebug` siempre igual en ambas mediciones.

## Dispositivos

| # | Modelo | Android | RAM | Gama |
|---|--------|---------|-----|------|
| 1 |        |         |     |      |
| 2 |        |         |     |      |
| 3 |        |         |     |      |

## Resultados (rellenar por dispositivo y por flujo)

Flujos: Login/Invitado · HU01 listado de álbumes · HU02 detalle de álbum · HU05 listado de coleccionistas · HU06 detalle de coleccionista · Cerrar sesión.

| Dispositivo | Flujo | Versión | Arranque TotalTime (ms) | Java Heap (MB) | Native Heap (MB) | TOTAL PSS (MB) | Frames janky (%) | CPU pico (%) |
|---|---|---|---|---|---|---|---|---|
| 1 | HU01 | antes | | | | | | |
| 1 | HU01 | después | | | | | | |
| 2 | HU01 | antes | | | | | | |
| 2 | HU01 | después | | | | | | |
| 3 | HU01 | antes | | | | | | |
| 3 | HU01 | después | | | | | | |

(Repetir filas para cada HU.)

## Conclusiones
- Qué mejoró y cuánto:
- Qué no mejoró y por qué:
