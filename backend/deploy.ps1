# Script de despliegue para Ecommerce GT Backend con Ngrok
# Uso: .\deploy.ps1 [profile]
# profile: dev (desarrollo) o prod (producción)

param(
    [string]$Profile = "prod"
)

Write-Host "🚀 Iniciando despliegue con perfil: $Profile" -ForegroundColor Green

# Verificar que Java esté instalado
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Java no está instalado. Instala JDK 17 o superior." -ForegroundColor Red
    exit 1
}

# Verificar que Maven esté instalado
$mavenAvailable = $false
if (Test-Path ".\mvnw.cmd") {
    $mavenAvailable = $true
    $mavenCmd = ".\mvnw.cmd"
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mavenAvailable = $true
    $mavenCmd = "mvn"
}

if (-not $mavenAvailable) {
    Write-Host "❌ Maven no está instalado y no se encuentra mvnw.cmd." -ForegroundColor Red
    exit 1
}

Write-Host "📦 Compilando proyecto..." -ForegroundColor Yellow
& $mavenCmd clean compile "-Dspring.profiles.active=$Profile" -q

Write-Host "🧪 Ejecutando pruebas..." -ForegroundColor Yellow
& $mavenCmd test "-Dspring.profiles.active=$Profile" -q

Write-Host "🏗️ Construyendo aplicación..." -ForegroundColor Yellow
& $mavenCmd package "-Dspring.profiles.active=$Profile" -DskipTests -q

Write-Host "✅ Build completado exitosamente!" -ForegroundColor Green

if ($Profile -eq "prod") {
    Write-Host ""
    Write-Host "🌐 Para exponer con Ngrok:" -ForegroundColor Cyan
    Write-Host "1. Instala Ngrok: https://ngrok.com/download" -ForegroundColor White
    Write-Host "2. Ejecuta: ngrok http 8080" -ForegroundColor White
    Write-Host "3. Copia la URL HTTPS generada" -ForegroundColor White
    Write-Host "4. Actualiza VITE_API_BASE_URL en el frontend" -ForegroundColor White
    Write-Host "5. Ejecuta: java -jar -Dspring.profiles.active=prod target\*.jar" -ForegroundColor White
    Write-Host ""
    Write-Host "📝 Variables de entorno necesarias:" -ForegroundColor Cyan
    Write-Host "   - DATABASE_URL" -ForegroundColor White
    Write-Host "   - DB_USERNAME" -ForegroundColor White
    Write-Host "   - DB_PASSWORD" -ForegroundColor White
    Write-Host " - CORS_ALLOWED_ORIGINS" -ForegroundColor White
    Write-Host "   - JWT_SECRET" -ForegroundColor White
}