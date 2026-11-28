Feature: HU07 Crear un álbum

  @user1 @mobile
  Scenario: CA07.3 No se crea un álbum con una portada que no es URL
    Given entro a la aplicación como coleccionista
    And espero a que cargue el listado de álbumes
    When toco el botón de crear álbum
    And veo el formulario de crear álbum
    And escribo el nombre del álbum "Album de prueba"
    And escribo la portada "portada-sin-url"
    And toco el botón de guardar álbum
    Then veo el error del formulario de álbum "Ingresa una URL válida (http o https)."
