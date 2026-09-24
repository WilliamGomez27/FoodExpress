package FastFoodApp.ui.dashboard

import FastFoodApp.theme.AppColors
import FastFoodApp.utils.SessionInfo
import FastFoodApp.viewmodel.DashboardViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope

@Composable
fun PantallaInicio(onNavegar: (String) -> Unit, scope: CoroutineScope) {
    val viewModel = remember { DashboardViewModel() }
    val resumen by viewModel.resumenHoy.collectAsState()
    val ventas by viewModel.ventasRecientes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(scope)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(36.dp)
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RAPPYFOOD  🍔 ",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "Bienvenido, ${SessionInfo.usuarioActual?.nombre ?: "Usuario"}. ¿Qué deseas hacer hoy?",
                    fontSize = 14.sp,
                    color = AppColors.TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Sección: Métricas del día (solo para Admin, o para todos si quieres)
        if (SessionInfo.isAdmin) {
            Text(
                text = "MÉTRICAS DE HOY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = AppColors.Primary)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MetricCard(
                        title = "Ventas Totales",
                        value = "COP ${resumen?.totalMontoVentas ?: "0.0"}",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Órdenes",
                        value = "${resumen?.totalVentas?.toInt() ?: 0}",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Estado",
                        value = "Abierto",
                        modifier = Modifier.weight(1f),
                        valueColor = AppColors.Success
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Sección: Acceso Rápido
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
                emoji = "🛒",
                titulo = "Ventas",
                descripcion = "Registra ventas del día",
                color = AppColors.Success,
                modifier = Modifier.weight(1f),
                onClick = { onNavegar("Ventas") }
            )

            if (SessionInfo.isAdmin) {
                AccesoRapidoCard(
                    emoji = "📦",
                    titulo = "Inventario",
                    descripcion = "Gestiona productos y stock",
                    color = AppColors.Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegar("Inventario") }
                )
                AccesoRapidoCard(
                    emoji = "💵",
                    titulo = "Gastos",
                    descripcion = "Control de egresos",
                    color = AppColors.Warning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegar("Gastos") }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Sección: Últimas Transacciones
        Text(
            text = "ÚLTIMAS TRANSACCIONES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AppColors.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 0.dp,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AppColors.Divider),
            backgroundColor = AppColors.CardBg
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (ventas.isEmpty()) {
                    Text(
                        text = "No hay ventas recientes.",
                        modifier = Modifier.padding(24.dp),
                        color = AppColors.TextMuted,
                        fontSize = 14.sp
                    )
                } else {
                    ventas.forEachIndexed { index, venta ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Venta #${venta.idVenta}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = AppColors.TextPrimary
                                )
                                Text(
                                    text = venta.fechaHora,
                                    fontSize = 12.sp,
                                    color = AppColors.TextMuted
                                )
                            }
                            Text(
                                text = "COP ${venta.totalVenta}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppColors.Success
                            )
                        }
                        if (index < ventas.lastIndex) {
                            Divider(color = AppColors.Divider, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier, valueColor: Color = AppColors.TextPrimary) {
    Card(
        modifier = modifier.height(100.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Divider),
        backgroundColor = AppColors.CardBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 12.sp, color = AppColors.TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

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
