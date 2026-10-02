package FoodExpress.ui.menu

import FoodExpress.model.ProductoVenta
import FoodExpress.model.RecetaIngrediente
import FoodExpress.repository.RecetaRepository
import FoodExpress.theme.AppColors
import FoodExpress.utils.ImageHelper
import FoodExpress.viewmodel.VentasViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

@Composable
fun PantallaMenu() {
    val scope = rememberCoroutineScope()
    // Reutilizamos el VentasViewModel porque ya tiene la lógica para traer el catálogo de productos disponibles
    val vm = remember { VentasViewModel(scope) }
    val focusManager = LocalFocusManager.current
    var busqueda by remember { mutableStateOf("") }
    
    // Estado global para controlar cuál producto está en vista de Zoom
    var productoZomeado by remember { mutableStateOf<ProductoVenta?>(null) }

    LaunchedEffect(Unit) { 
        vm.cargarDatos() 
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Surface)
                .padding(28.dp)
        ) {
            Text("Nuestro Menú", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            Text("Explora los productos disponibles", fontSize = 14.sp, color = AppColors.TextMuted)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(0.5f), // Mitad del ancho en escritorio
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.Primary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            val filtrados = vm.catalogoProductos.filter {
                busqueda.isBlank() || it.nombre.contains(busqueda, ignoreCase = true)
            }

            if (vm.cargando && vm.catalogoProductos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Primary)
                }
            } else if (filtrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos.", color = AppColors.TextMuted)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtrados) { producto ->
                        MenuCard(
                            producto = producto,
                            onClickImagen = { productoZomeado = producto }
                        )
                    }
                }
            }
        }

        // Overlay de Zoom que se dibuja sobre toda la pantalla
        productoZomeado?.let { prod ->
            ProductoZoomOverlay(
                producto = prod,
                onClose = { productoZomeado = null }
            )
        }
    }
}

@Composable
fun MenuCard(producto: ProductoVenta, onClickImagen: () -> Unit) {
    val urlImagen = ImageHelper.obtenerImagenParaProducto(producto.idProductoVenta, producto.imagenUrl)
        ?: ImageHelper.fallbackPorNombre(producto.nombre)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = AppColors.CardBg,
        border = BorderStroke(1.dp, AppColors.Divider)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carga de imagen con soporte de Zoom al presionar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Primary.copy(alpha = 0.1f))
                    .clickable { onClickImagen() },
                contentAlignment = Alignment.Center
            ) {
                if (urlImagen != null) {
                    AsyncImage(
                        model = urlImagen,
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder genérico cuando no hay imagen en la BD
                    Text(
                        text = "🍽️",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = producto.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = producto.categoria,
                fontSize = 12.sp,
                color = AppColors.TextMuted,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "COP %.2f".format(producto.precioVenta),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = AppColors.Success
            )
        }
    }
}

@Composable
fun ProductoZoomOverlay(producto: ProductoVenta, onClose: () -> Unit) {
    var ingredientes by remember { mutableStateOf<List<RecetaIngrediente>?>(null) }
    
    val urlImagen = ImageHelper.obtenerImagenParaProducto(producto.idProductoVenta, producto.imagenUrl)
        ?: ImageHelper.fallbackPorNombre(producto.nombre)

    LaunchedEffect(producto) {
        val repo = RecetaRepository()
        ingredientes = repo.obtenerReceta(producto.idProductoVenta)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .padding(16.dp)
                .clickable { /* Evita que el clic en la tarjeta cierre el zoom */ },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = AppColors.CardBg,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (urlImagen != null) {
                        AsyncImage(
                            model = urlImagen,
                            contentDescription = "Zoom de ${producto.nombre}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "🍽️",
                            fontSize = 80.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = producto.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = AppColors.Divider)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Ingredientes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (!producto.descripcionIngredientes.isNullOrBlank()) {
                    Text(
                        text = producto.descripcionIngredientes,
                        fontSize = 14.sp,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                } else if (ingredientes == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).padding(8.dp),
                        color = AppColors.Primary,
                        strokeWidth = 2.dp
                    )
                } else if (ingredientes!!.isEmpty()) {
                    Text(
                        text = "Este producto no tiene ingredientes registrados en su receta.",
                        fontSize = 13.sp,
                        color = AppColors.TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ingredientes!!.forEach { ing ->
                            val cantidadTexto = if (ing.cantidad % 1.0 == 0.0) {
                                ing.cantidad.toInt().toString()
                            } else {
                                "%.1f".format(ing.cantidad)
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "• ${ing.nombreMateria}",
                                    fontSize = 14.sp,
                                    color = AppColors.TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "($cantidadTexto ${ing.unidad})",
                                    fontSize = 14.sp,
                                    color = AppColors.TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}