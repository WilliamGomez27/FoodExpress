package FoodExpress.viewmodel

import FoodExpress.dao.GastoDAO
import FoodExpress.model.Gasto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GastoViewModel(private val scope: CoroutineScope) {

    private val dao = GastoDAO()

    var gastos by mutableStateOf(listOf<Gasto>())
        private set

    var cargando by mutableStateOf(false)
        private set
        
    var totalGastos by mutableStateOf(0.0)
        private set

    fun cargarGastos() {
        scope.launch {
            cargando = true
            val resultado = withContext(Dispatchers.IO) {
                dao.obtenerGastosMesActual()
            }
            gastos = resultado
            totalGastos = resultado.sumOf { it.monto }
            cargando = false
        }
    }

    fun registrarGasto(descripcion: String, monto: Double) {
        scope.launch {
            val exito = withContext(Dispatchers.IO) {
                dao.registrarGasto(Gasto(descripcion = descripcion, monto = monto))
            }
            if (exito) {
                cargarGastos()
            }
        }
    }
}
