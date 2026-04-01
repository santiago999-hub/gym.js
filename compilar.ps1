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
#   1. model      (Socio, sin dependencias externas)
#   2. persistence (GestorCSV, depende de model)
#   3. service    (Gimnasio, depende de model y persistence)
#   4. ui         (Main, depende de service)
& "$jdkBin\javac.exe" model/Socio.java persistence/GestorCSV.java service/Gimnasio.java ui/Main.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Compilacion exitosa`n" -ForegroundColor Green
    # Ejecutar el programa si no se paso el parametro -solo
    if ($args[0] -ne "-solo") {
        & "$jdkBin\java.exe" ui.Main
    }
} else {
    Write-Host "  [X] Error de compilacion. Revisa los archivos .java`n" -ForegroundColor Red
}
