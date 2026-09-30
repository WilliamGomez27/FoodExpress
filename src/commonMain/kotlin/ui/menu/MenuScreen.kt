package FoodExpress.ui.menu

import FoodExpress.model.ProductoVenta
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

@Composable
fun PantallaMenu() {
    val scope = rememberCoroutineScope()
    // Reutilizamos el VentasViewModel porque ya tiene la lógica para traer el catálogo de productos disponibles
    val vm = remember { VentasViewModel(scope) }
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { 
        vm.cargarDatos() 
    }

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
                    MenuCard(producto)
                }
            }
        }
    }
}

@Composable
fun MenuCard(producto: ProductoVenta) {
    var mostrarZoom by remember { mutableStateOf(false) }
    val urlImagen = ImageHelper.obtenerImagenParaProducto(producto.idProductoVenta, producto.imagenUrl)

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
                    .clickable { mostrarZoom = true },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = urlImagen,
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
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

    if (mostrarZoom) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { mostrarZoom = false },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .size(400.dp)
                    .clickable { mostrarZoom = false },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = AppColors.CardBg,
                elevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = urlImagen,
                        contentDescription = "Zoom de ${producto.nombre}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}