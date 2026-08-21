USE SuperMarket_db;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Eliminar la tabla si existe
-- ─────────────────────────────────────────────────────────────────────────────
DROP TABLE IF EXISTS productos;

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Crear la tabla de materia prima (productos)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE productos (
    id_producto   INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    unidades_paq  INT NOT NULL DEFAULT 1,
    peso_libras   DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    precio_venta  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,  -- Puede usarse como costo o precio histórico
    stock_actual  INT NOT NULL DEFAULT 0,
    stock_minimo  INT NOT NULL DEFAULT 5
);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. Insertar datos de ejemplo (Materia Prima)
--    Se incluyen los 6 requeridos para las hamburguesas más otros adicionales
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO productos (id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo) VALUES
-- Requeridos para las recetas (IDs 1 al 6 para que coincidan con la migración de ventas)
(1, 'Pan de Hamburguesa',     12, 1.50, 0.00, 100, 20),   -- medido en unidades
(2, 'Carne de Hamburguesa',   24, 5.00, 0.00, 200, 40),   -- medido en unidades
(3, 'Queso',                  1,  4.40, 0.00, 5000, 1000), -- medido en gramos (ej. 5000g = 5kg)
(4, 'Papa Chips',             1,  5.00, 0.00, 10000, 2000),-- medido en gramos (ej. 10000g = 10kg)
(5, 'Tomate',                 1,  2.20, 0.00, 3000, 500),  -- medido en gramos
(6, 'Cebolla',                1,  2.20, 0.00, 3000, 500),  -- medido en gramos

-- Otros ingredientes adicionales
(7, 'Lechuga',                1,  1.00, 0.00, 2000, 500),  -- medido en gramos
(8, 'Salsa Ketchup',          1,  8.00, 0.00, 4000, 1000), -- medido en ml o gramos
(9, 'Mayonesa',               1,  8.00, 0.00, 4000, 1000), -- medido en ml o gramos
(10, 'Aceite para freír',     1, 35.00, 0.00, 15000, 5000),-- medido en ml
(11, 'Tocino',                1,  2.20, 0.00, 2000, 500),  -- medido en gramos
(12, 'Salchicha',             20, 2.00, 0.00, 150, 30);    -- medido en unidades
