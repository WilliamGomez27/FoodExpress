package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.Gasto
import FastFoodApp.utils.AppLogger
import java.sql.SQLException

class GastoDAO {

    fun registrarGasto(gasto: Gasto): Boolean {
        val conexion = ConexionDB.getConexion() ?: return false
        val sql = "INSERT INTO gastos (descripcion, monto, fecha_hora) VALUES (?, ?, NOW())"
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, gasto.descripcion)
                    stmt.setDouble(2, gasto.monto)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            AppLogger.error("GastoDAO", "Error al registrar gasto: ${e.message}")
            false
        }
    }

    fun obtenerGastosMesActual(): List<Gasto> {
        val conexion = ConexionDB.getConexion() ?: return emptyList()
        val sql = """
            SELECT id_gasto, descripcion, monto, DATE_FORMAT(fecha_hora, '%d/%m/%Y %H:%i') as fecha 
            FROM gastos 
            WHERE MONTH(fecha_hora) = MONTH(NOW()) AND YEAR(fecha_hora) = YEAR(NOW())
            ORDER BY fecha_hora DESC
        """
        val lista = mutableListOf<Gasto>()
        return try {
            conexion.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        lista.add(
                            Gasto(
                                idGasto = rs.getInt("id_gasto"),
                                descripcion = rs.getString("descripcion"),
                                monto = rs.getDouble("monto"),
                                fechaHora = rs.getString("fecha")
                            )
                        )
                    }
                }
            }
            lista
        } catch (e: SQLException) {
            AppLogger.error("GastoDAO", "Error al obtener gastos: ${e.message}")
            emptyList()
        }
    }
}
