Feature: HU11 Agregar un álbum a la colección del coleccionista

  @user1 @mobile
  Scenario: CA11.2 No se agrega a la colección con precio cero
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    And lleno el formulario de crear álbum con datos válidos
    And activo la opción de agregar a mi colección
    And escribo el precio "0"
    And selecciono el estado "Active"
    And toco el botón de guardar álbum
    Then veo el error del formulario de álbum "Ingresa un precio entero mayor que 0."
