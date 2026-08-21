package FastFoodApp.repository

import FastFoodApp.dao.VentaDAO
import FastFoodApp.model.ItemVenta
import FastFoodApp.model.Venta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Capa de repositorio para Ventas (abstrae la fuente de datos del ViewModel)
class VentaRepository {

    private val dao = VentaDAO()

    suspend fun registrarVenta(items: List<ItemVenta>): Int = withContext(Dispatchers.IO) {
        dao.registrarVenta(items)
    }

    suspend fun obtenerUltimasVentas(limite: Int = 20): List<Venta> = withContext(Dispatchers.IO) {
        dao.obtenerUltimasVentas(limite)
    }
}
