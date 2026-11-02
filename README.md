# MISW - Aplicaciones Móviles

Aplicación móvil desarrollada como parte del curso **Desarrollo de Software para Aplicaciones Móviles (MISW)** de la Universidad de los Andes.

## Requisitos

Para instalar y ejecutar la aplicación se necesita:

- Un dispositivo Android compatible.
- El archivo APK generado por el proyecto.
- Espacio de almacenamiento disponible.
- Permitir temporalmente la instalación de aplicaciones desde fuentes desconocidas, si Android lo solicita.

## Instalación de la aplicación

### 1. Descargar el APK

Descarga el archivo APK desde la sección de entregables o desde el enlace de descarga compartido por el equipo.

### 2. Transferir el archivo

Si descargaste el APK desde un computador, transfiérelo al dispositivo Android mediante USB, Google Drive u otro medio disponible.

### 3. Instalar el APK

1. Abre el administrador de archivos del dispositivo.
2. Ubica el archivo `.apk`.
3. Selecciónalo para iniciar la instalación.
4. Si Android muestra una advertencia de seguridad, autoriza la instalación desde esa fuente.
5. Presiona **Instalar** y espera a que finalice el proceso.

### 4. Abrir la aplicación

Una vez instalada, selecciona **Abrir** o busca el icono de la aplicación en el menú de aplicaciones del dispositivo.

## Tecnologías utilizadas

- **Kotlin:** lenguaje de programación.
- **Android:** plataforma de desarrollo.
- **Gradle:** herramienta de construcción del proyecto.

## Estructura del proyecto

```text
MISW-Software-aplicaciones-moviles/
├── app/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Generación del APK

Para generar el APK desde Android Studio:

1. Abre el proyecto.
2. Espera a que Gradle finalice la sincronización.
3. Selecciona **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. Espera a que termine la compilación.
5. Selecciona **Locate** para encontrar el archivo generado.

Generalmente, el APK de depuración queda en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Consideraciones

- El APK de depuración está destinado a pruebas.
- Para distribuir una versión de producción, se recomienda generar un APK firmado.
- La instalación puede variar según la versión de Android y el fabricante del dispositivo.

## Equipo de desarrollo

Proyecto académico desarrollado en el marco del curso MISW - Software de Aplicaciones Móviles.
