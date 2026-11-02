Feature: HU02 Consultar la información detallada de un álbum

  @user1 @mobile
  Scenario: CA02.1 Ver la información del álbum
    Given abro la aplicación
    And espero a que cargue el listado de álbumes
    When toco el primer álbum de la lista
    Then veo el detalle del álbum que toqué
    And veo el año de lanzamiento con cuatro dígitos
    And veo el género y el sello discográfico
    And veo la descripción del álbum
