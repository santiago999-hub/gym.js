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
#   5. persistence/GestorCSV     (usa Socio)
#   6. persistence/GestorCuotasCSV / GestorIngresosCSV (usan modelos)
#   7. persistence/ArchivoManager (usa los 3 gestores anteriores)
#   8. service/Gimnasio          (usa modelos + ArchivoManager)
#   9. ui/Main                   (usa Gimnasio, Plan, ArchivoManager)
& "$jdkBin\javac.exe" model/Plan.java model/Socio.java model/RegistroCuota.java model/RegistroIngreso.java persistence/GestorCSV.java persistence/GestorCuotasCSV.java persistence/GestorIngresosCSV.java persistence/ArchivoManager.java service/Gimnasio.java ui/Main.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Compilacion exitosa`n" -ForegroundColor Green
    # Ejecutar el programa si no se paso el parametro -solo
    if ($args[0] -ne "-solo") {
        & "$jdkBin\java.exe" ui.Main
    }
} else {
    Write-Host "  [X] Error de compilacion. Revisa los archivos .java`n" -ForegroundColor Red
}
