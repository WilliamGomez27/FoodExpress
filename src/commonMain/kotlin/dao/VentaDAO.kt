package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.ItemVenta
import FastFoodApp.model.Venta
import java.sql.SQLException

class VentaDAO {

    private val recetaDAO = RecetaDAO()

    fun registrarVenta(items: List<ItemVenta>): Int {
        if (items.isEmpty()) {
            println("✗ [VentaDAO] No hay items en la venta")
            return -1
        }

        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            println("✗ [VentaDAO] No se pudo obtener conexión para registrarVenta")
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
                println("✗ [VentaDAO] Falló al obtener ID de venta")
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
                    println("✗ [VentaDAO] Error al descontar stock — transacción revertida")
                    return -1
                }
            }

            conexion.commit()
            println("✓ [VentaDAO] Venta #$idVenta registrada: Q$total (${items.size} items, stock descontado)")
            idVenta
        } catch (e: SQLException) {
            try {
                conexion.rollback()
                println("✗ [VentaDAO] Transacción revertida: ${e.message}")
            } catch (rollbackEx: SQLException) {
                println("✗ [VentaDAO] Error al hacer rollback: ${rollbackEx.message}")
            }
            println("✗ [VentaDAO] Error en registrarVenta: ${e.message}")
            -1
        } finally {
            try {
                conexion.autoCommit = true
            } catch (e: SQLException) {
                println("✗ [VentaDAO] Error al restaurar autoCommit: ${e.message}")
            }
        }
    }

    fun obtenerUltimasVentas(limite: Int = 20): List<Venta> {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            println("✗ [VentaDAO] No se pudo obtener conexión para obtenerUltimasVentas")
            return emptyList()
        }

        val lista = mutableListOf<Venta>()
        val sql = "SELECT id_venta, DATE_FORMAT(fecha_hora,'%d/%m/%Y %H:%i') as fecha_hora, total_venta FROM ventas ORDER BY fecha_hora DESC LIMIT ?"
        return try {
            conexion.prepareStatement(sql).use { stmt ->
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
            println("✓ [VentaDAO] Cargadas ${lista.size} ventas recientes")
            lista
        } catch (e: SQLException) {
            println("✗ [VentaDAO] Error en obtenerUltimasVentas: ${e.message}")
            emptyList()
        }
    }
}
