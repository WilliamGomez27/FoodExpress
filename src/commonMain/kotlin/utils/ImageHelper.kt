package FoodExpress.utils

/**
 * Utilidad para resolver la URL/model de imagen de un producto.
 *
 * ESTRATEGIA (en orden de prioridad):
 *   1. Si imagen_url en la BD es válida → se usa esa URL directamente (red).
 *   2. Si imagen_url es null/vacía → [fallbackPorNombre] busca una imagen local
 *      según palabras clave del nombre del producto (usa [ImageLoader] — cross-platform).
 *   3. Si no hay coincidencia → devuelve null (la UI muestra el placeholder 🍽️).
 */
object ImageHelper {

    /**
     * Lista de nombres de archivo de imágenes locales con su descripción.
     * Usado por el selector de imagen en el diálogo de Inventario.
     */
    val imagenesLocales: List<Pair<String, String>> = listOf(
        "img_1.jpeg"  to "Carta Bebidas",
        "img_2.jpeg"  to "Hamburguesa Especial",
        "img_3.jpeg"  to "Carta Frutería",
        "img_4.jpeg"  to "Frutería / Helados",
        "img_5.jpeg"  to "Carta Hamburguesas",
        "img_6.jpeg"  to "Carta Waffles / Brownies",
        "img_7.jpeg"  to "Carta Perros / Salchipapas",
        "img_8.jpeg"  to "Comida Rápida Variada",
        "img_9.jpeg"  to "Carta Otros Platos",
        "img_10.jpeg" to "Copa de Helado",
        "img_11.jpeg" to "Salchipapa Súper",
        "img_12.jpeg" to "Ensalada de Frutas",
        "img_13.jpeg" to "Hamburguesa Artesanal",
        "img_14.jpeg" to "Patacón Mixto",
        "img_15.jpeg" to "Banana Split",
        "img_16.jpeg" to "Ensalada Frutas Grande",
        "img_17.jpeg" to "Hamburguesa Abocado",
        "img_18.jpeg" to "Hamburguesa Mixta"
    )

    /**
     * Mapeo de palabras clave del nombre del producto → archivo de imagen local.
     * Orden importa: los más específicos primero.
     */
    private val mapaFallback: List<Pair<List<String>, String>> = listOf(
        listOf("abocado", "abogado", "avocado")                              to "img_17.jpeg",
        listOf("mixta", "mixto", "súper", "super")                           to "img_18.jpeg",
        listOf("artesanal", "sencilla", "doble carne")                       to "img_13.jpeg",
        listOf("hamburguesa", "burger")                                      to "img_2.jpeg",
        listOf("perro", "hot dog", "salchicha", "salchipapa")                to "img_11.jpeg",
        listOf("patacón", "patacon")                                         to "img_14.jpeg",
        listOf("banana split", "banana")                                     to "img_15.jpeg",
        listOf("ensalada de fruta", "ensalada fruta")                        to "img_16.jpeg",
        listOf("copa de fruta", "salpicón", "salpicon")                      to "img_12.jpeg",
        listOf("copa", "helado", "cono", "malteada", "granizado")            to "img_10.jpeg",
        listOf("waffle", "brownie")                                          to "img_6.jpeg",
        listOf("fresas con crema", "fresa")                                  to "img_3.jpeg",
        listOf("jugo", "limonada", "gaseosa", "bebida", "agua", "coca",
               "postobón", "postobon", "hit", "bretaña", "jarra")           to "img_1.jpeg",
        listOf("papa francesa", "papa criolla", "porción", "porcion",
               "arepita")                                                    to "img_9.jpeg",
        listOf("alita", "costilla", "bbq", "plancha", "pechuga", "pollo")   to "img_8.jpeg",
        listOf("pincho")                                                     to "img_9.jpeg"
    )

    /**
     * Devuelve el model para [AsyncImage] a partir de una URL de BD.
     * Si la URL es válida la devuelve tal cual (Coil la carga por red).
     * Si es null/vacía devuelve null para que la UI muestre el placeholder.
     */
    fun obtenerImagenParaProducto(imagenActual: String?): Any? {
        return if (imagenActual?.isNotBlank() == true) imagenActual else null
    }

    /**
     * Fallback: busca una imagen local que coincida con el nombre del producto.
     * Usa [ImageLoader.modelParaAsset] — compatible con Android y Desktop.
     *
     * @return model para AsyncImage, o null si no hay coincidencia.
     */
    fun fallbackPorNombre(nombre: String): Any? {
        val nombreLower = nombre.lowercase()
        for ((claves, archivo) in mapaFallback) {
            if (claves.any { nombreLower.contains(it) }) {
                return ImageLoader.modelParaAsset(archivo)
            }
        }
        return null
    }

    /**
     * Devuelve el model de Coil para un asset local por nombre de archivo.
     * Útil para el selector visual en el diálogo de Inventario.
     */
    fun modelDeAsset(nombreArchivo: String): Any? =
        ImageLoader.modelParaAsset(nombreArchivo)
}
