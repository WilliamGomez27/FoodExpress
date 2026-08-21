USE SuperMarket_db;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Tabla de Gastos
--    Para llevar el control de los egresos diarios/mensuales.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gastos (
    id_gasto    INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    monto       DECIMAL(10, 2) NOT NULL,
    fecha_hora  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Datos de Ejemplo para Gastos
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO gastos (descripcion, monto, fecha_hora)
SELECT 'Compra de insumos urgentes (Tomates)', 25.50, NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (
    SELECT 1 FROM gastos WHERE descripcion = 'Compra de insumos urgentes (Tomates)'
);

INSERT INTO gastos (descripcion, monto, fecha_hora)
SELECT 'Pago de servicio de luz', 150.00, NOW() - INTERVAL 1 DAY
WHERE NOT EXISTS (
    SELECT 1 FROM gastos WHERE descripcion = 'Pago de servicio de luz'
);

INSERT INTO gastos (descripcion, monto, fecha_hora)
SELECT 'Compra de servilletas y bolsas', 15.00, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM gastos WHERE descripcion = 'Compra de servilletas y bolsas'
);
