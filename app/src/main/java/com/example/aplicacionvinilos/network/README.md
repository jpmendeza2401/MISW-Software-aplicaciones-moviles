# network

Acceso remoto a la API REST.

- Interfaces de servicio de Retrofit (`ApiService`, y `AlbumService` para `GET albums/{id}`) con Gson.
- La URL base viene de `config/ApiConfig.BASE_URL`, inyectada según la variante de compilación.
- Entrega a los repositorios respuestas ya parseadas a `data/model`; sin UI ni LiveData.

## Patrón Service Adapter

Cada repositorio llega a la API a través de un adapter, nunca directo a Retrofit:

| Adapter | Repositorio | HU |
|---|---|---|
| `AlbumListServiceAdapter` | `AlbumListRepository` | HU01 |
| `AlbumServiceAdapter` | `AlbumRepository` | HU02 |
| `AlbumBandServiceAdapter` | `AlbumBandRepository` | ejemplo banda |
| `CollectorServiceAdapter` | `CollectorRepository` | HU05, HU06 |

El `try/catch` vive una sola vez en `ApiCall.execute`: traduce errores HTTP, de red y de parseo a `Result` con mensajes legibles. Un adapter nuevo es solo una línea por endpoint.
