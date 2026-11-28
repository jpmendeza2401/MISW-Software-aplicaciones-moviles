Feature: HU11 Agregar un álbum a la colección del coleccionista

  @user1 @mobile
  Scenario: CA11.3 Precio y estado solo se piden al activar la opción
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    Then no veo el elemento "priceInput"
    When activo la opción de agregar a mi colección
    Then veo el campo de precio y estado
