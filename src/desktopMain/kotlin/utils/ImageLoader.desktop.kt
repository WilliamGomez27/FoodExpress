package FoodExpress.utils

/**
 * Implementación Desktop de [ImageLoader].
 *
 * En Desktop (JVM) los recursos están empaquetados en el classpath bajo "images/".
 * Los leemos como ByteArray, que Coil 3 puede decodificar directamente.
 *
 * Las imágenes se ubican en:
 *   src/desktopMain/resources/images/img_*.jpeg
 * y quedan disponibles en el classpath bajo: /images/<archivo>
 */
actual object ImageLoader {

    actual fun modelParaAsset(nombreArchivo: String): Any? {
        return try {
            val stream = ImageLoader::class.java
                .getResourceAsStream("/images/$nombreArchivo")
                ?: return null
            stream.use { it.readBytes() }
        } catch (e: Exception) {
            null
        }
    }
}
