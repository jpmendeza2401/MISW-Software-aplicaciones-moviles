# viewmodels

Capa ViewModel.

- Un ViewModel por pantalla (por ejemplo `AlbumViewModel`), que extiende `ViewModel`.
- Guarda un `MutableLiveData` privado y lo expone como `LiveData` de solo lectura.
- Carga los datos mediante el repositorio dentro de `viewModelScope`.
- Publica los estados de carga, éxito y error; nunca guarda referencias a View, Fragment ni Activity.
