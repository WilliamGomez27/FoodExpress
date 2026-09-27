package FoodExpress

import FoodExpress.model.UsuarioSesion
import FoodExpress.utils.SessionInfo
import kotlin.test.*

/**
 * Pruebas unitarias para [SessionInfo].
 * Actualizado para usar [UsuarioSesion] (FIX SEC-007 — sin campo contrasena).
 */
class SessionInfoTest {

    @BeforeTest
    fun setUp() {
        SessionInfo.cerrarSesion()
    }

    @AfterTest
    fun tearDown() {
        SessionInfo.cerrarSesion()
    }

    // ─────────────────────────────────────────────
    // Estado inicial
    // ─────────────────────────────────────────────

    @Test
    fun `sin sesion el usuario actual es null`() {
        assertNull(SessionInfo.usuarioActual)
    }

    @Test
    fun `sin sesion isAdmin es false`() {
        assertFalse(SessionInfo.isAdmin)
    }

    // ─────────────────────────────────────────────
    // Login de admin (ahora usa UsuarioSesion)
    // ─────────────────────────────────────────────

    @Test
    fun `admin con rol admin tiene isAdmin true`() {
        SessionInfo.usuarioActual = UsuarioSesion(
            idUsuario = 1,
            nombre = "Administrador",
            usuario = "admin",
            rol = "admin"
            // Sin campo contrasena — FIX SEC-007
        )
        assertTrue(SessionInfo.isAdmin)
    }

    @Test
    fun `admin con rol en mayusculas tiene isAdmin true`() {
        SessionInfo.usuarioActual = UsuarioSesion(1, "Admin", "admin", "ADMIN")
        assertTrue(SessionInfo.isAdmin)
    }

    @Test
    fun `cajero con rol cajero tiene isAdmin false`() {
        SessionInfo.usuarioActual = UsuarioSesion(2, "Juan Cajero", "cajero1", "cajero")
        assertFalse(SessionInfo.isAdmin)
    }

    @Test
    fun `usuario sin rol reconocido tiene isAdmin false`() {
        SessionInfo.usuarioActual = UsuarioSesion(3, "Sin Rol", "usuario", "supervisor")
        assertFalse(SessionInfo.isAdmin)
    }

    // ─────────────────────────────────────────────
    // cerrarSesion
    // ─────────────────────────────────────────────

    @Test
    fun `cerrarSesion limpia el usuario actual`() {
        SessionInfo.usuarioActual = UsuarioSesion(1, "Admin", "admin", "admin")
        assertNotNull(SessionInfo.usuarioActual)
        SessionInfo.cerrarSesion()
        assertNull(SessionInfo.usuarioActual)
    }

    @Test
    fun `cerrarSesion hace isAdmin false`() {
        SessionInfo.usuarioActual = UsuarioSesion(1, "Admin", "admin", "admin")
        assertTrue(SessionInfo.isAdmin)
        SessionInfo.cerrarSesion()
        assertFalse(SessionInfo.isAdmin)
    }

    @Test
    fun `cerrarSesion sobre sesion ya nula no lanza excepcion`() {
        assertNull(SessionInfo.usuarioActual)
        SessionInfo.cerrarSesion()
        assertNull(SessionInfo.usuarioActual)
    }

    // ─────────────────────────────────────────────
    // Acceso a datos de sesión
    // ─────────────────────────────────────────────

    @Test
    fun `datos del usuario activo son accesibles en sesion`() {
        val sesion = UsuarioSesion(5, "María López", "mlopez", "cajero")
        SessionInfo.usuarioActual = sesion

        assertEquals(5, SessionInfo.usuarioActual?.idUsuario)
        assertEquals("mlopez", SessionInfo.usuarioActual?.usuario)
        assertEquals("cajero", SessionInfo.usuarioActual?.rol)
    }

    @Test
    fun `nombreUsuario retorna nombre cuando hay sesion`() {
        SessionInfo.usuarioActual = UsuarioSesion(1, "Carlos Pérez", "cperez", "cajero")
        assertEquals("Carlos Pérez", SessionInfo.nombreUsuario)
    }

    @Test
    fun `nombreUsuario retorna texto por defecto sin sesion`() {
        assertEquals("Sin sesión", SessionInfo.nombreUsuario)
    }
}
