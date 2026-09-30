# Script SQL para DBeaver (Actualización de Imágenes)

Copia y pega este script directamente en tu editor SQL en **DBeaver**. Se ha eliminado la cláusula `IF NOT EXISTS` para garantizar total compatibilidad con todas las versiones de MySQL.

```sql
USE SuperMarket_db;

-- 1. Agregar la columna imagen_url (si ya existe, puedes ignorar el error de columna duplicada)
ALTER TABLE productos_venta ADD COLUMN imagen_url VARCHAR(255) DEFAULT NULL;

-- 2. Asignar imágenes limpias a cada producto por su ID
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_1.jpeg' WHERE id_producto_venta = 1;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_2.jpeg' WHERE id_producto_venta = 2;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_3.jpeg' WHERE id_producto_venta = 3;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_4.jpeg' WHERE id_producto_venta = 4;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_5.jpeg' WHERE id_producto_venta = 5;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_6.jpeg' WHERE id_producto_venta = 6;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_7.jpeg' WHERE id_producto_venta = 7;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_8.jpeg' WHERE id_producto_venta = 8;
UPDATE productos_venta SET imagen_url = 'file:///android_asset/images/img_9.jpeg' WHERE id_producto_venta = 9;

-- 3. Ver los productos con sus respectivas imágenes asignadas
SELECT * FROM productos_venta;
```
