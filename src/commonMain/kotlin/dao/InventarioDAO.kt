package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.Producto
import FastFoodApp.utils.AppLogger
import java.sql.SQLException

class InventarioDAO {

    fun registrarMovimiento(idProducto: Int, cantidad: Int, motivo: String, esEntrada: Boolean): Boolean {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            AppLogger.error("InventarioDAO", "No se pudo obtener conexión para registrarMovimiento")
            return false
        }

        return try {
            conexion.autoCommit = false

            // 1. Guardar en historial
            val sqlHistorial = "INSERT INTO historial_inventario (id_producto, cantidad, motivo, tipo) VALUES (?, ?, ?, ?)"
            conexion.prepareStatement(sqlHistorial).use { stmt ->
                stmt.setInt(1, idProducto)
                stmt.setInt(2, cantidad)
                stmt.setString(3, motivo)
                stmt.setString(4, if (esEntrada) "ENTRADA" else "SALIDA")
                stmt.executeUpdate()
            }

            // 2. Actualizar stock
            // FIX SEC-010: Eliminado string interpolation ($operacion) de la query SQL para evitar inyección SQL
            val sqlStock = if (esEntrada) {
                "UPDATE productos SET stock_actual = stock_actual + ? WHERE id_producto = ?"
            } else {
                "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?"
            }
            
            conexion.prepareStatement(sqlStock).use { stmt ->
                stmt.setInt(1, cantidad)
                stmt.setInt(2, idProducto)
                stmt.executeUpdate()
            }

            conexion.commit()
            AppLogger.info("InventarioDAO", "Movimiento registrado: producto $idProducto, cantidad $cantidad")
            true
        } catch (e: SQLException) {
            try {
                conexion.rollback()
                AppLogger.error("InventarioDAO", "Transacción revertida por error: ${e.message}")
            } catch (rollbackEx: SQLException) {
                AppLogger.error("InventarioDAO", "Error además al hacer rollback: ${rollbackEx.message}")
            }
            AppLogger.error("InventarioDAO", "Error en registrarMovimiento: ${e.message}")
            false
        } finally {
            try {
                conexion.autoCommit = true
            } catch (e: SQLException) {
                AppLogger.error("InventarioDAO", "Error al restaurar autoCommit: ${e.message}")
            }
            conexion.close()
        }
    }

    fun obtenerTodosProductos(): List<Producto> {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            AppLogger.error("InventarioDAO", "No se pudo obtener conexión para obtenerTodosProductos")
            return emptyList()
        }

        val lista = mutableListOf<Producto>()
        val sql = """
            SELECT id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo
            FROM productos
            ORDER BY nombre ASC
        """
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(
                            Producto(
                                idProducto   = rs.getInt("id_producto"),
                                nombre       = rs.getString("nombre"),
                                unidadesPaq  = rs.getInt("unidades_paq"),
                                pesoLibras   = rs.getDouble("peso_libras"),
                                precioVenta  = rs.getDouble("precio_venta"),
                                stockActual  = rs.getInt("stock_actual"),
                                stockMinimo  = rs.getInt("stock_minimo")
                            )
                        )
                    }
                }
            }
            AppLogger.info("InventarioDAO", "Cargados ${lista.size} productos")
            lista
        } catch (e: SQLException) {
            AppLogger.error("InventarioDAO", "Error en obtenerTodosProductos: ${e.message}")
            emptyList()
        }
    }
}
