package FoodExpress.ui.inventario

import FoodExpress.theme.AppColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatCard(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = color.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(titulo, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
            Text(valor,  fontSize = 24.sp, color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
