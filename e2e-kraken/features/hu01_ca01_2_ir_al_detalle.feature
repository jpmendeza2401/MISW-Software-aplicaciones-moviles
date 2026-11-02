Feature: HU01 Consultar catálogo de álbumes

  @user1 @mobile
  Scenario: CA01.2 Ir al detalle de un álbum
    Given abro la aplicación
    When espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    Then veo el detalle del álbum que toqué
