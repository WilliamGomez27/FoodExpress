package FastFoodApp

data class Producto(
    val idProducto: Int,
    var nombre: String,
    var unidadesPaq: Int,
    var pesoLibras: Double,
    var precioVenta: Double,
    var stockActual: Int,
    val stockMinimo: Int
)