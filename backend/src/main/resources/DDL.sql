-- DDL.sql - Esquema de Base de Datos E-Commerce GT

-- ========================================
-- EXTENSIÓN PARA GENERAR UUIDs
-- ========================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ========================================
-- TABLA: ROLES
-- Descripción: Roles de usuarios (COMUN, MODERADOR, LOGISTICA, ADMIN)
-- ========================================
CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL  -- 'COMUN', 'MODERADOR', 'LOGISTICA', 'ADMIN'
);

-- ========================================
-- TABLA: USUARIOS
-- Descripción: Información de todos los usuarios del sistema
-- ========================================
CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nombre_usuario VARCHAR(100) UNIQUE NOT NULL,
  correo VARCHAR(255) UNIQUE NOT NULL,
  contrasena_hash VARCHAR(255) NOT NULL,
  nombre_completo VARCHAR(255),
  telefono VARCHAR(50),
  direccion TEXT,
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: USUARIO_ROLES (Relación Muchos a Muchos)
-- Descripción: Asigna roles a usuarios
-- ========================================
CREATE TABLE usuario_roles (
  usuario_id UUID REFERENCES usuarios(id) ON DELETE CASCADE,
  rol_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, rol_id)
);

-- ========================================
-- TABLA: CATEGORIAS
-- Descripción: Categorías de productos (Electrónica, Ropa, etc.)
-- ========================================
CREATE TABLE categorias (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL
);

