const { After, Before } = require('@cucumber/cucumber');
const { AndroidClient } = require('kraken-node');
const { spawn, execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

// ---------- Evidencias en video (opcional) ----------
// Solo se graba cuando existe la variable EVIDENCIAS_DIR, que define `npm run evidencias`
// (scripts/evidencias.js). En una corrida normal (`npx kraken-node run`) no se graba nada.
// Un video por escenario: <EVIDENCIAS_DIR>/videos/<HU>/<archivo-del-feature>.mp4
// Cualquier problema al grabar se ignora: la evidencia nunca debe hacer fallar una prueba.
const EVIDENCIAS_DIR = process.env.EVIDENCIAS_DIR;
const pausa = (ms) => new Promise((ok) => setTimeout(ok, ms));

async function iniciarGrabacion(mundo, escenario) {
  try {
    const base = path.basename(escenario.pickle.uri, '.feature'); // ej. hu09_ca09_1_publicar
    const hu = base.slice(0, 4).toUpperCase();                    // ej. HU09
    // EVIDENCIAS_PLANO=1 (videos para la wiki): todo directo en la carpeta, sin subcarpetas.
    const carpeta = process.env.EVIDENCIAS_PLANO ? EVIDENCIAS_DIR : path.join(EVIDENCIAS_DIR, 'videos', hu);
    fs.mkdirSync(carpeta, { recursive: true });
    mundo.video = {
      remoto: `/sdcard/vinilos_${base}.mp4`,
      local: path.join(carpeta, `${base}.mp4`),
    };
    // screenrecord graba máximo 3 minutos por archivo; cada escenario dura menos.
    // Si la grabadora se cae al arrancar, se reintenta (hasta 3 veces).
    for (let intento = 0; intento < 3; intento++) {
      const grabador = spawn('adb', ['-s', mundo.device.id, 'shell', 'screenrecord',
        '--bit-rate', '1500000', mundo.video.remoto], { stdio: 'ignore' });
      grabador.on('error', () => {});
      mundo.grabador = grabador;
      await pausa(1500);
      if (grabador.exitCode === null) break; // sigue viva: está grabando
    }
  } catch (e) {
    mundo.video = null;
  }
}

async function detenerGrabacion(mundo) {
  if (!mundo.video) return;
  const adb = (args) => execFileSync('adb', ['-s', mundo.device.id, ...args], { stdio: 'ignore' });
  try {
    await pausa(1000); // deja ver la pantalla final
    // SIGINT (señal 2) hace que screenrecord cierre bien el archivo; matar adb lo dejaría dañado.
    adb(['shell', 'pkill', '-2', 'screenrecord']);
  } catch (e) {
    // si pkill falla, igual se intenta traer lo que haya
  }
  // El dispositivo tarda un momento en cerrar el archivo: se reintenta la copia.
  for (let intento = 0; intento < 4; intento++) {
    await pausa(2000);
    try {
      adb(['pull', mundo.video.remoto, mundo.video.local]);
      if (fs.existsSync(mundo.video.local) && fs.statSync(mundo.video.local).size > 0) break;
    } catch (e) {
      // se reintenta; si no se logra, el reporte indicará «sin video»
    }
  }
  try { adb(['shell', 'rm', '-f', mundo.video.remoto]); } catch (e) {}
}

Before(async function (escenario) {
  this.deviceClient = new AndroidClient(
    this.device.id, this.apkPath,
    this.apkPackage, this.apkLaunchActivity,
    {}, this.userId
  );
  this.driver = await this.deviceClient.startKrakenForUserId(this.userId);
  // La grabación empieza DESPUÉS de que Kraken abre la sesión con el dispositivo: al abrirla
  // (sobre todo en el primer escenario de la corrida) se reinicia la conexión adb y eso
  // mataba la grabadora, dejando un video de 0 bytes.
  if (EVIDENCIAS_DIR) await iniciarGrabacion(this, escenario);
})

After(async function () {
  if (EVIDENCIAS_DIR) await detenerGrabacion(this);
  await this.deviceClient.stopKrakenForUserId(this.userId);
});
