Feature: HU05 Consultar el listado de coleccionistas

  @user1 @mobile
  Scenario: CA05.1 Ver el listado de coleccionistas
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    When voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    Then veo al menos un coleccionista con nombre y resumen de su colección
