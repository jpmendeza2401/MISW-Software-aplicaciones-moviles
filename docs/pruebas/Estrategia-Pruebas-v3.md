# Estrategia de pruebas · Aplicación Vinilos

| | |
|---|---|
| Versión | 3.0 (iteraciones 1 y 2) |
| Fecha | 15/11/2026 |
| Responsable de pruebas | Diana Cardozo |
| Reemplaza a | Versión 2.0 del 31/10/2026 |

**Cambios frente a la versión 2.0:** el alcance se ajusta a las cinco historias implementadas;
el ingreso a la app pasa a ser precondición de todas las pruebas; los identificadores y la
organización de archivos reflejan el código real; se agregan el análisis de desempeño, los
resultados de la iteración 2, los defectos reportados y la justificación de la versión mínima.

## 1. Objetivo

1. Verificar que la aplicación cumple los criterios de aceptación de las historias de usuario
   seleccionadas para las iteraciones 1 y 2.
2. Verificar que la aplicación funciona sin errores en el rango de versiones de Android que
   soporta (sección 5.2).
3. Medir el consumo de recursos de la aplicación y dejar registro de los resultados.
4. Publicar en el repositorio los resultados de las pruebas y reportar cada defecto como issue.

## 2. Alcance

### 2.1 Historias de usuario cubiertas

| HU | Historia | Rol | Iteración |
|---|---|---|---|
| HU01 | Consultar el catálogo de álbumes | Visitante y coleccionista | 1 |
| HU02 | Consultar la información detallada de un álbum | Visitante y coleccionista | 1 |
| HU05 | Consultar el listado de coleccionistas | Visitante y coleccionista | 2 |
| HU06 | Consultar la información detallada de un coleccionista | Visitante y coleccionista | 2 |
| HU09 | Publicar un comentario sobre un álbum | Coleccionista | 2 |

[COMPLETAR: confirmar la iteración de cada historia contra el backlog.]

### 2.2 Precondición: ingreso a la aplicación

La aplicación abre en una pantalla de inicio de sesión simulado (sin contraseña). No es una
historia de usuario, pero todas las pruebas pasan por ella:

- **Visitante**: botón de invitado. Puede consultar, no puede comentar.
- **Coleccionista**: ingresa con el correo de un coleccionista existente en el backend.
  Habilita el formulario de comentario (HU09).

### 2.3 Fuera de alcance

- HU07, HU08 y HU11: planeadas para la iteración 3.
- El backend: se trata como un sistema externo. Solo se verifica lo que la app muestra.
- iOS y tabletas.

## 3. Niveles y tipos de prueba

| Tipo | Herramienta | Qué cubre | Estado |
|---|---|---|---|
| Unitarias | JUnit + coroutines-test | ViewModels, repositorios y validadores (35 pruebas en 9 clases) | Implementadas |
| E2E automatizadas | Kraken (Cucumber + Appium/UiAutomator2) | Los criterios de aceptación de las 5 HU, sobre el APK | 14 escenarios |
| Compatibilidad | La suite E2E en más de un dispositivo | Versión mínima soportada y versión reciente | Parcial (sección 9) |
| Exploratorias manuales | Lista guía (sección 8) | Lo que no se puede automatizar: sin red, rotación, listas vacías | Antes de cada entrega |
| Desempeño | `medir.sh` (adb) y Android Studio Profiler | Arranque, memoria, fluidez y CPU por HU | Sección 7 |

Las pruebas unitarias cubren, entre otras, las reglas del comentario de HU09
(`CommentValidatorTest`: descripción vacía, límite de 600 caracteres, calificación de 1 a 5) y
el flujo de publicación (`AlbumViewModelCommentTest`). Por eso esos límites no se repiten en
las E2E. Se ejecutan con `./gradlew testLocalDebugUnitTest`.

## 4. Por qué Kraken

- Los escenarios se escriben en Gherkin, en español, y se leen como los criterios de aceptación.
- Prueba de caja negra sobre el APK: verifica lo mismo que ve el usuario.
- Un solo proyecto de pruebas para todo el equipo, dentro del repositorio (`e2e-kraken/`).

## 5. Entorno de pruebas

### 5.1 Dispositivos, aplicación y backend

| Elemento | Valor |
|---|---|
| Dispositivo físico | Honor NAA-LX3, Android 15 (API 35), 4 GB de RAM |
| Emulador | No se usó en las pruebas E2E; En el perfilado de desempeño (sección 7):sdk_gphone_arm64|
| Aplicación | APK de la variante `remoteDebug` |
| Backend | Desplegado por el equipo en AWS (`https://44-205-113-54.sslip.io/`) |
| Coleccionista de prueba | `manollo@caracol.com.co` (dato semilla del backend) |

