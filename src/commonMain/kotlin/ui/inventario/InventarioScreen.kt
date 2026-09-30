package FoodExpress.ui.inventario

import FoodExpress.theme.AppColors
import FoodExpress.viewmodel.InventarioViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import FoodExpress.model.Producto

@Composable
fun PantallaInventario() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { InventarioViewModel(scope) }
    val scaffoldState = rememberScaffoldState()
    var expandido     by remember { mutableStateOf("") }
    var mostrarDialogoNuevoProducto by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(Unit) { vm.cargarProductos() }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Inventario",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Gestión y movimientos de productos",
                        fontSize = 13.sp,
                        color = AppColors.TextMuted
                    )
                }
                Button(
                    onClick = { mostrarDialogoNuevoProducto = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) {
                    Text("Nuevo Producto", color = AppColors.White, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            val stockBajo = vm.productos.count { it.stockActual <= it.stockMinimo }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    titulo = "TOTAL PRODUCTOS",
                    valor  = "${vm.productos.size}",
                    color  = AppColors.Primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "STOCK BAJO ⚠️",
                    valor  = "$stockBajo",
                    color  = if (stockBajo > 0) AppColors.Danger else AppColors.Success,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "VALOR TOTAL EST.",
                    valor  = "COP %.2f".format(vm.productos.sumOf { it.precioVenta * it.stockActual }),
                    color  = AppColors.Success,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1.6f).fillMaxHeight(),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedBorder,
                    backgroundColor = AppColors.CardBg
                ) {
                    Column(modifier = Modifier.padding(0.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppColors.SidebarBg)
                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "Catálogo de Productos",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.White
                            )
                        }

                        if (vm.cargando && vm.productos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = AppColors.Primary) }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 250.dp),
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(vm.productos) { producto ->
                                    ProductoCard(
                                        producto = producto,
                                        isSelected = vm.productoSel?.idProducto == producto.idProducto,
                                        onClick = { vm.productoSel = producto },
                                        onEdit = { productoAEditar = producto },
                                        onDelete = { vm.eliminarProducto(producto.idProducto) }
                                    )
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedBorder,
                    backgroundColor = AppColors.CardBg
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Registrar Movimiento", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Selecciona un producto de la tabla", fontSize = 12.sp, color = AppColors.TextMuted)
                        Spacer(modifier = Modifier.height(20.dp))

                        Text("Producto", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AppColors.TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.Primary.copy(alpha = 0.08f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = vm.productoSel?.nombre ?: "Ninguno seleccionado",
                                fontSize = 14.sp,
                                color = if (vm.productoSel != null) AppColors.Primary else AppColors.TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Motivo", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AppColors.TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            OutlinedButton(
                                onClick = { expandido = "motivo" },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(vm.motivo, modifier = Modifier.weight(1f))
                            }
                            DropdownMenu(
                                expanded = expandido == "motivo",
                                onDismissRequest = { expandido = "" }
                            ) {
                                vm.motivos.forEach { m ->
                                    DropdownMenuItem(onClick = { vm.motivo = m; expandido = "" }) {
                                        Text(m)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Cantidad", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AppColors.TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = vm.cantidad,
                            onValueChange = { vm.cantidad = it },
                            placeholder = { Text("Ej: 10") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = AppColors.Primary
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { vm.registrarMovimiento() },
                            enabled = !vm.cargando,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                        ) {
                            if (vm.cargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = AppColors.White
                                )
                            } else {
                                Text("Procesar Movimiento", color = AppColors.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevoProducto || productoAEditar != null) {
        val esEdicion = productoAEditar != null
        var nombre by remember { mutableStateOf(productoAEditar?.nombre ?: "") }
        var unidadesPaq by remember { mutableStateOf(productoAEditar?.unidadesPaq?.toString() ?: "") }
        var pesoLibras by remember { mutableStateOf(productoAEditar?.pesoLibras?.toString() ?: "") }
        var precioVenta by remember { mutableStateOf(productoAEditar?.precioVenta?.toString() ?: "") }
        var stockActual by remember { mutableStateOf(productoAEditar?.stockActual?.toString() ?: "") }
        var stockMinimo by remember { mutableStateOf(productoAEditar?.stockMinimo?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { 
                mostrarDialogoNuevoProducto = false 
                productoAEditar = null
            },
            title = { Text(if (esEdicion) "Editar Producto" else "Nuevo Producto", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = unidadesPaq, onValueChange = { unidadesPaq = it }, label = { Text("Unidades por Paq.") })
                    OutlinedTextField(value = pesoLibras, onValueChange = { pesoLibras = it }, label = { Text("Peso (libras)") })
                    OutlinedTextField(value = precioVenta, onValueChange = { precioVenta = it }, label = { Text("Precio de Venta") })
                    OutlinedTextField(value = stockActual, onValueChange = { stockActual = it }, label = { Text("Stock Actual") })
                    OutlinedTextField(value = stockMinimo, onValueChange = { stockMinimo = it }, label = { Text("Stock Mínimo") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prod = Producto(
                            idProducto = productoAEditar?.idProducto ?: 0,
                            nombre = nombre,
                            unidadesPaq = unidadesPaq.toIntOrNull() ?: 0,
                            pesoLibras = pesoLibras.toDoubleOrNull() ?: 0.0,
                            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                            stockActual = stockActual.toIntOrNull() ?: 0,
                            stockMinimo = stockMinimo.toIntOrNull() ?: 0
                        )
                        if (esEdicion) {
                            vm.actualizarProducto(prod)
                        } else {
                            vm.registrarNuevoProducto(prod)
                        }
                        mostrarDialogoNuevoProducto = false
                        productoAEditar = null
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) {
                    Text("Guardar", color = AppColors.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { 
                    mostrarDialogoNuevoProducto = false 
                    productoAEditar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
