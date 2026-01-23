package n7.glyphicons.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DiscordBlurple,
    onPrimary = White,
    background = DiscordDarkest,
    onBackground = White,
    surface = DiscordDarker,
    onSurface = White,
    surfaceVariant = DiscordDark,
    onSurfaceVariant = White,
    secondary = DiscordGray,
    onSecondary = White
)

private val LightColorScheme = lightColorScheme(
    primary = DiscordBlurple,
    onPrimary = White,
    background = White,
    onBackground = DiscordDarkest,
    surface = White,
    onSurface = DiscordDarkest,
    surfaceVariant = DiscordGray,
    onSurfaceVariant = White,
    secondary = DiscordDark,
    onSecondary = White
)

@Composable
fun GlyphIconsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
