Feature: HU08 Asociar tracks a un álbum

  @user1 @mobile
  Scenario: CA08.3 No se agrega un track de menos de un minuto
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    And toco el primer álbum de la lista
    And veo el detalle del álbum que toqué
    When escribo el track "Track de prueba" con duración "0:30"
    And toco el botón de agregar track
    Then veo el error de track "La duración mínima es 1 minuto (1:00)."
