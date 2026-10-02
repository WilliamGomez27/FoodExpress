package FoodExpress.ui.inventario

import FoodExpress.model.Producto
import FoodExpress.theme.AppColors
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.IconButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card

import androidx.compose.material.icons.filled.Refresh

@Composable
fun ProductoCard(
    producto: Producto, 
    isSelected: Boolean, 
    enPapelera: Boolean = false,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRestore: () -> Unit = {}
) {
    val stockBajo = producto.stockActual <= producto.stockMinimo
    val borderColor = if (isSelected) AppColors.Primary else if (stockBajo) AppColors.Danger else AppColors.Divider
    val bgColor = if (isSelected) AppColors.Primary.copy(alpha = 0.05f) else AppColors.CardBg

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = if (isSelected) 4.dp else 1.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = producto.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    if (!enPapelera) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AppColors.Danger, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        IconButton(onClick = onRestore, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restaurar", tint = AppColors.Success, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Stock: ${producto.stockActual} ${producto.unidadMedida}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (stockBajo) AppColors.Danger else AppColors.TextPrimary)
                    Text("Mínimo: ${producto.stockMinimo}", fontSize = 11.sp, color = AppColors.TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Costo Unit.", fontSize = 11.sp, color = AppColors.TextMuted)
                    Text("COP %.2f".format(producto.precioVenta), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Success)
                }
            }
        }
    }
}
