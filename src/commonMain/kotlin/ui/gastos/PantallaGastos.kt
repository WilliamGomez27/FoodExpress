package FoodExpress.ui.gastos

import FoodExpress.theme.AppColors
import FoodExpress.viewmodel.GastoViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope

@Composable
fun PantallaGastos(scope: CoroutineScope) {
    val viewModel = remember { GastoViewModel(scope) }

    LaunchedEffect(Unit) {
        viewModel.cargarGastos()
    }

    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
    ) {
        Text(
            text = "Control de Gastos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del Gasto") },
                modifier = Modifier.weight(2f)
            )
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto (Q)") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull()
                    if (descripcion.isNotBlank() && montoDouble != null) {
                        viewModel.registrarGasto(descripcion, montoDouble)
                        descripcion = ""
                        monto = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar", tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Registrar", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Gastos del Mes:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "COP ${String.format("%.2f", viewModel.totalGastos)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Danger
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Historial de Gastos (Mes Actual)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.gastos) { gasto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 1.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = gasto.descripcion, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(text = gasto.fechaHora, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(
                            text = "-COP ${String.format("%.2f", gasto.monto)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Danger
                        )
                    }
                }
            }
        }
    }
}
