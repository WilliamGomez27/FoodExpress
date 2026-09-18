package FastFoodApp.repository

import FastFoodApp.dao.RecetaDAO
import FastFoodApp.model.RecetaIngrediente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecetaRepository {

    private val dao = RecetaDAO()

    suspend fun obtenerReceta(idProductoVenta: Int): List<RecetaIngrediente> =
        withContext(Dispatchers.IO) {
            dao.obtenerReceta(idProductoVenta)
        }

    suspend fun guardarReceta(
        idProductoVenta: Int,
        ingredientes: List<RecetaIngrediente>
    ): Boolean = withContext(Dispatchers.IO) {
        dao.guardarReceta(idProductoVenta, ingredientes)
    }
}
