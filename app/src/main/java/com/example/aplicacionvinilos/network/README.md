# network

Acceso remoto a la API REST.

- Interfaz de servicio de Retrofit con Gson y/o el singleton de `RequestQueue` de Volley.
- La URL base viene de `config/ApiConfig.BASE_URL`, inyectada según la variante de compilación.
- Entrega a los repositorios respuestas ya parseadas a `data/model`; sin UI ni LiveData.

## Patrón Service Adapter

Cada repositorio llega a la API a través de un adapter, nunca directo a Retrofit:

| Adapter | Repositorio | HU |
|---|---|---|
| `CollectorServiceAdapter` | `CollectorRepository` | HU05, HU06 |
| `AlbumServiceAdapter` (rama HU02) | `AlbumRepository` | HU02 |

El adapter traduce errores HTTP, de red y de parseo a `Result` con mensajes legibles.
