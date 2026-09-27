package FoodExpress.dao

import FoodExpress.infrastructure.DatabaseTestSetup
import FoodExpress.model.ItemVenta
import FoodExpress.model.ProductoVenta
import kotlin.test.*

/**
 * Tests de integración para [VentaDAO] + [RecetaDAO] usando MySQL real en Docker.
 *
 * Verifica:
 * - FIX SEC-006: Rollback cuando no hay stock suficiente
 * - Transacciones completas de venta
 * - Consistencia de datos tras múltiples ventas
 */
class VentaDAOIntegrationTest {

    private val ventaDAO = VentaDAO()

    @BeforeTest
    fun setUp() {
        DatabaseTestSetup.iniciar()  // Idempotente, solo inicia una vez
        DatabaseTestSetup.limpiarDatos()
        insertarDatosBase()
    }

    private fun insertarDatosBase() {
        DatabaseTestSetup.getTestConnection().use { conn ->
            // Insertar productos de materia prima con stock conocido
            conn.prepareStatement("""
                INSERT INTO productos (id_producto, nombre, unidades_paq, peso_libras, precio_venta, stock_actual, stock_minimo)
                VALUES (1, 'Pan de Hamburguesa', 12, 1.5, 0, 10, 5),
                       (2, 'Carne de Res', 1, 0.25, 0, 5, 2),
                       (3, 'Queso', 50, 2.0, 0, 20, 5)
            """.trimIndent()).use { it.executeUpdate() }

            // Insertar producto de venta
            conn.prepareStatement("""
                INSERT INTO productos_venta (id_producto_venta, nombre, precio_venta, categoria)
                VALUES (1, 'Hamburguesa Clasica', 35.0, 'Comida')
            """.trimIndent()).use { it.executeUpdate() }

            // Insertar receta (1 hamburguesa = 1 pan + 1 carne + 1 queso)
            conn.prepareStatement("""
                INSERT INTO receta_ingredientes (id_producto_venta, id_producto, cantidad, unidad)
                VALUES (1, 1, 1.0, 'Unidad'),
                       (1, 2, 1.0, 'Unidad'),
                       (1, 3, 1.0, 'Unidad')
            """.trimIndent()).use { it.executeUpdate() }
        }
    }

    private fun crearItem(cantidad: Int = 1): ItemVenta =
        ItemVenta(
            productoVenta = ProductoVenta(1, "Hamburguesa Clasica", 35.0, "Comida"),
            cantidad = cantidad
        )

    // ─────────────────────────────────────────────
    // Tests de venta exitosa
    // ─────────────────────────────────────────────

    @Test
    fun `venta exitosa retorna id positivo`() {
        val idVenta = ventaDAO.registrarVenta(listOf(crearItem(1)))
        assertTrue(idVenta > 0, "El ID de venta debe ser positivo")
    }

    @Test
    fun `venta exitosa descuenta stock correctamente`() {
        ventaDAO.registrarVenta(listOf(crearItem(2))) // 2 hamburguesas

        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT stock_actual FROM productos WHERE id_producto = ?").use { stmt ->
                stmt.setInt(1, 1) // Pan
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(8, rs.getInt("stock_actual")) // 10 - 2 = 8
            }
        }
    }

    @Test
    fun `venta con carrito vacio retorna -1`() {
        val idVenta = ventaDAO.registrarVenta(emptyList())
        assertEquals(-1, idVenta)
    }

    // ─────────────────────────────────────────────
    // FIX SEC-006: Rollback por stock insuficiente
    // ─────────────────────────────────────────────

    @Test
    fun `venta con stock insuficiente hace rollback completo`() {
        // Pedir 20 hamburguesas pero solo hay 5 carnes
        val idVenta = ventaDAO.registrarVenta(listOf(crearItem(20)))
        assertEquals(-1, idVenta, "Debe fallar por stock insuficiente")

        // Verificar que el stock NO fue modificado (rollback)
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT stock_actual FROM productos WHERE id_producto = 2").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(5, rs.getInt("stock_actual"), "Stock debe permanecer en 5 tras rollback")
            }
        }

        // Verificar que no se creó registro de venta
        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT COUNT(*) FROM ventas").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                assertEquals(0, rs.getInt(1), "No debe haber ventas registradas tras rollback")
            }
        }
    }

    @Test
    fun `multiples ventas decrementan stock acumuladamente`() {
        ventaDAO.registrarVenta(listOf(crearItem(1)))
        ventaDAO.registrarVenta(listOf(crearItem(2)))
        ventaDAO.registrarVenta(listOf(crearItem(1)))
        // Total: 4 hamburguesas = 4 panes, 4 carnes, 4 quesos

        DatabaseTestSetup.getTestConnection().use { conn ->
            conn.prepareStatement("SELECT id_producto, stock_actual FROM productos ORDER BY id_producto").use { stmt ->
                val rs = stmt.executeQuery()
                rs.next(); assertEquals(6, rs.getInt("stock_actual"))  // Pan: 10-4=6
                rs.next(); assertEquals(1, rs.getInt("stock_actual"))  // Carne: 5-4=1
                rs.next(); assertEquals(16, rs.getInt("stock_actual")) // Queso: 20-4=16
            }
        }
    }
}
