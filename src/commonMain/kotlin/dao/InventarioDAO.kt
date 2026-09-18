package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.Producto
import java.sql.SQLException

class InventarioDAO {

    fun registrarMovimiento(idProducto: Int, cantidad: Int, motivo: String, esEntrada: Boolean): Boolean {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            println("✗ [DAO] No se pudo obtener conexión para registrarMovimiento")
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
            val operacion = if (esEntrada) "+" else "-"
            val sqlStock = "UPDATE productos SET stock_actual = stock_actual $operacion ? WHERE id_producto = ?"
            conexion.prepareStatement(sqlStock).use { stmt ->
                stmt.setInt(1, cantidad)
                stmt.setInt(2, idProducto)
                stmt.executeUpdate()
            }

            conexion.commit()
            println("✓ [DAO] Movimiento registrado: producto $idProducto, cantidad $cantidad")
            true
        } catch (e: SQLException) {
            try {
                conexion.rollback()
                println("✗ [DAO] Transacción revertida por error: ${e.message}")
            } catch (rollbackEx: SQLException) {
                println("✗ [DAO] Error además al hacer rollback: ${rollbackEx.message}")
            }
            println("✗ [DAO] Error en registrarMovimiento: ${e.message}")
            false
        } finally {
            try {
                conexion.autoCommit = true
            } catch (e: SQLException) {
                println("✗ [DAO] Error al restaurar autoCommit: ${e.message}")
            }
        }
    }

    fun obtenerTodosProductos(): List<Producto> {
        val conexion = ConexionDB.getConexion()
        if (conexion == null) {
            println("✗ [DAO] No se pudo obtener conexión para obtenerTodosProductos")
            return emptyList()
        }

        val lista = mutableListOf<Producto>()
        val sql = """
            SELECT id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo
            FROM productos
            ORDER BY nombre ASC
        """
        return try {
            conexion.prepareStatement(sql).use { stmt ->
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
            println("✓ [DAO] Cargados ${lista.size} productos")
            lista
        } catch (e: SQLException) {
            println("✗ [DAO] Error en obtenerTodosProductos: ${e.message}")
            emptyList()
        }
    }
}
