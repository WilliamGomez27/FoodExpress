package FastFoodApp

import FastFoodApp.database.ConexionDB
import FastFoodApp.database.DbConfig
import FastFoodApp.navigation.AppFastFood
import FastFoodApp.utils.AppLogger
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.res.painterResource
import java.util.Properties

fun main() = application {

    // ── FIX SEC-002: Cargar credenciales desde db.properties (no hardcodeadas) ──
    cargarConfiguracionDB()

    val windowState = rememberWindowState(width = 1100.dp, height = 720.dp)
    Window(
        onCloseRequest = {
            ConexionDB.cerrarConexion()
            exitApplication()
        },
        title = "Creado Por WILLIAM.GOMEZ - FastFoodApp",
        state = windowState,
        icon = painterResource("ic_launcher_fastfood.xml")
    ) {
        AppFastFood()
    }
}

/**
 * Carga la configuración de BD desde src/desktopMain/resources/db.properties.
 * Si el archivo no existe, usa valores por defecto para no bloquear la app.
 */
private fun cargarConfiguracionDB() {
    val propsStream = object {}.javaClass.getResourceAsStream("/db.properties")

    if (propsStream == null) {
        System.err.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║  ADVERTENCIA: No se encontró 'db.properties'             ║
            ║  Se usarán valores por defecto (localhost).              ║
            ║                                                          ║
            ║  Para configuración personalizada crea:                  ║
            ║  src/desktopMain/resources/db.properties                 ║
            ╚══════════════════════════════════════════════════════════╝
        """.trimIndent())
        
        DbConfig.configure(
            host = "localhost",
            puerto = 3306,
            db = "SuperMarket_db",
            user = "root",
            password = "",
            usarSSL = false
        )
        return
    }

    val props = Properties().apply { load(propsStream) }

    DbConfig.configure(
        host     = props.getProperty("db.host", "localhost"),
        puerto   = props.getProperty("db.port", "3306").toInt(),
        db       = props.getProperty("db.name", "SuperMarket_db"),
        user     = props.getProperty("db.user", "root"),
        password = props.getProperty("db.password", ""),
        usarSSL  = props.getProperty("db.ssl", "false").toBoolean()
    )

    AppLogger.info("Main", "Configuración de BD cargada desde db.properties")
}
