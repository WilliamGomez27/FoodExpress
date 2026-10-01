package FoodExpress.utils

/**
 * Implementación Android de [ImageLoader].
 * Los assets de Android son accesibles via el esquema "file:///android_asset/".
 * Coil en Android resuelve esta URI directamente usando AssetFetcher.
 */
actual object ImageLoader {

    actual fun modelParaAsset(nombreArchivo: String): Any? {
        return "file:///android_asset/images/$nombreArchivo"
    }
}
