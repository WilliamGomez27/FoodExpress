USE SuperMarket_db;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Tabla de productos terminados (catálogo de venta)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS productos_venta (
    id_producto_venta INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    precio_venta      DECIMAL(10, 2) NOT NULL,
    categoria         VARCHAR(50) DEFAULT 'General'
);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Insertar productos terminados de ejemplo
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO productos_venta (nombre, precio_venta, categoria) VALUES
('Hamburguesa Clásica',       35.0, 'Comida'),
('Hamburguesa Doble',         45.0, 'Comida'),
('Papas Fritas Medianas',     15.0, 'Acompañamiento'),
('Papas Fritas Grandes',      20.0, 'Acompañamiento'),
('Gaseosa Cola 16oz',         12.0, 'Bebida'),
('Gaseosa Naranja 16oz',      12.0, 'Bebida'),
('Combo Hamburguesa Clásica', 55.0, 'Combos'),
('Nuggets de Pollo (6 pz)',   25.0, 'Comida'),
('Helado Cono',               10.0, 'Postre');

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. Renombrar columna en detalle_ventas: id_producto → id_producto_venta
--    NOTA: Si existe una Llave Foránea sobre id_producto, elimínala primero:
--    ALTER TABLE detalle_ventas DROP FOREIGN KEY nombre_de_tu_fk;
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE detalle_ventas
    CHANGE id_producto id_producto_venta INT NOT NULL;

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. Agregar FK hacia productos_venta en detalle_ventas
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE detalle_ventas
    ADD CONSTRAINT fk_detalle_producto_venta
    FOREIGN KEY (id_producto_venta)
    REFERENCES productos_venta(id_producto_venta);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. MÓDULO DE RECETAS
--    Tabla que vincula materia prima (productos) con productos terminados
--    (productos_venta). Define cuánto de cada ingrediente se consume al
--    fabricar UNA unidad del producto terminado.
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
-- 6. Recetas de ejemplo
--    AJUSTA los id_producto según los IDs reales de tu tabla `productos`.
--    El orden asumido aquí es:
--      id_producto = 1 → Pan de Hamburguesa
--      id_producto = 2 → Carne de Hamburguesa
--      id_producto = 3 → Queso
--      id_producto = 4 → Papa Chips
--      id_producto = 5 → Tomate
--      id_producto = 6 → Cebolla
-- ─────────────────────────────────────────────────────────────────────────────

-- Receta: Hamburguesa Clásica (id_producto_venta = 1)
--   Ingredientes: 2 panes, 1 carne, 50g queso, 150g papa chips, 100g tomate, 50g cebolla
INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(1, 1,   2,   'unidades'),   -- Pan Hamburguesa x2
(1, 2,   1,   'unidades'),   -- Carne Hamburguesa x1
(1, 3,   50,  'gramos'),     -- Queso x50g
(1, 4,   150, 'gramos'),     -- Papa Chips x150g
(1, 5,   100, 'gramos'),     -- Tomate x100g
(1, 6,   50,  'gramos');     -- Cebolla x50g

-- Receta: Hamburguesa Doble (id_producto_venta = 2)
--   Ingredientes: 2 panes, 2 carnes, 100g queso, 150g papa chips, 100g tomate, 50g cebolla
INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad) VALUES
(2, 1,   2,   'unidades'),   -- Pan Hamburguesa x2
(2, 2,   2,   'unidades'),   -- Carne Hamburguesa x2
(2, 3,   100, 'gramos'),     -- Queso x100g
(2, 4,   150, 'gramos'),     -- Papa Chips x150g
(2, 5,   100, 'gramos'),     -- Tomate x100g
(2, 6,   50,  'gramos');     -- Cebolla x50g
