package FastFoodApp.repository

import FastFoodApp.model.ProductoVenta

object CatalogoVentas {

    val productosTerminados = listOf(
        ProductoVenta(1, "Hamburguesa Clásica", 35.0, "Comida"),
        ProductoVenta(2, "Hamburguesa Doble", 45.0, "Comida"),
        ProductoVenta(3, "Papas Fritas Medianas", 15.0, "Acompañamiento"),
        ProductoVenta(4, "Papas Fritas Grandes", 20.0, "Acompañamiento"),
        ProductoVenta(5, "Gaseosa Cola 16oz", 12.0, "Bebida"),
        ProductoVenta(6, "Gaseosa Naranja 16oz", 12.0, "Bebida"),
        ProductoVenta(7, "Combo Hamburguesa Clásica", 55.0, "Combos"),
        ProductoVenta(8, "Nuggets de Pollo (6 pz)", 25.0, "Comida"),
        ProductoVenta(9, "Helado Cono", 10.0, "Postre")
    )

    fun obtenerCatalogo(): List<ProductoVenta> {
        return productosTerminados
    }
}
