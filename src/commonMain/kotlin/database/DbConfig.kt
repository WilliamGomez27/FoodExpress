package FoodExpress.database

/**
 * Configuración de conexión a la base de datos.
 *
 * SEGURIDAD FIX SEC-002 + SEC-008:
 * - Las credenciales NUNCA se hardcodean aquí.
 * - Este objeto es configurado en el punto de entrada de cada plataforma:
 *     · Desktop (Main.kt): lee de db.properties (ignorado por git)
 *     · Android (MainActivity.kt): lee de BuildConfig o recursos locales
 *
 * Uso:
 *   // Antes de llamar a ConexionDB.getConexion(), configurar:
 *   DbConfig.configure(host="192.168.1.10", puerto=3306, db="SuperMarket_db", user="app_user", password="secreto")
 */
object DbConfig {

    var host: String     = "localhost"
    var puerto: Int      = 3306
    var nombreDB: String = "SuperMarket_db"
    var usuario: String  = ""          // SIN valor por defecto — falla explícitamente si no se configura
    var password: String = ""
    var usarSSL: Boolean = false       // true en producción con certificado configurado

    private var configurado: Boolean = false

    /**
     * Configura la conexión a la BD. Llamar UNA VEZ al inicio de la app.
     * @throws IllegalArgumentException si los parámetros obligatorios están vacíos.
     */
    fun configure(
        host: String,
        puerto: Int,
        db: String,
        user: String,
        password: String,
        usarSSL: Boolean = false
    ) {
        require(host.isNotBlank())     { "DbConfig: 'host' no puede estar vacío" }
        require(db.isNotBlank())       { "DbConfig: 'db' no puede estar vacío" }
        require(user.isNotBlank())     { "DbConfig: 'user' no puede estar vacío. No uses root en producción." }

        this.host      = host
        this.puerto    = puerto
        this.nombreDB  = db
        this.usuario   = user
        this.password  = password
        this.usarSSL   = usarSSL
        this.configurado = true
    }

    /**
     * Verifica que la configuración fue inicializada antes de conectar.
     */
    fun verificar() {
        check(configurado) {
            "DbConfig no fue configurado. Llama DbConfig.configure(...) en Main.kt o MainActivity antes de usar la BD."
        }
    }
}
