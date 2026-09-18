package FastFoodApp.model

// TODO: Implementar modelo de Cliente
data class Cliente(
    val idCliente: Int,
    val nombre: String,
    val telefono: String = "",
    val direccion: String = ""
)
