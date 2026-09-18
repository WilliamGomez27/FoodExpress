package FastFoodApp.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ConexionDB {
    private val isAndroid: Boolean by lazy {
        try {
            Class.forName("android.os.Build")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    /**
     * Host por defecto:
     * - En Android (Emulador): 10.0.2.2 para comunicarse con MySQL corriendo en la PC local.
     * - En Desktop JVM: localhost
     */
    var host: String = if (isAndroid) "192.168.40.58" else "localhost"
    var puerto: Int = 3306
    var nombreDB: String = "SuperMarket_db"
    var usuario: String = "root"
    var password: String = ""

    private val urlConnection: String
        get() = "jdbc:mysql://$host:$puerto/$nombreDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=5000"

    @Volatile
    private var conexion: Connection? = null

    @Synchronized
    fun getConexion(): Connection? {
        try {
            if (conexion == null || conexion!!.isClosed) {
                // Registrar driver clásico para la versión 5.1.x
                Class.forName("com.mysql.jdbc.Driver")
                conexion = DriverManager.getConnection(urlConnection, usuario, password)
                println("✓ [DB] Conexión establecida con éxito en $host:$puerto")
            }
        } catch (e: Throwable) {
            System.err.println("✗ [DB] Error fatal de conexión: ${e.message} - ${e::class.simpleName}")
            throw e
        }
        return conexion
    }

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
