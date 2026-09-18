package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.Usuario
import java.sql.SQLException

class UsuarioDAO {

    fun obtenerPorCredenciales(usuario: String, contrasena: String): Result<Usuario?> {
        val conexion = try {
            ConexionDB.getConexion() ?: return Result.failure(Exception("El driver MySQL retornó null, revisa logcat"))
        } catch (e: Throwable) {
            return Result.failure(Exception("Error de Conexión Fatal: ${e.message} - ${e::class.simpleName}"))
        }
        val sql = "SELECT id_usuario, nombre, usuario, contrasena, rol FROM usuarios WHERE usuario = ? AND contrasena = ?"
        return try {
            conexion.prepareStatement(sql).use { stmt ->
                stmt.setString(1, usuario)
                stmt.setString(2, contrasena)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    Result.success(
                        Usuario(
                            idUsuario = rs.getInt("id_usuario"),
                            nombre = rs.getString("nombre"),
                            usuario = rs.getString("usuario"),
                            contrasena = rs.getString("contrasena"),
                            rol = rs.getString("rol")
                        )
                    )
                } else {
                    Result.success(null)
                }
            }
        } catch (e: SQLException) {
            println("✗ [UsuarioDAO] Error al obtener usuario: ${e.message}")
            Result.failure(e)
        }
    }

    fun insertar(usuario: Usuario): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "INSERT INTO usuarios (nombre, usuario, contrasena, rol) VALUES (?, ?, ?, ?)"
        return try {
            conexion.prepareStatement(sql).use { stmt ->
                stmt.setString(1, usuario.nombre)
                stmt.setString(2, usuario.usuario)
                stmt.setString(3, usuario.contrasena)
                stmt.setString(4, usuario.rol)
                stmt.executeUpdate() > 0
            }
        } catch (e: SQLException) {
            println("✗ [UsuarioDAO] Error al insertar usuario: ${e.message}")
            false
        }
    }
}
