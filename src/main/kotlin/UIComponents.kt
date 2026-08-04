package FastFoodApp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Paleta de colores del sistema ────────────────────────────────────────────
object AppColors {
    val SidebarBg       = Color(0xFF0F172A)   // Slate 900
    val Primary         = Color(0xFF6366F1)   // Indigo 500
    val PrimaryLight    = Color(0xFF818CF8)   // Indigo 400
    val Success         = Color(0xFF22C55E)   // Green 500
    val Warning         = Color(0xFFF59E0B)   // Amber 500
    val Danger          = Color(0xFFEF4444)   // Red 500
    val Surface         = Color(0xFFF8FAFC)   // Slate 50
    val TextPrimary     = Color(0xFF0F172A)   // Slate 900
    val TextMuted       = Color(0xFF64748B)   // Slate 500
    val White           = Color(0xFFFFFFFF)
    val CardBg          = Color(0xFFFFFFFF)
    val Divider         = Color(0xFFE2E8F0)   // Slate 200
}

// ── Tema Material personalizado ───────────────────────────────────────────────
val AppTheme: @Composable (@Composable () -> Unit) -> Unit = { content ->
    MaterialTheme(
        colors = lightColors(
            primary        = AppColors.Primary,
            primaryVariant = AppColors.PrimaryLight,
            secondary      = AppColors.Success,
            background     = AppColors.Surface,
            surface        = AppColors.CardBg,
            onPrimary      = AppColors.White,
            onBackground   = AppColors.TextPrimary,
            onSurface      = AppColors.TextPrimary,
        ),
        content = content
    )
}

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

    val menuItems = listOf(
        MenuItem("Inicio",       Icons.Default.Home,               "Inicio"),
        MenuItem("Inventario", Icons.AutoMirrored.Filled.List,     "Inventario"),
        MenuItem("Ventas",       Icons.Default.ShoppingCart,       "Ventas"),
        MenuItem("Gastos",       Icons.Default.Star,               "Gastos"),
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
                    "Inventario" -> {
                        PantallaInventario()
                    }
                    "Ventas"     -> PantallaVentas()
                    "Gastos"     -> PlaceholderPantalla("💵 Gastos", "Módulo de Gastos próximamente")
                    else         -> PantallaInicio(onNavegar = { pantallaActual = it })
                }
            }
        }
    }
}

// ── Pantalla de Inicio ────────────────────────────────────────────────────────
@Composable
fun PantallaInicio(onNavegar: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp)
    ) {
        // ── Encabezado ───────────────────────────────────────────────────────
        Text(
            text = "RAPPYFOOD  🍔 ",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Text(
            text = "Bienvenido al panel de control. ¿Qué deseas hacer hoy?",
            fontSize = 14.sp,
            color = AppColors.TextMuted
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ── Tarjetas de acceso rápido ─────────────────────────────────────
        Text(
            text = "ACCESO RÁPIDO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AppColors.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AccesoRapidoCard(
                emoji = "📦",
                titulo = "Inventario",
                descripcion = "Gestiona productos y stock",
                color = AppColors.Primary,
                modifier = Modifier.weight(1f),
                onClick = { onNavegar("Inventario") }
            )
            AccesoRapidoCard(
                emoji = "🛒",
                titulo = "Ventas",
                descripcion = "Registra ventas del día",
                color = AppColors.Success,
                modifier = Modifier.weight(1f),
                onClick = { onNavegar("Ventas") }
            )
            AccesoRapidoCard(
                emoji = "💵",
                titulo = "Gastos",
                descripcion = "Control de egresos",
                color = AppColors.Warning,
                modifier = Modifier.weight(1f),
                onClick = { onNavegar("Gastos") }
            )
        }
    }
}

// ── Tarjeta de acceso rápido ──────────────────────────────────────────────────
@Composable
fun AccesoRapidoCard(
    emoji: String,
    titulo: String,
    descripcion: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgAlpha by animateColorAsState(
        targetValue = if (isHovered) color.copy(alpha = 0.14f) else color.copy(alpha = 0.07f),
        animationSpec = tween(180)
    )
    val borderAlpha by animateColorAsState(
        targetValue = if (isHovered) color.copy(alpha = 0.5f) else color.copy(alpha = 0.2f),
        animationSpec = tween(180)
    )

    Card(
        modifier = modifier
            .height(160.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        elevation = if (isHovered) 6.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = bgAlpha,
        border = BorderStroke(1.5.dp, borderAlpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 36.sp)
            Column {
                Text(
                    text = titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}

// ── Placeholder para módulos en construcción ──────────────────────────────────
@Composable
fun PlaceholderPantalla(titulo: String, subtitulo: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(subtitulo, fontSize = 15.sp, color = AppColors.TextMuted)
        }
    }
}

// ── Componente: Tarjeta de estadística ───────────────────────────────────────
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