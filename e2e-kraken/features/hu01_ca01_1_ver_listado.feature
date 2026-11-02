Feature: HU01 Consultar catálogo de álbumes

  @user1 @mobile
  Scenario: CA01.1 Ver el listado de álbumes
    Given abro la aplicación
    When espero a que cargue el listado de álbumes
    Then veo al menos un álbum con nombre y artista
