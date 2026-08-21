package FastFoodApp.viewmodel

import FastFoodApp.repository.InventarioRepository
import FastFoodApp.model.Producto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Inventario.
 * Centraliza la lógica de negocio y el estado de la UI.
 * La pantalla solo observa propiedades y llama a funciones — sin tocar el DAO directamente.
 */
class InventarioViewModel(private val scope: CoroutineScope) {

    private val repository = InventarioRepository()

    // ── Estado observable ────────────────────────────────────────────────────
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

    // ── Acciones ─────────────────────────────────────────────────────────────

    /** Carga los productos desde la BD al iniciar la pantalla. */
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

    /** Registra un movimiento de inventario con validación de negocio. */
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
        // No permitir venta o consumo si el stock quedará menor a 0
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
                // Refrescar lista
                productos = repository.obtenerProductos()
                // Actualizar el producto seleccionado con datos frescos
                productoSel = productos.find { it.idProducto == producto.idProducto }
            } else {
                mensajeSnackbar = "❌ Error al registrar el movimiento"
            }
            cargando = false
        }
    }

    /** Registra un nuevo producto manualmente en la BD */
    fun registrarNuevoProducto(nuevoProducto: Producto) {
        scope.launch {
            cargando = true
            val exito = repository.insertarProducto(nuevoProducto)
            if (exito) {
                mensajeSnackbar = "✅ Producto '${nuevoProducto.nombre}' creado exitosamente"
                // Refrescar lista
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
