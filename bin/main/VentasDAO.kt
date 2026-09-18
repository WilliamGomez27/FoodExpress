package FastFoodApp

import java.sql.SQLException

/**
 * DAO para el módulo de Ventas.
 * Maneja el registro de ventas y sus items en transacción.
 */
class VentasDAO {
    private val conexion = ConexionDB.getConexion()

    /**
     * Registra una venta completa: encabezado + detalle + descuento de stock.
     * Todo en una sola transacción para garantizar consistencia.
     * @return El ID de la venta creada, o -1 si falló.
     */
    fun registrarVenta(items: List<ItemVenta>): Int {
        if (items.isEmpty()) return -1
        val total = items.sumOf { it.subtotal }

        return try {
            conexion?.autoCommit = false

            // 1. Insertar encabezado de la venta
            val sqlVenta = "INSERT INTO ventas (total_venta, fecha_hora) VALUES (?, NOW())"
            val idVenta: Int = conexion?.prepareStatement(sqlVenta, java.sql.Statement.RETURN_GENERATED_KEYS)?.use { stmt ->
                stmt.setDouble(1, total)
                stmt.executeUpdate()
                val keys = stmt.generatedKeys
                if (keys.next()) keys.getInt(1) else -1
            } ?: -1

            if (idVenta == -1) {
                conexion?.rollback()
                return -1
            }

            // 2. Insertar detalle de cada item
            val sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)"
            items.forEach { item ->
                conexion?.prepareStatement(sqlDetalle)?.use { stmt ->
                    stmt.setInt(1, idVenta)
                    stmt.setInt(2, item.producto.idProducto)
                    stmt.setInt(3, item.cantidad)
                    stmt.setDouble(4, item.producto.precioVenta)
                    stmt.setDouble(5, item.subtotal)
                    stmt.executeUpdate()
                }
            }

            // 3. Descontar stock de cada producto vendido
            val sqlStock = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?"
            items.forEach { item ->
                conexion?.prepareStatement(sqlStock)?.use { stmt ->
                    stmt.setInt(1, item.cantidad)
                    stmt.setInt(2, item.producto.idProducto)
                    stmt.executeUpdate()
                }
            }

            // 4. También registrar en historial_inventario
            val sqlHistorial = "INSERT INTO historial_inventario (id_producto, cantidad, motivo, tipo) VALUES (?, ?, 'Venta', 'SALIDA')"
            items.forEach { item ->
                conexion?.prepareStatement(sqlHistorial)?.use { stmt ->
                    stmt.setInt(1, item.producto.idProducto)
                    stmt.setInt(2, item.cantidad)
                    stmt.executeUpdate()
                }
            }

            conexion?.commit()
            idVenta
        } catch (e: SQLException) {
            conexion?.rollback()
            println("Error en registrarVenta: ${e.message}")
            -1
        } finally {
            conexion?.autoCommit = true
        }
    }

    /**
     * Obtiene el historial de las últimas ventas.
     */
    fun obtenerUltimasVentas(limite: Int = 20): List<Venta> {
        val lista = mutableListOf<Venta>()
        val sql = "SELECT id_venta, DATE_FORMAT(fecha_hora,'%d/%m/%Y %H:%i') as fecha_hora, total_venta FROM ventas ORDER BY fecha_hora DESC LIMIT ?"
        return try {
            conexion?.prepareStatement(sql)?.use { stmt ->
                stmt.setInt(1, limite)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    lista.add(
                        Venta(
                            idVenta    = rs.getInt("id_venta"),
                            fechaHora  = rs.getString("fecha_hora"),
                            totalVenta = rs.getDouble("total_venta")
                        )
                    )
                }
            }
            lista
        } catch (e: SQLException) {
            println("Error en obtenerUltimasVentas: ${e.message}")
            emptyList()
        }
    }
}
