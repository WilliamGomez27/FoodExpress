package FastFoodApp.ui.reportes

import FastFoodApp.theme.AppColors
import FastFoodApp.viewmodel.ReporteViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope

@Composable
fun PantallaReportes(scope: CoroutineScope) {
    val viewModel = remember { ReporteViewModel(scope) }
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Arqueo Diario", "Cierre Mensual")

    LaunchedEffect(Unit) {
        viewModel.cargarArqueoDiario("2026-03-01")
        viewModel.cargarCierreMensual(3, 2026)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.White,
            contentColor = AppColors.Primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(modifier = Modifier.padding(24.dp)) {
            if (viewModel.cargando) {
                CircularProgressIndicator()
            } else {
                if (tabIndex == 0) {
                    ArqueoDiarioView(viewModel)
                } else {
                    CierreMensualView(viewModel)
                }
            }
        }
    }
}

@Composable
fun ArqueoDiarioView(viewModel: ReporteViewModel) {
    val arqueo = viewModel.arqueoDiario ?: return

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Arqueo Diario: ${arqueo.fecha}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetaResumen("Ventas Totales", "${arqueo.totalVentas.toInt()} tickets", Modifier.weight(1f))
                TarjetaResumen("Ingresos del Día", "Q ${String.format("%.2f", arqueo.totalMontoVentas)}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text("Productos Terminados Vendidos", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Divider(Modifier.padding(vertical = 8.dp))
        }
        items(arqueo.productosVendidos) { prod ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(prod.nombre)
                Text("${prod.cantidadVendida} unidades", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            Text("Materia Prima Gastada", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Divider(Modifier.padding(vertical = 8.dp))
        }
        items(arqueo.materiaPrimaConsumida) { mat ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(mat.nombre)
                Text("${mat.cantidadConsumida} ${mat.unidad}", fontWeight = FontWeight.Bold, color = AppColors.Danger)
            }
        }
    }
}

@Composable
fun CierreMensualView(viewModel: ReporteViewModel) {
    val cierre = viewModel.cierreMensual ?: return

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Cierre Mensual: ${cierre.mesAnio}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetaResumen("Ingresos Brutos", "Q ${String.format("%.2f", cierre.ingresosBrutos)}", Modifier.weight(1f))
                TarjetaResumen("Gastos Totales", "Q ${String.format("%.2f", cierre.gastosTotales)}", Modifier.weight(1f), color = AppColors.Danger)
                TarjetaResumen("Ganancia Neta", "Q ${String.format("%.2f", cierre.gananciasNetas)}", Modifier.weight(1f), color = if (cierre.gananciasNetas >= 0) AppColors.Success else AppColors.Danger)
            }
            Spacer(Modifier.height(24.dp))
            Text("Estado de Inventario", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Divider(Modifier.padding(vertical = 8.dp))
        }

        items(cierre.estadoInventario) { item ->
            val colorStock = if (item.stockActual <= item.stockMinimo) AppColors.Danger else AppColors.TextPrimary
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.nombre)
                Text("Stock: ${item.stockActual} (Min: ${item.stockMinimo})", fontWeight = FontWeight.Bold, color = colorStock)
            }
        }
    }
}

@Composable
fun TarjetaResumen(titulo: String, valor: String, modifier: Modifier = Modifier, color: Color = AppColors.TextPrimary) {
    Card(modifier = modifier, elevation = 2.dp) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
