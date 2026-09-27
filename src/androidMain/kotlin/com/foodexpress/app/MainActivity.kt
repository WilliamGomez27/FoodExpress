package com.foodexpress.app

import FoodExpress.database.DbConfig
import FoodExpress.navigation.AppFoodExpress
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.io.FileNotFoundException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── FIX SEC-002: Configurar BD desde recursos Android ──
        // Edita 'app/src/main/assets/db.properties' con tus valores reales.
        // NUNCA pongas credenciales directamente en este archivo.
        cargarConfiguracionDB()

        setContent {
            AppFoodExpress()
        }
    }

    /**
     * Carga la configuración de BD desde assets/db.properties.
     * En producción, considera usar EncryptedSharedPreferences o un panel
     * de configuración en la UI para la primera vez que corre la app.
     */
    private fun cargarConfiguracionDB() {
        try {
            val props = java.util.Properties()
            assets.open("db.properties").use { stream ->
                props.load(stream)
            }
            DbConfig.configure(
                host     = props.getProperty("db.host", "10.0.2.2"),
                puerto   = props.getProperty("db.port", "3306").toInt(),
                db       = props.getProperty("db.name", "SuperMarket_db"),
                user     = props.getProperty("db.user", "root"),
                password = props.getProperty("db.password", ""),
                usarSSL  = props.getProperty("db.ssl", "false").toBoolean()
            )
        } catch (e: FileNotFoundException) {
            System.err.println("ADVERTENCIA: No se encontró assets/db.properties. Usando config por defecto.")
            DbConfig.configure(
                host = "10.0.2.2", 
                puerto = 3306, 
                db = "SuperMarket_db", 
                user = "root", 
                password = "", 
                usarSSL = false
            )
        }
    }
}
