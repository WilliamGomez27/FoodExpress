package FastFoodApp.ui.recetas

import FastFoodApp.model.Producto
import FastFoodApp.model.ProductoVenta
import FastFoodApp.model.RecetaIngrediente
import FastFoodApp.theme.AppColors
import FastFoodApp.viewmodel.RecetaViewModel
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaRecetas() {
    val scope         = rememberCoroutineScope()
    val vm            = remember { RecetaViewModel(scope) }
    val scaffoldState = rememberScaffoldState()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recetas de Producción",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Define los ingredientes necesarios para cada producto terminado",
                        fontSize = 13.sp,
                        color = AppColors.TextMuted
                    )
                }
                if (vm.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                PanelProductosTerminados(
                    productos          = vm.productosTerminados,
                    seleccionado       = vm.productoSeleccionado,
                    onSeleccionar      = { vm.seleccionarProducto(it) },
                    modifier           = Modifier.width(260.dp).fillMaxHeight()
                )

                PanelEditorReceta(
                    productoSeleccionado     = vm.productoSeleccionado,
                    ingredientes             = vm.ingredientes,
                    materiasPrimas           = vm.materiasPrimas,
                    materiaPrimaSeleccionada = vm.materiaPrimaSeleccionada,
                    onMateriaPrimaChange     = { vm.materiaPrimaSeleccionada = it },
                    cantidadInput            = vm.cantidadInput,
                    onCantidadChange         = { vm.cantidadInput = it },
                    unidadSeleccionada       = vm.unidadSeleccionada,
                    onUnidadChange           = { vm.unidadSeleccionada = it },
                    unidades                 = vm.unidades,
                    onAgregarIngrediente     = { vm.agregarIngrediente() },
                    onEliminarIngrediente    = { vm.eliminarIngrediente(it) },
                    onGuardarReceta          = { vm.guardarReceta() },
                    modifier                 = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun PanelProductosTerminados(
    productos: List<ProductoVenta>,
    seleccionado: ProductoVenta?,
    onSeleccionar: (ProductoVenta) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier           = modifier,
        elevation          = 2.dp,
        shape              = RoundedCornerShape(16.dp),
        backgroundColor    = AppColors.CardBg
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text       = "Productos Terminados",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = AppColors.TextPrimary
            )
            Text(
                text  = "Selecciona para editar su receta",
                fontSize = 11.sp,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = AppColors.Divider)
            Spacer(modifier = Modifier.height(8.dp))

            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin productos", color = AppColors.TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(productos) { prod ->
                        val isSelected = seleccionado?.idProductoVenta == prod.idProductoVenta
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) AppColors.Primary.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) AppColors.Primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSeleccionar(prod) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text       = prod.nombre,
                                    fontSize   = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color      = if (isSelected) AppColors.Primary else AppColors.TextPrimary,
                                    maxLines   = 1,
                                    overflow   = TextOverflow.Ellipsis
                                )
                                Text(
                                    text     = prod.categoria,
                                    fontSize = 11.sp,
                                    color    = AppColors.TextMuted
                                )
                            }
                            Text(
                                text     = "Q%.0f".format(prod.precioVenta),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color    = AppColors.Success
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelEditorReceta(
    productoSeleccionado: ProductoVenta?,
    ingredientes: List<RecetaIngrediente>,
    materiasPrimas: List<Producto>,
    materiaPrimaSeleccionada: Producto?,
    onMateriaPrimaChange: (Producto?) -> Unit,
    cantidadInput: String,
    onCantidadChange: (String) -> Unit,
    unidadSeleccionada: String,
    onUnidadChange: (String) -> Unit,
    unidades: List<String>,
    onAgregarIngrediente: () -> Unit,
    onEliminarIngrediente: (RecetaIngrediente) -> Unit,
    onGuardarReceta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier        = modifier,
        elevation       = 2.dp,
        shape           = RoundedCornerShape(16.dp),
        backgroundColor = AppColors.CardBg
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

            if (productoSeleccionado == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👈", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "Selecciona un producto terminado\npara ver o editar su receta",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
                return@Card
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = productoSeleccionado.nombre,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AppColors.TextPrimary
                    )
                    Text(
                        text     = "Receta: ${ingredientes.size} ingrediente(s)",
                        fontSize = 12.sp,
                        color    = AppColors.TextMuted
                    )
                }
                Button(
                    onClick = onGuardarReceta,
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Success),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = "Guardar",
                        tint = AppColors.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guardar Receta", color = AppColors.White, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = AppColors.Divider)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Agregar Ingrediente", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectorMateriaPrima(
                    materiasPrimas           = materiasPrimas,
                    materiaPrimaSeleccionada = materiaPrimaSeleccionada,
                    onSeleccionar            = onMateriaPrimaChange,
                    modifier                 = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value         = cantidadInput,
                    onValueChange = onCantidadChange,
                    label         = { Text("Cantidad", fontSize = 12.sp) },
                    singleLine    = true,
                    colors        = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor   = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Divider
                    ),
                    modifier = Modifier.weight(1f)
                )
                SelectorUnidad(
                    unidades           = unidades,
                    unidadSeleccionada = unidadSeleccionada,
                    onSeleccionar      = onUnidadChange,
                    modifier           = Modifier.weight(1f)
                )
                Button(
                    onClick = onAgregarIngrediente,
                    colors  = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary),
                    shape   = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = AppColors.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = AppColors.Divider)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Surface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Materia Prima",   modifier = Modifier.weight(3f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextMuted)
                Text("Cantidad",        modifier = Modifier.weight(1.5f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextMuted)
                Text("Unidad",          modifier = Modifier.weight(1.5f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextMuted)
                Spacer(modifier = Modifier.width(40.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))

            if (ingredientes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Sin ingredientes definidos. Agrega uno arriba.",
                        color = AppColors.TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(ingredientes) { ing ->
                        FilaIngrediente(
                            ingrediente = ing,
                            onEliminar  = { onEliminarIngrediente(ing) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaIngrediente(
    ingrediente: RecetaIngrediente,
    onEliminar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.CardBg)
            .border(1.dp, AppColors.Divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text     = ingrediente.nombreMateria.ifBlank { "ID: ${ingrediente.idProducto}" },
            fontSize = 13.sp,
            color    = AppColors.TextPrimary,
            modifier = Modifier.weight(3f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        val cantidadTexto = if (ingrediente.cantidad % 1.0 == 0.0)
            ingrediente.cantidad.toInt().toString()
        else "%.1f".format(ingrediente.cantidad)

        Text(
            text     = cantidadTexto,
            fontSize = 13.sp,
            color    = AppColors.TextPrimary,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text     = ingrediente.unidad,
            fontSize = 13.sp,
            color    = AppColors.TextMuted,
            modifier = Modifier.weight(1.5f)
        )
        IconButton(
            onClick  = onEliminar,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar ingrediente",
                tint = AppColors.Danger,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SelectorMateriaPrima(
    materiasPrimas: List<Producto>,
    materiaPrimaSeleccionada: Producto?,
    onSeleccionar: (Producto?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value         = materiaPrimaSeleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Materia Prima", fontSize = 12.sp) },
            trailingIcon  = {
                Text(
                    text     = if (expandido) "▲" else "▼",
                    modifier = Modifier.clickable { expandido = !expandido }.padding(end = 8.dp),
                    color    = AppColors.TextMuted,
                    fontSize = 11.sp
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor   = AppColors.Primary,
                unfocusedBorderColor = AppColors.Divider,
                disabledBorderColor  = AppColors.Divider
            ),
            modifier = Modifier.fillMaxWidth().clickable { expandido = true }
        )
        DropdownMenu(
            expanded         = expandido,
            onDismissRequest = { expandido = false },
            modifier         = Modifier.heightIn(max = 250.dp)
        ) {
            materiasPrimas.forEach { materia ->
                DropdownMenuItem(onClick = {
                    onSeleccionar(materia)
                    expandido = false
                }) {
                    Column {
                        Text(materia.nombre, fontSize = 13.sp, color = AppColors.TextPrimary)
                        Text("Stock: ${materia.stockActual}", fontSize = 11.sp, color = AppColors.TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorUnidad(
    unidades: List<String>,
    unidadSeleccionada: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value         = unidadSeleccionada,
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Unidad", fontSize = 12.sp) },
            trailingIcon  = {
                Text(
                    text     = if (expandido) "▲" else "▼",
                    modifier = Modifier.clickable { expandido = !expandido }.padding(end = 8.dp),
                    color    = AppColors.TextMuted,
                    fontSize = 11.sp
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor   = AppColors.Primary,
                unfocusedBorderColor = AppColors.Divider
            ),
            modifier = Modifier.fillMaxWidth().clickable { expandido = true }
        )
        DropdownMenu(
            expanded         = expandido,
            onDismissRequest = { expandido = false }
        ) {
            unidades.forEach { unidad ->
                DropdownMenuItem(onClick = {
                    onSeleccionar(unidad)
                    expandido = false
                }) {
                    Text(unidad, fontSize = 13.sp)
                }
            }
        }
    }
}
