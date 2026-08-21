package FastFoodApp.ui.dashboard

import FastFoodApp.theme.AppColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
