package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

// Pantalla principal del jugador: la salutació i la disponibilitat de les pistes
@Composable
fun IniciPantalla(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    var salutacio by remember { mutableStateOf("Hola, " + Sessio.nom) }

    // En obrir la pantalla, comprovem amb el servidor que el token encara és vàlid
    LaunchedEffect(Unit) {
        try {
            val resposta = ApiClient.api.jo()
            val jo = resposta.body()
            if (resposta.isSuccessful && jo != null) {
                salutacio = "Hola, " + jo.nom
            } else if (resposta.code() == 401) {
                // El token ha caducat: cal tornar a iniciar sessió
                Sessio.tancar()
                onSortir()
            }
        } catch (e: IOException) {
            // Sense connexió: la disponibilitat ja ensenyarà l'error
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(salutacio, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = {
                Sessio.tancar()
                onSortir()
            }) {
                Text("Tancar sessió")
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Reserva una pista", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Disponibilitat(onSortir = onSortir, modifier = Modifier.weight(1f))
    }
}