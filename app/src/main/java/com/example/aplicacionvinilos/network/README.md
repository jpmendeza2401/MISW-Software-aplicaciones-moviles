# network

Acceso remoto a la API REST.

- Interfaz de servicio de Retrofit con Gson y/o el singleton de `RequestQueue` de Volley.
- La URL base viene de `config/ApiConfig.BASE_URL`, inyectada según la variante de compilación.
- Entrega a los repositorios respuestas ya parseadas a `data/model`; sin UI ni LiveData.
