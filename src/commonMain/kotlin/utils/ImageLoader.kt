package FoodExpress.utils

/**
 * Construye el modelo de imagen correcto para cada plataforma.
 *
 * expect/actual pattern:
 * - androidMain → usa "file:///android_asset/images/<archivo>"
 * - desktopMain → usa el recurso del classpath vía getResourceAsStream
 *
 * Uso en Compose:
 *   AsyncImage(model = ImageLoader.modelParaAsset("img_2.jpeg"), ...)
 */
expect object ImageLoader {

    /**
     * Devuelve el model listo para pasarle a AsyncImage de Coil.
     * En Android es un Uri String; en Desktop es un ByteArray leído del classpath.
     *
     * @param nombreArchivo  p.ej. "img_2.jpeg"
     * @return objeto que Coil puede cargar, o null si el archivo no existe.
     */
    fun modelParaAsset(nombreArchivo: String): Any?
}
