package FastFoodApp.navigation

import FastFoodApp.theme.AppColors
import FastFoodApp.theme.AppTheme
import FastFoodApp.ui.dashboard.PantallaInicio
import FastFoodApp.ui.dashboard.PlaceholderPantalla
import FastFoodApp.ui.inventario.PantallaInventario
import FastFoodApp.ui.recetas.PantallaRecetas
import FastFoodApp.ui.ventas.PantallaVentas
import FastFoodApp.ui.reportes.PantallaReportes
import FastFoodApp.ui.gastos.PantallaGastos
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Ítem de menú lateral ──────────────────────────────────────────────────────
data class MenuItem(val label: String, val icon: ImageVector, val key: String)

@Composable
fun SidebarItem(item: MenuItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.Primary else Color.Transparent,
        animationSpec = tween(200)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.White else AppColors.White.copy(alpha = 0.6f),
        animationSpec = tween(200)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── Shell principal de la app ─────────────────────────────────────────────────
@Composable
fun AppFastFood() {
    var pantallaActual by remember { mutableStateOf("Inicio") }
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItem("Inicio",     Icons.Default.Home,                "Inicio"),
        MenuItem("Inventario", Icons.AutoMirrored.Filled.List,    "Inventario"),
        MenuItem("Ventas",     Icons.Default.ShoppingCart,        "Ventas"),
        MenuItem("Recetas",    Icons.Default.Build,               "Recetas"),
        MenuItem("Gastos",     Icons.Default.Star,                "Gastos"),
        MenuItem("Reportes",   Icons.Default.DateRange,           "Reportes"),
    )

    AppTheme {
        Row(modifier = Modifier.fillMaxSize()) {

            // ── SIDEBAR ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .width(230.dp)
                    .fillMaxHeight()
                    .background(AppColors.SidebarBg)
                    .padding(16.dp)
            ) {
                // Logo / Título
                Column(modifier = Modifier.padding(bottom = 28.dp, top = 8.dp)) {
                    Text(
                        text = "🍔 RAPPYFOOD",
                        color = AppColors.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Panel de Control",
                        color = AppColors.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }

                Divider(color = AppColors.White.copy(alpha = 0.08f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "MENÚ",
                    color = AppColors.White.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                menuItems.forEach { item ->
                    SidebarItem(
                        item = item,
                        isSelected = pantallaActual == item.key,
                        onClick = { pantallaActual = item.key }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer del sidebar
                Divider(color = AppColors.White.copy(alpha = 0.08f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "v1.0  •  FastFoodApp",
                    color = AppColors.White.copy(alpha = 0.25f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // ── CONTENIDO PRINCIPAL ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Surface)
            ) {
                when (pantallaActual) {
                    "Inventario" -> PantallaInventario()
                    "Ventas"     -> PantallaVentas()
                    "Recetas"    -> PantallaRecetas()
                    "Gastos"     -> PantallaGastos(scope)
                    "Reportes"   -> PantallaReportes(scope)
                    else         -> PantallaInicio(onNavegar = { pantallaActual = it })
                }
            }
        }
    }
}
