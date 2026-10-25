# data/repository

Única fuente de datos para los ViewModels.

- Un repositorio por entidad (por ejemplo `AlbumRepository`).
- Llama a la capa `network` y devuelve modelos de `data/model`.
- Convierte los errores en un resultado para que el ViewModel no maneje excepciones ni detalles HTTP.
- Decide el origen de los datos (red ahora, caché o Room después).
- No debe referenciar Activities, Fragments ni Views.
