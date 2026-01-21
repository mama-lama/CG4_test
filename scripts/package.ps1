$ErrorActionPreference = "Stop"
# Point 6: build a portable bundle with JavaFX dependencies.

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path | Split-Path -Parent
Set-Location $projectRoot

mvn -q clean package

$distDir = Join-Path $projectRoot "dist"
$modulesDir = Join-Path $distDir "modules"
$jarPath = Join-Path $projectRoot "target\\cgvsu-1.0-SNAPSHOT.jar"

if (Test-Path $distDir) {
    Remove-Item -Recurse -Force $distDir
}
New-Item -ItemType Directory -Path $distDir | Out-Null
New-Item -ItemType Directory -Path $modulesDir | Out-Null

Copy-Item -Path $jarPath -Destination (Join-Path $distDir "app.jar")
Copy-Item -Path (Join-Path $projectRoot "target\\modules\\*") -Destination $modulesDir -Recurse

$runBat = @"
@echo off
setlocal
set APP_DIR=%~dp0
java --module-path "%APP_DIR%modules" --add-modules javafx.controls,javafx.fxml -jar "%APP_DIR%app.jar"
"@
Set-Content -Path (Join-Path $distDir "run.bat") -Value $runBat -Encoding ASCII

$runSh = @"
#!/usr/bin/env bash
set -euo pipefail
APP_DIR="$(cd "$(dirname "$0")" && pwd)"
java --module-path "$APP_DIR/modules" --add-modules javafx.controls,javafx.fxml -jar "$APP_DIR/app.jar"
"@
Set-Content -Path (Join-Path $distDir "run.sh") -Value $runSh -Encoding ASCII
