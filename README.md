# 🛒 Ecommerce GT - Arquitectura de Producción

Una plataforma de ecommerce completa construida con Spring Boot (Backend) y Vue.js (Frontend), optimizada para despliegue en la nube.

## 🏗️ Arquitectura Implementada

### ✅ Base de Datos - Nivel 2 de Normalización (2NF)
- **Cumplimiento 2NF**: Todas las tablas cumplen con la segunda forma normal
- **Eliminación de dependencias parciales**: Corregida tabla `items_pedido`
- **Integridad referencial**: Claves foráneas correctamente implementadas
- **Índices optimizados**: Para consultas de alto rendimiento

### ✅ Frontend - Netlify (Público)
- **Despliegue automático**: Configurado para Netlify
- **SPA Routing**: Configurado para aplicaciones de una sola página
- **Variables de entorno**: Configuración flexible para diferentes entornos
- **Build optimizado**: Minificación y tree-shaking activados

### ✅ Backend - Ngrok con HTTPS
- **Exposición HTTPS**: Backend expuesto de forma segura con Ngrok
- **CORS dinámico**: Configurado por variables de entorno
- **Perfiles Spring**: Desarrollo y producción separados
- **Configuración externa**: Todas las configuraciones por variables de entorno

## 🚀 Inicio Rápido

### Prerrequisitos
- JDK 17+
- Node.js 18+
- PostgreSQL
- Maven 3.6+

### Instalación y Ejecución

```bash
# Clonar repositorio
git clone <repository-url>
cd ecommerce-gt

# Backend
cd backend
./mvnw spring-boot:run

# Frontend (nueva terminal)
cd frontend
npm install
npm run dev
```

Accede a:
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api

## 📁 Estructura del Proyecto

```
ecommerce-gt/
├── backend/                    # API REST - Spring Boot
│   ├── src/main/java/com/ecommercegt/backend/
│   │   ├── controllers/        # Endpoints REST
│   │   ├── models/            # Entidades JPA
│   │   ├── repositorios/      # Repositorios de datos
│   │   ├── service/           # Lógica de negocio
│   │   ├── security/          # Configuración JWT/Auth
│   │   └── config/            # Configuraciones Spring
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-prod.properties
│   │   └── DDL.sql            # Esquema de BD (2NF)
│   └── deploy.sh/deploy.ps1   # Scripts de despliegue
├── frontend/                   # SPA - Vue.js + Vite
│   ├── src/
│   │   ├── views/             # Páginas/Vistas
│   │   ├── components/        # Componentes reutilizables
│   │   ├── stores/            # Estado (Pinia)
│   │   ├── services/          # Servicios API
│   │   └── router/            # Configuración de rutas
│   ├── netlify.toml           # Configuración Netlify
│   └── .env.example           # Variables de entorno
└── DEPLOYMENT.md              # Guía completa de despliegue
```

## 🗄️ Base de Datos (2NF)

### Tablas Principales
- **usuarios**: Información de usuarios y roles
- **productos**: Catálogo de productos con vendedores
- **pedidos**: Órdenes de compra
- **items_pedido**: Detalle de productos por pedido (sin dependencias parciales)
- **pagos**: Información de pagos
- **notificaciones**: Sistema de notificaciones

### Características 2NF
- ✅ Eliminadas dependencias parciales
- ✅ Claves primarias compuestas correctamente
- ✅ Atributos no clave dependen completamente de la clave primaria
- ✅ Integridad referencial mantenida

## 🌐 Despliegue en Producción

### Frontend - Netlify
```bash
cd frontend
npm run build
# Desplegar en Netlify (ver DEPLOYMENT.md)
```

### Backend - Ngrok
```bash
cd backend
./deploy.sh prod
# Exponer con: ngrok http 8080
```

### Variables de Entorno Requeridas
```env
# Backend
DATABASE_URL=jdbc:postgresql://...
CORS_ALLOWED_ORIGINS=https://tu-netlify-site.netlify.app
JWT_SECRET=tu_clave_segura

# Frontend
VITE_API_BASE_URL=https://tu-ngrok-url.ngrok.io/api
```

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.x** - Framework principal
- **Spring Security + JWT** - Autenticación y autorización
- **Spring Data JPA** - ORM para PostgreSQL
- **PostgreSQL** - Base de datos relacional
- **Maven** - Gestión de dependencias

### Frontend
- **Vue.js 3** - Framework progresivo
- **Vite** - Build tool y dev server
- **Pinia** - State management
- **Vue Router** - Routing SPA
- **Tailwind CSS** - Framework CSS
- **Axios** - Cliente HTTP

### DevOps
- **Netlify** - Despliegue frontend
- **Ngrok** - Exposición HTTPS backend
- **Docker** - Containerización (futuro)

## 📊 API Endpoints

### Autenticación
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/register` - Registro de usuarios

### Productos
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto (vendedor)
- `PUT /api/productos/{id}` - Actualizar producto

### Pedidos
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos/mis-pedidos` - Mis pedidos

### Reportes (Admin)
- `GET /api/reportes/productos` - Productos más vendidos
- `GET /api/reportes/clientes/ganancias` - Clientes por ganancias
- `GET /api/reportes/clientes/ventas` - Clientes por ventas

## 🔒 Seguridad

- **JWT Authentication** - Tokens seguros para sesiones
- **Role-based Access** - Control de acceso por roles
- **CORS Configuration** - Protección contra ataques cross-origin
- **Password Encryption** - BCrypt para hashes seguros
- **Input Validation** - Validación de datos en backend

## 📈 Características

### Para Compradores
- 🛒 Catálogo de productos con filtros
- 🛍️ Carrito de compras persistente
- 💳 Sistema de pagos integrado
- 📦 Seguimiento de pedidos
- ⭐ Sistema de reseñas y calificaciones
- 🔔 Notificaciones en tiempo real

### Para Vendedores
- 📊 Dashboard de ventas
- 📦 Gestión de productos
- 📈 Reportes de rendimiento
- 💰 Seguimiento de ganancias
- 📬 Comunicación con compradores

### Para Administradores
- 👥 Gestión de usuarios
- 📊 Reportes completos del sistema
- 🛡️ Moderación de contenido
- ⚙️ Configuración del sistema
- 📋 Gestión de categorías

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 📞 Contacto

- **Email**: davidcano202132346@cunoc.edu.gt
- **Proyecto**: Ecommerce GT - Arquitectura de Producción

---

⭐ **¡Gracias por usar Ecommerce GT!** ⭐

*Construido con ❤️ para demostrar arquitectura de software de nivel empresarial*