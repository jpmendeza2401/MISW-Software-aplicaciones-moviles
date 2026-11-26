# Perfilado de recursos por historia de usuario


## Dispositivos

| # | Modelo | Android | RAM | Gama / tipo |
|---|--------|---------|-----|------|
| 1 | sdk_gphone64_arm64 | 17 | 3,8 GB | Emulador (Android Studio) |
| 2 | sdk_gphone16k_arm64 | 17 | 3,9 GB | Emulador (Android Studio) |
| 3 | sdk_gphone16k_arm64 | 17 | 3,9 GB | Emulador (Android Studio) |


## Resultados

Flujos: HU01 listado de álbumes · HU02 detalle de álbum · HU05 listado de coleccionistas · HU06 detalle de coleccionista · HU07 crear álbum · HU08 asociar tracks · HU09 agregar comentario · HU11 agregar álbum a un coleccionista.

Cada corrida arranca la app en frío (`LoginActivity`), se recorre el flujo de la HU y se capturan las métricas al final. Las 24 corridas registran arranque `COLD` con estado `ok`. Memoria en MB (convertida de KB); frames janky en % del total renderizado; p50/p90 es el tiempo por frame; CPU es una captura puntual de `top`, no un pico.

| Dispositivo | Flujo | Arranque TotalTime (ms) | Java Heap (MB) | Native Heap (MB) | TOTAL PSS (MB) | Frames renderizados | Frames janky (%) | Frame p50 / p90 (ms) | CPU (%) |
|---|---|---|---|---|---|---|---|---|---|
| 1 (emulador) | HU01 | 1599 | 41,2 | 23,0 | 115,6 | 154 | 61,04 | 30 / 150 | 53,8 |
| 1 (emulador) | HU02 | 1103 | 41,9 | 27,4 | 122,4 | 254 | 60,63 | 36 / 125 | 100,0 |
| 1 (emulador) | HU05 | 846 | 16,1 | 55,7 | 121,8 | 162 | 69,14 | 32 / 150 | 57,1 |
| 1 (emulador) | HU06 | 1245 | 53,8 | 27,3 | 134,9 | 336 | 58,63 | 34 / 93 | 92,3 |
| 1 (emulador) | HU07 | 1122 | 19,1 | 30,9 | 105,6 | 491 | 79,63 | 38 / 85 | 0,0 |
| 1 (emulador) | HU08 | 1176 | 59,8 | 25,6 | 139,4 | 335 | 69,25 | 40 / 105 | 0,0 |
| 1 (emulador) | HU09 | 1029 | 63,5 | 25,7 | 143,9 | 271 | 66,79 | 36 / 117 | 0,0 |
| 1 (emulador) | HU11 | 1299 | 50,9 | 24,4 | 127,8 | 256 | 61,33 | 32 / 97 | 96,1 |
| 2 (emulador) | HU01 | 1872 | 15,4 | 25,3 | 85,5 | 89 | 97,75 | 65 / 200 | 0,0 |
| 2 (emulador) | HU02 | 2158 | 15,6 | 22,5 | 82,3 | 163 | 96,93 | 65 / 200 | 0,0 |
| 2 (emulador) | HU05 | 1364 | 17,3 | 20,2 | 81,8 | 128 | 95,31 | 53 / 200 | 0,0 |
| 2 (emulador) | HU06 | 1388 | 16,5 | 21,4 | 82,0 | 197 | 95,94 | 81 / 200 | 0,0 |
| 2 (emulador) | HU07 | 882 | 18,2 | 27,4 | 92,1 | 343 | 97,38 | 69 / 150 | 0,0 |
| 2 (emulador) | HU08 | 892 | 16,0 | 21,8 | 81,7 | 175 | 94,86 | 53 / 150 | 0,0 |
| 2 (emulador) | HU09 | 952 | 16,1 | 22,9 | 83,1 | 232 | 93,10 | 53 / 125 | 0,0 |
| 2 (emulador) | HU11 | 1132 | 15,5 | 19,8 | 78,7 | 244 | 85,66 | 48 / 129 | 0,0 |
| 3 (emulador) | HU01 | 1581 | 17,7 | 19,0 | 82,6 | 234 | 99,57 | 57 / 150 | 0,0 |
| 3 (emulador) | HU02 | 3165 | 15,7 | 21,8 | 83,1 | 169 | 98,22 | 73 / 150 | 0,0 |
| 3 (emulador) | HU05 | 969 | 15,5 | 18,7 | 79,4 | 108 | 99,07 | 73 / 200 | 0,0 |
| 3 (emulador) | HU06 | 1580 | 16,0 | 20,5 | 81,9 | 213 | 98,12 | 65 / 150 | 0,0 |
| 3 (emulador) | HU07 | 1283 | 17,3 | 23,9 | 89,1 | 374 | 97,59 | 81 / 200 | 0,0 |
| 3 (emulador) | HU08 | 1132 | 16,2 | 20,8 | 82,0 | 182 | 99,45 | 65 / 150 | 0,0 |
| 3 (emulador) | HU09 | 1417 | 16,3 | 21,7 | 83,1 | 221 | 99,10 | 73 / 200 | 0,0 |
| 3 (emulador) | HU11 | 1241 | 15,5 | 18,9 | 79,0 | 178 | 98,31 | 77 / 200 | 0,0 |

