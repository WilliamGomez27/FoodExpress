package FoodExpress.model

data class Gasto(
    val idGasto: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fechaHora: String = ""
)
