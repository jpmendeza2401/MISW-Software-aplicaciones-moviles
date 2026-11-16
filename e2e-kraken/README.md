# Pruebas E2E con Kraken · Vinilos

Escenarios automatizados de las cinco historias de usuario de las iteraciones 1 y 2
(HU01, HU02, HU05, HU06 y HU09). Ver la estrategia de pruebas en la wiki para el detalle.

| HU | Escenario | Archivo |
|---|---|---|
| HU01 | CA01.1 Ver el listado de álbumes | `features/hu01_ca01_1_ver_listado.feature` |
| HU01 | CA01.2 Ir al detalle de un álbum | `features/hu01_ca01_2_ir_al_detalle.feature` |
| HU02 | CA02.1 Información del álbum | `features/hu02_ca02_1_informacion.feature` |
| HU02 | CA02.2 Tracks | `features/hu02_ca02_2_tracks.feature` |
| HU02 | CA02.3 Comentarios | `features/hu02_ca02_3_comentarios.feature` |
| HU02 | CA02.5 Volver al listado | `features/hu02_ca02_5_volver.feature` |
| HU05 | CA05.1 Ver el listado de coleccionistas | `features/hu05_ca05_1_ver_listado.feature` |
| HU05 | CA05.2 Ir al detalle de un coleccionista | `features/hu05_ca05_2_ir_al_detalle.feature` |
| HU06 | CA06.1 Información del coleccionista | `features/hu06_ca06_1_informacion.feature` |
| HU06 | CA06.2 Pestañas (álbumes, artistas, notas) | `features/hu06_ca06_2_pestanas.feature` |
| HU09 | CA09.1 El coleccionista publica un comentario | `features/hu09_ca09_1_publicar.feature` |
| HU09 | CA09.2 No se publica sin descripción | `features/hu09_ca09_2_descripcion_vacia.feature` |
| HU09 | CA09.3 El visitante no puede comentar | `features/hu09_ca09_3_visitante.feature` |
| HU09 | CA09.4 Cancelar limpia el formulario | `features/hu09_ca09_4_cancelar.feature` |

> **Un escenario por archivo.** En Kraken cada escenario de un `.feature` es un usuario
> distinto (`@user1`, `@user2`…), así que no se pueden poner dos escenarios `@user1` en el
> mismo archivo. Además Kraken solo lee los `.feature` que están directamente en `features/`.

> **Los códigos CA05, CA06 y CA09 son provisionales**: falta alinearlos con los criterios de
> aceptación del documento de historias de usuario.

## Ingreso a la app

La app abre en la pantalla de inicio de sesión. Los pasos disponibles son:

- `Given abro la aplicación` o `Given entro a la aplicación como visitante`: botón de invitado.
- `Given entro a la aplicación como coleccionista`: ingresa con el correo de un coleccionista
  que exista en el backend. Por defecto `manollo@caracol.com.co`; se cambia con la variable de
  entorno `VINILOS_CORREO_COLECCIONISTA`.

## Escenarios apartados (`pendientes/`)

Tienen extensión `.txt` para que Kraken no los ejecute.

| Archivo | Por qué está apartado |
|---|---|
| `hu09_teclado_bug.feature.txt` | Defecto conocido: en dispositivo físico el teclado reduce el formulario de comentario a una franja. El escenario falla mientras exista el defecto. |
| `hu09_comentario_no_aparece_bug.feature.txt` | Defecto conocido: el comentario publicado no aparece en la lista. El escenario falla mientras exista el defecto. |
| `hu01_roles.feature.txt`, `hu02_roles.feature.txt` | Versión antigua, previa al inicio de sesión. La verificación por rol que sí aplica hoy está en CA09.3. |

Cuando un defecto se corrija, su escenario se mueve a `features/` (un escenario por archivo,
extensión `.feature`) y queda como prueba de regresión.

## Requisitos (una vez por computador)

- Node.js LTS, Java (el JDK de Android Studio sirve) y Android SDK con `adb` en el PATH.
- Appium con el driver UiAutomator2:

```bash
npm install -g appium
appium driver install uiautomator2
```

## Instalación del proyecto de pruebas

Desde la raíz del repo:

```bash
# 1. Generar la plantilla oficial en una carpeta temporal (para no sobrescribir nuestros archivos)
#    y copiar solo los hooks de Kraken (features/support)
mkdir -p /tmp/kraken-plantilla && (cd /tmp/kraken-plantilla && npx -y kraken-node gen)
cp -R /tmp/kraken-plantilla/features/support e2e-kraken/features/

# 2. Instalar dependencias y verificar
cd e2e-kraken
npm install
npx kraken-node doctor   # todo debe salir en verde
```

