package FastFoodApp.ui.info

import FastFoodApp.theme.AppColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaInfo() {
    Box(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = AppColors.CardBg
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = AppColors.Primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RAPPYFOOD - FastFoodApp",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text("Versión 1.0.0", fontSize = 14.sp, color = AppColors.TextMuted)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Divider(color = AppColors.Divider)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Desarrollador", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                Text(
                    text = "William Alexander Gomez Pinzon",
                    fontSize = 16.sp,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Contexto", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                Text(
                    text = "Proyecto Universitario (UNIMINUTO)",
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = AppColors.Divider)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Posibles Mejoras Futuras", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                
                val mejoras = listOf(
                    "• Módulo de Ayuda y soporte técnico.",
                    "• Comentarios y feedback de clientes en tiempo real.",
                    "• Integración con facturación electrónica local.",
                    "• Analíticas avanzadas de ventas con IA.",
                    "• App móvil exclusiva para clientes (Delivery)."
                )
                
                mejoras.forEach { mejora ->
                    Text(
                        text = mejora,
                        fontSize = 13.sp,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
