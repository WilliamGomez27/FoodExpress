package FoodExpress.viewmodel

import FoodExpress.dao.ProductoVentaDAO
import FoodExpress.model.Producto
import FoodExpress.model.ProductoVenta
import FoodExpress.model.RecetaIngrediente
import FoodExpress.repository.CatalogoVentas
import FoodExpress.repository.InventarioRepository
import FoodExpress.repository.RecetaRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetaViewModel(private val scope: CoroutineScope) {

    private val recetaRepo    = RecetaRepository()
    private val inventarioRepo = InventarioRepository()

    var productosTerminados by mutableStateOf(listOf<ProductoVenta>())
        private set

    var productoSeleccionado by mutableStateOf<ProductoVenta?>(null)

    var ingredientes by mutableStateOf(listOf<RecetaIngrediente>())
        private set

    var materiasPrimas by mutableStateOf(listOf<Producto>())
        private set

    var materiaPrimaSeleccionada by mutableStateOf<Producto?>(null)
    var cantidadInput            by mutableStateOf("")
    var unidadSeleccionada       by mutableStateOf("unidades")

    val unidades = listOf("unidades", "gramos", "ml")

    var cargando        by mutableStateOf(false)
        private set

    var mensajeSnackbar by mutableStateOf<String?>(null)
        private set

    fun cargarDatos() {
        scope.launch {
            cargando = true
            val dao = ProductoVentaDAO()
            productosTerminados = withContext(Dispatchers.IO) { dao.obtenerTodos() }
            materiasPrimas      = inventarioRepo.obtenerProductos()
            if (productoSeleccionado == null && productosTerminados.isNotEmpty()) {
                seleccionarProducto(productosTerminados.first())
            }
            cargando = false
        }
    }

    fun actualizarPrecioProductoVenta(id: Int, nuevoPrecio: Double) {
        scope.launch {
            cargando = true
            val dao = ProductoVentaDAO()
            val exito = withContext(Dispatchers.IO) { dao.actualizarPrecio(id, nuevoPrecio) }
            if (exito) {
                mensajeSnackbar = "✅ Precio actualizado correctamente a COP %.2f".format(nuevoPrecio)
                productosTerminados = withContext(Dispatchers.IO) { dao.obtenerTodos() }
                productoSeleccionado = productosTerminados.find { it.idProductoVenta == id } ?: productoSeleccionado
            } else {
                mensajeSnackbar = "❌ Error al actualizar el precio"
            }
            cargando = false
        }
    }

    fun crearProductoTerminado(nombre: String, precio: Double, categoria: String) {
        scope.launch {
            cargando = true
            val dao = ProductoVentaDAO()
            val nuevo = ProductoVenta(0, nombre, precio, categoria)
            val idGenerado = withContext(Dispatchers.IO) { dao.insertar(nuevo) }
            
            if (idGenerado != -1) {
                mensajeSnackbar = "✅ Nuevo producto '$nombre' creado."
                productosTerminados = withContext(Dispatchers.IO) { dao.obtenerTodos() }
                val recienCreado = productosTerminados.find { it.idProductoVenta == idGenerado }
                if (recienCreado != null) seleccionarProducto(recienCreado)
            } else {
                mensajeSnackbar = "❌ Error al crear el producto"
            }
            cargando = false
        }
    }

    fun seleccionarProducto(producto: ProductoVenta) {
        productoSeleccionado = producto
        scope.launch {
            cargando     = true
            ingredientes = recetaRepo.obtenerReceta(producto.idProductoVenta)
            cargando     = false
        }
    }

    fun agregarIngrediente() {
        val materia   = materiaPrimaSeleccionada
        val cantidad  = cantidadInput.toDoubleOrNull()
        val prodSel   = productoSeleccionado

        if (materia == null) {
            mensajeSnackbar = "⚠️ Selecciona una materia prima"
            return
        }
        if (cantidad == null || cantidad <= 0) {
            mensajeSnackbar = "⚠️ Ingresa una cantidad válida"
            return
        }
        if (prodSel == null) {
            mensajeSnackbar = "⚠️ Selecciona un producto terminado"
            return
        }
        if (ingredientes.any { it.idProducto == materia.idProducto }) {
            mensajeSnackbar = "⚠️ '${materia.nombre}' ya está en la receta"
            return
        }

        val nuevoIngrediente = RecetaIngrediente(
            idProductoVenta = prodSel.idProductoVenta,
            idProducto      = materia.idProducto,
            nombreMateria   = materia.nombre,
            cantidad        = cantidad,
            unidad          = unidadSeleccionada
        )
        ingredientes = ingredientes + nuevoIngrediente
        cantidadInput            = ""
        materiaPrimaSeleccionada = null
    }

    fun eliminarIngrediente(ingrediente: RecetaIngrediente) {
        ingredientes = ingredientes.filter { it != ingrediente }
    }

    fun guardarReceta() {
        val prodSel = productoSeleccionado
        if (prodSel == null) {
            mensajeSnackbar = "⚠️ No hay producto seleccionado"
            return
        }
        scope.launch {
            cargando = true
            val exito = recetaRepo.guardarReceta(prodSel.idProductoVenta, ingredientes)
            if (exito) {
                mensajeSnackbar = "✅ Receta de '${prodSel.nombre}' guardada (${ingredientes.size} ingredientes)"
                ingredientes = recetaRepo.obtenerReceta(prodSel.idProductoVenta)
            } else {
                mensajeSnackbar = "❌ Error al guardar la receta"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() { mensajeSnackbar = null }
}
