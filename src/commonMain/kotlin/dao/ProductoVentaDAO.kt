package FoodExpress.dao

import FoodExpress.database.ConexionDB
import FoodExpress.model.ProductoVenta
import FoodExpress.utils.AppLogger
import java.sql.SQLException
import java.sql.Statement

class ProductoVentaDAO {

    fun obtenerTodos(): List<ProductoVenta> {
        val conexion = ConexionDB.getConexion() ?: return emptyList()
        val lista = mutableListOf<ProductoVenta>()
        val sql = "SELECT id_producto_venta, nombre, precio_venta, categoria FROM productos_venta ORDER BY nombre ASC"
        
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(
                            ProductoVenta(
                                idProductoVenta = rs.getInt("id_producto_venta"),
                                nombre = rs.getString("nombre"),
                                precioVenta = rs.getDouble("precio_venta"),
                                categoria = rs.getString("categoria") ?: "General"
                            )
                        )
                    }
                }
            }
            lista
        } catch (e: SQLException) {
            AppLogger.error("ProductoVentaDAO", "Error al obtener productos para venta: ${e.message}")
            emptyList()
        }
    }

    fun insertar(producto: ProductoVenta): Int {
        val conexion = ConexionDB.getConexion() ?: return -1
        val sql = "INSERT INTO productos_venta (nombre, precio_venta, categoria) VALUES (?, ?, ?)"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                    stmt.setString(1, producto.nombre)
                    stmt.setDouble(2, producto.precioVenta)
                    stmt.setString(3, producto.categoria)
                    stmt.executeUpdate()
                    val rs = stmt.generatedKeys
                    if (rs.next()) rs.getInt(1) else -1
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoVentaDAO", "Error al insertar producto para venta: ${e.message}")
            -1
        }
    }

    fun actualizarPrecio(idProductoVenta: Int, nuevoPrecio: Double): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "UPDATE productos_venta SET precio_venta = ? WHERE id_producto_venta = ?"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setDouble(1, nuevoPrecio)
                    stmt.setInt(2, idProductoVenta)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("ProductoVentaDAO", "Error al actualizar precio de venta: ${e.message}")
            false
        }
    }
}
