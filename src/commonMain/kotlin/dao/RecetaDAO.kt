package FoodExpress.dao

import FoodExpress.database.ConexionDB
import FoodExpress.model.RecetaIngrediente
import FoodExpress.utils.AppLogger
import java.sql.Connection
import java.sql.SQLException

class RecetaDAO {

    fun obtenerReceta(idProductoVenta: Int): List<RecetaIngrediente> {
        val conexion = ConexionDB.getConexion() ?: run {
            AppLogger.error("RecetaDAO", "Sin conexión para obtenerReceta")
            return emptyList()
        }
        val lista = mutableListOf<RecetaIngrediente>()
        val sql = """
            SELECT ri.id, ri.id_producto_venta, ri.id_producto,
                   p.nombre AS nombre_materia, ri.cantidad, ri.unidad
            FROM receta_ingredientes ri
            JOIN productos p ON p.id_producto = ri.id_producto
            WHERE ri.id_producto_venta = ?
            ORDER BY p.nombre ASC
        """
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, idProductoVenta)
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(
                            RecetaIngrediente(
                                id               = rs.getInt("id"),
                                idProductoVenta  = rs.getInt("id_producto_venta"),
                                idProducto       = rs.getInt("id_producto"),
                                nombreMateria    = rs.getString("nombre_materia"),
                                cantidad         = rs.getDouble("cantidad"),
                                unidad           = rs.getString("unidad")
                            )
                        )
                    }
                }
            }
            AppLogger.info("RecetaDAO", "${lista.size} ingredientes para producto_venta #$idProductoVenta")
            lista
        } catch (e: SQLException) {
            AppLogger.error("RecetaDAO", "Error en obtenerReceta: ${e.message}")
            emptyList()
        }
    }

    fun guardarReceta(idProductoVenta: Int, ingredientes: List<RecetaIngrediente>): Boolean {
        val conexion = ConexionDB.getConexion() ?: run {
            AppLogger.error("RecetaDAO", "Sin conexión para guardarReceta")
            return false
        }
        return try {
            conexion.autoCommit = false

            val sqlDelete = "DELETE FROM receta_ingredientes WHERE id_producto_venta = ?"
            conexion.prepareStatement(sqlDelete).use { stmt ->
                stmt.setInt(1, idProductoVenta)
                stmt.executeUpdate()
            }

            val sqlInsert = """
                INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad)
                VALUES (?, ?, ?, ?)
            """
            conexion.prepareStatement(sqlInsert).use { stmt ->
                ingredientes.forEach { ing ->
                    stmt.setInt(1, idProductoVenta)
                    stmt.setInt(2, ing.idProducto)
                    stmt.setDouble(3, ing.cantidad)
                    stmt.setString(4, ing.unidad)
                    stmt.addBatch()
                }
                stmt.executeBatch()
            }

            conexion.commit()
            AppLogger.info("RecetaDAO", "Receta guardada: ${ingredientes.size} ingredientes para #$idProductoVenta")
            true
        } catch (e: SQLException) {
            try { conexion.rollback() } catch (_: SQLException) {}
            AppLogger.error("RecetaDAO", "Error en guardarReceta: ${e.message}")
            false
        } finally {
            try { conexion.autoCommit = true } catch (_: SQLException) {}
            conexion.close()
        }
    }

    /**
     * Descuenta el stock de materia prima por receta.
     * Si algún ingrediente no tiene stock, lanza una IllegalStateException con el mensaje exacto.
     */
    fun descontarStockPorReceta(
        idProductoVenta: Int,
        cantidadVendida: Int,
        conexion: Connection
    ) {
        val ingredientes = obtenerRecetaConConexion(idProductoVenta, conexion)
        if (ingredientes.isEmpty()) {
            AppLogger.warn("RecetaDAO", "Sin receta para #$idProductoVenta — sin descuento de stock")
            return
        }

        // ── Verificar stock ANTES de descontar ──
        val sqlCheck = "SELECT nombre, stock_actual, unidad_medida FROM productos WHERE id_producto = ? FOR UPDATE"
        for (ing in ingredientes) {
            val totalDescontar = ing.cantidad * cantidadVendida
            conexion.prepareStatement(sqlCheck).use { stmt ->
                stmt.setInt(1, ing.idProducto)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val stockActual = rs.getDouble("stock_actual")
                    val nombre = rs.getString("nombre")
                    val unidad = rs.getString("unidad_medida") ?: "unidades"
                    if (stockActual < totalDescontar) {
                        AppLogger.error(
                            "RecetaDAO",
                            "Stock insuficiente de '$nombre': disponible=$stockActual, requerido=$totalDescontar"
                        )
                        // Lanzamos excepción controlada para mostrar en UI
                        throw IllegalStateException("Stock insuficiente de '$nombre'. Disponible: $stockActual $unidad, Requerido: $totalDescontar $unidad")
                    }
                } else {
                    throw IllegalStateException("El ingrediente con ID ${ing.idProducto} ya no existe en el inventario.")
                }
            }
        }

        // Stock suficiente — proceder con el descuento
        val sqlUpdate = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?"
        conexion.prepareStatement(sqlUpdate).use { stmt ->
            ingredientes.forEach { ing ->
                stmt.setDouble(1, ing.cantidad * cantidadVendida)
                stmt.setInt(2, ing.idProducto)
                stmt.addBatch()
            }
            stmt.executeBatch()
        }
        AppLogger.info("RecetaDAO", "Stock descontado para ${ingredientes.size} ingredientes")
    }

    private fun obtenerRecetaConConexion(
        idProductoVenta: Int,
        conexion: Connection
    ): List<RecetaIngrediente> {
        val lista = mutableListOf<RecetaIngrediente>()
        val sql = """
            SELECT ri.id, ri.id_producto_venta, ri.id_producto,
                   p.nombre AS nombre_materia, ri.cantidad, ri.unidad
            FROM receta_ingredientes ri
            JOIN productos p ON p.id_producto = ri.id_producto
            WHERE ri.id_producto_venta = ?
        """
        return try {
            conexion.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, idProductoVenta)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    lista.add(
                        RecetaIngrediente(
                            id              = rs.getInt("id"),
                            idProductoVenta = rs.getInt("id_producto_venta"),
                            idProducto      = rs.getInt("id_producto"),
                            nombreMateria   = rs.getString("nombre_materia"),
                            cantidad        = rs.getDouble("cantidad"),
                            unidad          = rs.getString("unidad")
                        )
                    )
                }
            }
            lista
        } catch (e: SQLException) {
            AppLogger.error("RecetaDAO", "Error en obtenerRecetaConConexion: ${e.message}")
            emptyList()
        }
    }
}
