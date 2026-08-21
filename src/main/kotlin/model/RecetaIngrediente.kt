package FastFoodApp.model

/**
 * Representa un ingrediente de materia prima dentro de la receta
 * de un producto terminado.
 *
 * Ejemplo: Para hacer 1 "Hamburguesa Clásica" se necesitan
 *   - 2 unidades de Pan Hamburguesa
 *   - 1 unidad de Carne de Hamburguesa
 *   - 50 gramos de Cebolla
 *   - etc.
 */
data class RecetaIngrediente(
    val id: Int = 0,
    val idProductoVenta: Int,        // FK → productos_venta (producto terminado)
    val idProducto: Int,             // FK → productos (materia prima en inventario)
    val nombreMateria: String = "",  // Nombre legible, obtenido por JOIN desde productos
    val cantidad: Double,            // Cuánto se consume por cada unidad producida
    val unidad: String               // "unidades", "gramos", "ml"
)
