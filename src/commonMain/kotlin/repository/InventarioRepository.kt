package FoodExpress.repository

import FoodExpress.dao.InventarioDAO
import FoodExpress.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventarioRepository {

    private val dao = InventarioDAO()

    suspend fun obtenerProductos(): List<Producto> = withContext(Dispatchers.IO) {
        dao.obtenerTodosProductos()
    }

    suspend fun registrarMovimiento(
        idProducto: Int,
        cantidad: Int,
        motivo: String,
        esEntrada: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        dao.registrarMovimiento(idProducto, cantidad, motivo, esEntrada)
    }

    suspend fun insertarProducto(producto: Producto): Boolean = withContext(Dispatchers.IO) {
        val productoDao = FoodExpress.dao.ProductoDAO()
        productoDao.insertar(producto)
    }
}
