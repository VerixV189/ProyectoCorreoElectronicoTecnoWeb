-- ejecutar todo el script en psql

DROP DATABASE IF EXISTS carpinteria;
CREATE DATABASE carpinteria;


-- Estructura para PostgreSQL
-- Basado en Diagrama de base de datos Tecno Web.drawio.png

-- 1. TABLAS INDEPENDIENTES Y PADRES

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'Activo',
	id_rol INT NOT NUL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    nombre_empresa VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    direccion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tipo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'Activo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. HERENCIA DE USUARIO (Relaciones 1:1)

CREATE TABLE carpintero (
    id INT PRIMARY KEY,
    especialidad VARCHAR(100),
    costo_hora DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE cliente (
    id INT PRIMARY KEY,
    nit_facturacion VARCHAR(50),
    razon_social VARCHAR(150),
    direccion_envio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES Usuario(id) ON DELETE CASCADE
);

-- 3. PRODUCCIÓN E INSUMOS

CREATE TABLE insumo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    imagen VARCHAR(255), -- Ruta de imagen directa o ID si prefieres FK
    id_proveedor INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_proveedor) REFERENCES Proveedor(id)
);

CREATE TABLE producto (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    cantidad INT DEFAULT 0,
    precio DECIMAL(12, 2) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'Disponible',
    id_tipo INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tipo) REFERENCES Tipo(id)
);

CREATE TABLE imagen (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
	id_producto INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id_producto) REFERENCES Producto(id)
);

CREATE TABLE insumo_producto (
    id SERIAL PRIMARY KEY,
    id_insumo INT NOT NULL,
    id_producto INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_insumo) REFERENCES Insumo(id),
    FOREIGN KEY (id_producto) REFERENCES Producto(id)
);

-- 4. FLUJO DE VENTAS Y NEGOCIO

CREATE TABLE cotizacion (
    id SERIAL PRIMARY KEY,
    descripcion TEXT,
	estado VARCHAR(20),
    id_cliente INT NOT NULL,
    id_carpintero INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id),
    FOREIGN KEY (id_carpintero) REFERENCES Carpintero(id)
);

CREATE TABLE pedido (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE,
    precio DECIMAL(12, 2),
    fecha_entrega DATE,
    id_cotizacion INT UNIQUE, -- Relación 0..1 a 1..1
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cotizacion) REFERENCES Cotizacion(id)
);

CREATE TABLE detalle_pedido (
    id SERIAL PRIMARY KEY,
    cantidad INT NOT NULL,
    precio DECIMAL(12, 2) NOT NULL,
    estado VARCHAR(50),
    descripcion TEXT,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pedido) REFERENCES Pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES Producto(id)
);

CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE,
    total_costo DECIMAL(12, 2) NOT NULL,
    fecha_entregado DATE,
    tipo VARCHAR(50), -- Contado / Crédito
    id_pedido INT UNIQUE, -- Relación 0..1 a 1..1
    id_cliente INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pedido) REFERENCES Pedido(id),
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id)
);

CREATE TABLE pago (
    id SERIAL PRIMARY KEY,
    subtotal DECIMAL(12, 2) NOT NULL,
    interes DECIMAL(12, 2) DEFAULT 0.00,
    id_venta INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_venta) REFERENCES Venta(id) ON DELETE CASCADE
);

-- FUNCIÓN PARA ACTUALIZAR AUTOMÁTICAMENTE EL CAMPO updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- EJEMPLO DE CÓMO APLICAR EL TRIGGER (Deberías aplicarlo a cada tabla)
-- CREATE TRIGGER update_usuario_modtime BEFORE UPDATE ON Usuario FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();