package FastFoodApp.dao

import FastFoodApp.database.ConexionDB
import FastFoodApp.model.*
import java.sql.SQLException

class ReporteDAO {

    fun obtenerArqueoDiario(fecha: String): ArqueoDiario? {
        val conexion = ConexionDB.getConexion() ?: return null
        
        var totalVentas = 0.0
        var cantVentas = 0.0
        val prodVendidos = mutableListOf<ProductoVendidoResumen>()
        val materiaPrima = mutableListOf<MateriaPrimaConsumidaResumen>()

        try {
            // 1. Totales
            val sqlTotales = "SELECT IFNULL(SUM(total_venta), 0) as total, COUNT(id_venta) as cant FROM ventas WHERE DATE(fecha_hora) = ?"
            conexion.prepareStatement(sqlTotales).use { stmt ->
                stmt.setString(1, fecha)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    totalVentas = rs.getDouble("total")
                    cantVentas = rs.getDouble("cant")
                }
            }

            // 2. Productos vendidos
            val sqlProd = """
                SELECT pv.nombre, SUM(dv.cantidad) as cant
                FROM detalle_ventas dv
                JOIN ventas v ON dv.id_venta = v.id_venta
                JOIN productos_venta pv ON dv.id_producto_venta = pv.id_producto_venta
                WHERE DATE(v.fecha_hora) = ?
                GROUP BY pv.nombre
            """
            conexion.prepareStatement(sqlProd).use { stmt ->
                stmt.setString(1, fecha)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    prodVendidos.add(ProductoVendidoResumen(rs.getString("nombre"), rs.getInt("cant")))
                }
            }

            // 3. Materia prima consumida (Basada en recetas)
            val sqlMateria = """
                SELECT p.nombre, SUM(dv.cantidad * ri.cantidad) as consumido, ri.unidad
                FROM detalle_ventas dv
                JOIN ventas v ON dv.id_venta = v.id_venta
                JOIN (
                    SELECT DISTINCT id_producto_venta, id_producto, cantidad, unidad
                    FROM receta_ingredientes
                ) ri ON dv.id_producto_venta = ri.id_producto_venta
                JOIN productos p ON ri.id_producto = p.id_producto
                WHERE DATE(v.fecha_hora) = ?
                GROUP BY p.nombre, ri.unidad
            """
            conexion.prepareStatement(sqlMateria).use { stmt ->
                stmt.setString(1, fecha)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    materiaPrima.add(MateriaPrimaConsumidaResumen(
                        nombre = rs.getString("nombre"),
                        cantidadConsumida = rs.getDouble("consumido"),
                        unidad = rs.getString("unidad")
                    ))
                }
            }

            return ArqueoDiario(
                fecha = fecha,
                totalVentas = cantVentas,
                totalMontoVentas = totalVentas,
                productosVendidos = prodVendidos,
                materiaPrimaConsumida = materiaPrima
            )
        } catch (e: SQLException) {
            println("✗ [ReporteDAO] Error en obtenerArqueoDiario: ${e.message}")
            return null
        }
    }

    fun obtenerCierreMensual(mes: Int, anio: Int): CierreMensual? {
        val conexion = ConexionDB.getConexion() ?: return null
        
        var ingresos = 0.0
        var gastos = 0.0
        val inventario = mutableListOf<EstadoInventario>()

        try {
            // Ingresos
            val sqlIng = "SELECT IFNULL(SUM(total_venta), 0) FROM ventas WHERE MONTH(fecha_hora) = ? AND YEAR(fecha_hora) = ?"
            conexion.prepareStatement(sqlIng).use { stmt ->
                stmt.setInt(1, mes)
                stmt.setInt(2, anio)
                val rs = stmt.executeQuery()
                if (rs.next()) ingresos = rs.getDouble(1)
            }

            // Gastos
            val sqlGast = "SELECT IFNULL(SUM(monto), 0) FROM gastos WHERE MONTH(fecha_hora) = ? AND YEAR(fecha_hora) = ?"
            conexion.prepareStatement(sqlGast).use { stmt ->
                stmt.setInt(1, mes)
                stmt.setInt(2, anio)
                val rs = stmt.executeQuery()
                if (rs.next()) gastos = rs.getDouble(1)
            }

            // Inventario Actual
            val sqlInv = "SELECT nombre, stock_actual, stock_minimo FROM productos ORDER BY nombre"
            conexion.prepareStatement(sqlInv).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    inventario.add(EstadoInventario(
                        nombre = rs.getString("nombre"),
                        stockActual = rs.getInt("stock_actual"),
                        stockMinimo = rs.getInt("stock_minimo")
                    ))
                }
            }

            return CierreMensual(
                mesAnio = "$mes/$anio",
                ingresosBrutos = ingresos,
                gastosTotales = gastos,
                gananciasNetas = ingresos - gastos,
                estadoInventario = inventario
            )

        } catch (e: SQLException) {
            println("✗ [ReporteDAO] Error en obtenerCierreMensual: ${e.message}")
            return null
        }
    }
}
