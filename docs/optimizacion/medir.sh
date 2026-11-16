#!/usr/bin/env bash
# Uso: ./medir.sh <serial_adb> <etiqueta>   (ej. ./medir.sh R58M123ABC pixel6-despues)
# Requiere: adb en PATH, dispositivo físico con depuración USB y la app instalada.
set -euo pipefail
SERIAL="${1:?serial adb (adb devices)}"; TAG="${2:?etiqueta}"
PKG=com.example.aplicacionvinilos
ACT=$PKG/.ui.LoginActivity
OUT="resultados/$TAG"; mkdir -p "$OUT"
A="adb -s $SERIAL"

$A shell getprop ro.product.model        >  "$OUT/dispositivo.txt"
$A shell getprop ro.build.version.release >> "$OUT/dispositivo.txt"
$A shell grep MemTotal /proc/meminfo      >> "$OUT/dispositivo.txt"

# Arranque en frío (TotalTime = ms hasta primer frame)
$A shell am force-stop $PKG
$A shell am start -W -n $ACT | tee "$OUT/arranque.txt"

echo ">> Recorre ahora el flujo de la HU en el dispositivo (30-60 s) y pulsa Enter"; read -r
$A shell dumpsys meminfo $PKG | tee "$OUT/meminfo.txt" | grep -E "Java Heap|Native Heap|TOTAL PSS|Graphics"
$A shell dumpsys gfxinfo $PKG | tee "$OUT/gfxinfo.txt" | grep -E "Total frames|Janky frames|50th|90th|95th|99th"
$A shell top -b -n 1 -p "$($A shell pidof $PKG)" > "$OUT/cpu.txt" || true
echo "Guardado en $OUT"
