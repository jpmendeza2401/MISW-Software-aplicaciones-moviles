Feature: HU11 Agregar un álbum a la colección del coleccionista

  # Escribe en el backend: crea un álbum y lo asocia al coleccionista de prueba con un
  # precio único por corrida, que es lo que se busca después en su detalle.

  @user1 @mobile
  Scenario: CA11.1 El coleccionista crea un álbum y lo agrega a su colección
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    And lleno el formulario de crear álbum con datos válidos
    And activo la opción de agregar a mi colección
    And escribo un precio único
    And selecciono el estado "Active"
    And toco el botón de guardar álbum
    And veo en el listado el álbum que creé
    And voy a la sección de coleccionistas
    And espero a que cargue el listado de coleccionistas
    And abro el detalle del coleccionista con el que ingresé
    And abro la pestaña "Álbumes"
    Then veo en sus álbumes el precio que registré
