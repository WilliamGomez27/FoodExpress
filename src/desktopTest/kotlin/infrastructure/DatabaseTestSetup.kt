package FastFoodApp.infrastructure

import FastFoodApp.database.DbConfig
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager
import java.sql.Connection

/**
 * Singleton que levanta un contenedor MySQL 8.0 con Docker para tests de integración.
 *
 * - El contenedor se inicia una sola vez para toda la suite de tests.
 * - El schema se carga automáticamente desde database.sql.
 * - Cada test puede limpiar sus datos sin afectar otros.
 *
 * Pre-requisito: Docker Desktop corriendo en la máquina.
 */
object DatabaseTestSetup {

    private val mysql: MySQLContainer<*> by lazy {
        MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("SuperMarket_test")
            .withUsername("test_user")
            .withPassword("test_password_123")
            .withInitScript("database_test.sql")  // Schema cargado al iniciar
    }

    /**
     * Inicia el contenedor y configura DbConfig para apuntar a él.
     * Idempotente — seguro de llamar múltiples veces.
     */
    fun iniciar() {
        if (!mysql.isRunning) {
            mysql.start()
        }
        DbConfig.configure(
            host     = mysql.host,
            puerto   = mysql.getMappedPort(3306),
            db       = "SuperMarket_test",
            user     = "test_user",
            password = "test_password_123",
            usarSSL  = false
        )
        Class.forName("com.mysql.jdbc.Driver")
    }

    /** Obtiene una conexión directa al contenedor para setup/teardown de tests. */
    fun getTestConnection(): Connection {
        return DriverManager.getConnection(
            mysql.jdbcUrl,
            mysql.username,
            mysql.password
        )
    }

    /** Limpia todas las tablas de datos (sin eliminar el schema). */
    fun limpiarDatos() {
        getTestConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0")
                listOf(
                    "detalle_ventas", "ventas", "historial_inventario",
                    "receta_ingredientes", "gastos", "productos_venta",
                    "productos", "usuarios"
                ).forEach { tabla ->
                    stmt.execute("TRUNCATE TABLE $tabla")
                }
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1")
            }
        }
    }

    fun detener() {
        if (mysql.isRunning) mysql.stop()
    }
}
