package FastFoodApp

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ConexionDB {
    // Configuración de la base de datos
    private const val URL      = "jdbc:mysql://localhost:3306/SuperMarket_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    private const val USUARIO  = "root"
    private const val PASSWORD = ""

    @Volatile
    private var conexion: Connection? = null

    // Sincronizado para evitar condiciones de carrera en ambiente multi-hilo
    @Synchronized
    fun getConexion(): Connection? {
        return try {
            if (conexion == null || conexion!!.isClosed) {
                Class.forName("com.mysql.cj.jdbc.Driver")
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD)
                println("✓ [DB] Conexión establecida con éxito")
            }
            conexion
        } catch (e: ClassNotFoundException) {
            System.err.println("✗ [DB] Driver MySQL no encontrado: ${e.message}")
            null
        } catch (e: SQLException) {
            System.err.println("✗ [DB] Error al conectar: ${e.message}")
            null
        }
    }

    /**
     * Cierra la conexión de forma segura al cerrar la aplicación.
     */
    @Synchronized
    fun cerrarConexion() {
        conexion?.let {
            if (!it.isClosed) {
                it.close()
                println("✓ [DB] Conexión cerrada correctamente")
            }
        }
        conexion = null
    }
}