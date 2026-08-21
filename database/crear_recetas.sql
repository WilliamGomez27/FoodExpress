USE SuperMarket_db;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Crear tabla de recetas (vincula productos terminados con materia prima)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS receta_ingredientes (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    id_producto_venta INT    NOT NULL,         -- FK → productos_venta
    id_producto       INT    NOT NULL,         -- FK → productos (materia prima)
    cantidad          DOUBLE NOT NULL,         -- cantidad consumida por unidad producida
    unidad            VARCHAR(20) NOT NULL DEFAULT 'unidades',  -- 'unidades','gramos','ml'
    UNIQUE KEY uk_receta_producto (id_producto_venta, id_producto),
    CONSTRAINT fk_receta_prod_venta    FOREIGN KEY (id_producto_venta) REFERENCES productos_venta(id_producto_venta),
    CONSTRAINT fk_receta_materia_prima FOREIGN KEY (id_producto)       REFERENCES productos(id_producto)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Recetas de ejemplo
--    Se asume que los id_producto_venta 1 y 2 (Hamburguesas) existen.
-- ─────────────────────────────────────────────────────────────────────────────

-- Limpiar recetas previas para evitar duplicados si se corre varias veces
DELETE FROM receta_ingredientes WHERE id_producto_venta IN (1, 2);

-- Receta: Hamburguesa Clásica (id_producto_venta = 1)
INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(1, 1,   2,   'unidades'),   -- Pan Hamburguesa x2
(1, 2,   1,   'unidades'),   -- Carne Hamburguesa x1
(1, 3,   50,  'gramos'),     -- Queso x50g
(1, 4,   150, 'gramos'),     -- Papa Chips x150g
(1, 5,   100, 'gramos'),     -- Tomate x100g
(1, 6,   50,  'gramos');     -- Cebolla x50g

-- Receta: Hamburguesa Doble (id_producto_venta = 2)
INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(2, 1,   2,   'unidades'),   -- Pan Hamburguesa x2
(2, 2,   2,   'unidades'),   -- Carne Hamburguesa x2
(2, 3,   100, 'gramos'),     -- Queso x100g
(2, 4,   150, 'gramos'),     -- Papa Chips x150g
(2, 5,   100, 'gramos'),     -- Tomate x100g
(2, 6,   50,  'gramos');     -- Cebolla x50g
