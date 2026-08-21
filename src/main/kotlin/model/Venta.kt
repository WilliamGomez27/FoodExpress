package FastFoodApp.model

/**
 * Representa una línea dentro de una venta (un producto y su cantidad).
 */
data class ItemVenta(
    val productoVenta: ProductoVenta,
    var cantidad: Int
) {
    val subtotal: Double get() = productoVenta.precioVenta * cantidad
}

/**
 * Representa una venta completa registrada en la base de datos.
 */
data class Venta(
    val idVenta: Int,
    val fechaHora: String,
    val totalVenta: Double,
    val items: List<ItemVenta> = emptyList()
)
