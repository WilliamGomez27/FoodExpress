package FoodExpress.repository

import FoodExpress.dao.VentaDAO
import FoodExpress.model.ItemVenta
import FoodExpress.model.Venta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VentaRepository {

    private val dao = VentaDAO()

    suspend fun registrarVenta(items: List<ItemVenta>): Int = withContext(Dispatchers.IO) {
        dao.registrarVenta(items)
    }

    suspend fun obtenerUltimasVentas(limite: Int = 20): List<Venta> = withContext(Dispatchers.IO) {
        dao.obtenerUltimasVentas(limite)
    }
}
