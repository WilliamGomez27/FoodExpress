package FastFoodApp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaInventario() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { InventarioViewModel(scope) }
    val scaffoldState = rememberScaffoldState()
    var expandido     by remember { mutableStateOf("") }

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
            // ── Header ──────────────────────────────────────────────────────
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
            Spacer(modifier = Modifier.height(20.dp))

            // ── Tarjetas de resumen ──────────────────────────────────────────
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
                    valor  = "Q %.2f".format(vm.productos.sumOf { it.precioVenta * it.stockActual }),
                    color  = AppColors.Success,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Layout de dos columnas ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── Tabla de productos (izquierda) ──────────────────────────
                Card(
                    modifier = Modifier.weight(1.6f).fillMaxHeight(),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedBorder,
                    backgroundColor = AppColors.CardBg
                ) {
                    Column(modifier = Modifier.padding(0.dp)) {
                        // Cabecera de tabla
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

                // ── Panel de operación (derecha) ────────────────────────────
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

                        // Producto seleccionado
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

                        // Selector de Motivo
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

                        // Campo de cantidad
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

                        // Botón de acción
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
}

// ── Fila de producto en tabla ──────────────────────────────────────────────────
@Composable
fun ProductoFila(producto: Producto, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) AppColors.Primary.copy(alpha = 0.06f) else Color.Transparent
    val stockBajo = producto.stockActual <= producto.stockMinimo

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre — clickable
        TextButton(
            onClick = onClick,
            modifier = Modifier.weight(2.5f),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (stockBajo) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Stock bajo",
                        tint = AppColors.Danger,
                        modifier = Modifier.size(14.dp).padding(end = 2.dp)
                    )
                }
                Text(
                    text = producto.nombre,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) AppColors.Primary else AppColors.TextPrimary
                )
            }
        }

        // Stock actual
        Text(
            text = "${producto.stockActual}",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = if (stockBajo) AppColors.Danger else AppColors.TextPrimary,
            fontWeight = if (stockBajo) FontWeight.Bold else FontWeight.Normal
        )

        // Stock mínimo
        Text(
            text = "${producto.stockMinimo}",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = AppColors.TextMuted
        )

        // Precio
        Text(
            text = "Q%.2f".format(producto.precioVenta),
            modifier = Modifier.weight(1.2f),
            fontSize = 13.sp,
            color = AppColors.TextPrimary
        )

        // Badge de estado
        StockBadge(stockBajo = stockBajo, modifier = Modifier.weight(1.5f))
    }
}

@Composable
fun StockBadge(stockBajo: Boolean, modifier: Modifier = Modifier) {
    val color = if (stockBajo) AppColors.Danger else AppColors.Success
    val texto = if (stockBajo) "Stock Bajo" else "OK"
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color.copy(alpha = 0.12f))
                .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(texto, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TableHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.5f),
        letterSpacing = 0.8.sp
    )
}