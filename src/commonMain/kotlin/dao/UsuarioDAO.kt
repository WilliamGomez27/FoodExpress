package FoodExpress.dao

import FoodExpress.database.ConexionDB
import FoodExpress.model.Usuario
import FoodExpress.model.UsuarioSesion
import FoodExpress.utils.AppLogger
import FoodExpress.utils.HashUtils
import java.sql.SQLException

class UsuarioDAO {

    /**
     * Autentica un usuario verificando el hash SHA-256 de su contraseña.
     *
     * SEGURIDAD FIX SEC-001:
     * - La BD almacena hash + salt, NO la contraseña en texto claro.
     * - El hash se verifica en código (tiempo constante), no con WHERE en SQL.
     * - Retorna [UsuarioSesion] sin campo contraseña (FIX SEC-007).
     */
    fun obtenerPorCredenciales(usuario: String, contrasena: String): Result<UsuarioSesion?> {
        val conexion = try {
            ConexionDB.getConexion()
        } catch (e: Throwable) {
            return Result.failure(Exception("Error de Conexión Fatal: ${e.message}"))
        }

        if (conexion == null) {
            // FIX para debugging profundo en Android: saber qué excepción silenciosa mató la conexión
            return Result.failure(Exception("El Driver MySQL retornó null silenciosamente. Asegúrate que Android tenga permisos de INTERNET y la IP sea alcanzable."))
        }

        // Recuperar por username solamente — el hash se verifica en código
        val sql = """
            SELECT id_usuario, nombre, usuario, contrasena, salt, rol
            FROM usuarios
            WHERE usuario = ?
        """
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, usuario)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        val hashAlmacenado = rs.getString("contrasena")
                        val salt           = rs.getString("salt") ?: ""

                        // Verificación en tiempo constante (anti timing-attack)
                        val esValido = HashUtils.verificarPassword(contrasena, hashAlmacenado, salt)

                        if (esValido) {
                            Result.success(
                                UsuarioSesion(
                                    idUsuario = rs.getInt("id_usuario"),
                                    nombre    = rs.getString("nombre"),
                                    usuario   = rs.getString("usuario"),
                                    rol       = rs.getString("rol")
                                    // 'contrasena' NO se incluye en UsuarioSesion
                                )
                            )
                        } else {
                            Result.success(null) // Credenciales inválidas
                        }
                    } else {
                        Result.success(null) // Usuario no existe
                    }
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("UsuarioDAO", "Error al autenticar usuario: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Inserta un nuevo usuario con hash de contraseña y salt generado automáticamente.
     * Incluye datos de recuperación (email, whatsapp).
     */
    fun insertar(usuario: Usuario): Result<Boolean> {
        val conexion = try {
            ConexionDB.getConexion()
        } catch (e: Throwable) {
            return Result.failure(Exception("Error Fatal: ${e.message}"))
        }
        if (conexion == null) return Result.failure(Exception("Sin conexión"))

        val salt = HashUtils.generarSalt()
        val hashContrasena = HashUtils.hashPassword(usuario.contrasena, salt)

        val sql = "INSERT INTO usuarios (nombre, usuario, contrasena, salt, rol, email, whatsapp) VALUES (?, ?, ?, ?, ?, ?, ?)"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, usuario.nombre)
                    stmt.setString(2, usuario.usuario)
                    stmt.setString(3, hashContrasena)
                    stmt.setString(4, salt)
                    stmt.setString(5, usuario.rol)
                    stmt.setString(6, usuario.email.ifBlank { null })
                    stmt.setString(7, usuario.whatsapp.ifBlank { null })
                    val filas = stmt.executeUpdate()
                    Result.success(filas > 0)
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("UsuarioDAO", "Error al insertar usuario: ${e.message}")
            if (e.message?.contains("Duplicate entry") == true) {
                Result.failure(Exception("El usuario, email o WhatsApp ya están registrados."))
            } else {
                Result.failure(Exception("Error de base de datos."))
            }
        }
    }

    /**
     * Verifica si existe un usuario por su email o whatsapp y le genera una clave temporal.
     * Retorna la nueva clave temporal si existe, o null si no se encontró el usuario.
     */
    fun generarClaveTemporal(contacto: String): Result<String?> {
        val conexion = try { ConexionDB.getConexion() } catch (e: Throwable) { return Result.failure(e) }
        if (conexion == null) return Result.failure(Exception("Sin conexión"))

        val sqlBusqueda = "SELECT id_usuario, usuario FROM usuarios WHERE email = ? OR whatsapp = ?"
        
        return try {
            conexion.use { conn ->
                // 1. Buscar usuario
                var idUsuarioEncontrado = -1
                conn.prepareStatement(sqlBusqueda).use { stmt ->
                    stmt.setString(1, contacto)
                    stmt.setString(2, contacto)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        idUsuarioEncontrado = rs.getInt("id_usuario")
                    }
                }

                // 2. Si existe, generar nueva clave, hashearla y guardarla
                if (idUsuarioEncontrado != -1) {
                    // Generar clave temporal de 6 caracteres aleatorios alfanuméricos
                    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                    val claveTemp = (1..6).map { chars.random() }.joinToString("")
                    
                    val nuevoSalt = HashUtils.generarSalt()
                    val nuevoHash = HashUtils.hashPassword(claveTemp, nuevoSalt)
                    
                    val sqlUpdate = "UPDATE usuarios SET contrasena = ?, salt = ? WHERE id_usuario = ?"
                    conn.prepareStatement(sqlUpdate).use { stmt ->
                        stmt.setString(1, nuevoHash)
                        stmt.setString(2, nuevoSalt)
                        stmt.setInt(3, idUsuarioEncontrado)
                        stmt.executeUpdate()
                    }
                    Result.success(claveTemp)
                } else {
                    Result.success(null) // Usuario no encontrado
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("UsuarioDAO", "Error en recuperación: ${e.message}")
            Result.failure(e)
        }
    }
}
