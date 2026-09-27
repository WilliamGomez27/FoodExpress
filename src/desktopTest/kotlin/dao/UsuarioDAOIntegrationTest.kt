package FoodExpress.dao

import FoodExpress.infrastructure.DatabaseTestSetup
import FoodExpress.model.Usuario
import FoodExpress.utils.HashUtils
import kotlin.test.*

/**
 * Tests de integración para [UsuarioDAO] usando MySQL real en Docker (Testcontainers).
 *
 * Pre-requisito: Docker Desktop debe estar corriendo.
 * Ejecutar con: .\gradlew desktopTest --tests "FoodExpress.dao.UsuarioDAOIntegrationTest"
 */
class UsuarioDAOIntegrationTest {

    private val dao = UsuarioDAO()

    @BeforeTest
    fun setUp() {
        DatabaseTestSetup.iniciar()  // Idempotente, solo inicia una vez
        DatabaseTestSetup.limpiarDatos()
        // Insertar usuarios de prueba con hash correcto
        insertarUsuarioTest("admin_test", "MiPassword123", "admin")
        insertarUsuarioTest("cajero_test", "OtraPass456", "cajero")
    }

    private fun insertarUsuarioTest(username: String, password: String, rol: String) {
        val salt = HashUtils.generarSalt()
        val hash = HashUtils.hashPassword(password, salt)
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO usuarios (nombre, usuario, contrasena, salt, rol) VALUES (?, ?, ?, ?, ?)"
            ).use { stmt ->
                stmt.setString(1, username.replaceFirstChar { it.uppercase() })
                stmt.setString(2, username)
                stmt.setString(3, hash)
                stmt.setString(4, salt)
                stmt.setString(5, rol)
                stmt.executeUpdate()
            }
        }
    }

    // ─────────────────────────────────────────────
    // FIX SEC-001: Verificación de hash
    // ─────────────────────────────────────────────

    @Test
    fun `login con credenciales correctas retorna sesion`() {
        val result = dao.obtenerPorCredenciales("admin_test", "MiPassword123")
        assertTrue(result.isSuccess)
        val sesion = result.getOrNull()
        assertNotNull(sesion)
        assertEquals("admin_test", sesion.usuario)
        assertEquals("admin", sesion.rol)
    }

    @Test
    fun `login con password incorrecto retorna null (no excepcion)`() {
        val result = dao.obtenerPorCredenciales("admin_test", "PasswordIncorrecto")
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `login con usuario inexistente retorna null`() {
        val result = dao.obtenerPorCredenciales("no_existe", "cualquierPass")
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `sesion retornada no contiene campo contrasena`() {
        // FIX SEC-007: UsuarioSesion no tiene campo contrasena
        val result = dao.obtenerPorCredenciales("cajero_test", "OtraPass456")
        val sesion = result.getOrNull()
        assertNotNull(sesion)
        // Verificar que UsuarioSesion no tiene propiedad contrasena
        // Esto es garantizado por el tipo en tiempo de compilación
        assertEquals("cajero", sesion.rol)
        assertEquals("cajero_test", sesion.usuario)
    }

    @Test
    fun `insertar usuario hashea la contrasena en BD`() {
        val usuario = Usuario(
            idUsuario = 0,
            nombre = "Nuevo Usuario",
            usuario = "nuevo_user",
            contrasena = "NuevaPass789",
            rol = "cajero"
        )
        val resultado = dao.insertar(usuario)
        assertTrue(resultado)

        // Verificar que en BD NO está el texto claro
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT contrasena, salt FROM usuarios WHERE usuario = ?").use { stmt ->
                stmt.setString(1, "nuevo_user")
                val rs = stmt.executeQuery()
                assertTrue(rs.next())
                val hashEnBD = rs.getString("contrasena")
                val saltEnBD = rs.getString("salt")

                // El hash no debe ser igual al password en claro
                assertNotEquals("NuevaPass789", hashEnBD)
                // Pero debe verificar correctamente
                assertTrue(HashUtils.verificarPassword("NuevaPass789", hashEnBD, saltEnBD))
                // Y debe fallar con el password incorrecto
                assertFalse(HashUtils.verificarPassword("OtroPassword", hashEnBD, saltEnBD))
            }
        }
    }

    @Test
    fun `dos usuarios con el mismo password tienen hashes distintos (salt unico)`() {
        val user1 = Usuario(0, "User1", "user1", "mismoPassword", "cajero")
        val user2 = Usuario(0, "User2", "user2", "mismoPassword", "cajero")

        dao.insertar(user1)
        dao.insertar(user2)

        DatabaseTestSetup.getTestConnection().use { conn ->
            val query = "SELECT contrasena FROM usuarios WHERE usuario IN ('user1', 'user2') ORDER BY usuario"
            conn.prepareStatement(query).use { stmt ->
                val rs = stmt.executeQuery()
                rs.next(); val hash1 = rs.getString("contrasena")
                rs.next(); val hash2 = rs.getString("contrasena")
                // Mismo password → hashes DIFERENTES gracias al salt único
                assertNotEquals(hash1, hash2)
            }
        }
    }
}
