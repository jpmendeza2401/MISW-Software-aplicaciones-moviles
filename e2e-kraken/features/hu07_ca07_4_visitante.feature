Feature: HU07 Crear un álbum

  @user1 @mobile
  Scenario: CA07.4 El visitante no puede crear álbumes
    Given entro a la aplicación como visitante
    And espero a que cargue el listado de álbumes
    Then no veo el elemento "createAlbumButton"
