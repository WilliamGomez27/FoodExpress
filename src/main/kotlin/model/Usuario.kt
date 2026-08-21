package FastFoodApp.model

// TODO: Implementar modelo de Usuario
data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val usuario: String,
    val contrasena: String,
    val rol: String = "cajero"
)