### Resumen por dispositivo (medianas de las 8 corridas)

| Dispositivo | Arranque (ms) | Java Heap (MB) | Native Heap (MB) | TOTAL PSS (MB) | Frames janky (%) | Frame p50 / p90 (ms) |
|---|---|---|---|---|---|---|
| 1 | 1149 | 46,4 | 26,5 | 125,1 | 64,06 | 35 / 111 |
| 2 | 1248 | 16,1 | 22,1 | 82,2 | 95,63 | 59 / 175 |
| 3 | 1350 | 16,1 | 20,6 | 82,3 | 98,69 | 73 / 175 |

## Conclusiones
- **Memoria en los dispositivos 2 y 3.** El `TOTAL PSS` se mantiene entre 78,7 y 92,1 MB, con Java Heap de 15,4 a 18,2 MB. En ambos, el PSS más alto corresponde a HU07 (92,1 y 89,1 MB).
- **Memoria en el dispositivo 1.** Consume más que los otros dos: PSS de 105,6 a 143,9 MB. Los valores más altos son HU09 (143,9 MB), HU08 (139,4 MB) y HU06 (134,9 MB). El Java Heap varía mucho, de 16,1 MB (HU05) a 63,5 MB (HU09). HU05 presenta además el Native Heap más alto (55,7 MB). Los dispositivos 2 y 3 no repiten estos picos. No se investigó la causa; conviene repetir la medición antes de atribuirlos a la app.
- **Arranque.** En las 24 corridas el arranque en frío va de 846 a 3165 ms, con medianas por dispositivo entre 1149 y 1350 ms. Solo dos corridas superan los 2 s, ambas de HU02: dispositivo 2 (2158 ms) y dispositivo 3 (3165 ms). El arranque se mide en `LoginActivity` antes de recorrer el flujo, por lo que las diferencias entre HU no se pueden atribuir a la HU.
- **Frames janky.** El dispositivo 1 marca entre 58,63 % y 79,63 %; los dispositivos 2 y 3, entre 85,66 % y 99,57 %. Al ser emuladores, estos porcentajes no permiten concluir cómo es la fluidez de la app en un teléfono real. En los dispositivos 2 y 3 el percentil 50 de `gpu` es 4950 ms, el último intervalo del histograma, y no se considera representativo. En el dispositivo 1 ese percentil está entre 15 y 19 ms, pero su percentil 90 de `gpu` también es 4950 ms.