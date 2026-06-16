-- ejecutar todo el script en psql

DROP DATABASE IF EXISTS carpinteria;
CREATE DATABASE carpinteria;


-- Estructura para PostgreSQL
-- Basado en Diagrama de base de datos Tecno Web.drawio.png

-- 1. TABLAS INDEPENDIENTES Y PADRES

CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permiso (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rol_permiso (
    id SERIAL PRIMARY KEY,
    id_rol INT NOT NULL,
    id_permiso INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id) ON DELETE CASCADE,
    FOREIGN KEY (id_permiso) REFERENCES Permiso(id) ON DELETE CASCADE
);

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'Activo',
    id_rol INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id) ON DELETE CASCADE
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
    id_producto INT NULL,
    id_insumo INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES Producto(id),
    FOREIGN KEY (id_insumo) REFERENCES Insumo(id) ON DELETE CASCADE
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id)
);

CREATE TABLE detalle_cotizacion (
    id SERIAL PRIMARY KEY,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    id_cotizacion INT NOT NULL,
    id_carpintero INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cotizacion) REFERENCES Cotizacion(id) ON DELETE CASCADE,
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
    id_producto INT NULL,
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
    estado VARCHAR(20) DEFAULT 'Pendiente',
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


-- =========================================================================
-- 5. SEMILLAS (INSERTS INICIALES) DE ROLES Y PERMISOS
-- =========================================================================

-- Insertar Roles
INSERT INTO rol (id, nombre, estado) VALUES 
(1, 'Administrador', 'Activo'),
(2, 'Cliente', 'Activo'),
(3, 'Carpintero', 'Activo');

-- Ajustar secuencia de id de la tabla rol en PostgreSQL
SELECT setval('rol_id_seq', (SELECT MAX(id) FROM rol));

-- Insertar Permisos (Comandos soportados en el sistema)
INSERT INTO permiso (id, nombre) VALUES
(1, 'LISUSU'), (2, 'REGUSU'), (3, 'ACTUSU'), (4, 'ELIMUSU'),
(5, 'LISCLI'), (6, 'REGCLI'), (7, 'ACTCLI'), (8, 'ELIMCLI'),
(9, 'LISCARP'), (10, 'REGCARP'), (11, 'ACTCARP'), (12, 'ELIMCARP'),
(13, 'LISPROD'), (14, 'REGPROD'), (15, 'ACTPROD'), (16, 'ELIMPROD'),
(17, 'LISCOT'), (18, 'REGCOT'), (19, 'ACTCOT'), (20, 'ELIMCOT'),
(21, 'LISPED'), (22, 'REGPED'), (23, 'ACTPED'), (24, 'ELIMPED'),
(25, 'LISDET'), (26, 'REGDET'), (27, 'ACTDET'), (28, 'ELIMDET'),
(29, 'LISPAG'), (30, 'REGPAG'), (31, 'ACTPAG'), (32, 'ELIMPAG'),
(33, 'LISVEN'), (34, 'REGVEN'), (35, 'ACTVEN'), (36, 'ELIMVEN'),
(37, 'LISINSU'), (38, 'REGINSU'), (39, 'ACTINSU'), (40, 'ELIMINSU'),
(41, 'LISPROV'), (42, 'REGPROV'), (43, 'ACTPROV'), (44, 'ELIMPROV'),
(45, 'LISROL'), (46, 'REGROL'), (47, 'ACTROL'), (48, 'ELIMROL'),
(49, 'LISTIP'), (50, 'REGTIP'), (51, 'ACTTIP'), (52, 'ELIMTIP'),
(53, 'LISIMG'), (54, 'REGIMG'), (55, 'ACTIMG'), (56, 'ELIMIMG'),
(57, 'LISPERM'), (58, 'REGPERM'), (59, 'ACTPERM'), (60, 'ELIMPERM');

-- Ajustar secuencia de id de la tabla permiso en PostgreSQL
SELECT setval('permiso_id_seq', (SELECT MAX(id) FROM permiso));


-- =========================================================================
-- RELACIONAR PERMISOS A ROLES (rol_permiso)
-- =========================================================================

-- 1. Administrador (id_rol = 1) tiene TODOS los permisos del sistema
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, id FROM permiso;

-- 2. Cliente (id_rol = 2) tiene permisos selectivos de visualización y autoservicio
INSERT INTO rol_permiso (id_rol, id_permiso) VALUES
(2, 5),  -- LISCLI
(2, 6),  -- REGCLI
(2, 7),  -- ACTCLI
(2, 13), -- LISPROD
(2, 17), -- LISCOT
(2, 18), -- REGCOT
(2, 19), -- ACTCOT
(2, 20), -- ELIMCOT
(2, 21), -- LISPED
(2, 25), -- LISDET
(2, 29), -- LISPAG
(2, 33), -- LISVEN
(2, 53); -- LISIMG

-- 3. Carpintero (id_rol = 3) tiene permisos enfocados a la manufactura y productos
INSERT INTO rol_permiso (id_rol, id_permiso) VALUES
(3, 9),  -- LISCARP
(3, 11), -- ACTCARP
(3, 13), -- LISPROD
(3, 14), -- REGPROD
(3, 15), -- ACTPROD
(3, 17), -- LISCOT
(3, 19), -- ACTCOT
(3, 21), -- LISPED
(3, 23), -- ACTPED
(3, 25), -- LISDET
(3, 27), -- ACTDET
(3, 37), -- LISINSU
(3, 38), -- REGINSU
(3, 39), -- ACTINSU
(3, 41), -- LISPROV
(3, 49), -- LISTIP
(3, 53), -- LISIMG
(3, 54), -- REGIMG
(3, 55), -- ACTIMG
(3, 56); -- ELIMIMG