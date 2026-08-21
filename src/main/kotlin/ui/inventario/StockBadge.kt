package FastFoodApp.ui.inventario

import FastFoodApp.theme.AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
