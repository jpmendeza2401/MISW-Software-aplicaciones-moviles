# Plan: micro-optimizaciones, ANRs, memoria y perfilado

Rama: `feature/micro-optimizaciones` (desde `feature/login`).

- [x] 1. Línea base: Lint (`lintLocalDebug`) guardado en `docs/optimizacion/lint-antes/`
- [x] 2. Configurar bloque `lint {}` en `app/build.gradle.kts`
- [x] 3. Aplicar fixes de Lint (Performance/Correctness)
- [x] 4. Quitar dependencias sin uso (volley, fragment-ktx, okhttp-logging, dataBinding) y activar minify/shrink en release + reglas ProGuard
- [x] 5. ANRs: `OkHttpClient` con timeouts, `Application` con StrictMode (debug), sin I/O en main
- [x] 6. Memoria: Glide (override, placeholder/error, clear en onViewRecycled) y LeakCanary (debug)
- [x] 7. Perfilado: plantilla `docs/optimizacion/perfilado.md` + script `docs/optimizacion/medir.sh`
- [ ] 7b. Perfilado ejecutado en >=3 dispositivos físicos (lo hace el usuario)
- [x] 8. Tests: arreglar los 7 tests rotos y dejar la suite en verde
- [x] 9. Lint después (`docs/optimizacion/lint-despues/`), comparación y push

## Resultado
- Lint: 61 → 46 hallazgos; 4 errores → 0 (`NewApi`, `DiffUtilEquals`). Quedan avisos de i18n (`SetTextI18n`, `HardcodedText`), versiones de dependencias y `Overdraw` en 3 layouts; no son de rendimiento/ANR y se dejaron fuera a propósito.
- Release con R8 + shrinkResources compila (`assembleRemoteRelease`); falta probar el APK release en dispositivo (reglas Gson en `proguard-rules.pro`).
- 7b pendiente: ejecutar `docs/optimizacion/medir.sh` en 3 dispositivos físicos y llenar `docs/optimizacion/perfilado.md`.
- No se agregó caché HTTP de OkHttp (requiere Context y el backend no envía cabeceras de caché).
