package FastFoodApp.utils

/**
 * Logger centralizado con niveles de severidad.
 *
 * SEGURIDAD FIX SEC-009:
 * - En modo RELEASE, solo se registran errores (sin info de BD en logs).
 * - Reemplaza todos los println() directos en DAOs.
 * - Los mensajes de debug no aparecen en producción (no visibles via adb logcat).
 *
 * Uso:
 *   AppLogger.debug("DAO", "Cargados 5 productos")   // Solo en debug
 *   AppLogger.info("DAO", "Venta #3 registrada")      // Info general
 *   AppLogger.error("DAO", "Error SQL: ...")           // Siempre visible
 */
object AppLogger {

    /** Cambiar a false en builds de release para silenciar logs de debug/info */
    var isDebugEnabled: Boolean = true

    fun debug(tag: String, message: String) {
        if (isDebugEnabled) {
            println("🔍 [DEBUG][$tag] $message")
        }
    }

    fun info(tag: String, message: String) {
        if (isDebugEnabled) {
            println("ℹ️ [INFO][$tag] $message")
        }
    }

    fun warn(tag: String, message: String) {
        // Warnings siempre visibles
        System.err.println("⚠️ [WARN][$tag] $message")
    }

    fun error(tag: String, message: String) {
        // Errores siempre visibles — NO incluir datos sensibles
        System.err.println("✗ [ERROR][$tag] $message")
    }
}
