package FastFoodApp.utils

import FastFoodApp.model.Usuario

object SessionInfo {
    var usuarioActual: Usuario? = null

    val isAdmin: Boolean
        get() = usuarioActual?.rol?.lowercase() == "admin"
        
    fun cerrarSesion() {
        usuarioActual = null
    }
}
