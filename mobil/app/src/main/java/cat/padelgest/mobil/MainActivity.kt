package cat.padelgest.mobil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cat.padelgest.mobil.ui.IniciPantalla
import cat.padelgest.mobil.ui.LoginPantalla
import cat.padelgest.mobil.ui.RegistrePantalla
import cat.padelgest.mobil.ui.theme.PadelGestTheme

// Activitat principal: és el que s'obre quan el jugador toca la icona de l'app
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sessio.iniciar(this)
        enableEdgeToEdge()
        setContent {
            PadelGestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { espais ->
                    App(modifier = Modifier.padding(espais))
                }
            }
        }
    }
}

// Les pantalles que té l'app
enum class Pantalla { LOGIN, REGISTRE, INICI }

// Decideix quina pantalla s'ensenya i com es passa d'una a l'altra
@Composable
fun App(modifier: Modifier = Modifier) {
    var pantalla by remember {
        mutableStateOf(if (Sessio.token == null) Pantalla.LOGIN else Pantalla.INICI)
    }

    when (pantalla) {
        Pantalla.LOGIN -> LoginPantalla(
            onEntrar = { pantalla = Pantalla.INICI },
            onRegistre = { pantalla = Pantalla.REGISTRE },
            modifier = modifier
        )
        Pantalla.REGISTRE -> RegistrePantalla(
            onEntrar = { pantalla = Pantalla.INICI },
            onTornar = { pantalla = Pantalla.LOGIN },
            modifier = modifier
        )
        Pantalla.INICI -> IniciPantalla(
            onSortir = { pantalla = Pantalla.LOGIN },
            modifier = modifier
        )
    }
}