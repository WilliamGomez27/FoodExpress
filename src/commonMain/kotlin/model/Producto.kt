package FoodExpress.model

data class Producto(
    val idProducto: Int,
    var nombre: String,
    var unidadesPaq: Int,
    var pesoLibras: Double,
    var precioVenta: Double, // Usado como Costo en materia prima
    var stockActual: Int,
    val stockMinimo: Int,
    var imagenUrl: String? = null,
    var unidadMedida: String = "unidades", // "unidades", "gramos", "ml"
    var activo: Boolean = true // Borrado lógico (Papelera)
)
