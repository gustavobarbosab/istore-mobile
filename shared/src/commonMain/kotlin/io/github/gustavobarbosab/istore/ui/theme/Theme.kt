package io.github.gustavobarbosab.istore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IStoreLightColorScheme = lightColorScheme(
    primary = RedPrimaryLight,
    onPrimary = OnRedPrimaryLight,
    primaryContainer = RedPrimaryContainerLight,
    onPrimaryContainer = OnRedPrimaryContainerLight,
    secondary = RedSecondaryLight,
    onSecondary = OnRedSecondaryLight,
    secondaryContainer = RedSecondaryContainerLight,
    onSecondaryContainer = OnRedSecondaryContainerLight,
    tertiary = RedTertiaryLight,
    onTertiary = OnRedTertiaryLight,
    tertiaryContainer = RedTertiaryContainerLight,
    onTertiaryContainer = OnRedTertiaryContainerLight,
    background = RedBackgroundLight,
    onBackground = OnRedBackgroundLight,
    surface = RedSurfaceLight,
    onSurface = OnRedSurfaceLight,
)

private val IStoreDarkColorScheme = darkColorScheme(
    primary = RedPrimaryDark,
    onPrimary = OnRedPrimaryDark,
    primaryContainer = RedPrimaryContainerDark,
    onPrimaryContainer = OnRedPrimaryContainerDark,
    secondary = RedSecondaryDark,
    onSecondary = OnRedSecondaryDark,
    secondaryContainer = RedSecondaryContainerDark,
    onSecondaryContainer = OnRedSecondaryContainerDark,
    tertiary = RedTertiaryDark,
    onTertiary = OnRedTertiaryDark,
    tertiaryContainer = RedTertiaryContainerDark,
    onTertiaryContainer = OnRedTertiaryContainerDark,
    background = RedBackgroundDark,
    onBackground = OnRedBackgroundDark,
    surface = RedSurfaceDark,
    onSurface = OnRedSurfaceDark,
)

/**
 * App-wide theme: red as the primary/brand color (replaces the Material
 * baseline purple), light/dark aware. Wrap the whole app with this instead of
 * a bare `MaterialTheme { ... }`.
 */
@Composable
fun IStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) IStoreDarkColorScheme else IStoreLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
