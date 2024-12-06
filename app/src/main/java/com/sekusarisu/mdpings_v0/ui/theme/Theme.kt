package com.sekusarisu.mdpings_v0.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeConfig
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeMode
import com.sekusarisu.mdpings_v0.vpings.domain.ThemeSeedColor

//private val DarkColorScheme = darkColorScheme(
//    primary = primaryDark,
//    onPrimary = onPrimaryDark,
//    primaryContainer = primaryContainerDark,
//    onPrimaryContainer = onPrimaryContainerDark,
//    secondary = secondaryDark,
//    onSecondary = onSecondaryDark,
//    secondaryContainer = secondaryContainerDark,
//    onSecondaryContainer = onSecondaryContainerDark,
//    tertiary = tertiaryDark,
//    onTertiary = onTertiaryDark,
//    tertiaryContainer = tertiaryContainerDark,
//    onTertiaryContainer = onTertiaryContainerDark,
//    error = errorDark,
//    onError = onErrorDark,
//    errorContainer = errorContainerDark,
//    onErrorContainer = onErrorContainerDark,
//    background = backgroundDark,
//    onBackground = onBackgroundDark,
//    surface = surfaceDark,
//    onSurface = onSurfaceDark,
//    surfaceVariant = surfaceVariantDark,
//    onSurfaceVariant = onSurfaceVariantDark,
//    outline = outlineDark,
//    outlineVariant = outlineVariantDark,
//    scrim = scrimDark,
//    inverseSurface = inverseSurfaceDark,
//    inverseOnSurface = inverseOnSurfaceDark,
//    inversePrimary = inversePrimaryDark,
//    surfaceDim = surfaceDimDark,
//    surfaceBright = surfaceBrightDark,
//    surfaceContainerLowest = surfaceContainerLowestDark,
//    surfaceContainerLow = surfaceContainerLowDark,
//    surfaceContainer = surfaceContainerDark,
//    surfaceContainerHigh = surfaceContainerHighDark,
//    surfaceContainerHighest = surfaceContainerHighestDark,
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = primaryLight,
//    onPrimary = onPrimaryLight,
//    primaryContainer = primaryContainerLight,
//    onPrimaryContainer = onPrimaryContainerLight,
//    secondary = secondaryLight,
//    onSecondary = onSecondaryLight,
//    secondaryContainer = secondaryContainerLight,
//    onSecondaryContainer = onSecondaryContainerLight,
//    tertiary = tertiaryLight,
//    onTertiary = onTertiaryLight,
//    tertiaryContainer = tertiaryContainerLight,
//    onTertiaryContainer = onTertiaryContainerLight,
//    error = errorLight,
//    onError = onErrorLight,
//    errorContainer = errorContainerLight,
//    onErrorContainer = onErrorContainerLight,
//    background = backgroundLight,
//    onBackground = onBackgroundLight,
//    surface = surfaceLight,
//    onSurface = onSurfaceLight,
//    surfaceVariant = surfaceVariantLight,
//    onSurfaceVariant = onSurfaceVariantLight,
//    outline = outlineLight,
//    outlineVariant = outlineVariantLight,
//    scrim = scrimLight,
//    inverseSurface = inverseSurfaceLight,
//    inverseOnSurface = inverseOnSurfaceLight,
//    inversePrimary = inversePrimaryLight,
//    surfaceDim = surfaceDimLight,
//    surfaceBright = surfaceBrightLight,
//    surfaceContainerLowest = surfaceContainerLowestLight,
//    surfaceContainerLow = surfaceContainerLowLight,
//    surfaceContainer = surfaceContainerLight,
//    surfaceContainerHigh = surfaceContainerHighLight,
//    surfaceContainerHighest = surfaceContainerHighestLight,
//)

@Composable
fun MDPingsTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    themeConfig: ThemeConfig = ThemeConfig(),
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        // 莫纳取色
        themeConfig.isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            when(themeConfig.themeMode) {
                ThemeMode.DARK -> dynamicDarkColorScheme(context)
                ThemeMode.LIGHT -> dynamicLightColorScheme(context)
                ThemeMode.SYSTEM -> if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }

        // 预设颜色
        themeConfig.themeMode == ThemeMode.DARK -> when (themeConfig.themeSeedColor) {
            ThemeSeedColor.GREEN -> darkColorSchemeGreen()
            ThemeSeedColor.YELLOW -> darkColorSchemeYellow()
            ThemeSeedColor.BLUE -> darkColorSchemeBlue()
            ThemeSeedColor.RED -> darkColorSchemeRed()
        }
        themeConfig.themeMode == ThemeMode.LIGHT -> when (themeConfig.themeSeedColor) {
            ThemeSeedColor.GREEN -> lightColorSchemeGreen()
            ThemeSeedColor.YELLOW -> lightColorSchemeYellow()
            ThemeSeedColor.BLUE -> lightColorSchemeBlue()
            ThemeSeedColor.RED -> lightColorSchemeRed()
        }
        themeConfig.themeMode == ThemeMode.SYSTEM && isDarkTheme -> when (themeConfig.themeSeedColor) {
            ThemeSeedColor.GREEN -> darkColorSchemeGreen()
            ThemeSeedColor.YELLOW -> darkColorSchemeYellow()
            ThemeSeedColor.BLUE -> darkColorSchemeBlue()
            ThemeSeedColor.RED -> darkColorSchemeRed()
        }
        else -> when (themeConfig.themeSeedColor) {
            ThemeSeedColor.GREEN -> lightColorSchemeGreen()
            ThemeSeedColor.YELLOW -> lightColorSchemeYellow()
            ThemeSeedColor.BLUE -> lightColorSchemeBlue()
            ThemeSeedColor.RED -> lightColorSchemeRed()
        }
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = LocalDensity.current.density,
            fontScale = 1f  // 强制使用1倍字体缩放
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}