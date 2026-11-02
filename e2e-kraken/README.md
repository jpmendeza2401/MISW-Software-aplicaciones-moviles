# Pruebas E2E con Kraken · Vinilos

Escenarios automatizados de las HU de la iteración 1. Ver la estrategia de pruebas para el detalle.

| HU | Escenario | Archivo |
|---|---|---|
| HU01 | CA01.1 Ver el listado | `features/hu01_ca01_1_ver_listado.feature` |
| HU01 | CA01.2 Ir al detalle | `features/hu01_ca01_2_ir_al_detalle.feature` |
| HU02 | CA02.1 Información | `features/hu02_ca02_1_informacion.feature` |
| HU02 | CA02.2 Tracks | `features/hu02_ca02_2_tracks.feature` |
| HU02 | CA02.3 Comentarios | `features/hu02_ca02_3_comentarios.feature` |
| HU02 | CA02.5 Volver | `features/hu02_ca02_5_volver.feature` |

> **Un escenario por archivo.** En Kraken cada escenario de un `.feature` es un usuario
> distinto (`@user1`, `@user2`…), así que no se pueden poner dos escenarios `@user1` en el
> mismo archivo. Además Kraken solo lee los `.feature` que están directamente en `features/`.

Pendientes: CA01.4 y CA02.6 dependen de la pantalla de elección de rol. Están en
`pendientes/` (con extensión `.txt` para que Kraken no los ejecute); al activarlos, cada
escenario va en su propio archivo dentro de `features/`.

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
./gradlew assembleRemoteDebug

# 2. Despertar el backend (Render duerme el servidor)
curl -s https://backvynils-q6yc.onrender.com/albums > /dev/null

# 3. Encender el emulador o conectar el celular y confirmar
adb devices

# 4. Ejecutar
cd e2e-kraken
npx kraken-node run
```

El reporte queda en `e2e-kraken/reports/`.

## Ids usados

Son los `android:id` reales del código: `main`, `albumsList`, `albumName`, `albumArtist`,
`nameText`, `performersText`, `releaseDateText`, `genreText`, `recordLabelText`,
`descriptionText`, `tracksList`, `trackName`, `trackDuration`, `tracksEmptyText`,
`commentsList`, `commentStars`, `commentText`, `commentsEmptyText`.
La flecha de volver se busca por su `contentDescription` («Volver»).
Si alguien cambia un id en un layout, debe actualizar `features/mobile/step_definitions/vinilos_steps.js`.
