package FoodExpress.database

import FoodExpress.utils.AppLogger
import java.sql.Connection
import java.sql.DriverManager

/**
 * Singleton de conexión a MySQL.
 *
 * SEGURIDAD FIX SEC-002: Las credenciales se leen de [DbConfig].
 * SEGURIDAD FIX SEC-004: SSL configurable via DbConfig.usarSSL.
 */
object ConexionDB {

    /**
     * Obtiene una nueva conexión a MySQL.
     * Es responsabilidad de quien llama cerrar la conexión usando .use { } o .close().
     */
    fun getConexion(): Connection? {
        DbConfig.verificar()

        return try {
            Class.forName("com.mysql.jdbc.Driver")

            val ssl = if (DbConfig.usarSSL) "true" else "false"
            val jdbcUrl = "jdbc:mysql://${DbConfig.host}:${DbConfig.puerto}/${DbConfig.nombreDB}" +
                    "?useSSL=$ssl" +
                    "&serverTimezone=UTC" +
                    "&allowPublicKeyRetrieval=${!DbConfig.usarSSL}" +
                    "&characterEncoding=UTF-8" +
                    "&connectTimeout=5000" +
                    "&socketTimeout=5000"

            val conn = DriverManager.getConnection(jdbcUrl, DbConfig.usuario, DbConfig.password)
            // AppLogger.info("ConexionDB", "Nueva conexión establecida en ${DbConfig.host}:${DbConfig.puerto}")
            conn
        } catch (e: Exception) {
            AppLogger.error("ConexionDB", "Error al conectar: ${e.message}")
            null
        }
    }

    fun cerrarConexion() {
        // Obsoleto: Cada DAO cierra su propia conexión con .use { }
    }
}