-- ========================================
-- TABLA: PRODUCTOS
-- Descripción: Productos publicados por vendedores
-- ========================================
CREATE TABLE productos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  vendedor_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  precio NUMERIC(12,2) NOT NULL CHECK (precio >= 0),
  existencia INTEGER NOT NULL CHECK (existencia >= 0),
  condicion VARCHAR(20) NOT NULL,  -- 'NUEVO' o 'USADO'
  categoria_id INTEGER REFERENCES categorias(id),
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',  -- 'PENDIENTE', 'APROBADO', 'RECHAZADO'
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: IMAGENES_PRODUCTO
-- Descripción: Imágenes asociadas a cada producto
-- ========================================
CREATE TABLE imagenes_producto (
  id SERIAL PRIMARY KEY,
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  url TEXT NOT NULL,
  texto_alternativo TEXT,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: SOLICITUDES_MODERACION
-- Descripción: Solicitudes de aprobación de productos
-- ========================================
CREATE TABLE solicitudes_moderacion (
  id SERIAL PRIMARY KEY,
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  solicitante_id UUID REFERENCES usuarios(id),
  fecha_solicitud TIMESTAMP WITH TIME ZONE DEFAULT now(),
  moderador_id UUID REFERENCES usuarios(id),
  fecha_revision TIMESTAMP WITH TIME ZONE,
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',  -- 'PENDIENTE', 'APROBADO', 'RECHAZADO'
  razon TEXT
);

-- ========================================
-- TABLA: RESENAS_PRODUCTO
-- Descripción: Calificaciones y comentarios de compradores
-- ========================================
CREATE TABLE resenas_producto (
  id SERIAL PRIMARY KEY,
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  comprador_id UUID REFERENCES usuarios(id),
  calificacion SMALLINT CHECK (calificacion >= 1 AND calificacion <= 5),
  comentario TEXT,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  UNIQUE(producto_id, comprador_id)  -- Un usuario solo puede dejar 1 reseña por producto
);

-- ========================================
-- TABLA: SANCIONES
-- Descripción: Sanciones aplicadas a usuarios
-- ========================================
CREATE TABLE sanciones (
  id SERIAL PRIMARY KEY,
  usuario_id UUID REFERENCES usuarios(id),
  moderador_id UUID REFERENCES usuarios(id),
  razon TEXT NOT NULL,
  fecha_inicio TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_fin TIMESTAMP WITH TIME ZONE,
  activa BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: CARRITOS
-- Descripción: Carrito de compras de cada usuario
-- ========================================
CREATE TABLE carritos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  usuario_id UUID UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: ITEMS_CARRITO
-- Descripción: Productos agregados al carrito
-- ========================================
CREATE TABLE items_carrito (
  id SERIAL PRIMARY KEY,
  carrito_id UUID REFERENCES carritos(id) ON DELETE CASCADE,
  producto_id UUID REFERENCES productos(id),
  cantidad INTEGER NOT NULL CHECK (cantidad > 0),
  precio_unitario NUMERIC(12,2) NOT NULL,
  fecha_agregado TIMESTAMP WITH TIME ZONE DEFAULT now(),
  UNIQUE (carrito_id, producto_id)  -- Un producto solo puede estar una vez en el carrito
);

-- ========================================
-- TABLA: PEDIDOS
-- Descripción: Órdenes de compra realizadas
-- ========================================
CREATE TABLE pedidos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  comprador_id UUID REFERENCES usuarios(id),
  monto_total NUMERIC(12,2) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'EN_CURSO',  -- 'EN_CURSO', 'ENTREGADO'
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_entrega_estimada DATE,
  fecha_entregado TIMESTAMP WITH TIME ZONE
);

-- ========================================
-- TABLA: ITEMS_PEDIDO
-- Descripción: Productos incluidos en cada pedido
-- ========================================
CREATE TABLE items_pedido (
  id SERIAL PRIMARY KEY,
  pedido_id UUID REFERENCES pedidos(id) ON DELETE CASCADE,
  producto_id UUID REFERENCES productos(id),
  vendedor_id UUID REFERENCES usuarios(id),
  cantidad INTEGER NOT NULL,
  precio_unitario NUMERIC(12,2) NOT NULL,
  comision NUMERIC(12,2) NOT NULL,  -- 5% del (precio_unitario * cantidad)
  monto_vendedor NUMERIC(12,2) NOT NULL  -- 95% del (precio_unitario * cantidad)
);

-- ========================================
-- TABLA: PAGOS
-- Descripción: Información de pagos procesados
-- ========================================
CREATE TABLE pagos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  pedido_id UUID REFERENCES pedidos(id) UNIQUE,
  proveedor_pago VARCHAR(100) DEFAULT 'MOCK',
  id_pago_proveedor VARCHAR(255),
  monto NUMERIC(12,2) NOT NULL,
  moneda VARCHAR(10) DEFAULT 'GTQ',
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',  -- 'PENDIENTE', 'COMPLETADO', 'FALLIDO'
  ultimos_4_digitos VARCHAR(4),
  marca_tarjeta VARCHAR(50),
  token_tarjeta_guardada VARCHAR(255),
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: TARJETAS_GUARDADAS
-- Descripción: Tarjetas de crédito guardadas por usuarios
-- ========================================
CREATE TABLE tarjetas_guardadas (
  id SERIAL PRIMARY KEY,
  usuario_id UUID REFERENCES usuarios(id) ON DELETE CASCADE,
  token_proveedor VARCHAR(255) NOT NULL,
  ultimos_4_digitos VARCHAR(4),
  marca VARCHAR(50),
  mes_expiracion INTEGER,
  anio_expiracion INTEGER,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: ENVIOS
-- Descripción: Información de envíos de pedidos
-- ========================================
CREATE TABLE envios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  pedido_id UUID REFERENCES pedidos(id) UNIQUE,
  usuario_logistica_id UUID REFERENCES usuarios(id),
  estado VARCHAR(30) DEFAULT 'EN_CURSO',  -- 'EN_CURSO', 'ENTREGADO'
  fecha_entrega_estimada DATE,
  fecha_entregado TIMESTAMP WITH TIME ZONE,
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TABLA: NOTIFICACIONES
-- Descripción: Notificaciones enviadas a usuarios
-- ========================================
CREATE TABLE notificaciones (
  id SERIAL PRIMARY KEY,
  usuario_id UUID REFERENCES usuarios(id),
  tipo VARCHAR(100),
  titulo VARCHAR(255),
  cuerpo TEXT,
  metadatos JSONB,
  fecha_envio TIMESTAMP WITH TIME ZONE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  leida BOOLEAN DEFAULT FALSE
);

-- ========================================
-- TABLA: TRANSACCIONES
-- Descripción: Registro de transacciones financieras
-- ========================================
CREATE TABLE transacciones (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  pedido_id UUID REFERENCES pedidos(id),
  monto NUMERIC(12,2) NOT NULL,
  comision NUMERIC(12,2) NOT NULL,
  vendedor_id UUID REFERENCES usuarios(id),
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);


-- ========================================
-- ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ========================================
CREATE INDEX idx_productos_vendedor ON productos (vendedor_id);
CREATE INDEX idx_productos_categoria ON productos (categoria_id);
CREATE INDEX idx_productos_estado ON productos (estado);
CREATE INDEX idx_pedidos_comprador ON pedidos (comprador_id);
CREATE INDEX idx_solicitudes_moderacion_estado ON solicitudes_moderacion (estado);
CREATE INDEX idx_envios_estado ON envios (estado);

-- ========================================
-- FUNCIÓN Y TRIGGERS PARA ACTUALIZAR FECHA_ACTUALIZACION
-- ========================================
CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_actualizar_usuarios BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_actualizacion();

CREATE TRIGGER trigger_actualizar_productos BEFORE UPDATE ON productos
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_actualizacion();

CREATE TRIGGER trigger_actualizar_carritos BEFORE UPDATE ON carritos
    FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_actualizacion();