package FastFoodApp.viewmodel

import FastFoodApp.dao.VentaDAO
import FastFoodApp.model.ItemVenta
import FastFoodApp.model.ProductoVenta
import FastFoodApp.model.Venta
import FastFoodApp.repository.CatalogoVentas
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para la pantalla de Ventas.
 * Gestiona el carrito, catálogo de productos y registro de ventas.
 */
class VentasViewModel(private val scope: CoroutineScope) {

    private val ventasDAO = VentaDAO()

    // ── Estado observable ────────────────────────────────────────────────────
    var catalogoProductos by mutableStateOf(listOf<ProductoVenta>())
        private set

    var carrito           by mutableStateOf(listOf<ItemVenta>())

    var historialVentas   by mutableStateOf(listOf<Venta>())
        private set

    var cargando          by mutableStateOf(false)
        private set

    var mensajeSnackbar   by mutableStateOf<String?>(null)
        private set

    var ultimaVentaId     by mutableStateOf<Int?>(null)
        private set

    // ── Totales calculados ────────────────────────────────────────────────────
    val totalCarrito: Double get() = carrito.sumOf { it.subtotal }
    val cantidadItems: Int   get() = carrito.sumOf { it.cantidad }

    // ── Acciones ─────────────────────────────────────────────────────────────

    fun cargarDatos() {
        scope.launch {
            cargando = true
            catalogoProductos = CatalogoVentas.obtenerCatalogo()
            historialVentas   = withContext(Dispatchers.IO) { ventasDAO.obtenerUltimasVentas() }
            cargando = false
        }
    }

    /** Agrega un producto al carrito o incrementa su cantidad si ya existe. */
    fun agregarAlCarrito(producto: ProductoVenta) {
        val existente = carrito.find { it.productoVenta.idProductoVenta == producto.idProductoVenta }
        carrito = if (existente != null) {
            carrito.map {
                if (it.productoVenta.idProductoVenta == producto.idProductoVenta)
                    it.copy(cantidad = it.cantidad + 1)
                else it
            }
        } else {
            carrito + ItemVenta(producto, 1)
        }
    }

    /** Reduce en 1 la cantidad de un item del carrito; si llega a 0 lo elimina. */
    fun reducirDelCarrito(producto: ProductoVenta) {
        carrito = carrito
            .map { if (it.productoVenta.idProductoVenta == producto.idProductoVenta) it.copy(cantidad = it.cantidad - 1) else it }
            .filter { it.cantidad > 0 }
    }

    /** Elimina un item del carrito por completo. */
    fun eliminarDelCarrito(producto: ProductoVenta) {
        carrito = carrito.filter { it.productoVenta.idProductoVenta != producto.idProductoVenta }
    }

    fun limpiarCarrito() { carrito = emptyList() }

    /** Procesa la venta: valida, registra en BD y refresca el catálogo. */
    fun procesarVenta() {
        if (carrito.isEmpty()) {
            mensajeSnackbar = "⚠️ El carrito está vacío"
            return
        }
        scope.launch {
            cargando = true
            val id = withContext(Dispatchers.IO) { ventasDAO.registrarVenta(carrito) }
            if (id != -1) {
                ultimaVentaId = id
                mensajeSnackbar = "✅ Venta #$id registrada — Total: Q %.2f".format(totalCarrito)
                limpiarCarrito()
                // Refrescar catálogo y historial
                catalogoProductos = CatalogoVentas.obtenerCatalogo()
                historialVentas   = withContext(Dispatchers.IO) { ventasDAO.obtenerUltimasVentas() }
            } else {
                mensajeSnackbar = "❌ Error al procesar la venta"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() { mensajeSnackbar = null }
}
