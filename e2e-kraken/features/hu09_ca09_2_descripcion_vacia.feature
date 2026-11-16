Feature: HU09 Publicar un comentario sobre un álbum

  @user1 @mobile
  Scenario: CA09.2 No se publica un comentario sin descripción
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    When toco el botón de publicar comentario
    Then veo el error de comentario "Escribe la descripción del comentario."
