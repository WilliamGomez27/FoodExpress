-- Script SQL para la creación inicial de la base de datos "SuperMarket_db"
-- Útil para documentación y recrear la BD de manera limpia desde 0.

DROP DATABASE IF EXISTS SuperMarket_db;
CREATE DATABASE SuperMarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE SuperMarket_db;

-- =========================================================================
-- 1. TABLAS INDEPENDIENTES (Sin dependencias)
-- =========================================================================

CREATE TABLE IF NOT EXISTS productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    unidades_paq INT NOT NULL,
    peso_libras DOUBLE NOT NULL,
    precio_venta DOUBLE NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_stock_no_negativo CHECK (stock_actual >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS productos_venta (
    id_producto_venta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio_venta DOUBLE NOT NULL,
    categoria VARCHAR(50) DEFAULT 'General'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    total_venta DOUBLE NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS gastos (
    id_gasto INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    monto DOUBLE NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(64) NOT NULL,   -- Hash SHA-256 (64 chars hex)
    salt VARCHAR(64) NOT NULL,          -- Salt aleatorio por usuario
    rol VARCHAR(20) NOT NULL DEFAULT 'cajero'
) ENGINE=InnoDB;

-- =========================================================================
-- 2. TABLAS DEPENDIENTES (Con claves foráneas)
-- =========================================================================

CREATE TABLE IF NOT EXISTS receta_ingredientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_producto_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad DOUBLE NOT NULL,
    unidad VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_producto_venta) REFERENCES productos_venta(id_producto_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_venta INT NOT NULL,
    id_producto_venta INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto_venta) REFERENCES productos_venta(id_producto_venta) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS historial_inventario (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================================================
-- 3. DATOS DE INICIALIZACIÓN (Pruebas y usuarios base)
-- =========================================================================

-- Usuario Administrador (Usuario: admin / Clave: admin123)
-- Hash generado mediante SHA2(CONCAT('FastFoodSalt2026','admin123'), 256)
INSERT IGNORE INTO usuarios (nombre, usuario, contrasena, salt, rol) VALUES
('Administrador', 'aleks', '8b3687cdaaf59e742410a80e1291880ce8227b686e04d49a376eb1c28c89a05b', 'FastFoodSalt2026', 'admin');

-- Cajera Principal (Usuario: 623250 / Clave: op557)
-- Hash generado mediante SHA2(CONCAT('FastFoodSalt2026','op557'), 256)
INSERT IGNORE INTO usuarios (nombre, usuario, contrasena, salt, rol) VALUES
('Cajera Principal', '623250', '3dc6de1172a6b25cd0cf53258c7e0c460012ba119c8d76a7d76f5713ec440fce', 'FastFoodSalt2026', 'cajero');

-- Materia Prima Inicial
INSERT IGNORE INTO productos (id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo) VALUES
(1, 'Pan de Hamburguesa', 12, 1.5, 0, 100, 20),
(2, 'Carne de Res (Torta)', 1, 0.25, 0, 80, 15),
(3, 'Queso Americano', 50, 2.0, 0, 200, 30),
(4, 'Papas Fritas Congeladas', 1, 5.0, 0, 50, 10);

-- Productos Terminados Iniciales
INSERT IGNORE INTO productos_venta (id_producto_venta, nombre, precio_venta, categoria) VALUES
(1, 'Hamburguesa Clásica', 35.0, 'Comida'),
(2, 'Papas Fritas', 15.0, 'Acompañamiento');

-- Recetas Iniciales
INSERT IGNORE INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(1, 1, 1, 'Unidad'),
(1, 2, 1, 'Unidad'),
(1, 3, 1, 'Unidad'),
(2, 4, 0.5, 'Libras');