# data/model

Clases de datos que representan las entidades de la API (Album, Band, Collector, Comment, ...).

- `data class` de Kotlin, sin dependencias del framework Android.
- Anotar con `@SerializedName` de Gson cuando el nombre del JSON difiera del de la propiedad.
- Sin lógica de red, parseo ni UI.
- Reflejan las entidades que expone BackAlbums.
