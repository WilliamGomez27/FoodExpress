package FoodExpress.model

data class ArqueoDiario(
    val fecha: String,
    val totalVentas: Double,
    val totalMontoVentas: Double,
    val productosVendidos: List<ProductoVendidoResumen>,
    val materiaPrimaConsumida: List<MateriaPrimaConsumidaResumen>
)

data class ProductoVendidoResumen(
    val nombre: String,
    val cantidadVendida: Int
)

data class MateriaPrimaConsumidaResumen(
    val nombre: String,
    val cantidadConsumida: Double,
    val unidad: String
)

data class CierreMensual(
    val mesAnio: String,
    val ingresosBrutos: Double,
    val gastosTotales: Double,
    val gananciasNetas: Double,
    val estadoInventario: List<EstadoInventario>
)

data class EstadoInventario(
    val nombre: String,
    val stockActual: Int,
    val stockMinimo: Int
)
