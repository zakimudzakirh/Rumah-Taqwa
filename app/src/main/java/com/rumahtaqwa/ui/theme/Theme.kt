package com.rumahtaqwa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.rumahtaqwa.core.util.ThemeMode

//private val DarkColorScheme = darkColorScheme(
//    primary           = Teal500,
//    onPrimary         = Teal900,
//    primaryContainer  = Teal700,
//    onPrimaryContainer= Teal300,
//
//    background        = Gray900,
//    onBackground      = White,
//
//    surface           = Gray800,
//    onSurface         = White,
//    surfaceVariant    = Gray700,
//    onSurfaceVariant  = Gray400,
//
//    outline           = Gray700,
//    outlineVariant    = Gray800,
//
//    error             = Red400,
//    onError           = Gray900,
//
//    scrim             = Gray950,
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary            = Teal500,
//    onPrimary          = White,
//    primaryContainer   = TealBg,
//    onPrimaryContainer = Teal700,
//
//    background         = White,     // level 0
//    onBackground       = Gray900,
//
//    surface            = Gray100,   // level 1 — sekarang jelas beda dari White
//    onSurface          = Gray900,
//    surfaceVariant     = Gray150,   // level 2 — beda dari surface
//    onSurfaceVariant   = Gray500,
//
//    outline            = Gray200,   // level 3
//    outlineVariant     = Gray150,
//
//    error              = Red400,
//    onError            = White,
//)

private val DarkColorScheme = darkColorScheme(
    primary            = Amber500,
    onPrimary          = Navy900,
    primaryContainer   = Amber600,
    onPrimaryContainer = Amber400,

    background         = Navy900,   // #0F172A
    onBackground       = White,

    surface            = Navy800,   // #1E293B — card
    onSurface          = White,
    surfaceVariant     = Navy700,   // #263548 — track/inset
    onSurfaceVariant   = Navy200,

    outline            = Navy600,
    outlineVariant     = Navy700,

    error              = Red400,
    onError            = Navy900,
    scrim              = Navy950,
)

private val LightColorScheme = lightColorScheme(
    primary            = Amber500,
    onPrimary          = White,
    primaryContainer   = AmberBg,
    onPrimaryContainer = Amber600,

    background         = White,
    onBackground       = Navy900,

    surface            = Navy50,    // #F1F5F9 — card
    onSurface          = Navy900,
    surfaceVariant     = Navy100,   // #E2E8F0 — track/inset
    onSurfaceVariant   = Navy400,

    outline            = Navy200,
    outlineVariant     = Navy100,

    error              = Red400,
    onError            = White,
)

@Composable
fun RumahTaqwaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RumahTaqwaTypography,
        content = {
            CompositionLocalProvider(
                LocalTextSelectionColors provides TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                content()
            }
        }
    )
}