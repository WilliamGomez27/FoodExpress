package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.RecetaIngrediente
import java.sql.SQLException

/**
 * DAO para el módulo de Recetas.
 * Permite consultar, guardar y eliminar la receta (lista de ingredientes)
 * asociada a un producto terminado (productos_venta).
 */
class RecetaDAO {

    /**
     * Obtiene la lista de ingredientes (materia prima) de un producto terminado.
     * Hace JOIN con `productos` para traer el nombre de cada materia prima.
     */
    fun obtenerReceta(idProductoVenta: Int): List<RecetaIngrediente> {
        val conexion = ConexionDB.getConexion() ?: run {
            println("✗ [RecetaDAO] Sin conexión para obtenerReceta")
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
            conexion.prepareStatement(sql).use { stmt ->
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
            println("✓ [RecetaDAO] ${lista.size} ingredientes para producto_venta #$idProductoVenta")
            lista
        } catch (e: SQLException) {
            println("✗ [RecetaDAO] Error en obtenerReceta: ${e.message}")
            emptyList()
        }
    }

    /**
     * Guarda (reemplaza) la receta completa de un producto terminado.
     * Borra los ingredientes existentes e inserta los nuevos en transacción.
     */
    fun guardarReceta(idProductoVenta: Int, ingredientes: List<RecetaIngrediente>): Boolean {
        val conexion = ConexionDB.getConexion() ?: run {
            println("✗ [RecetaDAO] Sin conexión para guardarReceta")
            return false
        }
        return try {
            conexion.autoCommit = false

            // 1. Borrar ingredientes anteriores
            val sqlDelete = "DELETE FROM receta_ingredientes WHERE id_producto_venta = ?"
            conexion.prepareStatement(sqlDelete).use { stmt ->
                stmt.setInt(1, idProductoVenta)
                stmt.executeUpdate()
            }

            // 2. Insertar nuevos ingredientes
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
            println("✓ [RecetaDAO] Receta guardada: ${ingredientes.size} ingredientes para producto_venta #$idProductoVenta")
            true
        } catch (e: SQLException) {
            try { conexion.rollback() } catch (_: SQLException) {}
            println("✗ [RecetaDAO] Error en guardarReceta: ${e.message}")
            false
        } finally {
            try { conexion.autoCommit = true } catch (_: SQLException) {}
        }
    }

    /**
     * Descuenta del inventario (tabla `productos`) la materia prima usada
     * para fabricar [cantidadVendida] unidades de un producto terminado.
     * Llamado dentro de una transacción existente (no hace commit propio).
     *
     * @param idProductoVenta  ID del producto terminado vendido.
     * @param cantidadVendida  Cuántas unidades del producto terminado se vendieron.
     * @param conexion         La conexión con transacción activa.
     */
    fun descontarStockPorReceta(
        idProductoVenta: Int,
        cantidadVendida: Int,
        conexion: java.sql.Connection
    ): Boolean {
        val ingredientes = obtenerRecetaConConexion(idProductoVenta, conexion)
        if (ingredientes.isEmpty()) {
            // Si no hay receta definida, no descontamos nada (producto sin ingredientes)
            println("⚠ [RecetaDAO] Sin receta para producto_venta #$idProductoVenta — sin descuento de stock")
            return true
        }

        val sqlUpdate = "UPDATE productos SET stock_actual = stock_actual - ? WHERE id_producto = ?"
        return try {
            conexion.prepareStatement(sqlUpdate).use { stmt ->
                ingredientes.forEach { ing ->
                    val totalDescontar = ing.cantidad * cantidadVendida
                    stmt.setDouble(1, totalDescontar)
                    stmt.setInt(2, ing.idProducto)
                    stmt.addBatch()
                }
                stmt.executeBatch()
            }
            println("✓ [RecetaDAO] Stock de materia prima descontado para ${ingredientes.size} ingredientes")
            true
        } catch (e: SQLException) {
            println("✗ [RecetaDAO] Error al descontar stock: ${e.message}")
            false
        }
    }

    /** Versión de obtenerReceta que usa una conexión existente (para usar en transacción). */
    private fun obtenerRecetaConConexion(
        idProductoVenta: Int,
        conexion: java.sql.Connection
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
            println("✗ [RecetaDAO] Error en obtenerRecetaConConexion: ${e.message}")
            emptyList()
        }
    }
}
