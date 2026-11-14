# Comparativo de Lint: antes y después de los ajustes

Reportes de origen (variante `localDebug`, Android Lint 9.4.1):

- `docs/optimizacion/lint-antes/lint-results-localDebug.{html,txt,xml}`
- `docs/optimizacion/lint-despues/lint-results-localDebug.{html,txt,xml}`

## Resumen

| | Antes | Después | Diferencia |
|---|---|---|---|
| Errores | 4 | 0 | −4 |
| Advertencias | 57 | 46 | −11 |
| **Total** | **61** | **46** | **−15** |

## Detalle por tipo de hallazgo

| Hallazgo (id de Lint) | Severidad | Antes | Después | Estado |
|---|---|---|---|---|
| `NewApi` | Error | 3 | 0 | Corregido |
| `DiffUtilEquals` | Error | 1 | 0 | Corregido |
| `UnusedResources` | Advertencia | 9 | 0 | Corregido |
| `Overdraw` | Advertencia | 5 | 3 | Parcial |
| `UnusedAttribute` | Advertencia | 18 | 18 | Sin cambio |
| `SetTextI18n` | Advertencia | 12 | 12 | Sin cambio |
| `HardcodedText` | Advertencia | 9 | 9 | Sin cambio |
| `GradleDependency` | Advertencia | 3 | 3 | Sin cambio |
| `UseCompoundDrawables` | Advertencia | 1 | 1 | Sin cambio |

## Qué se corrigió y por qué

### Errores (4 → 0)
- **`NewApi` (3):** los estilos de `themes.xml` usaban `android:textFontWeight`, que existe desde la API 28, mientras que la app soporta desde la API 24. En dispositivos antiguos el atributo se ignora o puede provocar inconsistencias. Se marcó con `tools:targetApi="28"`, que declara que el atributo solo aplica donde existe.
- **`DiffUtilEquals` (1):** `ItemDiffCallback` compara elementos genéricos con `==`, y Lint no puede comprobar que `T` implemente `equals()`. Todos los elementos que lo usan (`Track`, `Comment`) son `data class`, por lo que la comparación es correcta; se anotó con `@SuppressLint("DiffUtilEquals")` y un comentario que lo justifica.

### Advertencias de rendimiento y tamaño
- **`UnusedResources` (9 → 0):** se eliminaron los recursos que nada usaba: los colores `black` y `white`, y los textos `collector_name_placeholder`, `collector_id_placeholder`, `collectors_title`, `collectors_subtitle`, `collectors_error`, `login_titulo` y `login_email_hint`. Menos recursos significan un APK algo menor y un `resources.arsc` más pequeño.
- **`Overdraw` (5 → 3):** `activity_login.xml` y `activity_album_detail.xml` pintaban un fondo en la vista raíz que el tema ya pinta, de modo que cada píxel se dibujaba dos veces. Se quitó el fondo redundante y la GPU deja de dibujar una capa de más en esas pantallas.

## Qué sigue pendiente

- **`Overdraw` (3):** quedan `activity_album_list.xml`, `activity_collector_list.xml` y `activity_collector_detail.xml`, cuya vista raíz declara el fondo `#100F0D`. No se tocaron.
- **`UnusedAttribute` (18):** son las familias de fuentes `manrope.xml` y `playfair_display.xml`. Lint avisa de que `android:font`, `android:fontStyle` y `android:fontWeight` solo se leen desde la API 26 y la app soporta desde la 24. Se añadieron los atributos equivalentes `app:` para las APIs anteriores, pero Lint sigue emitiendo el aviso, así que este cambio **no lo resolvió** y habría que revisar otro enfoque.
- **`SetTextI18n` (12) y `HardcodedText` (9):** textos escritos directamente en código y en layouts en vez de en `strings.xml`, lo que dificulta traducir la app. No afectan al rendimiento ni a los ANR, por lo que quedaron fuera del alcance.
- **`GradleDependency` (3):** hay versiones más recientes de `activity-ktx` y de `lifecycle-livedata-ktx` y `lifecycle-viewmodel-ktx`. Actualizar dependencias es un cambio aparte y no se hizo.
- **`UseCompoundDrawables` (1):** un `LinearLayout` con ícono y texto en `activity_login.xml` podría ser un único `TextView` con ícono. Es una optimización menor.

## Conclusión

Los 4 errores de Lint quedaron corregidos y se redujo el total de hallazgos de 61 a 46 (−25 %). Lo corregido son los errores de compatibilidad con APIs antiguas, los recursos sin usar y parte del `Overdraw`. Lo que queda son avisos sin impacto en ANR ni en memoria (internacionalización y versiones de dependencias), tres casos de `Overdraw` y el aviso `UnusedAttribute` de las fuentes, que sigue sin resolverse.
