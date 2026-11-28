Feature: HU07 Crear un álbum

  @user1 @mobile
  Scenario: CA07.2 No se crea un álbum con el formulario vacío
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    And toco el botón de guardar álbum
    Then veo el error del formulario de álbum "El nombre debe tener al menos 3 caracteres."
