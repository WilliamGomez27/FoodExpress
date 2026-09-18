package FastFoodApp.ui.inventario

import FastFoodApp.model.Producto
import FastFoodApp.theme.AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

        Text(
            text = "${producto.stockActual}",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = if (stockBajo) AppColors.Danger else AppColors.TextPrimary,
            fontWeight = if (stockBajo) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = "${producto.stockMinimo}",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = AppColors.TextMuted
        )

        Text(
            text = "Q%.2f".format(producto.precioVenta),
            modifier = Modifier.weight(1.2f),
            fontSize = 13.sp,
            color = AppColors.TextPrimary
        )

        StockBadge(stockBajo = stockBajo, modifier = Modifier.weight(1.5f))
    }
}
