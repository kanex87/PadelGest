package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.Sessio
import cat.padelgest.mobil.api.ApiClient
import java.io.IOException

// Pantalla principal del jugador (de moment, només la salutació)
@Composable
fun IniciPantalla(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("Hola, " + Sessio.nom) }

    // En obrir la pantalla, comprovem amb el servidor que el token encara és vàlid
    LaunchedEffect(Unit) {
        try {
            val resposta = ApiClient.api.jo()
            val jo = resposta.body()
            if (resposta.isSuccessful && jo != null) {
                text = "Hola, " + jo.nom + " " + jo.cognoms + "\n" + jo.email
            } else if (resposta.code() == 401) {
                // El token ha caducat: cal tornar a iniciar sessió
                Sessio.tancar()
                onSortir()
            } else {
                text = ApiClient.missatgeError(resposta)
            }
        } catch (e: IOException) {
            text = "No es pot connectar amb el servidor"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PadelGest", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Text(text)
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = {
            Sessio.tancar()
            onSortir()
        }) {
            Text("Tancar sessió")
        }
    }
}