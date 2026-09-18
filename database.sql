-- Script SQL para crear la Base de Datos "SuperMarket_db" y sus tablas
DROP DATABASE IF EXISTS SuperMarket_db;
CREATE DATABASE SuperMarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE SuperMarket_db;

-- 1. Tabla de inventario de materia prima
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    unidades_paq INT NOT NULL,
    peso_libras DOUBLE NOT NULL,
    precio_venta DOUBLE NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- 2. Tabla de productos finales a vender
CREATE TABLE productos_venta (
    id_producto_venta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio_venta DOUBLE NOT NULL,
    categoria VARCHAR(50) DEFAULT 'General'
) ENGINE=InnoDB;

-- 3. Tabla puente: Recetas de productos de venta
CREATE TABLE receta_ingredientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_producto_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad DOUBLE NOT NULL,
    unidad VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_producto_venta) REFERENCES productos_venta(id_producto_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. Cabecera de Ventas
CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    total_venta DOUBLE NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. Detalle de Ventas
CREATE TABLE detalle_ventas (
    id_venta INT NOT NULL,
    id_producto_venta INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto_venta) REFERENCES productos_venta(id_producto_venta) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. Tabla de Gastos
CREATE TABLE gastos (
    id_gasto INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    monto DOUBLE NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 7. Historial de movimientos manuales de inventario
CREATE TABLE historial_inventario (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Control de Usuarios del sistema
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'cajero'
) ENGINE=InnoDB;

-- =========================================================================
-- Datos iniciales opcionales
-- =========================================================================

INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES ('Administrador', 'admin', 'admin123', 'admin');

INSERT INTO productos (id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo) VALUES
(1, 'Pan de Hamburguesa', 12, 1.5, 0, 100, 20),
(2, 'Carne de Res (Torta)', 1, 0.25, 0, 80, 15),
(3, 'Queso Americano', 50, 2.0, 0, 200, 30),
(4, 'Papas Fritas Congeladas', 1, 5.0, 0, 50, 10);

INSERT INTO productos_venta (id_producto_venta, nombre, precio_venta, categoria) VALUES
(1, 'Hamburguesa Clásica', 35.0, 'Comida'),
(2, 'Papas Fritas', 15.0, 'Acompañamiento');

INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(1, 1, 1, 'Unidad'),
(1, 2, 1, 'Unidad'),
(1, 3, 1, 'Unidad'),
(2, 4, 0.5, 'Libras');