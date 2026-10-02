# Actualización de Base de Datos: Unidades de Medida en Inventario

Para que el administrador pueda especificar si la materia prima está en gramos, mililitros o unidades, ejecuta el siguiente comando SQL en tu **DBeaver** o consola MySQL.

```sql
USE SuperMarket_db;

-- Agregamos la columna de unidad de medida a la tabla de materia prima
ALTER TABLE productos
ADD COLUMN unidad_medida VARCHAR(20) NOT NULL DEFAULT 'unidades';

-- (Opcional) Ajustar el stock_actual y stock_minimo a DOUBLE si en el futuro deseas usar decimales para inventario
ALTER TABLE productos
MODIFY COLUMN stock_actual DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
MODIFY COLUMN stock_minimo DECIMAL(10, 2) NOT NULL DEFAULT 5.00;

-- Actualizamos algunos ejemplos para que tengan sentido:
UPDATE productos SET unidad_medida = 'unidades' WHERE nombre LIKE '%Pan%';
UPDATE productos SET unidad_medida = 'gramos' WHERE nombre LIKE '%Carne%';
UPDATE productos SET unidad_medida = 'gramos' WHERE nombre LIKE '%Queso%';
UPDATE productos SET unidad_medida = 'ml' WHERE nombre LIKE '%Salsa%';
```