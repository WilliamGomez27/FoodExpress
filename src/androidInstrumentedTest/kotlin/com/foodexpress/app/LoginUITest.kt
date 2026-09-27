package com.foodexpress.app

import FoodExpress.ui.login.LoginScreen
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class LoginUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginFlow_UI() {
        // Configuramos la UI a probar
        var loginCalled = false
        composeTestRule.setContent {
            LoginScreen(onLoginSuccess = { loginCalled = true })
        }

        // Simular que el usuario escribe en el campo "Usuario"
        // (Buscamos por el texto que dice "Usuario" que sirve de placeholder o label)
        composeTestRule.onNodeWithText("Usuario").performTextInput("admin")

        // Simular que el usuario escribe en la contraseña
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password_falso")

        // Simulamos clic en el botón de "Ingresar"
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // El login con credenciales falsas no debería tener éxito
        assertTrue(!loginCalled, "El login no debería ser exitoso con credenciales inválidas")
    }
}
