package FastFoodApp.viewmodel

import FastFoodApp.dao.ReporteDAO
import FastFoodApp.model.ArqueoDiario
import FastFoodApp.model.CierreMensual
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ReporteViewModel(private val scope: CoroutineScope) {

    private val dao = ReporteDAO()

    var arqueoDiario by mutableStateOf<ArqueoDiario?>(null)
        private set

    var cierreMensual by mutableStateOf<CierreMensual?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    fun cargarArqueoDiario(fecha: String = LocalDate.now().toString()) {
        scope.launch {
            cargando = true
            arqueoDiario = withContext(Dispatchers.IO) {
                dao.obtenerArqueoDiario(fecha)
            }
            cargando = false
        }
    }

    fun cargarCierreMensual(mes: Int = LocalDate.now().monthValue, anio: Int = LocalDate.now().year) {
        scope.launch {
            cargando = true
            cierreMensual = withContext(Dispatchers.IO) {
                dao.obtenerCierreMensual(mes, anio)
            }
            cargando = false
        }
    }
}
