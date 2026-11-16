#!/usr/bin/env node
// Graba los videos de demostración para la wiki (no hacen parte de la suite de pruebas):
//   - bug_comentario_no_aparece.mp4   el defecto del comentario que no se muestra
//   - recorrido_app.mp4               recorrido por las cinco historias de usuario
//
//   npm run videos-wiki          (desde e2e-kraken/, con UN dispositivo en `adb devices`)
//
// Quedan en docs/videos-wiki/ (carpeta ignorada por git: se suben a la wiki o a la nube).
// Los guiones están en e2e-kraken/demo-wiki/features/.
const { spawnSync, execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const RAIZ = path.resolve(__dirname, '..');
const DEMO = path.join(RAIZ, 'demo-wiki');
const SALIDA = path.resolve(RAIZ, '..', 'docs', 'videos-wiki');

let lineas = [];
try {
  lineas = execFileSync('adb', ['devices'], { encoding: 'utf8' }).split('\n').slice(1)
    .map((l) => l.trim().split(/\s+/)).filter((p) => p[1] === 'device');
} catch (e) { /* adb no está en el PATH */ }
if (lineas.length !== 1) {
  console.error(lineas.length === 0
    ? 'No hay ningún dispositivo en `adb devices`. Conecta el celular o enciende el emulador.'
    : 'Hay más de un dispositivo conectado. Deja solo uno.');
  process.exit(1);
}

fs.mkdirSync(SALIDA, { recursive: true });
console.log(`\nVideos en: ${SALIDA}\n`);
const corrida = spawnSync('node', [path.join(RAIZ, 'node_modules', 'kraken-node', 'bin', 'kraken-node'), 'run'], {
  cwd: DEMO, stdio: 'inherit',
  env: { ...process.env, EVIDENCIAS_DIR: SALIDA, EVIDENCIAS_PLANO: '1' },
});

const videos = fs.readdirSync(SALIDA).filter((f) => f.endsWith('.mp4'));
console.log(`\nVideos grabados (${videos.length}):`);
for (const v of videos) {
  console.log(`  ${v}  ${(fs.statSync(path.join(SALIDA, v)).size / 1e6).toFixed(1)} MB`);
}
process.exit(corrida.status || 0);
