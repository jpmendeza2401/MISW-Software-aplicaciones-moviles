Feature: Demostración del defecto «El comentario publicado no aparece en la lista»

  # Video para la wiki y para el issue. No es una prueba de la suite: no verifica el
  # defecto, solo lo muestra. Guion:
  #   1. Se ven los comentarios actuales del álbum.
  #   2. Se escribe y publica un comentario («Prueba E2E <número>»).
  #   3. La app avisa «Comentario publicado» y limpia el formulario.
  #   4. El comentario no aparece en la lista.
  #   5. Se sale del álbum y se vuelve a entrar: tampoco aparece.

  @user1 @mobile
  Scenario: El comentario publicado no aparece en la lista
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    And me desplazo hasta el elemento "commentText"
    And hago una pausa de 3 segundos
    When escribo un comentario nuevo
    And hago una pausa de 3 segundos
    And toco el botón de publicar comentario
    And hago una pausa de 4 segundos
    Then el contador del comentario marca "0 / 600"
    And hago una pausa de 3 segundos
    When toco la flecha de volver
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    And deslizo la pantalla hacia abajo 4 veces
    And hago una pausa de 4 segundos
