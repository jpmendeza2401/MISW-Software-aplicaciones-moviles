# Comparativo de Lint, iteración 3: antes y después de los ajustes

Reportes de origen (variante `localDebug`, Android Lint 9.4.1):

- `docs/optimizacion/lint-antes-iteracion3/lint-results-localDebug.{html,txt,xml}`: código de `dev` en el commit `71308d7`.
- `docs/optimizacion/lint-despues-iteracion3/lint-results-localDebug.{html,txt,xml}`: rama `fix/lint-iteracion3`, creada desde ese commit.

## Resumen

| | Antes | Después | Diferencia |
|---|---|---|---|
| Errores | 1 | 0 | −1 |
| Advertencias | 51 | 1 | −50 |
| **Total** | **52** | **1** | **−51** |

## Detalle por tipo de hallazgo

| Hallazgo (id de Lint) | Severidad | Antes | Después | Estado |
|---|---|---|---|---|
| `NewApi` | Error | 1 | 0 | Corregido |
| `UnusedAttribute` | Advertencia | 18 | 0 | Corregido |
| `SetTextI18n` | Advertencia | 12 | 0 | Corregido |
| `HardcodedText` | Advertencia | 9 | 0 | Corregido |
| `GradleDependency` | Advertencia | 3 | 0 | Corregido |
| `Overdraw` | Advertencia | 3 | 0 | Corregido |
| `PluralsCandidate` | Advertencia | 3 | 0 | Corregido |
| `TextFields` | Advertencia | 1 | 0 | Corregido |
| `UseCompoundDrawables` | Advertencia | 1 | 0 | Corregido |
| `AndroidGradlePluginVersion` | Advertencia | 1 | 1 | Pendiente |

Ningún hallazgo se ocultó con `tools:ignore`, `@SuppressLint` ni cambios en la configuración de Lint en esta iteración. En `NewApi` se usó `tools:targetApi="28"`, que declara desde qué versión aplica el atributo.

## Qué se corrigió

### Error
- **`NewApi` (1):** el estilo `Groove.Text.Label` usaba `android:textFontWeight` (API 28) con `minSdk` 24. Se marcó con `tools:targetApi="28"`, igual que los demás estilos.

### Advertencias
- **`UnusedAttribute` (18):** las fuentes `manrope.xml` y `playfair_display.xml` en `res/font/` conservan solo los atributos `app:`. Las versiones con atributos `android:` están en `res/font-v26/`, que se usa desde la API 26. Este hallazgo había quedado pendiente en la iteración 2.
- **`SetTextI18n` (12):** los textos del detalle y de las tarjetas del coleccionista pasan a cadenas con parámetros. En las calificaciones con estrellas, el texto se arma en una variable antes de asignarlo.
- **`HardcodedText` (9):** los textos de los layouts pasan a recursos (los nuevos están en `strings_lint.xml`). El texto de ejemplo "MB" pasa a `tools:text` y la flecha "›" de las tarjetas se reemplaza por el ícono `ic_chevron_right`.
- **`Overdraw` (3):** se quitó el fondo de la vista raíz en los listados y en el detalle del coleccionista. El mismo color lo pinta la ventana mediante el tema `Theme.AplicacionVinilos.FondoOscuro`.
- **`PluralsCandidate` (3):** tres mensajes de validación pasan a `plurals` y el código usa `getQuantityString`.
- **`GradleDependency` (3):** `activity-ktx` pasa de 1.8.0 a 1.13.0; `lifecycle-livedata-ktx` y `lifecycle-viewmodel-ktx`, de 2.8.7 a 2.11.1.
- **`TextFields` (1):** el campo de fecha usa `inputType="date"`. Sigue sin ser enfocable y la fecha se elige con el selector.
- **`UseCompoundDrawables` (1):** el ícono y el texto del pie del inicio de sesión quedan en un solo `TextView`.

## Qué sigue pendiente

- **`AndroidGradlePluginVersion` (1):** se intentó actualizar el plugin de Android de 9.4.1 a 9.5.3. La compilación falló al resolver dependencias porque no se pudo acceder a `repo.maven.apache.org` ("Host desconocido"). Se volvió a 9.4.1. No se llegó a comprobar si 9.5.3 es compatible con el proyecto; conviene reintentar cuando ese repositorio sea accesible.

## Verificación

- `assembleLocalDebug` y `assembleRemoteDebug` compilan.
- `testLocalDebugUnitTest`: 61 pruebas, 0 fallos.
- `lintLocalDebug`: 0 errores y 1 advertencia. El conteo se toma del reporte, porque con `abortOnError = false` la compilación termina bien aunque haya errores.
- No se probaron las pantallas en un dispositivo ni se ejecutaron las pruebas instrumentadas o de Kraken. Falta revisar visualmente los fondos de las tres pantallas, las fuentes (en API 24-25 y en 26 o superior), el pie del inicio de sesión, las flechas de las tarjetas y el campo de fecha.

## Nota sobre la integración

La rama `fix/accesibilidad-scanner` no está incluida en la base de este comparativo y modifica algunos de los mismos archivos (entre ellos `activity_login.xml`, `activity_collector_detail.xml`, `item_collector.xml`, `themes.xml`). Al unir ambas ramas pueden aparecer conflictos. En particular:

- Las dos definen la cadena `coleccionista_tab_albumes`, con valores distintos. Debe quedar una sola definición, con el valor "Sus álbumes" del ajuste de accesibilidad.
- En el pie del inicio de sesión se deben conservar el `TextView` único y los colores del ajuste de accesibilidad.

Después de unirlas hay que volver a ejecutar Lint y Accessibility Scanner.

## Conclusión

Los hallazgos de Lint bajaron de 52 a 1 y el único error quedó corregido. La advertencia restante es la versión del plugin de Android, que no se pudo actualizar por la falla de acceso al repositorio.
