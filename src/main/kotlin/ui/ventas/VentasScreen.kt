package FastFoodApp.ui.ventas

import FastFoodApp.model.ItemVenta
import FastFoodApp.model.ProductoVenta
import FastFoodApp.theme.AppColors
import FastFoodApp.viewmodel.VentasViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaVentas() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { VentasViewModel(scope) }
    val scaffoldState = rememberScaffoldState()
    var busqueda      by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.cargarDatos() }

    LaunchedEffect(vm.mensajeSnackbar) {
        vm.mensajeSnackbar?.let {
            scaffoldState.snackbarHostState.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(scaffoldState = scaffoldState, backgroundColor = AppColors.Surface) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(28.dp)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Text("Punto de Venta", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            Text("Selecciona productos y procesa la venta", fontSize = 13.sp, color = AppColors.TextMuted)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── CATÁLOGO (izquierda) ─────────────────────────────────────
                Column(modifier = Modifier.weight(1.4f).fillMaxHeight()) {

                    // Buscador
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        placeholder = { Text("Buscar producto...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = AppColors.Primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista de productos
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        elevation = 0.dp,
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedBorder,
                        backgroundColor = AppColors.CardBg
                    ) {
                        val filtrados = vm.catalogoProductos.filter {
                            busqueda.isBlank() || it.nombre.contains(busqueda, ignoreCase = true)
                        }
                        if (vm.cargando && vm.catalogoProductos.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = AppColors.Primary)
                            }
                        } else {
                            LazyColumn {
                                items(filtrados) { producto ->
                                    CatalogoFila(
                                        producto = producto,
                                        enCarrito = vm.carrito.find { it.productoVenta.idProductoVenta == producto.idProductoVenta }?.cantidad ?: 0,
                                        onAgregar = { vm.agregarAlCarrito(producto) }
                                    )
                                    Divider(color = AppColors.Divider, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // ── CARRITO (derecha) ─────────────────────────────────────────
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedBorder,
                    backgroundColor = AppColors.CardBg
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛒 Carrito", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            if (vm.carrito.isNotEmpty()) {
                                TextButton(onClick = { vm.limpiarCarrito() }) {
                                    Text("Limpiar", color = AppColors.Danger, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (vm.carrito.isEmpty()) {
                            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛒", fontSize = 36.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Carrito vacío", color = AppColors.TextMuted, fontSize = 13.sp)
                                    Text("Agrega productos del catálogo", color = AppColors.TextMuted, fontSize = 11.sp)
                                }
                            }
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(vm.carrito) { item ->
                                    CarritoFila(
                                        item = item,
                                        onReducir  = { vm.reducirDelCarrito(item.productoVenta) },
                                        onAgregar  = { vm.agregarAlCarrito(item.productoVenta) },
                                        onEliminar = { vm.eliminarDelCarrito(item.productoVenta) }
                                    )
                                    Divider(color = AppColors.Divider, thickness = 0.5.dp)
                                }
                            }
                        }

                        // ── Totales y botón ──────────────────────────────────
                        Divider(color = AppColors.Divider)
                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("${vm.cantidadItems} items", color = AppColors.TextMuted, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text("Q %.2f".format(vm.totalCarrito), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppColors.TextPrimary)
                        }
                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = { vm.procesarVenta() },
                            enabled = !vm.cargando && vm.carrito.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Success)
                        ) {
                            if (vm.cargando) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = AppColors.White)
                            } else {
                                Text("Cobrar  Q %.2f".format(vm.totalCarrito), color = AppColors.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Fila del catálogo ─────────────────────────────────────────────────────────
@Composable
fun CatalogoFila(producto: ProductoVenta, enCarrito: Int, onAgregar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(producto.nombre, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
            Text("${producto.categoria}  •  Q%.2f".format(producto.precioVenta), fontSize = 11.sp, color = AppColors.TextMuted)
        }
        if (enCarrito > 0) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AppColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("$enCarrito", color = AppColors.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }
        IconButton(
            onClick = onAgregar,
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.Primary.copy(0.1f))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = AppColors.Primary, modifier = Modifier.size(18.dp))
        }
    }
}

// ── Fila del carrito ──────────────────────────────────────────────────────────
@Composable
fun CarritoFila(item: ItemVenta, onReducir: () -> Unit, onAgregar: () -> Unit, onEliminar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productoVenta.nombre, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text("Q%.2f".format(item.subtotal), fontSize = 12.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold)
        }
        // Controles de cantidad
        Row(verticalAlignment = Alignment.CenterVertically) {
            SmallControlButton("-", onClick = onReducir)
            Text("${item.cantidad}", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            SmallControlButton("+", onClick = onAgregar)
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onEliminar, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AppColors.Danger, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun SmallControlButton(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(AppColors.Divider)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
    }
}
