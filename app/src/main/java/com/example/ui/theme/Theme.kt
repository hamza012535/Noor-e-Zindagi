package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = DeepEmerald,
    onPrimary = PureIvory,
    primaryContainer = SoftSage,
    onPrimaryContainer = DarkForestGreen,
    secondary = BurnishedGold,
    onSecondary = DarkForestGreen,
    secondaryContainer = WarmGoldLight,
    onSecondaryContainer = DeepGold,
    tertiary = RichEmerald,
    onTertiary = PureIvory,
    tertiaryContainer = MintTint,
    onTertiaryContainer = DeepEmerald,
    background = WarmCream,
    onBackground = CharcoalInk,
    surface = PureIvory,
    onSurface = CharcoalInk,
    surfaceVariant = SoftAlabaster,
    onSurfaceVariant = MutedForestGray,
    outline = SandBorder,
    error = SoftTerracotta,
    onError = PureIvory
)

private val DarkColorScheme = darkColorScheme(
    primary = AmberGlow,
    onPrimary = DarkForestGreen,
    primaryContainer = RichEmerald,
    onPrimaryContainer = CreamInk,
    secondary = BurnishedGold,
    onSecondary = DarkForestGreen,
    secondaryContainer = DeepGold,
    onSecondaryContainer = WarmGoldLight,
    tertiary = SerenityTeal,
    onTertiary = CreamInk,
    tertiaryContainer = ElevatedNightCard,
    onTertiaryContainer = SoftSilverSage,
    background = MidnightForest,
    onBackground = CreamInk,
    surface = DeepNightCard,
    onSurface = CreamInk,
    surfaceVariant = ElevatedNightCard,
    onSurfaceVariant = SoftSilverSage,
    outline = ColorTokens.DarkOutline,
    error = SoftTerracotta,
    onError = CreamInk
)

private object ColorTokens {
    val DarkOutline = androidx.compose.ui.graphics.Color(0xFF28463B)
}

val NoorShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun NoorEZindagiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoorTypography,
        shapes = NoorShapes,
        content = content
    )
}
