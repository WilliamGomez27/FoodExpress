package FastFoodApp

import FastFoodApp.database.ConexionDB
import FastFoodApp.navigation.AppFastFood
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp

fun main() = application {
    val windowState = rememberWindowState(width = 1100.dp, height = 720.dp)
    Window(
        onCloseRequest = {
            ConexionDB.cerrarConexion()
            exitApplication()
        },
        title = "Creado Por WILLIAM.GOMEZ",
        state = windowState
    ) {
        AppFastFood()
    }
}