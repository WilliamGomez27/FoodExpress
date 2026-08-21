package FastFoodApp.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

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