### 5.2 Versión mínima soportada

El enunciado pide soporte desde Android 5.0 (API 21). La aplicación define `minSdk = 24`
(Android 7.0). La diferencia es una decisión del equipo:

- Google Play Services dejó de actualizarse para Android 5.x (API 21 y 22) en julio de 2024,
  cuando esas versiones sumaban menos del 1 % de los dispositivos activos; hoy solo se soporta
  desde Android 6.0 (API 23).
- Las librerías AndroidX que usa el proyecto tienen como mínimo por defecto API 23, valor que
  según Google cubre al 99 % de los usuarios.
- [COMPLETAR: razón por la que el proyecto usa 24 y no 23, o decisión de bajar a 23.]

Las pruebas de compatibilidad se ejecutan en el nivel mínimo soportado y en una versión
reciente. Soportar versiones anteriores queda registrado como deuda técnica.

### 5.3 Datos de prueba

- El backend es compartido: los datos pueden cambiar entre corridas. Los escenarios no dependen
  de valores fijos; trabajan sobre el primer álbum y el primer coleccionista del listado.
- El escenario CA09.1 **escribe** en el backend: cada corrida publica un comentario
  «Prueba E2E <número>» en el primer álbum.

## 6. Diseño de los escenarios E2E

### 6.1 Reglas

- **Un escenario por archivo `.feature`**, con las etiquetas `@user1 @mobile`. Kraken trata cada
  escenario de un archivo como un usuario distinto y solo lee los `.feature` que están
  directamente en `e2e-kraken/features/`.
- Nombre del archivo: `hu<NN>_ca<NN>_<n>_<descripción>.feature`.
- Los elementos se ubican por su `android:id` real (convención camelCase del código). La tabla
  de identificadores por pantalla está en el [README de Kraken](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/blob/dev/e2e-kraken/README.md).
- Si un escenario falla, Kraken detiene la corrida: los siguientes quedan como «no ejecutado».

### 6.2 Matriz de trazabilidad

| HU | Código | Escenario | Rol | Qué verifica |
|---|---|---|---|---|
| HU01 | CA01.1 | Ver el listado de álbumes | Visitante | Cada álbum muestra nombre y artista |
| HU01 | CA01.2 | Ir al detalle de un álbum | Visitante | El detalle corresponde al álbum tocado |
| HU02 | CA02.1 | Ver la información del álbum | Visitante | Año de cuatro dígitos, género, sello y descripción |
| HU02 | CA02.2 | Ver los tracks | Visitante | Nombre y duración con formato de tiempo, o mensaje de vacío |
| HU02 | CA02.3 | Ver los comentarios | Visitante | Calificación en estrellas y texto, o mensaje de vacío |
| HU02 | CA02.5 | Volver al listado | Visitante | La flecha regresa al listado |
| HU05 | CA05.1 | Ver el listado de coleccionistas | Visitante | Nombre y resumen («3 álbumes · 1 artista favorito») |
| HU05 | CA05.2 | Ir al detalle de un coleccionista | Visitante | El detalle corresponde al coleccionista tocado |
| HU06 | CA06.1 | Ver la información del coleccionista | Visitante | Nombre, identificador y pestañas Álbumes, Artistas y Notas |
| HU06 | CA06.2 | Cambiar entre pestañas | Visitante | Cada pestaña queda seleccionada al tocarla |
| HU09 | CA09.1 | Publicar un comentario | Coleccionista | Tras publicar, el formulario se limpia y no hay error |
| HU09 | CA09.2 | Comentario sin descripción | Coleccionista | Mensaje «Escribe la descripción del comentario.» |
| HU09 | CA09.3 | El visitante no puede comentar | Visitante | El formulario no existe para el visitante |
| HU09 | CA09.4 | Cancelar | Coleccionista | El formulario vuelve a su estado inicial |

[COMPLETAR: los códigos CA05, CA06 y CA09 son provisionales; alinearlos con los criterios de
aceptación del backlog.]

### 6.3 Limitaciones conocidas del diseño

- **Aserciones condicionales**: CA02.2 y CA02.3 aceptan tanto datos como el mensaje de lista
  vacía, porque el contenido del backend no es fijo. Si el primer álbum no tiene tracks, el
  formato de la duración no llega a verificarse.
- **Solo lo visible**: UiAutomator ve únicamente lo que está en pantalla. En listas largas se
  validan los elementos visibles, no todos.
- **No automatizado**: listado vacío y error de carga (requieren manipular el backend o la red)
  y volver desde el detalle de coleccionista (ver defecto de la flecha, sección 9.2). Se
  cubren en las pruebas exploratorias.

