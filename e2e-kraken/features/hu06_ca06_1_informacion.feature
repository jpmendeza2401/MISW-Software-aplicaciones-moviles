Feature: HU06 Consultar la información detallada de un coleccionista

  @user1 @mobile
  Scenario: CA06.1 Ver la información del coleccionista
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    And voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    When toco el primer coleccionista de la lista
    Then veo el detalle del coleccionista que toqué
    And veo el identificador del coleccionista
    And veo las pestañas "Sus álbumes", "Artistas" y "Notas"
