package FoodExpress.model

/**
 * Representa un producto terminado disponible para la venta (ej. Hamburguesa, Gaseosa).
 */
data class ProductoVenta(
    val idProductoVenta: Int, // ID interno para el catálogo
    val nombre: String,
    val precioVenta: Double,
    val categoria: String = "General"
)
