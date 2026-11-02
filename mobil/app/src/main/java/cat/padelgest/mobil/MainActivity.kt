package cat.padelgest.mobil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.api.ApiClient
import cat.padelgest.mobil.model.LoginRequest
import cat.padelgest.mobil.ui.theme.PadelGestTheme
import java.io.IOException
import kotlinx.coroutines.launch

// Activitat principal: és el que s'obre quan el jugador toca la icona de l'app
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PadelGestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { espais ->
                    ProvaConnexio(modifier = Modifier.padding(espais))
                }
            }
        }
    }
}

// Pantalla provisional per comprovar que l'app parla amb el servidor
@Composable
fun ProvaConnexio(modifier: Modifier = Modifier) {
    var resultat by remember { mutableStateOf("Encara no s'ha provat") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PadelGest", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            // La petició va dins una corrutina perquè no bloquegi la pantalla
            scope.launch {
                resultat = provarLogin()
            }
        }) {
            Text("Provar connexió")
        }
        Spacer(Modifier.height(24.dp))
        Text(resultat)
    }
}

// Fa un login amb el jugador de prova i torna un text amb el resultat
suspend fun provarLogin(): String {
    return try {
        val resposta = ApiClient.api.login(LoginRequest("fernando@padelgest.cat", "jugador1234"))
        if (resposta.isSuccessful) {
            "Connectat! Hola, " + resposta.body()?.nom
        } else {
            "Error " + resposta.code() + ": " + resposta.errorBody()?.string()
        }
    } catch (e: IOException) {
        "No es pot connectar amb el servidor"
    }
}