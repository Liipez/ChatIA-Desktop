<#
  Gera o instalador do HyvexIA para Windows (HyvexIA-1.0.0.exe).

  Pré-requisitos:
    - JDK 21 (com jpackage) no PATH
    - Maven (ou o wrapper do IntelliJ)
    - WiX Toolset 3.14 no PATH (exigido pelo jpackage do JDK 21 para gerar .exe)
      https://github.com/wixtoolset/wix3/releases

  Uso (na raiz do projeto):
    powershell -ExecutionPolicy Bypass -File packaging\build-installer.ps1
    powershell -ExecutionPolicy Bypass -File packaging\build-installer.ps1 -Console   # versão com console para depuração
#>
param(
    [switch]$Console
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

$AppName    = "HyvexIA"
$AppVersion = "1.0.0"
$MainJar    = "ChatIA-Desktop-$AppVersion.jar"
# UUID fixo: permite que uma nova versão atualize a instalação anterior.
$UpgradeUuid = "809e94ae-cd14-48e9-8530-bf5757b30fa8"

Write-Host "==> Verificando ferramentas..." -ForegroundColor Cyan
foreach ($tool in @("java", "jpackage")) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        throw "$tool não encontrado no PATH. Instale o JDK 21."
    }
}
if (-not (Get-Command "candle.exe" -ErrorAction SilentlyContinue)) {
    throw "WiX Toolset 3.14 não encontrado no PATH (candle.exe). Instale em https://github.com/wixtoolset/wix3/releases e reabra o terminal."
}

Write-Host "==> Compilando e copiando dependências (mvn package)..." -ForegroundColor Cyan
$mvn = if (Test-Path ".\mvnw.cmd") { ".\mvnw.cmd" } else { "mvn" }
& $mvn -B -q clean package -DskipTests
if ($LASTEXITCODE -ne 0) { throw "Falha no build do Maven." }

Write-Host "==> Gerando instalador com jpackage..." -ForegroundColor Cyan
$args = @(
    "--type", "exe",
    "--name", $AppName,
    "--app-version", $AppVersion,
    "--vendor", "Liipez",
    "--description", "Assistente desktop com IA, RAG e MongoDB",
    "--input", "target\installer-input",
    "--main-jar", $MainJar,
    "--main-class", "br.com.chatia.Launcher",
    "--icon", "packaging\icon.ico",
    "--dest", "target\installer",
    # Módulos do Java incluídos no runtime embutido:
    #  jdk.naming.dns -> necessário para URIs mongodb+srv:// (Atlas)
    #  jdk.crypto.ec  -> TLS com o MongoDB Atlas e a API da Groq
    #  jdk.jsobject / jdk.xml.dom / jdk.unsupported -> WebView (splash com GSAP)
    "--add-modules", "java.se,jdk.unsupported,jdk.crypto.ec,jdk.naming.dns,jdk.jsobject,jdk.xml.dom,jdk.charsets,jdk.localedata,jdk.zipfs",
    "--java-options", "-Dfile.encoding=UTF-8",
    "--win-shortcut",
    "--win-shortcut-prompt",
    "--win-menu",
    "--win-menu-group", $AppName,
    "--win-dir-chooser",
    "--win-upgrade-uuid", $UpgradeUuid
)
if ($Console) { $args += "--win-console" }

& jpackage @args
if ($LASTEXITCODE -ne 0) { throw "Falha no jpackage." }

Write-Host ""
Write-Host "Instalador gerado em: target\installer\$AppName-$AppVersion.exe" -ForegroundColor Green
Write-Host "Configure as credenciais em: %APPDATA%\HyvexIA\config.properties (modelo: config.example.properties)" -ForegroundColor Yellow
