Feature: HU06 Consultar la información detallada de un coleccionista

  @user1 @mobile
  Scenario: CA06.2 Cambiar entre álbumes, artistas y notas
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    And voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    And toco el primer coleccionista de la lista
    And veo el detalle del coleccionista que toqué
    When abro la pestaña "Artistas"
    Then la pestaña "Artistas" queda seleccionada
    When abro la pestaña "Notas"
    Then la pestaña "Notas" queda seleccionada