## Ejecución

```bash
# 1. Generar el APK (desde la raíz del repo)
./gradlew assembleRemoteDebug      # si dice "permission denied": chmod +x gradlew

# 2. Comprobar que el backend responde
curl -s https://44-205-113-54.sslip.io/albums > /dev/null

# 3. Encender el emulador o conectar el celular y confirmar que aparece UNO en estado "device"
adb devices

# 4. Ejecutar
cd e2e-kraken
npx kraken-node run
```

Kraken deja un reporte por escenario en `e2e-kraken/reports/` (carpeta ignorada por git).

Cosas que conviene saber:

- **Sin dispositivo, Kraken no avisa bien**: termina con un error `ENOENT ... data.json`.
  Significa que `adb devices` está vacío.
- **Si un escenario falla, Kraken detiene la corrida** y los siguientes no se ejecutan.
- **CA09.1 escribe en el backend**: cada corrida publica un comentario «Prueba E2E <número>»
  en el primer álbum del listado.
- En celulares Honor/Huawei, activar «Permitir depuración ADB en modo Solo carga» para que la
  depuración USB no se desactive al reconectar.

## Corrida con evidencias (videos + reporte HTML)

```bash
cd e2e-kraken
npm run evidencias
```

Ejecuta la misma suite grabando la pantalla del dispositivo y al final arma un reporte
consolidado. Todo queda en `docs/pruebas/iteracion-2/<fecha>_<modelo>/`:

| Elemento | Contenido |
|---|---|
| `index.html` | Reporte: resumen por HU, pasos y duración de cada escenario, video y pantalla final |
| `README.md` | Resumen en tablas; GitHub lo muestra al entrar a la carpeta |
| `resultados.json` | Los mismos datos en bruto |
| `videos/<HU>/` | Un `.mp4` por escenario (**no se versiona**, ver abajo) |
| `capturas/<HU>/` | Pantalla final de cada escenario |

Una carpeta por corrida y por dispositivo, así se pueden guardar las evidencias del emulador
y del celular por separado. La grabación solo se activa con este comando; `npx kraken-node run`
no graba nada.

**Los videos no van al repositorio** (pesan decenas de MB; `docs/pruebas/**/videos/` está en
`.gitignore`). Se suben a una carpeta externa y se enlazan en el reporte:

```bash
node scripts/evidencias.js --solo-reporte ../docs/pruebas/iteracion-2/<carpeta> --videos-url "<enlace a la carpeta>"
```

Ese comando no repite las pruebas: solo rehace `index.html` y `README.md` con el enlace.

## Videos de demostración para la wiki

```bash
cd e2e-kraken
npm run videos-wiki
```

Graba dos videos en `docs/videos-wiki/` (carpeta ignorada por git):

| Video | Qué muestra |
|---|---|
| `bug_comentario_no_aparece.mp4` | El defecto: se publica un comentario, la app confirma, y el comentario no aparece ni al volver a entrar al álbum |
| `recorrido_app.mp4` | Recorrido por las cinco historias de usuario: inicio de sesión, álbumes, detalle, comentario, coleccionistas y cierre de sesión |

No hacen parte de la suite: viven en `demo-wiki/`, un proyecto Kraken aparte que reutiliza los
mismos pasos. Los guiones son los `.feature` de `demo-wiki/features/` y se pueden editar
(orden, pausas) sin afectar las pruebas.

## Ids usados

Son los `android:id` reales del código. Si alguien cambia un id en un layout, debe actualizar
`features/mobile/step_definitions/vinilos_steps.js`.

| Pantalla | Ids |
|---|---|
| Inicio de sesión | `guestButton`, `emailInput`, `loginButton` |
| Listado de álbumes | `albumsList`, `albumName`, `albumArtist` |
| Detalle de álbum | `nameText`, `performersText`, `releaseDateText`, `genreText`, `recordLabelText`, `descriptionText`, `tracksList`, `trackName`, `trackDuration`, `tracksEmptyText`, `commentsList`, `commentStars`, `commentText`, `commentsEmptyText` |
| Formulario de comentario | `addCommentSection`, `commentAuthorText`, `commentDescriptionInput`, `commentCounterText`, `commentErrorText`, `publishCommentButton`, `cancelCommentButton` |
| Listado de coleccionistas | `collectorsList`, `collectorName`, `collectorStats` |
| Detalle de coleccionista | `collectorId`, `collectorName`, `detailTabs` |
| Barra inferior | `navigation_albums`, `navigation_collectors`, `navigation_logout` |

La flecha de volver del detalle de álbum se busca por su `contentDescription` («Volver»).
