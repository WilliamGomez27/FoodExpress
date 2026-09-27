package FoodExpress.viewmodel

import FoodExpress.dao.ReporteDAO
import FoodExpress.model.ArqueoDiario
import FoodExpress.model.CierreMensual
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReporteViewModel(private val scope: CoroutineScope) {

    private val dao = ReporteDAO()

    var arqueoDiario by mutableStateOf<ArqueoDiario?>(null)
        private set

    var cierreMensual by mutableStateOf<CierreMensual?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    fun cargarArqueoDiario(fecha: String) {
        scope.launch {
            cargando = true
            arqueoDiario = withContext(Dispatchers.IO) {
                dao.obtenerArqueoDiario(fecha)
            }
            cargando = false
        }
    }

    fun cargarCierreMensual(mes: Int, anio: Int) {
        scope.launch {
            cargando = true
            cierreMensual = withContext(Dispatchers.IO) {
                dao.obtenerCierreMensual(mes, anio)
            }
            cargando = false
        }
    }
}
