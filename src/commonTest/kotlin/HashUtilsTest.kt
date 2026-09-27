package FoodExpress

import FoodExpress.utils.HashUtils
import kotlin.test.*

/**
 * Pruebas unitarias para [HashUtils] — hashing de contraseñas.
 * No requiere base de datos.
 */
class HashUtilsTest {

    @Test
    fun `generarSalt produce string de 64 caracteres hex`() {
        val salt = HashUtils.generarSalt()
        assertEquals(64, salt.length)
        assertTrue(salt.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun `dos salts generados son distintos`() {
        val salt1 = HashUtils.generarSalt()
        val salt2 = HashUtils.generarSalt()
        assertNotEquals(salt1, salt2)
    }

    @Test
    fun `hashPassword produce string de 64 caracteres`() {
        val hash = HashUtils.hashPassword("miPassword", "unSalt")
        assertEquals(64, hash.length)
    }

    @Test
    fun `mismo password y salt siempre produce el mismo hash`() {
        val hash1 = HashUtils.hashPassword("password123", "saltFijo")
        val hash2 = HashUtils.hashPassword("password123", "saltFijo")
        assertEquals(hash1, hash2)
    }

    @Test
    fun `diferente salt produce diferente hash para mismo password`() {
        val hash1 = HashUtils.hashPassword("password123", "salt1")
        val hash2 = HashUtils.hashPassword("password123", "salt2")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hashPassword no retorna el password en claro`() {
        val password = "admin123"
        val hash = HashUtils.hashPassword(password, "cualquierSalt")
        assertNotEquals(password, hash)
        assertFalse(hash.contains(password))
    }

    @Test
    fun `verificarPassword retorna true con password correcto`() {
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword("miPassword", salt)
        assertTrue(HashUtils.verificarPassword("miPassword", hash, salt))
    }

    @Test
    fun `verificarPassword retorna false con password incorrecto`() {
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword("miPassword", salt)
        assertFalse(HashUtils.verificarPassword("otraPassword", hash, salt))
    }

    @Test
    fun `verificarPassword retorna false con salt distinto`() {
        val saltReal = HashUtils.generarSalt()
        val saltFalso = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword("miPassword", saltReal)
        assertFalse(HashUtils.verificarPassword("miPassword", hash, saltFalso))
    }

    @Test
    fun `verificarPassword retorna false con hash manipulado`() {
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword("password", salt)
        val hashManipulado = hash.replaceFirst(hash[0], if (hash[0] == 'a') 'b' else 'a')
        assertFalse(HashUtils.verificarPassword("password", hashManipulado, salt))
    }

    @Test
    fun `password vacio puede ser hasheado y verificado`() {
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword("", salt)
        assertTrue(HashUtils.verificarPassword("", hash, salt))
        assertFalse(HashUtils.verificarPassword("noVacio", hash, salt))
    }

    @Test
    fun `password con caracteres especiales funciona correctamente`() {
        val password = "P@ssw0rd!#$%áéíóú"
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword(password, salt)
        assertTrue(HashUtils.verificarPassword(password, hash, salt))
    }
}
