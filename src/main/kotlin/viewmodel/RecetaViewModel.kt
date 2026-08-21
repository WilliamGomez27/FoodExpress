package FastFoodApp.viewmodel

import FastFoodApp.model.Producto
import FastFoodApp.model.ProductoVenta
import FastFoodApp.model.RecetaIngrediente
import FastFoodApp.repository.CatalogoVentas
import FastFoodApp.repository.InventarioRepository
import FastFoodApp.repository.RecetaRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Recetas.
 * Gestiona:
 * - El catálogo de productos terminados (productos_venta).
 * - La lista de materias primas disponibles en el inventario.
 * - Los ingredientes de la receta del producto terminado seleccionado.
 * - El guardado de recetas en la BD.
 */
class RecetaViewModel(private val scope: CoroutineScope) {

    private val recetaRepo    = RecetaRepository()
    private val inventarioRepo = InventarioRepository()

    // ── Estado observable ─────────────────────────────────────────────────────

    /** Catálogo de productos terminados disponibles */
    var productosTerminados by mutableStateOf(listOf<ProductoVenta>())
        private set

    /** El producto terminado seleccionado en el panel izquierdo */
    var productoSeleccionado by mutableStateOf<ProductoVenta?>(null)

    /** Ingredientes (receta) del producto seleccionado, cargados desde BD */
    var ingredientes by mutableStateOf(listOf<RecetaIngrediente>())
        private set

    /** Lista de materias primas del inventario (para el selector de agregar ingrediente) */
    var materiasPrimas by mutableStateOf(listOf<Producto>())
        private set

    // Estado del formulario para agregar un ingrediente nuevo
    var materiaPrimaSeleccionada by mutableStateOf<Producto?>(null)
    var cantidadInput            by mutableStateOf("")
    var unidadSeleccionada       by mutableStateOf("unidades")

    val unidades = listOf("unidades", "gramos", "ml")

    var cargando        by mutableStateOf(false)
        private set

    var mensajeSnackbar by mutableStateOf<String?>(null)
        private set

    // ── Acciones ──────────────────────────────────────────────────────────────

    /** Carga el catálogo de productos terminados y las materias primas del inventario. */
    fun cargarDatos() {
        scope.launch {
            cargando = true
            productosTerminados = CatalogoVentas.obtenerCatalogo()
            materiasPrimas      = inventarioRepo.obtenerProductos()
            // Seleccionar el primero por defecto
            if (productoSeleccionado == null && productosTerminados.isNotEmpty()) {
                seleccionarProducto(productosTerminados.first())
            }
            cargando = false
        }
    }

    /** Cambia el producto terminado seleccionado y carga su receta desde la BD. */
    fun seleccionarProducto(producto: ProductoVenta) {
        productoSeleccionado = producto
        scope.launch {
            cargando     = true
            ingredientes = recetaRepo.obtenerReceta(producto.idProductoVenta)
            cargando     = false
        }
    }

    /** Agrega un ingrediente a la lista local (no guarda en BD todavía). */
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
        // No permitir duplicados
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
        // Limpiar formulario
        cantidadInput            = ""
        materiaPrimaSeleccionada = null
    }

    /** Elimina un ingrediente de la lista local (no guarda en BD todavía). */
    fun eliminarIngrediente(ingrediente: RecetaIngrediente) {
        ingredientes = ingredientes.filter { it != ingrediente }
    }

    /** Guarda la receta completa en la base de datos. */
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
                // Recargar desde BD para confirmar
                ingredientes = recetaRepo.obtenerReceta(prodSel.idProductoVenta)
            } else {
                mensajeSnackbar = "❌ Error al guardar la receta"
            }
            cargando = false
        }
    }

    fun limpiarMensaje() { mensajeSnackbar = null }
}
