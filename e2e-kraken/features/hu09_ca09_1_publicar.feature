Feature: HU09 Publicar un comentario sobre un álbum

  # La app confirma la publicación limpiando el formulario (contador en cero y sin error).
  # Que el comentario aparezca en la lista se verifica en
  # pendientes/hu09_comentario_no_aparece_bug.feature.txt (defecto conocido, ver issue).

  @user1 @mobile
  Scenario: CA09.1 El coleccionista publica un comentario
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    And veo el formulario para comentar a mi nombre
    When escribo un comentario nuevo
    And toco el botón de publicar comentario
    Then el contador del comentario marca "0 / 600"
    And no veo el elemento "commentErrorText"
