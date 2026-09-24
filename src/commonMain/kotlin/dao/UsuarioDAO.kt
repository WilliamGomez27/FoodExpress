package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.Usuario
import FastFoodApp.model.UsuarioSesion
import FastFoodApp.utils.AppLogger
import FastFoodApp.utils.HashUtils
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
     *
     * SEGURIDAD: Nunca se almacena la contraseña en texto plano.
     */
    fun insertar(usuario: Usuario): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false

        val salt = HashUtils.generarSalt()
        val hashContrasena = HashUtils.hashPassword(usuario.contrasena, salt)

        val sql = "INSERT INTO usuarios (nombre, usuario, contrasena, salt, rol) VALUES (?, ?, ?, ?, ?)"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, usuario.nombre)
                    stmt.setString(2, usuario.usuario)
                    stmt.setString(3, hashContrasena)
                    stmt.setString(4, salt)
                    stmt.setString(5, usuario.rol)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("UsuarioDAO", "Error al insertar usuario: ${e.message}")
            false
        }
    }
}
