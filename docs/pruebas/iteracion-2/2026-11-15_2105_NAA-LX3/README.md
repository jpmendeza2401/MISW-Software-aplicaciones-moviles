# Resultados de las pruebas E2E · Vinilos

**Los 14 escenarios pasaron.**

| Dato | Valor |
|---|---|
| Fecha de la corrida | 15 de noviembre de 2026 a las 9:05 p. m. |
| Dispositivo | NAA-LX3 · Dispositivo físico |
| Sistema | Android 15 (API 35) |
| Versión probada | `feature/e2e-kraken-iteracion2` @ `db471f9` + cambios sin confirmar · APK remoteDebug |
| Herramienta | Kraken (Cucumber + Appium) |
| Duración de las pruebas | 317.5 s |
| Videos | Carpeta `videos/` (no versionada en el repositorio) |

El detalle paso a paso está en `index.html` (descargar y abrir en el navegador).

## Resumen por historia de usuario

| HU | Historia | Escenarios | Pasaron | Fallaron | No ejecutados |
|---|---|---:|---:|---:|---:|
| HU01 | Consultar catálogo de álbumes | 2 | 2 | 0 | 0 |
| HU02 | Consultar la información detallada de un álbum | 4 | 4 | 0 | 0 |
| HU05 | Consultar el listado de coleccionistas | 2 | 2 | 0 | 0 |
| HU06 | Consultar la información detallada de un coleccionista | 2 | 2 | 0 | 0 |
| HU09 | Publicar un comentario sobre un álbum | 4 | 4 | 0 | 0 |

## Detalle por escenario

| HU | Escenario | Resultado | Duración | Pantalla final | Video |
|---|---|---|---:|---|---|
| HU01 | CA01.1 Ver el listado de álbumes | ✅ pasó | 17.3 s | [captura](capturas/HU01/hu01_ca01_1_ver_listado.png) | `hu01_ca01_1_ver_listado.mp4` |
| HU01 | CA01.2 Ir al detalle de un álbum | ✅ pasó | 18.6 s | [captura](capturas/HU01/hu01_ca01_2_ir_al_detalle.png) | `hu01_ca01_2_ir_al_detalle.mp4` |
| HU02 | CA02.1 Ver la información del álbum | ✅ pasó | 21.0 s | [captura](capturas/HU02/hu02_ca02_1_informacion.png) | `hu02_ca02_1_informacion.mp4` |
| HU02 | CA02.2 Ver los tracks del álbum | ✅ pasó | 19.6 s | [captura](capturas/HU02/hu02_ca02_2_tracks.png) | `hu02_ca02_2_tracks.mp4` |
| HU02 | CA02.3 Ver los comentarios del álbum | ✅ pasó | 22.8 s | [captura](capturas/HU02/hu02_ca02_3_comentarios.png) | `hu02_ca02_3_comentarios.mp4` |
| HU02 | CA02.5 Volver al listado con la flecha | ✅ pasó | 21.0 s | [captura](capturas/HU02/hu02_ca02_5_volver.png) | `hu02_ca02_5_volver.mp4` |
| HU05 | CA05.1 Ver el listado de coleccionistas | ✅ pasó | 19.2 s | [captura](capturas/HU05/hu05_ca05_1_ver_listado.png) | `hu05_ca05_1_ver_listado.mp4` |
| HU05 | CA05.2 Ir al detalle de un coleccionista | ✅ pasó | 20.7 s | [captura](capturas/HU05/hu05_ca05_2_ir_al_detalle.png) | `hu05_ca05_2_ir_al_detalle.mp4` |
| HU06 | CA06.1 Ver la información del coleccionista | ✅ pasó | 22.3 s | [captura](capturas/HU06/hu06_ca06_1_informacion.png) | `hu06_ca06_1_informacion.mp4` |
| HU06 | CA06.2 Cambiar entre álbumes, artistas y notas | ✅ pasó | 24.0 s | [captura](capturas/HU06/hu06_ca06_2_pestanas.png) | `hu06_ca06_2_pestanas.mp4` |
| HU09 | CA09.1 El coleccionista publica un comentario | ✅ pasó | 29.9 s | [captura](capturas/HU09/hu09_ca09_1_publicar.png) | `hu09_ca09_1_publicar.mp4` |
| HU09 | CA09.2 No se publica un comentario sin descripción | ✅ pasó | 27.1 s | [captura](capturas/HU09/hu09_ca09_2_descripcion_vacia.png) | `hu09_ca09_2_descripcion_vacia.mp4` |
| HU09 | CA09.3 El visitante no puede comentar | ✅ pasó | 24.9 s | [captura](capturas/HU09/hu09_ca09_3_visitante.png) | `hu09_ca09_3_visitante.mp4` |
| HU09 | CA09.4 Cancelar limpia el formulario | ✅ pasó | 29.0 s | [captura](capturas/HU09/hu09_ca09_4_cancelar.png) | `hu09_ca09_4_cancelar.mp4` |

## Escenarios apartados por defectos conocidos

No hacen parte de la corrida: fallan mientras exista el defecto que detectan. Se reactivan como pruebas de regresión cuando el defecto se corrija.

| Escenario | Motivo | Archivo |
|---|---|---|
| CA09.1b El comentario publicado aparece en la lista | DEFECTO CONOCIDO (issue #86: https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/86): la app muestra «Comentario publicado» pero el comentario nuevo no se ve en la lista del detalle del álbum. Detectado por este escenario el 15/11/2026 en Honor NAA-LX3 (Android 15) y confirmado manualmente. Este escenario FALLA mientras el defecto exista; por eso está en pendientes/ (.txt). Al corregirse, moverlo a features/ como .feature: queda como prueba de regresión. | `e2e-kraken/pendientes/hu09_comentario_no_aparece_bug.feature.txt` |
| CA09.5 La caja de comentario sigue visible con el teclado abierto | DEFECTO CONOCIDO (issue #87: https://github.com/jpmendeza2401/MISW-Software-aplicaciones-moviles/issues/87): en dispositivo físico, al abrir el teclado el formulario queda reducido a una franja y la caja de texto deja de verse. Este escenario FALLA mientras el defecto exista; por eso está en pendientes/ (.txt) y no se ejecuta con la suite. Activarlo (mover a features/ como .feature) cuando el defecto se corrija, para que quede como prueba de regresión. Requiere agregar el paso «toco la caja de descripción del comentario» (clic sin cerrar teclado). | `e2e-kraken/pendientes/hu09_teclado_bug.feature.txt` |

_Generado por `e2e-kraken/scripts/evidencias.js`._
