package FastFoodApp.utils

object Validaciones {

    fun esNombreValido(nombre: String): Boolean =
        nombre.isNotBlank() && nombre.length in 2..100

    fun esPrecioValido(precio: String): Boolean =
        precio.toDoubleOrNull()?.let { it > 0 } ?: false

    fun esCantidadValida(cantidad: String): Boolean =
        cantidad.toIntOrNull()?.let { it > 0 } ?: false

    fun esStockValido(stock: String): Boolean =
        stock.toIntOrNull()?.let { it >= 0 } ?: false
}
