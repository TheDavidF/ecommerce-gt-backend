# 🚀 Guía de Despliegue - Ecommerce GT

## Arquitectura Implementada

### ✅ Base de Datos - Nivel 2 de Normalización (2NF)
- Se corrigieron dependencias parciales en la tabla `items_pedido`
- Eliminado campo `vendedor_id` redundante (se obtiene por JOIN)
- Todas las tablas cumplen con 2NF

### ✅ Frontend - Netlify
- Configurado para despliegue público en Netlify
- Variables de entorno para diferentes entornos
- SPA routing configurado

### ✅ Backend - Ngrok con HTTPS
- Perfil de producción configurado
- CORS dinámico por variables de entorno
- Exposición HTTPS mediante Ngrok

---

## 📋 Prerrequisitos

### Sistema
- JDK 17 o superior
- Maven 3.6+ o usar `./mvnw`
- Node.js 18+ y npm
- PostgreSQL (para producción)

### Cuentas Externas
- [Netlify](https://netlify.com) - Para el frontend
- [Ngrok](https://ngrok.com) - Para exponer el backend

---

## 🚀 Despliegue Paso a Paso

### 1. Preparar el Backend

```bash
cd backend

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus valores reales

# Construir y probar
./deploy.sh prod  # Linux/Mac
# o
.\deploy.ps1 prod  # Windows
```

### 2. Exponer Backend con Ngrok

```bash
# Instalar Ngrok si no lo tienes
# https://ngrok.com/download

# Exponer el puerto 8080
ngrok http 8080

# Copiar la URL HTTPS generada (ej: https://abc123.ngrok.io)
```

### 3. Preparar el Frontend

```bash
cd frontend

# Configurar variables de entorno
cp .env.example .env.local

# Editar .env.local y cambiar:
VITE_API_BASE_URL=https://tu-ngrok-url.ngrok.io/api

# Construir
npm run build
```

### 4. Desplegar Frontend en Netlify

#### Opción A: Interfaz Web
1. Ve a [Netlify](https://app.netlify.com)
2. Arrastra la carpeta `frontend/dist` o conecta tu repositorio
3. Configura las variables de entorno en Site Settings

#### Opción B: Netlify CLI
```bash
# Instalar Netlify CLI
npm install -g netlify-cli

# Desplegar
cd frontend
netlify deploy --prod --dir=dist

# Configurar variables de entorno
netlify env:set VITE_API_BASE_URL https://tu-ngrok-url.ngrok.io/api
```

---

## ⚙️ Configuración de Variables de Entorno

### Backend (.env)
```env
# Base de datos
DATABASE_URL=jdbc:postgresql://tu-db-host:5432/ecommerce_gt
DB_USERNAME=tu_usuario_db
DB_PASSWORD=tu_password_db

# CORS - URLs permitidas (separadas por coma)
CORS_ALLOWED_ORIGINS=https://tu-netlify-site.netlify.app,http://localhost:5173

# JWT
JWT_SECRET=tu_jwt_secret_muy_seguro
JWT_EXPIRATION=86400000

# Email (opcional)
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
```

### Frontend (.env.local)
```env
VITE_API_BASE_URL=https://tu-ngrok-url.ngrok.io/api
VITE_APP_NAME=Ecommerce GT
VITE_APP_VERSION=1.0.0
```

---

## 🔧 Comandos Útiles

### Backend
```bash
# Desarrollo
./mvnw spring-boot:run

# Producción
java -jar -Dspring.profiles.active=prod target/*.jar
```

### Frontend
```bash
# Desarrollo
npm run dev

# Construir para producción
npm run build

# Vista previa de producción
npm run preview
```

### Ngrok
```bash
# Exponer puerto
ngrok http 8080

# Con autenticación
ngrok http 8080 --auth="user:password"

# Inspeccionar requests
ngrok http 8080 --log=stdout
```

---

## 🔒 Seguridad

### Producción
- ✅ Cambia `JWT_SECRET` por una clave segura
- ✅ Configura `CORS_ALLOWED_ORIGINS` solo con tu dominio de Netlify
- ✅ Usa HTTPS siempre (Ngrok lo proporciona)
- ✅ Configura base de datos externa (no localhost)
- ✅ Deshabilita `spring.jpa.show-sql=true` en producción

### Checklist de Seguridad
- [ ] JWT secret cambiado
- [ ] CORS configurado correctamente
- [ ] Base de datos externa configurada
- [ ] Logs sensibles deshabilitados
- [ ] Variables de entorno no commiteadas

---

## 🐛 Solución de Problemas

### Error de CORS
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solución:** Verifica `CORS_ALLOWED_ORIGINS` en el backend incluya tu dominio de Netlify.

### Error de conexión al backend
```
Failed to fetch / Network Error
```
**Solución:** Verifica que Ngrok esté corriendo y `VITE_API_BASE_URL` apunte a la URL correcta.

### Error de build en Netlify
```
Build failed
```
**Solución:** Verifica que `netlify.toml` esté en la raíz y las variables de entorno estén configuradas.

---

## 📊 Monitoreo

### Ngrok
- Dashboard: https://dashboard.ngrok.com
- Logs locales: `ngrok http 8080 --log=stdout`

### Netlify
- Dashboard: https://app.netlify.com
- Logs de build y funciones
- Analytics de sitio

---

## 🎯 Próximos Pasos

1. **Base de datos externa**: Configurar PostgreSQL en la nube (ElephantSQL, AWS RDS, etc.)
2. **CDN para imágenes**: Usar Cloudinary o AWS S3 para archivos estáticos
3. **Monitoreo**: Implementar logging centralizado y alertas
4. **SSL personalizado**: Configurar dominio personalizado con SSL
5. **Cache**: Implementar Redis para mejorar performance

---

¡Tu aplicación ecommerce está lista para producción! 🎉