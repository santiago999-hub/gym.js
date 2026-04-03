# compilar.ps1
# ================================================
# Script de compilacion y ejecucion del proyecto
# Uso: ejecuta este archivo desde PowerShell
#   .\compilar.ps1         -> compila y ejecuta
#   .\compilar.ps1 -solo   -> solo compila
# ================================================

# Ruta al JDK embebido en la extension de Java de VS Code
$jdkBin = "$env:USERPROFILE\.vscode\extensions\redhat.java-1.53.0-win32-x64\jre\21.0.10-win32-x86_64\bin"

Write-Host "`n  Compilando proyecto..." -ForegroundColor Cyan

# Compilar en orden de dependencias:
#   1. model/Plan.java           (enum de planes, sin dependencias)
#   2. model/Socio.java          (usa Plan)
#   3. model/RegistroCuota.java  (sin dependencias externas)
#   4. model/RegistroIngreso.java(sin dependencias externas)
#   5. persistence/              (usa Socio, RegistroCuota, RegistroIngreso)
#   6. service/                  (usa Socio, Plan, todos los gestores CSV)
#   7. ui/                       (usa Gimnasio y Plan)
& "$jdkBin\javac.exe" model/Plan.java model/Socio.java model/RegistroCuota.java model/RegistroIngreso.java persistence/GestorCSV.java persistence/GestorCuotasCSV.java persistence/GestorIngresosCSV.java service/Gimnasio.java ui/Main.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Compilacion exitosa`n" -ForegroundColor Green
    # Ejecutar el programa si no se paso el parametro -solo
    if ($args[0] -ne "-solo") {
        & "$jdkBin\java.exe" ui.Main
    }
} else {
    Write-Host "  [X] Error de compilacion. Revisa los archivos .java`n" -ForegroundColor Red
}
