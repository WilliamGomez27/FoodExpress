package FoodExpress.utils

/**
 * Utilidad para asignar y resolver imágenes locales limpias.
 */
object ImageHelper {
    private val listaImagenes = (1..18).map { "img_$it.jpeg" }

    fun obtenerImagenParaProducto(id: Int, imagenActual: String?): String {
        if (imagenActual?.isNotBlank() == true) return imagenActual
        val idPositivo = if (id < 0) -id else id
        val index = idPositivo % listaImagenes.size
        val nombreArchivo = listaImagenes[index]
        return "file:///android_asset/images/$nombreArchivo"
    }
}