### 6.4 Escenarios apartados

Dos escenarios detectan defectos abiertos y fallan mientras existan. Están en
`e2e-kraken/pendientes/` con extensión `.txt` para que no detengan la corrida. Cuando el defecto
se corrija, pasan a la suite como pruebas de regresión.

| Escenario | Defecto que detecta |
|---|---|
| CA09.1b El comentario publicado aparece en la lista | Issue [#86](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/86) |
| CA09.5 La caja de comentario sigue visible con el teclado abierto | Issue [#87](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/87) |

## 7. Análisis de desempeño

### 7.1 Qué se mide y cómo

| Métrica | Cómo se obtiene | Para qué |
|---|---|---|
| Tiempo de arranque en frío | `adb shell am start -W` (TotalTime) | Detectar arranques lentos |
| Memoria (Java Heap, Native Heap, PSS total) | `adb shell dumpsys meminfo` | Detectar sobreconsumo y fugas |
| Fluidez (cuadros lentos, percentiles por cuadro) | `adb shell dumpsys gfxinfo` | Detectar retrasos en la interfaz |
| CPU | `adb shell top` | Detectar trabajo excesivo |

El procedimiento está automatizado en
[`docs/optimizacion/medir.sh`](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/blob/dev/docs/optimizacion/medir.sh): arranca la app en
frío, se recorre el flujo de una historia de usuario y captura las métricas al final. Se mide
una HU por corrida. Como apoyo se usan Android Studio Profiler, StrictMode en compilaciones de
depuración y LeakCanary.

### 7.2 Resultados

El informe completo, con los datos por dispositivo y por HU, está en
[`docs/optimizacion/perfilado.md`](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/blob/dev/docs/optimizacion/perfilado.md). Resumen:

| Dispositivo | Arranque | PSS total | Cuadros lentos |
|---|---|---|---|
| Honor NAA-LX3 (físico), recorrido completo | 2,2 s | 111,6 MB | 3,54 % |
| Emuladores (2), por HU | 1,1 a 3,0 s | 76 a 138 MB | No representativo |

- La memoria se mantiene estable entre historias en la mayoría de las mediciones.
- En el dispositivo físico, el 96 % de los cuadros se dibuja a tiempo.
- Durante las corridas E2E en el dispositivo físico no se observaron cierres inesperados ni
  bloqueos (ANR).

### 7.3 Limitaciones y deuda técnica

- Solo un dispositivo es físico. Los emuladores dibujan con la GPU del computador y sus cifras
  de fluidez no son representativas.
- El dispositivo físico se midió en un único recorrido, sin detalle por HU.
- La columna de CPU es una captura puntual, no el consumo durante el flujo.
- [COMPLETAR: comparación antes/después de las micro-optimizaciones en el dispositivo físico.
  Las mediciones existen (`naa-lx3-antes` y `naa-lx3-despues`) y falta incorporarlas al informe.]

Queda como deuda técnica repetir la medición por HU en dispositivos físicos y analizar los
picos de memoria observados en un emulador en HU06 y HU09.

## 8. Pruebas exploratorias manuales

Sesión de 30 minutos antes de cada entrega, sobre una historia implementada por otra persona:

1. Rotar el dispositivo en cada pantalla.
2. Activar modo avión antes de cargar un listado y al publicar un comentario.
3. Navegar hacia atrás con la flecha, con el gesto del sistema y con la barra inferior.
4. Desplazarse rápido en los listados.
5. Abrir el teclado en cada campo de texto y verificar que el contenido sigue visible.
6. Usar la app con un lector de pantalla (TalkBack) en al menos un flujo.

Los puntos 5 y 6 se agregaron en esta versión a raíz de los defectos 1 y 3 de la sección 9.2.

## 9. Resultados de la iteración 2

### 9.1 Pruebas E2E

| Dispositivo | Fecha | Escenarios | Pasaron | Fallaron |
|---|---|---|---|---|
| Honor NAA-LX3, Android 15 (físico) | 15/11/2026 | 14 | 14 | 0 |
| Emulador | No ejecutado | | | |

- Reporte con el detalle de cada escenario y su pantalla final:
  [`docs/pruebas/iteracion-2/`](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/tree/dev/docs/pruebas/iteracion-2).
- Videos, uno por escenario: [carpeta de videos](https://uniandes-my.sharepoint.com/:f:/r/personal/dp_cardozo_uniandes_edu_co/Documents/Attachments/Mobiles_evidencias_pruebas/2026-11-15_1955_NAA-LX3?d=wfdf50ebd3b544a4ebcb70e12275e2cd3&csf=1&web=1&e=IMt6en).
- Videos de demostración (recorrido por la app y defecto del comentario): [COMPLETAR: enlace].

### 9.2 Defectos reportados

| # | Defecto | HU | Severidad | Cómo se detectó | Issue |
|---|---|---|---|---|---|
| 1 | Al abrir el teclado, el formulario de comentario queda oculto (dispositivo físico) | HU09 | Alta | Exploratoria | [#87](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/87) |
| 2 | El comentario publicado no aparece en la lista de comentarios | HU09 | Alta | Escenario CA09.1 automatizado | [#86](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/86) |
| 3 | La flecha de volver del detalle de coleccionista no tiene descripción de accesibilidad | HU06 | Baja | Al automatizar HU06 | [#__] |

### 9.3 Observaciones pendientes de decisión

- **Datos de contacto en HU06**: el detalle del coleccionista muestra teléfono y correo a
  cualquier usuario, incluido el visitante. El equipo debe decidir si es el comportamiento
  esperado. No se registra como defecto mientras esté en discusión.
- **Versión mínima**: ver sección 5.2.

### 9.4 Lección de la iteración

Al agregar la barra de navegación inferior (HU05), el escenario CA02.3 empezó a fallar sin que
hubiera un defecto en la app: la barra tapaba los comentarios y la prueba dejó de verlos. Se
corrigió la prueba. Un cambio de interfaz puede romper escenarios de otras historias, por lo
que la suite completa debe ejecutarse antes de cada integración a `dev`.

## 10. Criterios de entrada y de salida

**Entrada** (para iniciar las pruebas de una historia):

- La historia está integrada en `dev` y el APK `remoteDebug` compila.
- El backend responde y existe el coleccionista de prueba.
- Los elementos que la prueba necesita tienen `android:id`.

**Salida** (para dar por probada la iteración):

- [x] Cada historia tiene al menos un escenario E2E automatizado.
- [x] La suite completa pasa en el dispositivo físico.
- [ ] La suite completa pasa en un segundo dispositivo.
- [x] No hay cierres inesperados durante las corridas.
- [x] Los resultados y las evidencias están publicados.
- [x] Los defectos están reportados como issues.
- [ ] No quedan defectos de severidad alta abiertos (hay dos, sección 9.2).

## 11. Proceso en cada iteración

1. **Inicio**: confirmar las historias y sus criterios de aceptación; acordar los identificadores.
2. **Desarrollo**: quien implementa la historia agrega los `android:id`; pruebas escribe el
   escenario.
3. **Antes de integrar a `dev`**: ejecutar la suite completa, no solo los escenarios de la
   historia nueva.
4. **Cierre**: corrida completa con evidencias, sesión exploratoria, reporte de defectos y
   actualización de esta estrategia.

## 12. Riesgos y mitigación

| Riesgo | Mitigación |
|---|---|
| Datos del backend compartido cambian | Escenarios sin valores fijos; aserciones condicionales documentadas |
| Un cambio de interfaz rompe escenarios de otras historias | Suite completa antes de cada integración |
| Un defecto solo aparece en dispositivos físicos | Ejecutar en físico, que es donde aparece; repetir en emulador cuando sea posible; pruebas exploratorias con teclado |
| Un identificador cambia en un layout | Tabla de identificadores en el README; el cambio debe actualizar los pasos |
| Los comentarios de prueba se acumulan en el backend | Texto identificable («Prueba E2E»); limpieza periódica |
| Entornos distintos entre computadores | Requisitos y problemas frecuentes documentados en el README de Kraken |

## 13. Ejecución

Instrucciones completas en el [README de Kraken](https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/blob/dev/e2e-kraken/README.md).

```bash
./gradlew assembleRemoteDebug     # 1. generar el APK
cd e2e-kraken
npx kraken-node run               # 2. ejecutar la suite
npm run evidencias                # 3. o ejecutarla grabando video y generando el reporte
```

## 14. Respuesta a los criterios de evaluación

| Criterio | Dónde se responde |
|---|---|
| La estrategia de pruebas es clara | Todo el documento |
| Tiene los componentes esperados | Objetivo (1), alcance (2), tipos (3), entorno (5), diseño (6), criterios (10), proceso (11), riesgos (12) |
| Incluye pruebas E2E para las HU de las iteraciones 1 y 2 | Sección 6.2 |
| Incluye la descripción del análisis de desempeño | Sección 7 |
| Escenarios implementados con Kraken y ejecutados exitosamente | Secciones 6.2 y 9.1 |
| Sin errores en dispositivos Android desde la versión mínima | Secciones 5.2 y 9.1 |
| Resultados reportados en el repositorio, con perfilamiento; errores como issues | Secciones 7.2, 9.1 y 9.2 |
