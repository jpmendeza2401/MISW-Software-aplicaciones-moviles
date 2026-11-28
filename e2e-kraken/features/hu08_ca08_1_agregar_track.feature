Feature: HU08 Asociar tracks a un álbum

  # Escribe en el backend: cada corrida agrega un track «Track E2E <número>» al primer álbum.

  @user1 @mobile
  Scenario: CA08.1 El coleccionista agrega un track a un álbum
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    And veo el formulario para agregar tracks
    When escribo un track nuevo con duración "3:45"
    And toco el botón de agregar track
    Then el formulario de track queda vacío
    And veo el track que agregué en la lista de tracks
