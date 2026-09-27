package FoodExpress

import FoodExpress.model.ItemVenta
import FoodExpress.model.Producto
import FoodExpress.model.ProductoVenta
import kotlin.test.*

/**
 * Pruebas unitarias para la lógica de negocio de ventas.
 * Prueba el modelo [ItemVenta] y cálculos del carrito sin necesidad de base de datos.
 */
class VentaLogicaTest {

    // ─────────────────────────────────────────────
    // Fixtures reutilizables
    // ─────────────────────────────────────────────

    private fun crearProductoVenta(
        id: Int = 1,
        nombre: String = "Hamburguesa Clásica",
        precio: Double = 35.0,
        categoria: String = "Comida"
    ) = ProductoVenta(
        idProductoVenta = id,
        nombre = nombre,
        precioVenta = precio,
        categoria = categoria
    )

    // ─────────────────────────────────────────────
    // ItemVenta — cálculo de subtotal
    // ─────────────────────────────────────────────

    @Test
    fun `subtotal es precio por cantidad para un item`() {
        val item = ItemVenta(crearProductoVenta(precio = 35.0), cantidad = 1)
        assertEquals(35.0, item.subtotal, 0.001)
    }

    @Test
    fun `subtotal es precio multiplicado por cantidad mayor a 1`() {
        val item = ItemVenta(crearProductoVenta(precio = 35.0), cantidad = 3)
        assertEquals(105.0, item.subtotal, 0.001)
    }

    @Test
    fun `subtotal con precio decimal es correcto`() {
        val item = ItemVenta(crearProductoVenta(precio = 15.50), cantidad = 2)
        assertEquals(31.0, item.subtotal, 0.001)
    }

    @Test
    fun `subtotal con cantidad cero es cero`() {
        val item = ItemVenta(crearProductoVenta(precio = 35.0), cantidad = 0)
        assertEquals(0.0, item.subtotal, 0.001)
    }

    // ─────────────────────────────────────────────
    // Lógica de carrito (simulada sin ViewModel)
    // ─────────────────────────────────────────────

    @Test
    fun `total carrito es suma de subtotales`() {
        val carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1, precio = 35.0), cantidad = 2),  // 70.0
            ItemVenta(crearProductoVenta(id = 2, nombre = "Papas Fritas", precio = 15.0), cantidad = 3) // 45.0
        )
        val totalCalculado = carrito.sumOf { it.subtotal }
        assertEquals(115.0, totalCalculado, 0.001)
    }

    @Test
    fun `carrito vacio tiene total cero`() {
        val carrito = emptyList<ItemVenta>()
        val total = carrito.sumOf { it.subtotal }
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun `cantidad de items en carrito es suma de cantidades`() {
        val carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 2),
            ItemVenta(crearProductoVenta(id = 2, nombre = "Papas"), cantidad = 3)
        )
        val cantidadTotal = carrito.sumOf { it.cantidad }
        assertEquals(5, cantidadTotal)
    }

    @Test
    fun `agregar producto existente incrementa cantidad`() {
        var carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 1)
        )
        val producto = crearProductoVenta(id = 1)

        // Simular lógica de agregarAlCarrito del VentasViewModel
        val existente = carrito.find { it.productoVenta.idProductoVenta == producto.idProductoVenta }
        carrito = if (existente != null) {
            carrito.map {
                if (it.productoVenta.idProductoVenta == producto.idProductoVenta)
                    it.copy(cantidad = it.cantidad + 1)
                else it
            }
        } else {
            carrito + ItemVenta(producto, 1)
        }

        assertEquals(1, carrito.size)
        assertEquals(2, carrito.first().cantidad)
    }

    @Test
    fun `agregar producto nuevo lo aniade al carrito`() {
        var carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 1)
        )
        val nuevoProd = crearProductoVenta(id = 2, nombre = "Papas Fritas")

        val existente = carrito.find { it.productoVenta.idProductoVenta == nuevoProd.idProductoVenta }
        carrito = if (existente != null) {
            carrito.map {
                if (it.productoVenta.idProductoVenta == nuevoProd.idProductoVenta)
                    it.copy(cantidad = it.cantidad + 1)
                else it
            }
        } else {
            carrito + ItemVenta(nuevoProd, 1)
        }

        assertEquals(2, carrito.size)
    }

    @Test
    fun `reducir item a cero lo elimina del carrito`() {
        var carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 1),
            ItemVenta(crearProductoVenta(id = 2, nombre = "Papas"), cantidad = 2)
        )

        // Simular lógica de reducirDelCarrito
        val producto = crearProductoVenta(id = 1)
        carrito = carrito
            .map {
                if (it.productoVenta.idProductoVenta == producto.idProductoVenta)
                    it.copy(cantidad = it.cantidad - 1)
                else it
            }
            .filter { it.cantidad > 0 }

        assertEquals(1, carrito.size)
        assertEquals("Papas", carrito.first().productoVenta.nombre)
    }

    @Test
    fun `eliminar producto del carrito lo remueve`() {
        var carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 2),
            ItemVenta(crearProductoVenta(id = 2, nombre = "Papas"), cantidad = 1)
        )

        carrito = carrito.filter { it.productoVenta.idProductoVenta != 1 }

        assertEquals(1, carrito.size)
        assertEquals(2, carrito.first().productoVenta.idProductoVenta)
    }

    @Test
    fun `limpiar carrito resulta en lista vacia`() {
        var carrito = listOf(
            ItemVenta(crearProductoVenta(id = 1), cantidad = 3)
        )
        carrito = emptyList()
        assertTrue(carrito.isEmpty())
    }

    // ─────────────────────────────────────────────
    // Lógica de inventario
    // ─────────────────────────────────────────────

    @Test
    fun `producto con stock menor que minimo esta en alerta`() {
        val producto = Producto(
            idProducto = 1,
            nombre = "Pan",
            unidadesPaq = 12,
            pesoLibras = 1.5,
            precioVenta = 0.0,
            stockActual = 3,
            stockMinimo = 20
        )
        assertTrue(producto.stockActual < producto.stockMinimo)
    }

    @Test
    fun `producto con stock igual al minimo no esta en alerta critica`() {
        val producto = Producto(
            idProducto = 2,
            nombre = "Carne",
            unidadesPaq = 1,
            pesoLibras = 0.25,
            precioVenta = 0.0,
            stockActual = 15,
            stockMinimo = 15
        )
        assertFalse(producto.stockActual < producto.stockMinimo)
    }
}
