package FastFoodApp

import FastFoodApp.utils.Validaciones
import kotlin.test.*

/**
 * Pruebas unitarias para [Validaciones].
 * Cubre casos válidos, límites e inválidos para cada función de validación.
 */
class ValidacionesTest {

    // ─────────────────────────────────────────────
    // esNombreValido
    // ─────────────────────────────────────────────

    @Test
    fun `esNombreValido acepta nombre normal`() {
        assertTrue(Validaciones.esNombreValido("Hamburguesa Clásica"))
    }

    @Test
    fun `esNombreValido acepta nombre de exactamente 2 caracteres`() {
        assertTrue(Validaciones.esNombreValido("AB"))
    }

    @Test
    fun `esNombreValido acepta nombre de exactamente 100 caracteres`() {
        val nombre = "A".repeat(100)
        assertTrue(Validaciones.esNombreValido(nombre))
    }

    @Test
    fun `esNombreValido rechaza nombre vacio`() {
        assertFalse(Validaciones.esNombreValido(""))
    }

    @Test
    fun `esNombreValido rechaza nombre de solo espacios`() {
        assertFalse(Validaciones.esNombreValido("   "))
    }

    @Test
    fun `esNombreValido rechaza nombre de 1 caracter`() {
        assertFalse(Validaciones.esNombreValido("A"))
    }

    @Test
    fun `esNombreValido rechaza nombre de 101 caracteres`() {
        val nombre = "A".repeat(101)
        assertFalse(Validaciones.esNombreValido(nombre))
    }

    // ─────────────────────────────────────────────
    // esPrecioValido
    // ─────────────────────────────────────────────

    @Test
    fun `esPrecioValido acepta precio positivo entero`() {
        assertTrue(Validaciones.esPrecioValido("100"))
    }

    @Test
    fun `esPrecioValido acepta precio positivo decimal`() {
        assertTrue(Validaciones.esPrecioValido("35.50"))
    }

    @Test
    fun `esPrecioValido acepta precio minimo positivo`() {
        assertTrue(Validaciones.esPrecioValido("0.01"))
    }

    @Test
    fun `esPrecioValido rechaza precio cero`() {
        assertFalse(Validaciones.esPrecioValido("0"))
    }

    @Test
    fun `esPrecioValido rechaza precio negativo`() {
        assertFalse(Validaciones.esPrecioValido("-5.00"))
    }

    @Test
    fun `esPrecioValido rechaza texto no numerico`() {
        assertFalse(Validaciones.esPrecioValido("abc"))
    }

    @Test
    fun `esPrecioValido rechaza cadena vacia`() {
        assertFalse(Validaciones.esPrecioValido(""))
    }

    @Test
    fun `esPrecioValido rechaza precio con simbolo de moneda`() {
        assertFalse(Validaciones.esPrecioValido("Q35.00"))
    }

    // ─────────────────────────────────────────────
    // esCantidadValida
    // ─────────────────────────────────────────────

    @Test
    fun `esCantidadValida acepta cantidad positiva`() {
        assertTrue(Validaciones.esCantidadValida("5"))
    }

    @Test
    fun `esCantidadValida acepta cantidad minima de 1`() {
        assertTrue(Validaciones.esCantidadValida("1"))
    }

    @Test
    fun `esCantidadValida rechaza cantidad cero`() {
        assertFalse(Validaciones.esCantidadValida("0"))
    }

    @Test
    fun `esCantidadValida rechaza cantidad negativa`() {
        assertFalse(Validaciones.esCantidadValida("-1"))
    }

    @Test
    fun `esCantidadValida rechaza cantidad decimal`() {
        assertFalse(Validaciones.esCantidadValida("2.5"))
    }

    @Test
    fun `esCantidadValida rechaza texto no numerico`() {
        assertFalse(Validaciones.esCantidadValida("diez"))
    }

    // ─────────────────────────────────────────────
    // esStockValido
    // ─────────────────────────────────────────────

    @Test
    fun `esStockValido acepta stock cero`() {
        // Stock 0 es válido (producto agotado pero existente)
        assertTrue(Validaciones.esStockValido("0"))
    }

    @Test
    fun `esStockValido acepta stock positivo`() {
        assertTrue(Validaciones.esStockValido("50"))
    }

    @Test
    fun `esStockValido rechaza stock negativo`() {
        assertFalse(Validaciones.esStockValido("-1"))
    }

    @Test
    fun `esStockValido rechaza texto`() {
        assertFalse(Validaciones.esStockValido("muchos"))
    }

    @Test
    fun `esStockValido rechaza decimal`() {
        assertFalse(Validaciones.esStockValido("10.5"))
    }
}
