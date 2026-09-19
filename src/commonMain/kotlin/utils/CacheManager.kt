package FastFoodApp.utils

import java.io.File
import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Gestor de caché de la sesión del usuario.
 *
 * SEGURIDAD FIX SEC-003:
 * - SOLO se guarda el nombre de usuario (para pre-rellenar el campo).
 * - La contraseña NUNCA se persiste en disco, ni cifrada.
 * - El usuario siempre debe ingresar su contraseña manualmente.
 *
 * Esto elimina el vector de ataque de robo de credenciales del filesystem.
 */
object CacheManager {
    private val isAndroid: Boolean by lazy {
        try {
            Class.forName("android.os.Build")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    private val cacheFile: File by lazy {
        val dir = if (isAndroid) {
            File(System.getProperty("java.io.tmpdir") ?: "/data/data/com.fastfood.app/cache")
        } else {
            File(System.getProperty("user.home"), ".rappyfood")
        }
        if (!dir.exists()) dir.mkdirs()
        File(dir, "sesion.properties")  // Renombrado de "credenciales.properties"
    }

    /**
     * Guarda SOLO el nombre de usuario para pre-rellenar el login.
     * La contraseña NUNCA se guarda.
     */
    fun guardarUsuario(nombreUsuario: String) {
        try {
            val props = Properties()
            props.setProperty("usuario", nombreUsuario)
            // 'contrasena' fue removida intencionalmente — FIX SEC-003
            FileOutputStream(cacheFile).use { out ->
                props.store(out, "Sesion cacheada — FastFoodApp (solo usuario, sin password)")
            }
        } catch (e: Exception) {
            AppLogger.error("CacheManager", "Error al guardar usuario: ${e.message}")
        }
    }

    /**
     * Carga el nombre de usuario cacheado.
     * Retorna null si no hay caché o el campo está vacío.
     */
    fun cargarUsuario(): String? {
        if (!cacheFile.exists()) return null
        return try {
            val props = Properties()
            FileInputStream(cacheFile).use { input -> props.load(input) }
            props.getProperty("usuario")?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            AppLogger.error("CacheManager", "Error al cargar usuario: ${e.message}")
            null
        }
    }

    fun limpiarSesion() {
        if (cacheFile.exists()) cacheFile.delete()
    }

    // ── Retrocompatibilidad: si existe el archivo viejo con contraseña, borrarlo ──
    fun migrarArchivoLegacy() {
        val archivoViejo = File(cacheFile.parent, "credenciales.properties")
        if (archivoViejo.exists()) {
            archivoViejo.delete()
            AppLogger.warn("CacheManager", "Archivo legacy de credenciales eliminado por seguridad")
        }
    }
}
