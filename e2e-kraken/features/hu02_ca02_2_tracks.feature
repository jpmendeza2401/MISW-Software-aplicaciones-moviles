Feature: HU02 Consultar la información detallada de un álbum

  @user1 @mobile
  Scenario: CA02.2 Ver los tracks del álbum
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    When toco el primer álbum de la lista
    Then veo el detalle del álbum que toqué
    And veo la sección "Tracks" con nombre y duración de cada track
