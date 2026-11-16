Feature: HU09 Publicar un comentario sobre un álbum

  @user1 @mobile
  Scenario: CA09.4 Cancelar limpia el formulario
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    When escribo un comentario nuevo
    Then el contador del comentario ya no está en cero
    When toco el botón de cancelar comentario
    Then el contador del comentario marca "0 / 600"
