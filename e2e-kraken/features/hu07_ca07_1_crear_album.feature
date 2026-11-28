Feature: HU07 Crear un álbum

  # Escribe en el backend: cada corrida crea un álbum «Album E2E <número>».

  @user1 @mobile
  Scenario: CA07.1 El coleccionista crea un álbum
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    And lleno el formulario de crear álbum con datos válidos
    And toco el botón de guardar álbum
    Then veo el listado de álbumes
    And veo en el listado el álbum que creé
