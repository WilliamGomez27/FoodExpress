package FastFoodApp.utils

object Validaciones {

    /**
     * Valida que el nombre de usuario y campos de texto no contengan
     * caracteres sospechosos comunes en ataques XSS o inyecciones de comandos.
     */
    fun esTextoSeguro(texto: String): Boolean {
        if (texto.isBlank()) return false
        val regex = Regex("^[a-zA-Z0-9_ \\-.,@]+$")
        return regex.matches(texto)
    }

    fun esNombreValido(nombre: String): Boolean =
        nombre.isNotBlank() && nombre.length in 2..100 && esTextoSeguro(nombre)

    fun esUsuarioValido(usuario: String): Boolean =
        usuario.isNotBlank() && usuario.length in 4..50 && esTextoSeguro(usuario)

    fun esPrecioValido(precio: String): Boolean =
        precio.toDoubleOrNull()?.let { it >= 0 } ?: false

    fun esCantidadValida(cantidad: String): Boolean =
        cantidad.toIntOrNull()?.let { it > 0 } ?: false

    fun esStockValido(stock: String): Boolean =
        stock.toIntOrNull()?.let { it >= 0 } ?: false
}
