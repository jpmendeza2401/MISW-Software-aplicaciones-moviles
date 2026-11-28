Feature: HU08 Asociar tracks a un álbum

  @user1 @mobile
  Scenario: CA08.4 El visitante no puede agregar tracks
    Given entro a la aplicación como visitante
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    Then no veo el formulario para agregar tracks
