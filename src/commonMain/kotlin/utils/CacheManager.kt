package FastFoodApp.utils

import java.io.File
import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream

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
            // En Android, guardamos en el directorio de cache interno
            File(System.getProperty("java.io.tmpdir") ?: "/data/data/com.fastfood.app/cache")
        } else {
            // En Desktop, guardamos en la carpeta del usuario
            File(System.getProperty("user.home"), ".rappyfood")
        }
        
        if (!dir.exists()) {
            dir.mkdirs()
        }
        File(dir, "credenciales.properties")
    }

    fun guardarCredenciales(usuario: String, contrasena: String) {
        try {
            val props = Properties()
            props.setProperty("usuario", usuario)
            props.setProperty("contrasena", contrasena)
            
            FileOutputStream(cacheFile).use { out ->
                props.store(out, "Credenciales cacheadas de RappyFood")
            }
        } catch (e: Exception) {
            println("✗ [CacheManager] Error al guardar credenciales: ${e.message}")
        }
    }

    fun cargarCredenciales(): Pair<String, String>? {
        if (!cacheFile.exists()) return null
        
        return try {
            val props = Properties()
            FileInputStream(cacheFile).use { input ->
                props.load(input)
            }
            val u = props.getProperty("usuario")
            val p = props.getProperty("contrasena")
            if (!u.isNullOrEmpty() && !p.isNullOrEmpty()) {
                Pair(u, p)
            } else {
                null
            }
        } catch (e: Exception) {
            println("✗ [CacheManager] Error al cargar credenciales: ${e.message}")
            null
        }
    }

    fun limpiarCredenciales() {
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }
}
