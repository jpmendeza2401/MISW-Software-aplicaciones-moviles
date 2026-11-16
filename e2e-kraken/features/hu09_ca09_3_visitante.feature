Feature: HU09 Publicar un comentario sobre un álbum

  @user1 @mobile
  Scenario: CA09.3 El visitante no puede comentar
    Given entro a la aplicación como visitante
    And espero a que cargue el listado de álbumes
    When toco el primer álbum de la lista
    Then veo el detalle del álbum que toqué
    And no veo el formulario para comentar
