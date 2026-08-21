package FastFoodApp.repository

import FastFoodApp.dao.RecetaDAO
import FastFoodApp.model.RecetaIngrediente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para el módulo de Recetas.
 * Abstrae el acceso al [RecetaDAO] y garantiza que las operaciones
 * de BD se ejecuten en el hilo de IO correcto.
 */
class RecetaRepository {

    private val dao = RecetaDAO()

    /** Obtiene los ingredientes (materia prima) de un producto terminado. */
    suspend fun obtenerReceta(idProductoVenta: Int): List<RecetaIngrediente> =
        withContext(Dispatchers.IO) {
            dao.obtenerReceta(idProductoVenta)
        }

    /**
     * Guarda (reemplaza) la receta completa de un producto terminado.
     * @return true si se guardó correctamente.
     */
    suspend fun guardarReceta(
        idProductoVenta: Int,
        ingredientes: List<RecetaIngrediente>
    ): Boolean = withContext(Dispatchers.IO) {
        dao.guardarReceta(idProductoVenta, ingredientes)
    }
}
