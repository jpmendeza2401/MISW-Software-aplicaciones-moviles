Feature: HU05 Consultar el listado de coleccionistas

  @user1 @mobile
  Scenario: CA05.2 Ir al detalle de un coleccionista
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    And voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    When toco el primer coleccionista de la lista
    Then veo el detalle del coleccionista que toqué
