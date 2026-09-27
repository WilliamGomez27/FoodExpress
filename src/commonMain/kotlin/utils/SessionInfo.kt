package FoodExpress.utils

import FoodExpress.model.UsuarioSesion

/**
 * Información de la sesión activa del usuario.
 *
 * SEGURIDAD FIX SEC-007:
 * - Usa [UsuarioSesion] en lugar de [Usuario] — sin campo contraseña.
 * - La contraseña nunca persiste más allá de la autenticación.
 */
object SessionInfo {
    var usuarioActual: UsuarioSesion? = null

    val isAdmin: Boolean
        get() = usuarioActual?.rol?.lowercase() == "admin"

    val nombreUsuario: String
        get() = usuarioActual?.nombre ?: "Sin sesión"

    fun cerrarSesion() {
        usuarioActual = null
        CacheManager.limpiarSesion()
    }
}
