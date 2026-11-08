package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.Sessio
import cat.padelgest.mobil.api.ApiClient
import java.io.IOException

// Les tres pestanyes de la pantalla principal, amb el text del seu botó
enum class Pestanya(val text: String) {
    RESERVAR("Reservar"),
    RESERVES("Reserves"),
    COMPTE("Compte")
}

// Pantalla principal del jugador: la salutació, els botons de les pestanyes i la pestanya triada
@Composable
fun IniciPantalla(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    var salutacio by remember { mutableStateOf("Hola, " + Sessio.nom) }
    var pestanya by remember { mutableStateOf(Pestanya.RESERVAR) }

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
            // Sense connexió: la pestanya ja ensenyarà l'error
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(salutacio, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // Un botó per pestanya; el de la pestanya oberta surt ple, com els dies.
        // weight(1f) reparteix l'amplada a parts iguals entre els tres botons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (unaPestanya in Pestanya.entries) {
                if (unaPestanya == pestanya) {
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(unaPestanya.text)
                    }
                } else {
                    OutlinedButton(
                        onClick = { pestanya = unaPestanya },
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(unaPestanya.text)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        when (pestanya) {
            Pestanya.RESERVAR -> Disponibilitat(onSortir = onSortir, modifier = Modifier.weight(1f))
            Pestanya.RESERVES -> MevesReserves(onSortir = onSortir, modifier = Modifier.weight(1f))
            Pestanya.COMPTE -> Compte(onSortir = onSortir, modifier = Modifier.weight(1f))
        }
    }
}