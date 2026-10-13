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
            try {
                Class.forName("org.mariadb.jdbc.Driver")
            } catch (_: Throwable) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver")
                } catch (_: Throwable) {
                    Class.forName("com.mysql.jdbc.Driver")
                }
            }

            val ssl = if (DbConfig.usarSSL) "true" else "false"
            val host = DbConfig.host
            val puerto = DbConfig.puerto
            val db = DbConfig.nombreDB
            val params = "?useSSL=$ssl" +
                    "&serverTimezone=UTC" +
                    "&allowPublicKeyRetrieval=${!DbConfig.usarSSL}" +
                    "&characterEncoding=UTF-8" +
                    "&connectTimeout=5000" +
                    "&socketTimeout=5000"

            val conn = try {
                DriverManager.getConnection("jdbc:mariadb://$host:$puerto/$db$params", DbConfig.usuario, DbConfig.password)
            } catch (_: Throwable) {
                DriverManager.getConnection("jdbc:mysql://$host:$puerto/$db$params", DbConfig.usuario, DbConfig.password)
            }
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
