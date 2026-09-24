package FastFoodApp.database

import FastFoodApp.utils.AppLogger
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Singleton de conexión a MySQL.
 *
 * SEGURIDAD FIX SEC-002: Las credenciales se leen de [DbConfig].
 * SEGURIDAD FIX SEC-004: SSL configurable via DbConfig.usarSSL.
 */
object ConexionDB {

    @Volatile
    private var conexion: Connection? = null

    /**
     * Obtiene la conexión activa o crea una nueva si está cerrada.
     */
    @Synchronized
    fun getConexion(): Connection? {
        DbConfig.verificar()

        return try {
            if (conexion == null || conexion!!.isClosed) {
                // Registrar driver clásico para la versión 5.1.x
                Class.forName("com.mysql.jdbc.Driver")

                val ssl = if (DbConfig.usarSSL) "true" else "false"
                val jdbcUrl = "jdbc:mysql://${DbConfig.host}:${DbConfig.puerto}/${DbConfig.nombreDB}" +
                        "?useSSL=$ssl" +
                        "&serverTimezone=UTC" +
                        "&allowPublicKeyRetrieval=${!DbConfig.usarSSL}" +
                        "&characterEncoding=UTF-8" +
                        "&connectTimeout=5000" +
                        "&socketTimeout=5000"

                conexion = DriverManager.getConnection(jdbcUrl, DbConfig.usuario, DbConfig.password)
                AppLogger.info("ConexionDB", "Conexión establecida con éxito en ${DbConfig.host}:${DbConfig.puerto}")
            }
            conexion
        } catch (e: ClassNotFoundException) {
            AppLogger.error("ConexionDB", "Driver MySQL no encontrado: ${e.message}")
            throw Exception("ClassNotFoundException: ${e.message}")
        } catch (e: SQLException) {
            AppLogger.error("ConexionDB", "Error SQL al conectar: ${e.message}")
            throw Exception("SQLException: ${e.message}")
        } catch (e: Throwable) {
            AppLogger.error("ConexionDB", "Error Fatal al conectar: ${e.message} - ${e::class.simpleName}")
            throw Exception("Throwable: ${e.message}")
        }
    }

    @Synchronized
    fun cerrarConexion() {
        try {
            conexion?.let {
                if (!it.isClosed) {
                    it.close()
                    AppLogger.info("ConexionDB", "Conexión cerrada correctamente")
                }
            }
        } catch (e: Exception) {
            AppLogger.error("ConexionDB", "Error al cerrar conexión: ${e.message}")
        } finally {
            conexion = null
        }
    }
}
