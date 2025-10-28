#!/bin/bash

# Script de despliegue para Ecommerce GT Backend con Ngrok
# Uso: ./deploy.sh [profile]
# profile: dev (desarrollo) o prod (producción)

set -e

PROFILE=${1:-prod}
echo "🚀 Iniciando despliegue con perfil: $PROFILE"

# Verificar que Java esté instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Instala JDK 17 o superior."
    exit 1
fi

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null && [ ! -f "./mvnw" ]; then
    echo "❌ Maven no está instalado y no se encuentra mvnw."
    exit 1
fi

# Función para usar Maven (mvnw si existe, mvn si no)
maven_cmd() {
    if [ -f "./mvnw" ]; then
        ./mvnw "$@"
    else
        mvn "$@"
    fi
}

echo "📦 Compilando proyecto..."
maven_cmd clean compile -Dspring.profiles.active=$PROFILE -q

echo "🧪 Ejecutando pruebas..."
maven_cmd test -Dspring.profiles.active=$PROFILE -q

echo "🏗️ Construyendo aplicación..."
maven_cmd package -Dspring.profiles.active=$PROFILE -DskipTests -q

echo "✅ Build completado exitosamente!"

if [ "$PROFILE" = "prod" ]; then
    echo ""
    echo "🌐 Para exponer con Ngrok:"
    echo "1. Instala Ngrok: https://ngrok.com/download"
    echo "2. Ejecuta: ngrok http 8080"
    echo "3. Copia la URL HTTPS generada"
    echo "4. Actualiza VITE_API_BASE_URL en el frontend"
    echo "5. Ejecuta: java -jar -Dspring.profiles.active=prod target/*.jar"
    echo ""
    echo "📝 Variables de entorno necesarias:"
    echo "   - DATABASE_URL"
    echo "   - DB_USERNAME"
    echo "   - DB_PASSWORD"
    echo "   - CORS_ALLOWED_ORIGINS"
    echo "   - JWT_SECRET"
fi