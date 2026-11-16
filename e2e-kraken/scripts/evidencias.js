#!/usr/bin/env node
// Corrida completa con evidencias: ejecuta la suite de Kraken grabando un video por escenario
// y arma un reporte HTML consolidado.
//
//   npm run evidencias                 (desde e2e-kraken/, con UN dispositivo en `adb devices`)
//
// Resultado en docs/pruebas/iteracion-2/<fecha>_<modelo>/
//   index.html         reporte consolidado (abrir en el navegador)
//   resultados.json    los mismos datos, en bruto
//   videos/<HU>/       un .mp4 por escenario
//   capturas/<HU>/     pantalla final de cada escenario
//
//   README.md          resumen que GitHub muestra al entrar a la carpeta
//
// Para rehacer el reporte de una corrida ya hecha (por ejemplo, tras subir los videos a una
// carpeta externa):
//   node scripts/evidencias.js --solo-reporte <carpeta> --videos-url <enlace>
const { spawnSync, execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const RAIZ = path.resolve(__dirname, '..');            // e2e-kraken/
const REPO = path.resolve(RAIZ, '..');
const SALIDA_BASE = path.join(REPO, 'docs', 'pruebas', 'iteracion-2');

const sh = (cmd, args) => {
  try { return execFileSync(cmd, args, { cwd: REPO, encoding: 'utf8' }).trim(); }
  catch (e) { return ''; }
};
const esc = (t) => String(t).replace(/[&<>"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
const seg = (ns) => (ns / 1e9).toFixed(1);

function dispositivo() {
  const lineas = sh('adb', ['devices']).split('\n').slice(1)
    .map((l) => l.trim().split(/\s+/)).filter((p) => p[1] === 'device');
  if (lineas.length === 0) {
    console.error('No hay ningún dispositivo en `adb devices`. Conecta el celular o enciende el emulador.');
    process.exit(1);
  }
  if (lineas.length > 1) {
    console.error('Hay más de un dispositivo conectado. Deja solo uno para que la evidencia sea de un único equipo.');
    process.exit(1);
  }
  const id = lineas[0][0];
  const prop = (p) => sh('adb', ['-s', id, 'shell', 'getprop', p]);
  return {
    id,
    modelo: prop('ro.product.model'),
    android: prop('ro.build.version.release'),
    api: prop('ro.build.version.sdk'),
    tipo: id.startsWith('emulator-') ? 'Emulador' : 'Dispositivo físico',
  };
}

// Lee los report.json que Kraken dejó en reports/ a partir de `desde` (uno por escenario).
function leerResultados(desde) {
  const porArchivo = {};
  const base = path.join(RAIZ, 'reports');
  if (!fs.existsSync(base)) return porArchivo;
  for (const dir of fs.readdirSync(base)) {
    const ruta = path.join(base, dir);
    let st; try { st = fs.statSync(ruta); } catch (e) { continue; }
    if (!st.isDirectory() || st.mtimeMs < desde) continue;
    for (const sub of fs.readdirSync(ruta)) {
      const json = path.join(ruta, sub, 'report.json');
      if (!fs.existsSync(json)) continue;
      let datos; try { datos = JSON.parse(fs.readFileSync(json, 'utf8')); } catch (e) { continue; }
      for (const feature of datos) {
        for (const el of feature.elements || []) {
          porArchivo[path.basename(feature.uri)] = { feature, el, mtime: st.mtimeMs };
        }
      }
    }
  }
  return porArchivo;
}

function recolectar(salida, desde, disp, codigoKraken) {
  const resultados = leerResultados(desde);
  const archivos = fs.readdirSync(path.join(RAIZ, 'features')).filter((f) => f.endsWith('.feature')).sort();
  const escenarios = archivos.map((archivo) => {
    const baseN = archivo.replace(/\.feature$/, '');
    const hu = baseN.slice(0, 4).toUpperCase();
    const texto = fs.readFileSync(path.join(RAIZ, 'features', archivo), 'utf8');
    const nombre = (texto.match(/^\s*Scenario:\s*(.+)$/m) || [])[1] || baseN;
    const titulo = (texto.match(/^\s*Feature:\s*(.+)$/m) || [])[1] || hu;
    const r = resultados[archivo];
    const e = { archivo, hu, titulo, nombre, estado: 'no ejecutado', duracion: 0, pasos: [], video: null, captura: null };
    if (r) {
      let ultimaImagen = null;
      for (const p of r.el.steps) {
        e.duracion += (p.result && p.result.duration) || 0;
        if (p.hidden) {
          if (p.result.status !== 'passed') {
            e.pasos.push({ texto: `(${p.keyword.trim()}: preparación o cierre)`, estado: p.result.status, duracion: p.result.duration || 0, error: p.result.error_message || '' });
          }
          continue;
        }
        const img = (p.embeddings || []).filter((x) => x.mime_type === 'image/png').pop();
        if (img) ultimaImagen = img.data;
        e.pasos.push({ texto: p.keyword + p.name, estado: p.result.status, duracion: p.result.duration || 0, error: p.result.error_message || '' });
      }
      e.estado = r.el.steps.every((p) => p.result.status === 'passed') ? 'pasó' : 'falló';
      if (ultimaImagen) {
        const dirCap = path.join(salida, 'capturas', hu);
        fs.mkdirSync(dirCap, { recursive: true });
        fs.writeFileSync(path.join(dirCap, `${baseN}.png`), Buffer.from(ultimaImagen, 'base64'));
        e.captura = `capturas/${hu}/${baseN}.png`;
      }
    }
    const video = path.join(salida, 'videos', hu, `${baseN}.mp4`);
    if (fs.existsSync(video) && fs.statSync(video).size > 0) e.video = `videos/${hu}/${baseN}.mp4`;
    return e;
  });

  // Escenarios apartados por defectos conocidos (pendientes/*_bug.feature.txt)
  const dirPend = path.join(RAIZ, 'pendientes');
  const apartados = (fs.existsSync(dirPend) ? fs.readdirSync(dirPend) : [])
    .filter((f) => /_bug\.feature\.txt$/.test(f)).sort().map((f) => {
      const t = fs.readFileSync(path.join(dirPend, f), 'utf8');
      return {
        archivo: `pendientes/${f}`,
        nombre: (t.match(/^\s*Scenario:\s*(.+)$/m) || [])[1] || f,
        motivo: t.split('\n').filter((l) => /^\s*#/.test(l)).map((l) => l.replace(/^\s*#\s?/, '')).join(' '),
      };
    });

  const total = escenarios.length;
  const n = (estado) => escenarios.filter((e) => e.estado === estado).length;
  const resumen = {
    fecha: new Date(desde).toLocaleString('es-CO', { dateStyle: 'long', timeStyle: 'short' }),
    dispositivo: disp,
    rama: sh('git', ['rev-parse', '--abbrev-ref', 'HEAD']),
    commit: sh('git', ['rev-parse', '--short', 'HEAD']),
    cambiosSinConfirmar: sh('git', ['status', '--porcelain', '--untracked-files=no']) !== '',
    apk: 'remoteDebug',
    total, pasaron: n('pasó'), fallaron: n('falló'), noEjecutados: n('no ejecutado'),
    duracionTotalSeg: Number(seg(escenarios.reduce((a, e) => a + e.duracion, 0))),
    codigoSalidaKraken: codigoKraken,
    inicioMs: desde,
    videosUrl: '',
  };
  return { resumen, escenarios, apartados };
}

// Escribe resultados.json, index.html (para abrir en el navegador) y README.md (GitHub lo
// muestra al entrar a la carpeta, cosa que no hace con el HTML).
function escribir(salida, datos) {
  fs.writeFileSync(path.join(salida, 'resultados.json'), JSON.stringify(datos, null, 2));
  fs.writeFileSync(path.join(salida, 'index.html'), html(datos.resumen, datos.escenarios, datos.apartados));
  fs.writeFileSync(path.join(salida, 'README.md'), markdown(datos.resumen, datos.escenarios, datos.apartados));
  return datos.resumen;
}

function markdown(r, escenarios, apartados) {
  const icono = { 'pasó': '✅ pasó', 'falló': '❌ falló', 'no ejecutado': '⚪ no ejecutado' };
  const hus = [...new Set(escenarios.map((e) => e.hu))];
  const veredicto = r.fallaron === 0 && r.noEjecutados === 0
    ? `Los ${r.total} escenarios pasaron.`
    : `${r.pasaron} de ${r.total} escenarios pasaron; ${r.fallaron} fallaron y ${r.noEjecutados} no se ejecutaron.`;
  const L = [];
  L.push('# Resultados de las pruebas E2E · Vinilos', '');
  L.push(`**${veredicto}**`, '');
  L.push('| Dato | Valor |', '|---|---|');
  L.push(`| Fecha de la corrida | ${r.fecha} |`);
  L.push(`| Dispositivo | ${r.dispositivo.modelo} · ${r.dispositivo.tipo} |`);
  L.push(`| Sistema | Android ${r.dispositivo.android} (API ${r.dispositivo.api}) |`);
  L.push(`| Versión probada | \`${r.rama}\` @ \`${r.commit}\`${r.cambiosSinConfirmar ? ' + cambios sin confirmar' : ''} · APK ${r.apk} |`);
  L.push(`| Herramienta | Kraken (Cucumber + Appium) |`);
  L.push(`| Duración de las pruebas | ${r.duracionTotalSeg} s |`);
  L.push(`| Videos | ${r.videosUrl ? `[Carpeta de videos](${r.videosUrl}), un video por escenario` : 'Carpeta `videos/` (no versionada en el repositorio)'} |`, '');
  L.push('El detalle paso a paso está en `index.html` (descargar y abrir en el navegador).', '');
  L.push('## Resumen por historia de usuario', '');
  L.push('| HU | Historia | Escenarios | Pasaron | Fallaron | No ejecutados |', '|---|---|---:|---:|---:|---:|');
  for (const hu of hus) {
    const es = escenarios.filter((e) => e.hu === hu);
    const c = (x) => es.filter((e) => e.estado === x).length;
    L.push(`| ${hu} | ${es[0].titulo.replace(/^HU\d+\s*/, '')} | ${es.length} | ${c('pasó')} | ${c('falló')} | ${c('no ejecutado')} |`);
  }
  L.push('', '## Detalle por escenario', '');
  L.push('| HU | Escenario | Resultado | Duración | Pantalla final | Video |', '|---|---|---|---:|---|---|');
  for (const e of escenarios) {
    const video = e.video ? `\`${path.basename(e.video)}\`` : 'sin video';
    L.push(`| ${e.hu} | ${e.nombre} | ${icono[e.estado]} | ${e.estado === 'no ejecutado' ? '' : seg(e.duracion) + ' s'} | ${e.captura ? `[captura](${e.captura})` : ''} | ${video} |`);
  }
  const fallidos = escenarios.filter((e) => e.estado === 'falló');
  if (fallidos.length) {
    L.push('', '## Escenarios fallidos', '');
    for (const e of fallidos) {
      const p = e.pasos.find((x) => x.estado !== 'passed') || {};
      L.push(`- **${e.nombre}**: falló en «${p.texto || '?'}». ${(p.error || '').split('\n')[0]}`);
    }
  }
  if (apartados.length) {
    L.push('', '## Escenarios apartados por defectos conocidos', '');
    L.push('No hacen parte de la corrida: fallan mientras exista el defecto que detectan. Se reactivan como pruebas de regresión cuando el defecto se corrija.', '');
    L.push('| Escenario | Motivo | Archivo |', '|---|---|---|');
    for (const a of apartados) L.push(`| ${a.nombre} | ${a.motivo} | \`e2e-kraken/${a.archivo}\` |`);
  }
  L.push('', '_Generado por `e2e-kraken/scripts/evidencias.js`._', '');
  return L.join('\n');
}

function html(r, escenarios, apartados) {
  const clase = { 'pasó': 'ok', 'falló': 'mal', 'no ejecutado': 'nd', passed: 'ok', failed: 'mal', skipped: 'nd', undefined: 'mal', pending: 'nd', ambiguous: 'mal' };
  const etiqueta = { passed: 'pasó', failed: 'falló', skipped: 'omitido', undefined: 'sin definir', pending: 'pendiente', ambiguous: 'ambiguo' };
  const hus = [...new Set(escenarios.map((e) => e.hu))];
  const veredicto = r.fallaron === 0 && r.noEjecutados === 0
    ? `Los ${r.total} escenarios pasaron.`
    : `${r.pasaron} de ${r.total} escenarios pasaron; ${r.fallaron} fallaron y ${r.noEjecutados} no se ejecutaron.`;

  const filaResumen = hus.map((hu) => {
    const es = escenarios.filter((e) => e.hu === hu);
    const c = (s) => es.filter((e) => e.estado === s).length;
    return `<tr><td><a href="#${hu}">${hu}</a></td><td>${esc(es[0].titulo.replace(/^HU\d+\s*/, ''))}</td>
      <td class="num">${es.length}</td><td class="num">${c('pasó')}</td><td class="num">${c('falló')}</td><td class="num">${c('no ejecutado')}</td></tr>`;
  }).join('\n');

  const secciones = hus.map((hu) => {
    const es = escenarios.filter((e) => e.hu === hu);
    return `<section id="${hu}"><h2>${esc(es[0].titulo)}</h2>` + es.map((e) => `
      <details ${e.estado === 'pasó' ? '' : 'open'}>
        <summary><span class="pill ${clase[e.estado]}">${e.estado}</span> <strong>${esc(e.nombre)}</strong>
          <span class="meta">${e.estado === 'no ejecutado' ? '' : seg(e.duracion) + ' s · '}${esc(e.archivo)}</span></summary>
        <div class="cuerpo">
          <div class="pasos">
            ${e.pasos.length === 0 ? '<p class="meta">Este escenario no se ejecutó en esta corrida (Kraken detiene la corrida cuando un escenario falla).</p>' : `
            <table><thead><tr><th>Paso</th><th>Resultado</th><th class="num">Duración</th></tr></thead><tbody>
            ${e.pasos.map((p) => `<tr><td>${esc(p.texto)}${p.error ? `<pre>${esc(p.error.split('\n').slice(0, 6).join('\n'))}</pre>` : ''}</td>
              <td><span class="pill ${clase[p.estado] || 'nd'}">${etiqueta[p.estado] || esc(p.estado)}</span></td><td class="num">${seg(p.duracion)} s</td></tr>`).join('\n')}
            </tbody></table>`}
          </div>
          <div class="medios">
            ${!e.video ? '<p class="meta">Sin video para este escenario.</p>'
              : r.videosUrl ? `<p>Video: <code>${esc(path.basename(e.video))}</code> en la <a href="${esc(r.videosUrl)}">carpeta de videos</a>.</p>`
              : `<figure><video controls preload="none" src="${e.video}"></video><figcaption><a href="${e.video}">Video del escenario</a></figcaption></figure>`}
            ${e.captura ? `<figure><a href="${e.captura}"><img loading="lazy" src="${e.captura}" alt="Pantalla final del escenario ${esc(e.nombre)}"></a><figcaption>Pantalla final</figcaption></figure>` : ''}
          </div>
        </div>
      </details>`).join('\n') + '</section>';
  }).join('\n');

  const bloqueApartados = apartados.length === 0 ? '' : `
    <section id="apartados"><h2>Escenarios apartados por defectos conocidos</h2>
    <p>Estos escenarios no hacen parte de la corrida: fallan mientras exista el defecto que detectan. Se reactivan como pruebas de regresión cuando el defecto se corrija.</p>
    <table><thead><tr><th>Escenario</th><th>Motivo</th><th>Archivo</th></tr></thead><tbody>
    ${apartados.map((a) => `<tr><td>${esc(a.nombre)}</td><td>${esc(a.motivo)}</td><td><code>${esc(a.archivo)}</code></td></tr>`).join('\n')}
    </tbody></table></section>`;

  return `<!DOCTYPE html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Resultados E2E · Vinilos · ${esc(r.dispositivo.modelo)}</title>
<style>
  :root { --fondo:#faf8f4; --tarjeta:#fff; --texto:#1f1d1a; --suave:#6b665e; --linea:#e4dfd6; --acento:#8a6a1c;
          --ok:#1f6b3a; --okf:#e3f2e8; --mal:#a12626; --malf:#fbe6e6; --nd:#6b665e; --ndf:#eeeae3; }
  @media (prefers-color-scheme: dark) { :root { --fondo:#151412; --tarjeta:#1e1c19; --texto:#f2ede4; --suave:#a8a195; --linea:#35312b; --acento:#d8ad4a;
          --ok:#7fd39b; --okf:#17301f; --mal:#f29a9a; --malf:#3a1a1a; --nd:#a8a195; --ndf:#2a2722; } }
  * { box-sizing: border-box; }
  body { margin:0; background:var(--fondo); color:var(--texto); font:16px/1.5 system-ui, -apple-system, "Segoe UI", sans-serif; }
  main { max-width: 1040px; margin: 0 auto; padding: 32px 20px 64px; }
  h1 { font-size: 1.7rem; margin: 0 0 4px; } h2 { font-size: 1.2rem; margin: 40px 0 12px; }
  .sub, .meta { color: var(--suave); } .meta { font-size: .85rem; font-weight: 400; margin-left: 6px; }
  .veredicto { font-size: 1.15rem; margin: 20px 0; padding: 14px 16px; border-left: 4px solid var(--acento); background: var(--tarjeta); }
  .datos { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 12px; margin: 20px 0; padding: 0; }
  .datos div { background: var(--tarjeta); border: 1px solid var(--linea); border-radius: 8px; padding: 12px 14px; }
  .datos dt { font-size: .78rem; text-transform: uppercase; letter-spacing: .05em; color: var(--suave); }
  .datos dd { margin: 2px 0 0; font-weight: 600; overflow-wrap: anywhere; }
  table { width: 100%; border-collapse: collapse; background: var(--tarjeta); border: 1px solid var(--linea); border-radius: 8px; }
  th, td { text-align: left; padding: 8px 12px; border-bottom: 1px solid var(--linea); vertical-align: top; }
  th { font-size: .8rem; text-transform: uppercase; letter-spacing: .04em; color: var(--suave); }
  tr:last-child td { border-bottom: 0; } .num { text-align: right; font-variant-numeric: tabular-nums; white-space: nowrap; }
  a { color: var(--acento); }
  .pill { display: inline-block; min-width: 4.6em; text-align: center; padding: 1px 9px; border-radius: 999px; font-size: .8rem; font-weight: 600; }
  .ok { color: var(--ok); background: var(--okf); } .mal { color: var(--mal); background: var(--malf); } .nd { color: var(--nd); background: var(--ndf); }
  details { background: var(--tarjeta); border: 1px solid var(--linea); border-radius: 8px; margin: 10px 0; }
  summary { cursor: pointer; padding: 12px 14px; }
  .cuerpo { display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 20px; padding: 4px 14px 16px; }
  @media (max-width: 760px) { .cuerpo { grid-template-columns: 1fr; } }
  .cuerpo table { border: 0; } .medios { display: grid; gap: 12px; align-content: start; }
  figure { margin: 0; } video, img { width: 100%; max-height: 420px; object-fit: contain; background: #000; border-radius: 6px; display: block; }
  figcaption { font-size: .82rem; color: var(--suave); margin-top: 4px; }
  pre { white-space: pre-wrap; overflow-wrap: anywhere; font-size: .8rem; color: var(--mal); background: var(--malf); padding: 8px; border-radius: 6px; margin: 6px 0 0; }
  code { font-size: .85em; }
</style></head><body><main>
  <h1>Resultados de las pruebas E2E · Vinilos</h1>
  <p class="sub">Suite automatizada con Kraken (Cucumber + Appium) sobre las historias de usuario de las iteraciones 1 y 2.</p>
  <p class="veredicto">${veredicto}</p>
  <dl class="datos">
    <div><dt>Fecha de la corrida</dt><dd>${esc(r.fecha)}</dd></div>
    <div><dt>Dispositivo</dt><dd>${esc(r.dispositivo.modelo)} · ${esc(r.dispositivo.tipo)}</dd></div>
    <div><dt>Sistema</dt><dd>Android ${esc(r.dispositivo.android)} (API ${esc(r.dispositivo.api)})</dd></div>
    <div><dt>Versión probada</dt><dd>${esc(r.rama)} @ ${esc(r.commit)}${r.cambiosSinConfirmar ? ' + cambios sin confirmar' : ''} · APK ${esc(r.apk)}</dd></div>
    <div><dt>Duración de las pruebas</dt><dd>${r.duracionTotalSeg} s</dd></div>
    ${r.videosUrl ? `<div><dt>Videos</dt><dd><a href="${esc(r.videosUrl)}">Carpeta de videos</a></dd></div>` : ''}
  </dl>
  <h2>Resumen por historia de usuario</h2>
  <table><thead><tr><th>HU</th><th>Historia</th><th class="num">Escenarios</th><th class="num">Pasaron</th><th class="num">Fallaron</th><th class="num">No ejecutados</th></tr></thead>
  <tbody>${filaResumen}</tbody></table>
  ${secciones}
  ${bloqueApartados}
  <p class="meta" style="margin:40px 0 0">Generado por <code>e2e-kraken/scripts/evidencias.js</code>. ${r.videosUrl ? 'Las capturas están en la carpeta <code>capturas/</code> junto a este archivo; los videos, en la carpeta enlazada arriba.' : 'Los videos y capturas están en las carpetas <code>videos/</code> y <code>capturas/</code> junto a este archivo.'}</p>
</main></body></html>`;
}

// ---------- Programa ----------
const args = process.argv.slice(2);
const opcion = (nombre) => (args.includes(nombre) ? args[args.indexOf(nombre) + 1] : undefined);

if (args[0] === '--solo-reporte') {
  // Rehace index.html y README.md de una corrida ya hecha, sin volver a ejecutar las pruebas.
  //   --videos-url <enlace>   los videos están en una carpeta externa (Drive, OneDrive…)
  const salida = path.resolve(args[1]);
  const archivo = path.join(salida, 'resultados.json');
  if (!fs.existsSync(archivo)) {
    console.error(`No existe ${archivo}. Indica la carpeta de una corrida hecha con \`npm run evidencias\`.`);
    process.exit(1);
  }
  const datos = JSON.parse(fs.readFileSync(archivo, 'utf8'));
  const url = opcion('--videos-url');
  if (url !== undefined) datos.resumen.videosUrl = url;
  const r = escribir(salida, datos);
  console.log(`Reporte rehecho en ${salida} (${r.pasaron}/${r.total} pasaron${r.videosUrl ? ', videos enlazados a ' + r.videosUrl : ''})`);
  process.exit(0);
}

const disp = dispositivo();
const inicio = Date.now();
const f = new Date(inicio);
const dos = (x) => String(x).padStart(2, '0');
const marca = `${f.getFullYear()}-${dos(f.getMonth() + 1)}-${dos(f.getDate())}_${dos(f.getHours())}${dos(f.getMinutes())}`;
const salida = path.join(SALIDA_BASE, `${marca}_${disp.modelo.replace(/[^A-Za-z0-9]+/g, '-')}`);
fs.mkdirSync(salida, { recursive: true });
console.log(`\nDispositivo: ${disp.modelo} (Android ${disp.android}, API ${disp.api})`);
console.log(`Evidencias en: ${salida}\n`);

const corrida = spawnSync('npx', ['kraken-node', 'run'], {
  cwd: RAIZ, stdio: 'inherit', env: { ...process.env, EVIDENCIAS_DIR: salida },
});

const r = escribir(salida, recolectar(salida, inicio, disp, corrida.status));
console.log(`\n${r.pasaron} de ${r.total} escenarios pasaron (${r.fallaron} fallaron, ${r.noEjecutados} no ejecutados).`);
console.log(`Reporte: ${path.join(salida, 'index.html')}`);
process.exit(r.fallaron === 0 && r.noEjecutados === 0 ? 0 : 1);
