CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ========================================
-- ROLES
-- ========================================
CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL
);

-- ========================================
-- USUARIOS
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
-- USUARIO_ROLES
-- ========================================
CREATE TABLE usuario_roles (
  usuario_id UUID REFERENCES usuarios(id) ON DELETE CASCADE,
  rol_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, rol_id)
);

-- ========================================
-- CATEGORIAS
-- ========================================
CREATE TABLE categorias (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) UNIQUE NOT NULL,
  descripcion TEXT,
  imagen_url VARCHAR(500),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- PRODUCTOS
-- ========================================
CREATE TABLE productos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  vendedor_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  precio NUMERIC(12,2) NOT NULL CHECK (precio >= 0),
  precio_descuento NUMERIC(10,2),
  stock INTEGER NOT NULL CHECK (stock >= 0),
  marca VARCHAR(100),
  modelo VARCHAR(100),
  categoria_id INTEGER REFERENCES categorias(id),
  estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_REVISION',
  destacado BOOLEAN DEFAULT FALSE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- IMAGENES_PRODUCTO
-- ========================================
CREATE TABLE imagenes_producto (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  url_imagen VARCHAR(500) NOT NULL,
  es_principal BOOLEAN DEFAULT FALSE,
  orden INTEGER DEFAULT 0,
  producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  fecha_subida TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- SOLICITUDES_MODERACION
-- ========================================
CREATE TABLE solicitudes_moderacion (
  id SERIAL PRIMARY KEY,
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  solicitante_id UUID REFERENCES usuarios(id),
  fecha_solicitud TIMESTAMP WITH TIME ZONE DEFAULT now(),
  moderador_id UUID REFERENCES usuarios(id),
  fecha_revision TIMESTAMP WITH TIME ZONE,
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  razon TEXT
);

-- ========================================
-- RESENAS_PRODUCTO
-- ========================================
CREATE TABLE resenas_producto (
  id SERIAL PRIMARY KEY,
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  comprador_id UUID REFERENCES usuarios(id),
  calificacion SMALLINT CHECK (calificacion >= 1 AND calificacion <= 5),
  comentario TEXT,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  UNIQUE(producto_id, comprador_id)
);

-- ========================================
-- SANCIONES
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
-- CARRITOS
-- ========================================
CREATE TABLE carritos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  usuario_id UUID UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- ITEMS_CARRITO
-- ========================================
CREATE TABLE items_carrito (
  id SERIAL PRIMARY KEY,
  carrito_id UUID REFERENCES carritos(id) ON DELETE CASCADE,
  producto_id UUID REFERENCES productos(id),
  cantidad INTEGER NOT NULL CHECK (cantidad > 0),
  precio_unitario NUMERIC(12,2) NOT NULL,
  fecha_agregado TIMESTAMP WITH TIME ZONE DEFAULT now(),
  UNIQUE (carrito_id, producto_id)
);

-- ========================================
-- PEDIDOS
-- ========================================
CREATE TABLE pedidos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  comprador_id UUID REFERENCES usuarios(id),
  monto_total NUMERIC(12,2) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'EN_CURSO',
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now(),
  fecha_entrega_estimada DATE,
  fecha_entregado TIMESTAMP WITH TIME ZONE
);

-- ========================================
-- ITEMS_PEDIDO
-- ========================================
CREATE TABLE items_pedido (
  id SERIAL PRIMARY KEY,
  pedido_id UUID REFERENCES pedidos(id) ON DELETE CASCADE,
  producto_id UUID REFERENCES productos(id),
  cantidad INTEGER NOT NULL,
  precio_unitario NUMERIC(12,2) NOT NULL,
  comision NUMERIC(12,2) NOT NULL,
  monto_vendedor NUMERIC(12,2) NOT NULL
);

-- ========================================
-- PAGOS
-- ========================================
CREATE TABLE pagos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  pedido_id UUID REFERENCES pedidos(id) UNIQUE,
  proveedor_pago VARCHAR(100) DEFAULT 'MOCK',
  id_pago_proveedor VARCHAR(255),
  monto NUMERIC(12,2) NOT NULL,
  moneda VARCHAR(10) DEFAULT 'GTQ',
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  ultimos_4_digitos VARCHAR(4),
  marca_tarjeta VARCHAR(50),
  token_tarjeta_guardada VARCHAR(255),
  fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- TARJETAS_GUARDADAS
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
-- ENVIOS
-- ========================================
CREATE TABLE envios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  pedido_id UUID REFERENCES pedidos(id) UNIQUE,
  usuario_logistica_id UUID REFERENCES usuarios(id),
  estado VARCHAR(30) DEFAULT 'EN_CURSO',
  fecha_entrega_estimada DATE,
  fecha_entregado TIMESTAMP WITH TIME ZONE,
  fecha_actualizacion TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ========================================
-- NOTIFICACIONES
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
-- TRANSACCIONES
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
-- ÍNDICES
-- ========================================
CREATE INDEX idx_productos_vendedor ON productos (vendedor_id);
CREATE INDEX idx_productos_categoria ON productos (categoria_id);
CREATE INDEX idx_productos_estado ON productos (estado);
CREATE INDEX idx_pedidos_comprador ON pedidos (comprador_id);
CREATE INDEX idx_solicitudes_moderacion_estado ON solicitudes_moderacion (estado);
CREATE INDEX idx_envios_estado ON envios (estado);
CREATE INDEX idx_imagenes_producto ON imagenes_producto (producto_id);