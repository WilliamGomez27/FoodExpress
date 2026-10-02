package FoodExpress.viewmodel

import FoodExpress.repository.InventarioRepository
import FoodExpress.model.Producto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InventarioViewModel(private val scope: CoroutineScope) {

    private val repository = InventarioRepository()

    var productos       by mutableStateOf(listOf<Producto>())
        private set

    var verPapelera     by mutableStateOf(false)

    var productoSel     by mutableStateOf<Producto?>(null)

    var cantidad        by mutableStateOf("")

    var motivo          by mutableStateOf("Entrada (Compra)")

    var cargando        by mutableStateOf(false)
        private set

    var mensajeSnackbar by mutableStateOf<String?>(null)
        private set

    val motivos = listOf("Entrada (Compra)", "Ajuste de Entrada", "Ajuste de Salida", "Devolución", "Consumo/Merma")

    fun cargarProductos() {
        scope.launch {
            cargando = true
            val todos = repository.obtenerProductos()
            productos = if (verPapelera) todos.filter { !it.activo } else todos.filter { it.activo }
            if (productoSel == null && productos.isNotEmpty()) {
                productoSel = productos.first()
            }
            cargando = false
        }
    }

    fun alternarVistaPapelera() {
        verPapelera = !verPapelera
        productoSel = null
        cargarProductos()
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

        val esEntrada = (motivo == "Entrada (Compra)" || motivo == "Ajuste de Entrada" || motivo == "Devolución")
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

    fun actualizarProducto(producto: Producto) {
        scope.launch {
            cargando = true
            val exito = repository.actualizarProducto(producto)
            if (exito) {
                mensajeSnackbar = "✅ Producto '${producto.nombre}' actualizado exitosamente"
                productos = repository.obtenerProductos()
                if (productoSel?.idProducto == producto.idProducto) {
                    productoSel = productos.find { it.idProducto == producto.idProducto }
                }
            } else {
                mensajeSnackbar = "❌ Error al actualizar el producto"
            }
            cargando = false
        }
    }

    fun eliminarProducto(idProducto: Int) {
        scope.launch {
            cargando = true
            val exito = repository.eliminarProducto(idProducto)
            if (exito) {
                mensajeSnackbar = "🗑️ Producto enviado a la papelera"
                cargarProductos()
                if (productoSel?.idProducto == idProducto) {
                    productoSel = productos.firstOrNull()
                }
            } else {
                mensajeSnackbar = "❌ Error al enviar a papelera."
            }
            cargando = false
        }
    }

    fun restaurarProducto(idProducto: Int) {
        scope.launch {
            cargando = true
            val exito = repository.restaurarProducto(idProducto)
            if (exito) {
                mensajeSnackbar = "✅ Producto restaurado con éxito"
                cargarProductos()
            } else {
                mensajeSnackbar = "❌ Error al restaurar producto"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() {
        mensajeSnackbar = null
    }
}
