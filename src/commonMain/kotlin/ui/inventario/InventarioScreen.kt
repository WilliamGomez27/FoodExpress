package FoodExpress.ui.inventario

import FoodExpress.theme.AppColors
import FoodExpress.viewmodel.InventarioViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import FoodExpress.model.Producto
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun PantallaInventario() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { InventarioViewModel(scope) }
    val scaffoldState = rememberScaffoldState()
    val focusManager  = LocalFocusManager.current
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { vm.alternarVistaPapelera() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (vm.verPapelera) "Ver Activos" else "Ver Papelera", fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { mostrarDialogoNuevoProducto = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                    ) {
                        Text("Nuevo Producto", color = AppColors.White, fontWeight = FontWeight.Medium)
                    }
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
                                .background(if (vm.verPapelera) AppColors.Danger else AppColors.SidebarBg)
                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (vm.verPapelera) "Papelera de Reciclaje" else "Catálogo de Productos",
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
                                        enPapelera = vm.verPapelera,
                                        onClick = { vm.productoSel = producto },
                                        onEdit = { productoAEditar = producto },
                                        onDelete = { vm.eliminarProducto(producto.idProducto) },
                                        onRestore = { vm.restaurarProducto(producto.idProducto) }
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                focusManager.clearFocus()
                                vm.registrarMovimiento() 
                            }),
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
        var nombre      by remember { mutableStateOf(productoAEditar?.nombre ?: "") }
        var unidadesPaq by remember { mutableStateOf(productoAEditar?.unidadesPaq?.toString() ?: "") }
        var precioVenta by remember { mutableStateOf(productoAEditar?.precioVenta?.toString() ?: "") }
        var stockActual by remember { mutableStateOf(productoAEditar?.stockActual?.toString() ?: "") }
        var stockMinimo by remember { mutableStateOf(productoAEditar?.stockMinimo?.toString() ?: "") }
        var unidadMedida by remember { mutableStateOf(productoAEditar?.unidadMedida ?: "unidades") }
        var imagenUrl   by remember { mutableStateOf(productoAEditar?.imagenUrl ?: "") }

        AlertDialog(
            onDismissRequest = { 
                mostrarDialogoNuevoProducto = false 
                productoAEditar = null
            },
            title = { Text(if (esEdicion) "Editar Materia Prima" else "Nueva Materia Prima", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = unidadesPaq,
                            onValueChange = { unidadesPaq = it },
                            label = { Text("Und/Paq.") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                        )
                        OutlinedTextField(
                            value = precioVenta,
                            onValueChange = { precioVenta = it },
                            label = { Text("Costo Unit. (COP)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockActual,
                            onValueChange = { stockActual = it },
                            label = { Text("Stock Actual") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                        )
                        OutlinedTextField(
                            value = stockMinimo,
                            onValueChange = { stockMinimo = it },
                            label = { Text("Stock Mínimo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                        )
                    }

                    // Dropdown for Unit of Measure
                    var expandidoUM by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = unidadMedida,
                            onValueChange = {},
                            label = { Text("Unidad de Medida (Stock en BD)") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { expandidoUM = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                        )
                        DropdownMenu(
                            expanded = expandidoUM,
                            onDismissRequest = { expandidoUM = false }
                        ) {
                            listOf("unidades", "gramos", "ml", "litros").forEach { um ->
                                DropdownMenuItem(onClick = {
                                    unidadMedida = um
                                    expandidoUM = false
                                }) {
                                    Text(um)
                                }
                            }
                        }
                    }

                    Divider(color = AppColors.Divider, thickness = 0.5.dp)

                    // Campo para pegar URL de imagen del producto
                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("🖼️ URL de Imagen") },
                        placeholder = { Text("https://... ó dejar vacío", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val prod = Producto(
                                idProducto  = productoAEditar?.idProducto ?: 0,
                                nombre      = nombre,
                                unidadesPaq = unidadesPaq.toIntOrNull() ?: 0,
                                pesoLibras  = 0.0, // Ya no es tan relevante con gramos, lo dejamos en 0.0
                                precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                                stockActual = stockActual.toIntOrNull() ?: 0,
                                stockMinimo = stockMinimo.toIntOrNull() ?: 0,
                                imagenUrl   = imagenUrl.trim().ifBlank { null },
                                unidadMedida = unidadMedida
                            )
                            if (esEdicion) {
                                vm.actualizarProducto(prod)
                            } else {
                                vm.registrarNuevoProducto(prod)
                            }
                            mostrarDialogoNuevoProducto = false
                            productoAEditar = null
                        }),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = AppColors.Primary)
                    )
                    Text(
                        text = "Pega una URL pública de imagen. Déjalo vacío para el 🍽️ genérico.",
                        fontSize = 10.sp,
                        color = AppColors.TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prod = Producto(
                            idProducto  = productoAEditar?.idProducto ?: 0,
                            nombre      = nombre,
                            unidadesPaq = unidadesPaq.toIntOrNull() ?: 0,
                            pesoLibras  = 0.0,
                            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                            stockActual = stockActual.toIntOrNull() ?: 0,
                            stockMinimo = stockMinimo.toIntOrNull() ?: 0,
                            imagenUrl   = imagenUrl.trim().ifBlank { null },
                            unidadMedida = unidadMedida
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
