// Pasos E2E de la app Vinilos (HU01 y HU02).
// Los ids son los android:id reales de los layouts (convención camelCase del código).
const { Given, When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

const PKG = 'com.example.aplicacionvinilos';
const byId = (id) => `id=${PKG}:id/${id}`;

// Tiempos de espera
const CARGA_BACKEND_MS = 90000; // Render puede tardar ~1 min si el servidor estaba dormido
const ESPERA_MS = 30000;

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

async function existe(driver, id) {
  const els = await driver.$$(byId(id));
  return els.length > 0;
}

// ---------- Inicio ----------

Given('abro la aplicación', async function () {
  // Hoy la app abre directamente en el listado de álbumes (AlbumListActivity).
  // Cuando exista la pantalla de rol, este paso se reemplaza por «entro a la aplicación como …».
  const main = await this.driver.$(byId('main'));
  await main.waitForExist({ timeout: ESPERA_MS });
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

  // El álbum puede no tener tracks: en ese caso debe verse el mensaje de vacío.
  const nombres = await this.driver.$$(byId('trackName'));
  if (nombres.length === 0) {
    assert.ok(await existe(this.driver, 'tracksEmptyText'),
      'No hay tracks y tampoco se muestra el mensaje «Aún no hay tracks»');
    return;
  }
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

  const textos = await this.driver.$$(byId('commentText'));
  if (textos.length === 0) {
    assert.ok(await existe(this.driver, 'commentsEmptyText'),
      'No hay comentarios y tampoco se muestra el mensaje «Aún no hay comentarios»');
    return;
  }
  const estrellas = await this.driver.$$(byId('commentStars'));
  assert.strictEqual(estrellas.length, textos.length, 'Hay comentarios sin calificación');
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
