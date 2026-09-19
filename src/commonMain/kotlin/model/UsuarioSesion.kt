package FastFoodApp.model

/**
 * Modelo de sesión activa del usuario.
 *
 * SEGURIDAD: Intencionalmente NO incluye el campo [contrasena].
 * Después de autenticarse, solo se conservan los datos de identidad
 * y rol necesarios para la sesión — la contraseña nunca viaja
 * más allá del proceso de autenticación.
 */
data class UsuarioSesion(
    val idUsuario: Int,
    val nombre: String,
    val usuario: String,
    val rol: String = "cajero"
)
