package cat.padelgest.mobil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Verd80,
    secondary = VerdGris80,
    tertiary = Groc80
)

private val LightColorScheme = lightColorScheme(
    primary = Verd40,
    secondary = VerdGris40,
    tertiary = Groc40
)

// Tema de l'app: sempre els colors de PadelGest, clars o foscos segons el mòbil
@Composable
fun PadelGestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}