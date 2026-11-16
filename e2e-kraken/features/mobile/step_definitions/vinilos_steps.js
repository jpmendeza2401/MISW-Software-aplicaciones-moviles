// Pasos E2E de la app Vinilos: ingreso (LoginActivity), HU01, HU02, HU05, HU06 y HU09.
// Los ids son los android:id reales de los layouts (convención camelCase del código).
const { Given, When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

const PKG = 'com.example.aplicacionvinilos';
const byId = (id) => `id=${PKG}:id/${id}`;

// Tiempos de espera
const CARGA_BACKEND_MS = 90000; // margen amplio por si el backend o la red están lentos
const ESPERA_MS = 30000;

// Coleccionista de prueba (datos semilla del backend).
const CORREO_COLECCIONISTA =
  process.env.VINILOS_CORREO_COLECCIONISTA || 'manollo@caracol.com.co';

// Desplaza la pantalla (ScrollView) hasta que el elemento con ese id sea visible.
// Necesario porque UiAutomator solo ve lo que está en pantalla.
async function scrollHasta(driver, id) {
  const selector =
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    `.scrollIntoView(new UiSelector().resourceId("${PKG}:id/${id}"))`;
  const el = await driver.$(selector);
  await el.waitForDisplayed({ timeout: ESPERA_MS });
  return el;
}

// Igual que scrollHasta, pero no falla si el elemento no existe: devuelve true/false.
// Sirve para elementos opcionales (filas de una lista que puede estar vacía).
async function scrollSiExiste(driver, id) {
  const selector =
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    `.scrollIntoView(new UiSelector().resourceId("${PKG}:id/${id}"))`;
  const el = await driver.$(selector);
  return el.isExisting();
}

// Cierra el teclado en pantalla si está abierto. No falla si ya estaba cerrado.
async function cerrarTeclado(driver) {
  try {
    if (await driver.isKeyboardShown()) await driver.hideKeyboard();
  } catch (e) {
    // Algunos dispositivos no reportan el estado del teclado: se continúa.
  }
  await driver.pause(500); // la pantalla se reacomoda al cerrarse el teclado
}

// Desplaza hasta un elemento por su texto exacto. Devuelve true/false.
async function scrollHastaTexto(driver, texto) {
  const selector =
    'android=new UiScrollable(new UiSelector().scrollable(true))' +
    `.scrollIntoView(new UiSelector().text("${texto}"))`;
  const el = await driver.$(selector);
  return el.isExisting();
}

async function existe(driver, id) {
  const els = await driver.$$(byId(id));
  return els.length > 0;
}

// ---------- Inicio ----------

// La app abre en LoginActivity. Hay dos formas de entrar:
//  - visitante: botón «guestButton» (no necesita backend).
//  - coleccionista: correo en «emailInput» + «loginButton» (consulta el backend).
async function esperarLogin(driver) {
  const boton = await driver.$(byId('guestButton'));
  await boton.waitForDisplayed({
    timeout: ESPERA_MS,
    timeoutMsg: 'No apareció la pantalla de inicio de sesión',
  });
}

async function entrarComoVisitante(driver) {
  await esperarLogin(driver);
  await (await driver.$(byId('guestButton'))).click();
}

async function entrarComoColeccionista(driver, correo) {
  await esperarLogin(driver);
  const campo = await driver.$(byId('emailInput'));
  await campo.setValue(correo);
  // El teclado puede tapar el botón: se cierra antes de tocarlo.
  await cerrarTeclado(driver);
  await (await driver.$(byId('loginButton'))).click();
}

// «abro la aplicación» = entrar como visitante. Así los escenarios de consulta
// (HU01, HU02, HU05, HU06) no dependen de que exista un coleccionista en el backend.
Given('abro la aplicación', async function () {
  await entrarComoVisitante(this.driver);
});

Given('entro a la aplicación como visitante', async function () {
  await entrarComoVisitante(this.driver);
});

// El correo debe existir en el backend (GET /collectors). Se puede cambiar sin tocar
// el código con la variable de entorno VINILOS_CORREO_COLECCIONISTA.
Given('entro a la aplicación como coleccionista', async function () {
  await entrarComoColeccionista(this.driver, CORREO_COLECCIONISTA);
});

Given('entro a la aplicación con el correo {string}', async function (correo) {
  await entrarComoColeccionista(this.driver, correo);
});

// ---------- HU01 · Listado ----------

When('espero a que cargue el listado de álbumes', async function () {
  const lista = await this.driver.$(byId('albumsList'));
  await lista.waitForDisplayed({
    timeout: CARGA_BACKEND_MS,
    timeoutMsg: 'El listado de álbumes no cargó (¿servidor dormido o error de red?)',
  });
});

Then('veo al menos un álbum con nombre y artista', async function () {
  const nombres = await this.driver.$$(byId('albumName'));
  const artistas = await this.driver.$$(byId('albumArtist'));
  assert.ok(nombres.length > 0, 'La lista de álbumes está vacía');
  assert.strictEqual(
    artistas.length, nombres.length,
    'Hay álbumes que no muestran la línea de artista'
  );
  for (const n of nombres) {
    assert.ok((await n.getText()).trim().length > 0, 'Hay un álbum sin nombre');
  }
  for (const a of artistas) {
    assert.ok((await a.getText()).trim().length > 0, 'Hay un álbum con la línea de artista vacía');
  }
});

When('toco el primer álbum de la lista', async function () {
  const nombres = await this.driver.$$(byId('albumName'));
  assert.ok(nombres.length > 0, 'No hay álbumes para tocar');
  this.albumSeleccionado = (await nombres[0].getText()).trim();
  await nombres[0].click();
});

Then('veo el listado de álbumes', async function () {
  const lista = await this.driver.$(byId('albumsList'));
  await lista.waitForDisplayed({ timeout: ESPERA_MS });
});

// ---------- HU02 · Detalle ----------

Then('veo el detalle del álbum que toqué', async function () {
  const nombre = await this.driver.$(byId('nameText'));
  await nombre.waitForDisplayed({
    timeout: CARGA_BACKEND_MS,
    timeoutMsg: 'No se abrió el detalle del álbum',
  });
  const texto = (await nombre.getText()).trim();
  assert.strictEqual(
    texto, this.albumSeleccionado,
    `El detalle muestra "${texto}" pero se tocó "${this.albumSeleccionado}"`
  );
});

Then('veo el año de lanzamiento con cuatro dígitos', async function () {
  const anio = await this.driver.$(byId('releaseDateText'));
  await anio.waitForDisplayed({ timeout: ESPERA_MS });
  const texto = (await anio.getText()).trim();
  assert.match(texto, /^\d{4}$/, `El año "${texto}" no tiene cuatro dígitos`);
});

Then('veo el género y el sello discográfico', async function () {
  for (const id of ['genreText', 'recordLabelText']) {
    const el = await scrollHasta(this.driver, id);
    assert.ok((await el.getText()).trim().length > 0, `El campo ${id} está vacío`);
  }
});

Then('veo la descripción del álbum', async function () {
  const el = await scrollHasta(this.driver, 'descriptionText');
  assert.ok((await el.getText()).trim().length > 0, 'La descripción está vacía');
});

Then('veo la sección {string} con nombre y duración de cada track', async function (titulo) {
  await scrollHasta(this.driver, 'tracksList');
  const encabezado = await this.driver.$(`android=new UiSelector().text("${titulo}")`);
  assert.ok(await encabezado.isDisplayed(), `No se ve el título "${titulo}"`);

  // Que el borde de la lista esté en pantalla no garantiza que sus filas lo estén
  // (la barra inferior las puede tapar): se desplaza hasta la primera fila.
  // El álbum puede no tener tracks: en ese caso debe verse el mensaje de vacío.
  if (!(await scrollSiExiste(this.driver, 'trackName'))) {
    assert.ok(await scrollSiExiste(this.driver, 'tracksEmptyText'),
      'No hay tracks y tampoco se muestra el mensaje «Aún no hay tracks»');
    return;
  }
  const nombres = await this.driver.$$(byId('trackName'));
  const duraciones = await this.driver.$$(byId('trackDuration'));
  assert.strictEqual(duraciones.length, nombres.length, 'Hay tracks sin duración');
  for (const d of duraciones) {
    const t = (await d.getText()).trim();
    assert.match(t, /^\d{1,2}:\d{2}(:\d{2})?$/, `Duración con formato inesperado: "${t}"`);
  }
});

Then('veo la sección {string} con estrellas y texto de cada comentario', async function (titulo) {
  await scrollHasta(this.driver, 'commentsList');
  const encabezado = await this.driver.$(`android=new UiSelector().text("${titulo}")`);
  assert.ok(await encabezado.isDisplayed(), `No se ve el título "${titulo}"`);

  // Se desplaza hasta el texto del primer comentario (las estrellas van encima,
  // así que quedan visibles también). Si no hay comentarios debe verse el mensaje de vacío.
  if (!(await scrollSiExiste(this.driver, 'commentText'))) {
    assert.ok(await scrollSiExiste(this.driver, 'commentsEmptyText'),
      'No hay comentarios y tampoco se muestra el mensaje «Aún no hay comentarios»');
    return;
  }
  // Solo se validan los comentarios visibles: el último puede quedar cortado por la
  // barra inferior, por eso no se exige el mismo número de estrellas que de textos.
  const textos = await this.driver.$$(byId('commentText'));
  const estrellas = await this.driver.$$(byId('commentStars'));
  assert.ok(estrellas.length > 0, 'Hay comentarios pero ninguno muestra calificación');
  for (const t of textos) {
    assert.ok((await t.getText()).trim().length > 0, 'Hay un comentario sin texto');
  }
  for (const e of estrellas) {
    assert.ok((await e.getText()).includes('★'), 'Un comentario no muestra estrellas');
  }
});

When('toco la flecha de volver', async function () {
  // Flecha de la toolbar: su contentDescription es «Volver»
  const flecha = await this.driver.$('~Volver');
  await flecha.waitForDisplayed({ timeout: ESPERA_MS });
  await flecha.click();
});

// ---------- HU05 · Listado de coleccionistas ----------

When('voy a la sección de coleccionistas', async function () {
  // Ítem de la barra inferior (su id es el del menú: navigation_collectors)
  const item = await this.driver.$(byId('navigation_collectors'));
  await item.waitForDisplayed({ timeout: ESPERA_MS });
  await item.click();
});

When('espero a que cargue el listado de coleccionistas', async function () {
  const lista = await this.driver.$(byId('collectorsList'));
  await lista.waitForDisplayed({
    timeout: CARGA_BACKEND_MS,
    timeoutMsg: 'El listado de coleccionistas no cargó (¿error de red?)',
  });
});

Then('veo al menos un coleccionista con nombre y resumen de su colección', async function () {
  const nombres = await this.driver.$$(byId('collectorName'));
  const resumenes = await this.driver.$$(byId('collectorStats'));
  assert.ok(nombres.length > 0, 'La lista de coleccionistas está vacía');
  for (const n of nombres) {
    assert.ok((await n.getText()).trim().length > 0, 'Hay un coleccionista sin nombre');
  }
  assert.ok(resumenes.length > 0, 'Ningún coleccionista muestra el resumen de su colección');
  for (const r of resumenes) {
    const t = (await r.getText()).trim();
    // Ej.: «3 álbumes · 1 artista favorito»
    assert.match(t, /^\d+ álbum(es)? · \d+ artistas? favoritos?$/,
      `Resumen con formato inesperado: "${t}"`);
  }
});

When('toco el primer coleccionista de la lista', async function () {
  const nombres = await this.driver.$$(byId('collectorName'));
  assert.ok(nombres.length > 0, 'No hay coleccionistas para tocar');
  this.coleccionistaSeleccionado = (await nombres[0].getText()).trim();
  await nombres[0].click();
});

Then('veo el listado de coleccionistas', async function () {
  const lista = await this.driver.$(byId('collectorsList'));
  await lista.waitForDisplayed({ timeout: ESPERA_MS });
});

// ---------- HU06 · Detalle de coleccionista ----------

Then('veo el detalle del coleccionista que toqué', async function () {
  // «collectorId» solo existe en la pantalla de detalle (en el listado no).
  const id = await this.driver.$(byId('collectorId'));
  await id.waitForDisplayed({
    timeout: CARGA_BACKEND_MS,
    timeoutMsg: 'No se abrió el detalle del coleccionista',
  });
  const nombre = await this.driver.$(byId('collectorName'));
  const texto = (await nombre.getText()).trim();
  assert.strictEqual(
    texto, this.coleccionistaSeleccionado,
    `El detalle muestra "${texto}" pero se tocó "${this.coleccionistaSeleccionado}"`
  );
});

Then('veo el identificador del coleccionista', async function () {
  const id = await this.driver.$(byId('collectorId'));
  const texto = (await id.getText()).trim();
  assert.match(texto, /^ID: \d+$/, `Identificador con formato inesperado: "${texto}"`);
});

Then('veo las pestañas {string}, {string} y {string}', async function (a, b, c) {
  await scrollHasta(this.driver, 'detailTabs');
  for (const titulo of [a, b, c]) {
    const tab = await this.driver.$(`android=new UiSelector().text("${titulo}")`);
    assert.ok(await tab.isDisplayed(), `No se ve la pestaña "${titulo}"`);
  }
});

When('abro la pestaña {string}', async function (titulo) {
  await scrollHasta(this.driver, 'detailTabs');
  const tab = await this.driver.$(`android=new UiSelector().text("${titulo}")`);
  await tab.waitForDisplayed({ timeout: ESPERA_MS });
  await tab.click();
});

Then('la pestaña {string} queda seleccionada', async function (titulo) {
  const tab = await this.driver.$(`android=new UiSelector().text("${titulo}")`);
  assert.strictEqual(await tab.getAttribute('selected'), 'true',
    `La pestaña "${titulo}" no quedó seleccionada`);
});

// ---------- HU09 · Publicar comentario ----------

Then('veo el formulario para comentar a mi nombre', async function () {
  const autor = await scrollHasta(this.driver, 'commentAuthorText');
  const texto = (await autor.getText()).trim();
  assert.match(texto, /^Publicando como .+/, `Texto de autor inesperado: "${texto}"`);
});

Then('no veo el formulario para comentar', async function () {
  // Se recorre toda la pantalla buscándolo: para el visitante no debe existir.
  assert.strictEqual(await scrollSiExiste(this.driver, 'addCommentSection'), false,
    'El visitante no debería ver el formulario para comentar');
});

When('escribo un comentario nuevo', async function () {
  // Texto único por ejecución: el backend es compartido y así se distingue de los demás.
  this.comentario = `Prueba E2E ${Date.now()}`;
  const campo = await scrollHasta(this.driver, 'commentDescriptionInput');
  await campo.setValue(this.comentario);
  await cerrarTeclado(this.driver);
});

When('toco el botón de publicar comentario', async function () {
  const boton = await scrollHasta(this.driver, 'publishCommentButton');
  await boton.click();
});

When('toco el botón de cancelar comentario', async function () {
  const boton = await scrollHasta(this.driver, 'cancelCommentButton');
  await boton.click();
});

Then('veo mi comentario en la lista de comentarios', async function () {
  // Tras publicar, la app recarga el álbum. Se reintenta hasta que el texto aparezca.
  const limite = Date.now() + ESPERA_MS;
  let visible = false;
  while (!visible && Date.now() < limite) {
    visible = await scrollHastaTexto(this.driver, this.comentario);
    if (!visible) await this.driver.pause(1000);
  }
  assert.ok(visible, `El comentario "${this.comentario}" no apareció en la lista`);
});

Then('veo el error de comentario {string}', async function (mensaje) {
  const error = await scrollHasta(this.driver, 'commentErrorText');
  assert.strictEqual((await error.getText()).trim(), mensaje);
});

Then('el contador del comentario marca {string}', async function (valor) {
  const contador = await scrollHasta(this.driver, 'commentCounterText');
  assert.strictEqual((await contador.getText()).trim(), valor);
});

Then('el contador del comentario ya no está en cero', async function () {
  const contador = await scrollHasta(this.driver, 'commentCounterText');
  const texto = (await contador.getText()).trim();
  assert.ok(!texto.startsWith('0 /'), `El contador sigue en cero: "${texto}"`);
});

// ---------- Demostraciones (videos para la wiki, carpeta demo-wiki/) ----------
// Pasos de navegación y pausas para que el video se pueda seguir. No verifican nada nuevo.

// Desliza el dedo por la pantalla. 'abajo' muestra el contenido que está más abajo.
async function deslizar(driver, direccion) {
  let tam;
  try { tam = await driver.getWindowRect(); } catch (e) { tam = await driver.getWindowSize(); }
  const x = Math.round(tam.width / 2);
  const yBaja = Math.round(tam.height * 0.70);
  const yAlta = Math.round(tam.height * 0.35);
  const [desde, hasta] = direccion === 'abajo' ? [yBaja, yAlta] : [yAlta, yBaja];
  await driver.touchAction([
    { action: 'press', x, y: desde },
    { action: 'wait', ms: 400 },
    { action: 'moveTo', x, y: hasta },
    'release',
  ]);
  await driver.pause(700);
}

When('hago una pausa de {int} segundos', async function (segundos) {
  await this.driver.pause(segundos * 1000);
});

When('deslizo la pantalla hacia abajo {int} veces', async function (veces) {
  for (let i = 0; i < veces; i++) await deslizar(this.driver, 'abajo');
});

When('deslizo la pantalla hacia arriba {int} veces', async function (veces) {
  for (let i = 0; i < veces; i++) await deslizar(this.driver, 'arriba');
});

When('me desplazo hasta el elemento {string}', async function (id) {
  await scrollHasta(this.driver, id);
});

When('cierro la sesión', async function () {
  const item = await this.driver.$(byId('navigation_logout'));
  await item.waitForDisplayed({ timeout: ESPERA_MS });
  await item.click();
});

Then('veo la pantalla de inicio de sesión', async function () {
  await esperarLogin(this.driver);
});

// ---------- Genéricos (para HU futuras) ----------

Then('veo el elemento {string}', async function (id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: ESPERA_MS });
});

Then('no veo el elemento {string}', async function (id) {
  assert.strictEqual(await existe(this.driver, id), false, `El elemento "${id}" no debería verse`);
});

Then('no veo el botón {string}', async function (texto) {
  const botones = await this.driver.$$(`android=new UiSelector().textContains("${texto}")`);
  assert.strictEqual(botones.length, 0, `El botón "${texto}" no debería verse`);
});
