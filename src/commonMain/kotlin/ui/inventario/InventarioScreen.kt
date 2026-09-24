package FastFoodApp.ui.inventario

import FastFoodApp.theme.AppColors
import FastFoodApp.viewmodel.InventarioViewModel
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

@Composable
fun PantallaInventario() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { InventarioViewModel(scope) }
    val scaffoldState = rememberScaffoldState()
    var expandido     by remember { mutableStateOf("") }
    var mostrarDialogoNuevoProducto by remember { mutableStateOf(false) }

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
                            TableHeader("Producto",    Modifier.weight(2.5f))
                            TableHeader("Stock",       Modifier.weight(1f))
                            TableHeader("Mín.",        Modifier.weight(1f))
                            TableHeader("Precio",      Modifier.weight(1.2f))
                            TableHeader("Estado",      Modifier.weight(1.5f))
                        }

                        if (vm.cargando && vm.productos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = AppColors.Primary) }
                        } else {
                            LazyColumn {
                                items(vm.productos) { producto ->
                                    ProductoFila(
                                        producto = producto,
                                        isSelected = vm.productoSel?.idProducto == producto.idProducto,
                                        onClick = { vm.productoSel = producto }
                                    )
                                    Divider(color = AppColors.Divider, thickness = 0.5.dp)
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

    if (mostrarDialogoNuevoProducto) {
        var nombre by remember { mutableStateOf("") }
        var unidadesPaq by remember { mutableStateOf("") }
        var pesoLibras by remember { mutableStateOf("") }
        var precioVenta by remember { mutableStateOf("") }
        var stockActual by remember { mutableStateOf("") }
        var stockMinimo by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevoProducto = false },
            title = { Text("Nuevo Producto", fontWeight = FontWeight.Bold) },
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
                        val nuevoProducto = FastFoodApp.model.Producto(
                            idProducto = 0,
                            nombre = nombre,
                            unidadesPaq = unidadesPaq.toIntOrNull() ?: 0,
                            pesoLibras = pesoLibras.toDoubleOrNull() ?: 0.0,
                            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                            stockActual = stockActual.toIntOrNull() ?: 0,
                            stockMinimo = stockMinimo.toIntOrNull() ?: 0
                        )
                        vm.registrarNuevoProducto(nuevoProducto)
                        mostrarDialogoNuevoProducto = false
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) {
                    Text("Guardar", color = AppColors.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoNuevoProducto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
