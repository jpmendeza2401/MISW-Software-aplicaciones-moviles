# Perfilado de recursos por historia de usuario

Mediciones con `medir.sh` (arranque, memoria, frames y CPU) sobre la versión con los ajustes de `feature/micro-optimizaciones`.
Los resultados crudos de cada corrida están en `docs/optimizacion/resultados/<dispositivo>-<HU>/`.

## Dispositivos

| # | Modelo | Android | RAM | Gama / tipo |
|---|--------|---------|-----|------|
| 1 | sdk_gphone64_arm64 | 17 | 3,8 GB | Emulador (Android Studio) |
| 2 | NAA-LX3 | 15 | 3,7 GB (4 GB nominal) | Media-baja |
| 3 | sdk_gphone16k_arm64 | 17 | 3,9 GB | Emulador (Android Studio) |

> **Nota sobre el dispositivo 2.** A diferencia de los dispositivos 1 y 3, que son emuladores de Android Studio y se midieron una HU por corrida con `medir.sh`, el dispositivo 2 es un **dispositivo físico real**. Por eso sus métricas no están detalladas por HU: se registró un único recorrido completo (Login → HU01 → HU02 → HU09 → HU05 → HU06 → Cerrar sesión).

## Resultados

Flujos: HU01 listado de álbumes · HU02 detalle de álbum · HU05 listado de coleccionistas · HU06 detalle de coleccionista · HU09.

Cada corrida arranca la app en frío (`LoginActivity`), se recorre solo el flujo de la HU y se capturan las métricas al final. Memoria en MB (convertida de KB); frames janky en % del total renderizado; p50/p90 es el tiempo por frame; CPU es una captura puntual de `top`, no un pico.

| Dispositivo | Flujo | Arranque TotalTime (ms) | Java Heap (MB) | Native Heap (MB) | TOTAL PSS (MB) | Frames janky (%) | Frame p50 / p90 (ms) | CPU (%) |
|---|---|---|---|---|---|---|---|---|
| 1 (emulador) | HU01 | 1930 | 15,8 | 20,8 | 81,3 | 84,67 | 42 / 150 | 13,3 |
| 1 (emulador) | HU02 | 1257 | 17,5 | 39,0 | 108,8 | 87,71 | 48 / 150 | 85,1 |
| 1 (emulador) | HU05 | 1282 | 14,5 | 16,4 | 75,5 | 91,72 | 48 / 150 | 88,8 |
| 1 (emulador) | HU06 | 1303 | 55,0 | 25,2 | 134,5 | 94,63 | 57 / 200 | 96,1 |
| 1 (emulador) | HU09 | 1278 | 57,9 | 25,5 | 138,2 | 93,48 | 65 / 150 | 0,0 |
| 2 (físico) | Recorrido completo (Login → HU01 → HU02 → HU09 → HU05 → HU06 → Cerrar sesión) | 2200 | 9,1 | 15,2 | 111,6 | 3,54 | no detallado | pendiente |
| 3 (emulador) | HU01 | 1440 | 16,6 | 16,6 | 77,8 | 100,00 | 150 / 450 | 0,0 |
| 3 (emulador) | HU02 | 1232 | 15,3 | 20,7 | 81,0 | 100,00 | 133 / 300 | 0,0 |
| 3 (emulador) | HU05 | 2977 | 15,2 | 17,8 | 77,9 | 97,03 | 117 / 550 | 0,0 |
| 3 (emulador) | HU06 | 1251 | 16,8 | 20,0 | 82,8 | 97,62 | 129 / 300 | 0,0 |
| 3 (emulador) | HU09 | 1075 | 16,3 | 20,8 | 82,9 | 98,97 | 121 / 400 | 0,0 |

## Conclusiones
- **Memoria estable por HU.** En ambos emuladores el `TOTAL PSS` se mantiene entre ~76 y ~83 MB en casi todas las HU, con Java Heap de 14 a 18 MB. En el dispositivo físico, el recorrido completo cierra con 111,6 MB de PSS y 9,1 MB de Java Heap.
- **Picos en el dispositivo 1.** HU02 sube el Native Heap a 39,0 MB, y HU06 y HU09 elevan el Java Heap a 55,0 y 57,9 MB (PSS de 134,5 y 138,2 MB). El dispositivo 3 no repite ese comportamiento en las mismas HU (Java Heap de 15 a 17 MB). No se investigó la causa; conviene repetir la medición antes de atribuirla a la app.
- **Arranque.** El arranque en frío tarda entre ~1,1 y ~1,9 s en los emuladores (excepto HU05 del dispositivo 3, con 2,98 s) y 2,2 s en el dispositivo físico.
- **Frames janky.** Los emuladores marcan entre 84 % y 100 % de frames janky, mientras que el dispositivo físico marca 3,54 %. Los emuladores renderizan sobre la GPU del equipo anfitrión, por lo que esos porcentajes y los percentiles `gpu` (4950 ms) no son representativos del comportamiento real de la app. Para fluidez, el dato fiable es el del dispositivo físico.
- **CPU.** Es una captura puntual de `top` al terminar el recorrido. Los valores 0,0 indican que la app estaba inactiva en ese instante; no representan el consumo durante el flujo. La columna del dispositivo físico queda pendiente.
- **Limitación.** Solo el dispositivo 2 es físico y no tiene detalle por HU. Los dispositivos 1 y 3 aportan el desglose por HU, pero sus métricas de rendimiento gráfico dependen del emulador.
