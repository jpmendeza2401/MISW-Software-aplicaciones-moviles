# ui

Activities y Fragments (capa View). Layouts XML con ViewBinding.

- Solo dibujan el estado y reenvían los eventos del usuario al ViewModel.
- Observan el `LiveData` del ViewModel con `viewLifecycleOwner`.
- Sin llamadas de red ni lógica de negocio.
- `adapters/` contiene los adaptadores de RecyclerView.
