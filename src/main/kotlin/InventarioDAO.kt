package FastFoodApp

import java.sql.SQLException

class InventarioDAO {
    private val conexion = ConexionDB.getConexion()

    /**
     * Registra un movimiento de inventario (entrada o salida) con su motivo.
     * Usa transacción para garantizar consistencia entre historial y stock.
     */
    fun registrarMovimiento(idProducto: Int, cantidad: Int, motivo: String, esEntrada: Boolean): Boolean {
        return try {
            conexion?.autoCommit = false

            // 1. Guardar en historial
            val sqlHistorial = "INSERT INTO historial_inventario (id_producto, cantidad, motivo, tipo) VALUES (?, ?, ?, ?)"
            conexion?.prepareStatement(sqlHistorial)?.use { stmt ->
                stmt.setInt(1, idProducto)
                stmt.setInt(2, cantidad)
                stmt.setString(3, motivo)
                stmt.setString(4, if (esEntrada) "ENTRADA" else "SALIDA")
                stmt.executeUpdate()
            }

            // 2. Actualizar stock
            val operacion = if (esEntrada) "+" else "-"
            val sqlStock = "UPDATE productos SET stock_actual = stock_actual $operacion ? WHERE id_producto = ?"
            conexion?.prepareStatement(sqlStock)?.use { stmt ->
                stmt.setInt(1, cantidad)
                stmt.setInt(2, idProducto)
                stmt.executeUpdate()
            }

            conexion?.commit()
            true
        } catch (e: SQLException) {
            conexion?.rollback()
            println("Error en registrarMovimiento: ${e.message}")
            false
        } finally {
            conexion?.autoCommit = true
        }
    }

    /**
     * Obtiene todos los productos con su información completa de inventario.
     * CORRECCIÓN: Esta función estaba incorrectamente anidada dentro de registrarMovimiento().
     */
    fun obtenerTodosProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val sql = """
            SELECT id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo
            FROM productos
            ORDER BY nombre ASC
        """
        return try {
            conexion?.prepareStatement(sql)?.use { stmt ->
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
            lista
        } catch (e: SQLException) {
            println("Error en obtenerTodosProductos: ${e.message}")
            emptyList()
        }
    }
}