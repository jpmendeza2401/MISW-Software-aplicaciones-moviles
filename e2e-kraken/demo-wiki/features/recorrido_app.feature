Feature: Recorrido por la aplicación Vinilos

  # Video para la wiki: muestra las cinco historias de usuario de las iteraciones 1 y 2.
  # No es una prueba de la suite. Guion:
  #   inicio de sesión → listado de álbumes (HU01) → detalle de álbum (HU02) con el
  #   formulario de comentario (HU09) → listado de coleccionistas (HU05) → detalle de
  #   coleccionista y sus pestañas (HU06) → cerrar sesión.

  @user1 @mobile
  Scenario: Recorrido por la aplicación
    Given veo la pantalla de inicio de sesión
    And hago una pausa de 2 segundos
    When entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And hago una pausa de 2 segundos
    And deslizo la pantalla hacia abajo 2 veces
    And deslizo la pantalla hacia arriba 2 veces
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    And hago una pausa de 2 segundos
    And deslizo la pantalla hacia abajo 4 veces
    And hago una pausa de 3 segundos
    And toco la flecha de volver
    And veo el listado de álbumes
    And voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    And hago una pausa de 2 segundos
    And toco el primer coleccionista de la lista
    And veo el detalle del coleccionista que toqué
    And hago una pausa de 2 segundos
    And abro la pestaña "Artistas"
    And hago una pausa de 2 segundos
    And abro la pestaña "Notas"
    And hago una pausa de 2 segundos
    And cierro la sesión
    Then veo la pantalla de inicio de sesión
    And hago una pausa de 2 segundos
