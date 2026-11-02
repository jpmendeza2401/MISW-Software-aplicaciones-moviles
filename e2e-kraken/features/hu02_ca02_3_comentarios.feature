Feature: HU02 Consultar la información detallada de un álbum

  @user1 @mobile
  Scenario: CA02.3 Ver los comentarios del álbum
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    When toco el primer álbum de la lista
    Then veo el detalle del álbum que toqué
    And veo la sección "Comentarios" con estrellas y texto de cada comentario
