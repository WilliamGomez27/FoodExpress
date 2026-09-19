package FastFoodApp.dao

import FastFoodApp.infrastructure.DatabaseTestSetup
import kotlin.test.*

/**
 * Tests de integración para [InventarioDAO] usando MySQL real en Docker.
 *
 * Verifica:
 * - Movimientos de entrada y salida
 * - FIX SEC-006: El CHECK constraint impide stock negativo a nivel DB
 * - Historial de movimientos registrado correctamente
 */
class InventarioDAOIntegrationTest {

    private val inventarioDAO = InventarioDAO()

    @BeforeTest
    fun setUp() {
        DatabaseTestSetup.iniciar()  // Idempotente, solo inicia una vez
        DatabaseTestSetup.limpiarDatos()
        // Insertar producto con stock inicial de 20
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("""
                INSERT INTO productos (id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo)
                VALUES (1, 'Pan de Hamburguesa', 12, 1.5, 0, 20, 5)
            """.trimIndent()).use { it.executeUpdate() }
        }
    }

    // ─────────────────────────────────────────────
    // Movimientos de inventario
    // ─────────────────────────────────────────────

    @Test
    fun `entrada de inventario incrementa stock`() {
        val ok = inventarioDAO.registrarMovimiento(1, 50, "Reabastecimiento semanal", esEntrada = true)
        assertTrue(ok)

        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT stock_actual FROM productos WHERE id_producto = 1").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(70, rs.getInt("stock_actual")) // 20 + 50
            }
        }
    }

    @Test
    fun `salida de inventario decrementa stock`() {
        val ok = inventarioDAO.registrarMovimiento(1, 5, "Ajuste por merma", esEntrada = false)
        assertTrue(ok)

        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT stock_actual FROM productos WHERE id_producto = 1").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(15, rs.getInt("stock_actual")) // 20 - 5
            }
        }
    }

    @Test
    fun `movimiento registra en historial`() {
        inventarioDAO.registrarMovimiento(1, 10, "Compra de proveedor", esEntrada = true)

        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement(
                "SELECT motivo, tipo, cantidad FROM historial_inventario WHERE id_producto = 1"
            ).use { stmt ->
                val rs = stmt.executeQuery()
                assertTrue(rs.next())
                assertEquals("Compra de proveedor", rs.getString("motivo"))
                assertEquals("ENTRADA", rs.getString("tipo"))
                assertEquals(10, rs.getInt("cantidad"))
            }
        }
    }

    @Test
    fun `obtenerTodosProductos retorna lista correcta`() {
        val productos = inventarioDAO.obtenerTodosProductos()
        assertEquals(1, productos.size)
        assertEquals("Pan de Hamburguesa", productos.first().nombre)
        assertEquals(20, productos.first().stockActual)
    }

    // ─────────────────────────────────────────────
    // FIX SEC-006: CHECK constraint impide stock negativo
    // ─────────────────────────────────────────────

    @Test
    fun `sacar mas stock del disponible falla con rollback`() {
        // Intentar sacar 100 unidades cuando solo hay 20
        val ok = inventarioDAO.registrarMovimiento(1, 100, "Salida masiva", esEntrada = false)
        assertFalse(ok, "Debe fallar: stock insuficiente")

        // Stock debe permanecer en 20 tras el rollback
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT stock_actual FROM productos WHERE id_producto = 1").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(20, rs.getInt("stock_actual"), "Stock debe permanecer en 20 tras rollback")
            }
        }
    }
}
