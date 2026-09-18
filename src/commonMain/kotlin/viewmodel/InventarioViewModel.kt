package FastFoodApp.viewmodel

import FastFoodApp.repository.InventarioRepository
import FastFoodApp.model.Producto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InventarioViewModel(private val scope: CoroutineScope) {

    private val repository = InventarioRepository()

    var productos       by mutableStateOf(listOf<Producto>())
        private set

    var productoSel     by mutableStateOf<Producto?>(null)

    var cantidad        by mutableStateOf("")

    var motivo          by mutableStateOf("Venta")

    var cargando        by mutableStateOf(false)
        private set

    var mensajeSnackbar by mutableStateOf<String?>(null)
        private set

    val motivos = listOf("Venta", "Ajuste", "Devolución", "Consumos")

    fun cargarProductos() {
        scope.launch {
            cargando = true
            productos = repository.obtenerProductos()
            if (productoSel == null && productos.isNotEmpty()) {
                productoSel = productos.first()
            }
            cargando = false
        }
    }

    fun registrarMovimiento() {
        val producto = productoSel
        if (producto == null) {
            mensajeSnackbar = "⚠️ Selecciona un producto"
            return
        }
        val cant = cantidad.toIntOrNull()
        if (cant == null || cant <= 0) {
            mensajeSnackbar = "⚠️ Ingresa una cantidad válida"
            return
        }

        val esEntrada = (motivo == "Ajuste" || motivo == "Devolución")
        if (!esEntrada && (producto.stockActual - cant < 0)) {
            mensajeSnackbar = "⚠️ No hay stock suficiente (Stock actual: ${producto.stockActual})"
            return
        }

        scope.launch {
            cargando = true
            val exito = repository.registrarMovimiento(producto.idProducto, cant, motivo, esEntrada)
            
            if (exito) {
                mensajeSnackbar = "✅ $motivo registrada: $cant unidades de ${producto.nombre}"
                cantidad = ""
                productos = repository.obtenerProductos()
                productoSel = productos.find { it.idProducto == producto.idProducto }
            } else {
                mensajeSnackbar = "❌ Error al registrar el movimiento"
            }
            cargando = false
        }
    }

    fun registrarNuevoProducto(nuevoProducto: Producto) {
        scope.launch {
            cargando = true
            val exito = repository.insertarProducto(nuevoProducto)
            if (exito) {
                mensajeSnackbar = "✅ Producto '${nuevoProducto.nombre}' creado exitosamente"
                productos = repository.obtenerProductos()
            } else {
                mensajeSnackbar = "❌ Error al crear el producto"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() {
        mensajeSnackbar = null
    }
}
