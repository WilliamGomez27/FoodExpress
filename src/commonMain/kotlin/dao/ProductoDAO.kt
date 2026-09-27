package FoodExpress.dao

import FoodExpress.database.ConexionDB
import FoodExpress.model.Producto
import FoodExpress.utils.AppLogger
import java.sql.SQLException
import java.sql.Statement

class ProductoDAO {

    fun obtenerPorId(id: Int): Producto? {
        val conexion = ConexionDB.getConexion() ?: return null
        var producto: Producto? = null
        val sql = "SELECT id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo FROM productos WHERE id_producto = ?"
        try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        producto = Producto(
                            idProducto   = rs.getInt("id_producto"),
                            nombre       = rs.getString("nombre"),
                            unidadesPaq  = rs.getInt("unidades_paq"),
                            pesoLibras   = rs.getDouble("peso_libras"),
                            precioVenta  = rs.getDouble("precio_venta"),
                            stockActual  = rs.getInt("stock_actual"),
                            stockMinimo  = rs.getInt("stock_minimo")
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoDAO", "Error al obtener producto: ${e.message}")
        }
        return producto
    }

    fun insertar(producto: Producto): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "INSERT INTO productos (nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo) VALUES (?, ?, ?, ?, ?, ?)"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, producto.nombre)
                    stmt.setInt(2, producto.unidadesPaq)
                    stmt.setDouble(3, producto.pesoLibras)
                    stmt.setDouble(4, producto.precioVenta)
                    stmt.setInt(5, producto.stockActual)
                    stmt.setInt(6, producto.stockMinimo)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoDAO", "Error al insertar producto: ${e.message}")
            false
        }
    }

    fun actualizar(producto: Producto): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "UPDATE productos SET nombre = ?, unidades_paq = ?, peso_libras = ?, precio_venta = ?, stock_actual = ?, stock_minimo = ? WHERE id_producto = ?"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, producto.nombre)
                    stmt.setInt(2, producto.unidadesPaq)
                    stmt.setDouble(3, producto.pesoLibras)
                    stmt.setDouble(4, producto.precioVenta)
                    stmt.setInt(5, producto.stockActual)
                    stmt.setInt(6, producto.stockMinimo)
                    stmt.setInt(7, producto.idProducto)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoDAO", "Error al actualizar producto: ${e.message}")
            false
        }
    }

    fun eliminar(id: Int): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "DELETE FROM productos WHERE id_producto = ?"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoDAO", "Error al eliminar producto: ${e.message}")
            false
        }
    }
}
