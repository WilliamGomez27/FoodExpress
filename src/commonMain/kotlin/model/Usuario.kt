package FastFoodApp.model

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val usuario: String,
    val contrasena: String,
    val rol: String = "cajero",
    val email: String = "",
    val whatsapp: String = ""
)
