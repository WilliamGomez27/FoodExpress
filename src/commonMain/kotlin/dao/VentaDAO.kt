package FoodExpress.dao

import FoodExpress.database.ConexionDB
import FoodExpress.model.ItemVenta
import FoodExpress.model.Venta
import FoodExpress.utils.AppLogger
import java.sql.SQLException

class VentaDAO {

    private val recetaDAO = RecetaDAO()

    fun registrarVenta(items: List<ItemVenta>): Int {
        if (items.isEmpty()) {
            AppLogger.warn("VentaDAO", "No hay items en la venta")
            return -1
        }

        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            AppLogger.error("VentaDAO", "No se pudo obtener conexión para registrarVenta")
            return -1
        }

        val total = items.sumOf { it.subtotal }

        return try {
            conexion.autoCommit = false

            val sqlVenta = "INSERT INTO ventas (total_venta, fecha_hora) VALUES (?, NOW())"
            val idVenta: Int = conexion.prepareStatement(sqlVenta, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setDouble(1, total)
                stmt.executeUpdate()
                val keys = stmt.generatedKeys
                if (keys.next()) keys.getInt(1) else -1
            }

            if (idVenta == -1) {
                conexion.rollback()
                AppLogger.error("VentaDAO", "Falló al obtener ID de venta")
                return -1
            }

            val sqlDetalle = """
                INSERT INTO detalle_ventas (id_venta, id_producto_venta, cantidad, precio_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
            """
            items.forEach { item ->
                conexion.prepareStatement(sqlDetalle).use { stmt ->
                    stmt.setInt(1, idVenta)
                    stmt.setInt(2, item.productoVenta.idProductoVenta)
                    stmt.setInt(3, item.cantidad)
                    stmt.setDouble(4, item.productoVenta.precioVenta)
                    stmt.setDouble(5, item.subtotal)
                    stmt.executeUpdate()
                }
            }

            items.forEach { item ->
                val ok = recetaDAO.descontarStockPorReceta(
                    idProductoVenta  = item.productoVenta.idProductoVenta,
                    cantidadVendida  = item.cantidad,
                    conexion         = conexion
                )
                if (!ok) {
                    conexion.rollback()
                    AppLogger.error("VentaDAO", "Error al descontar stock — transacción revertida")
                    return -1
                }
            }

            conexion.commit()
            AppLogger.info("VentaDAO", "Venta #$idVenta registrada: COP $total (${items.size} items)")
            idVenta
        } catch (e: SQLException) {
            try {
                conexion.rollback()
                AppLogger.error("VentaDAO", "Transacción revertida: ${e.message}")
            } catch (rollbackEx: SQLException) {
                AppLogger.error("VentaDAO", "Error al hacer rollback: ${rollbackEx.message}")
            }
            AppLogger.error("VentaDAO", "Error en registrarVenta: ${e.message}")
            -1
        } finally {
            try {
                conexion.autoCommit = true
            } catch (e: SQLException) {
                AppLogger.error("VentaDAO", "Error al restaurar autoCommit: ${e.message}")
            }
            conexion.close()
        }
    }

    fun obtenerUltimasVentas(limite: Int = 20): List<Venta> {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            AppLogger.error("VentaDAO", "No se pudo obtener conexión para obtenerUltimasVentas")
            return emptyList()
        }

        val lista = mutableListOf<Venta>()
        val sql = "SELECT id_venta, DATE_FORMAT(fecha_hora,'%d/%m/%Y %H:%i') as fecha_hora, total_venta FROM ventas ORDER BY fecha_hora DESC LIMIT ?"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
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
            }
            AppLogger.info("VentaDAO", "Cargadas ${lista.size} ventas recientes")
            lista
        } catch (e: SQLException) {
            AppLogger.error("VentaDAO", "Error en obtenerUltimasVentas: ${e.message}")
            emptyList()
        }
    }
}
