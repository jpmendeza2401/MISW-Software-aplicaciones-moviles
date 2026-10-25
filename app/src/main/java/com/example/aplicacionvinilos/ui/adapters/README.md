# ui/adapters

Adaptadores y ViewHolders de RecyclerView.

- Usar `ListAdapter` con `DiffUtil.ItemCallback` para recibir los datos.
- Enlazar cada fila con ViewBinding en el layout del ítem.
- Exponer los clics como lambdas; la navegación la hace el Fragment, no el adaptador.
