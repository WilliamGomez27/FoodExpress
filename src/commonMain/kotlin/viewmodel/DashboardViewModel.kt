package FastFoodApp.viewmodel

import FastFoodApp.dao.ReporteDAO
import FastFoodApp.dao.VentaDAO
import FastFoodApp.model.ArqueoDiario
import FastFoodApp.model.Venta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel {
    private val reporteDAO = ReporteDAO()
    private val ventaDAO = VentaDAO()

    private val _resumenHoy = MutableStateFlow<ArqueoDiario?>(null)
    val resumenHoy: StateFlow<ArqueoDiario?> = _resumenHoy

    private val _ventasRecientes = MutableStateFlow<List<Venta>>(emptyList())
    val ventasRecientes: StateFlow<List<Venta>> = _ventasRecientes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarDatos(scope: CoroutineScope) {
        scope.launch {
            _isLoading.value = true
            val resumen = withContext(Dispatchers.IO) { reporteDAO.obtenerResumenHoy() }
            val ventas = withContext(Dispatchers.IO) { ventaDAO.obtenerUltimasVentas(5) } // Solo las últimas 5 para el dashboard
            
            _resumenHoy.value = resumen
            _ventasRecientes.value = ventas
            _isLoading.value = false
        }
    }
}
