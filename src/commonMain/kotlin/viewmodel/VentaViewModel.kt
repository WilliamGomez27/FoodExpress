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

class VentasViewModel(private val scope: CoroutineScope) {

    private val ventasDAO = VentaDAO()

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

    val totalCarrito: Double get() = carrito.sumOf { it.subtotal }
    val cantidadItems: Int   get() = carrito.sumOf { it.cantidad }

    fun cargarDatos() {
        scope.launch {
            cargando = true
            catalogoProductos = CatalogoVentas.obtenerCatalogo()
            historialVentas   = withContext(Dispatchers.IO) { ventasDAO.obtenerUltimasVentas() }
            cargando = false
        }
    }

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

    fun reducirDelCarrito(producto: ProductoVenta) {
        carrito = carrito
            .map { if (it.productoVenta.idProductoVenta == producto.idProductoVenta) it.copy(cantidad = it.cantidad - 1) else it }
            .filter { it.cantidad > 0 }
    }

    fun eliminarDelCarrito(producto: ProductoVenta) {
        carrito = carrito.filter { it.productoVenta.idProductoVenta != producto.idProductoVenta }
    }

    fun limpiarCarrito() { carrito = emptyList() }

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
