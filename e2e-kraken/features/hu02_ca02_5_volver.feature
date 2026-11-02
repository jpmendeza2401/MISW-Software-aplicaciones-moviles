Feature: HU02 Consultar la información detallada de un álbum

  @user1 @mobile
  Scenario: CA02.5 Volver al listado con la flecha
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    When toco la flecha de volver
    Then veo el listado de álbumes
