package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.Sessio
import cat.padelgest.mobil.api.ApiClient
import cat.padelgest.mobil.model.Reserva
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.launch

// Les reserves del jugador que encara han de venir, amb un botó per cancel·lar-les
@Composable
fun MevesReserves(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    var reserves by remember { mutableStateOf(listOf<Reserva>()) }
    var missatge by remember { mutableStateOf("") }
    var carregant by remember { mutableStateOf(true) }
    var recarregar by remember { mutableStateOf(0) }
    // La reserva que el jugador vol cancel·lar (mentre surt el diàleg de confirmació)
    var reservaTriada by remember { mutableStateOf<Reserva?>(null) }
    // Resultat de la cancel·lació; mentre no és buit surt un diàleg
    var avis by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(recarregar) {
        carregant = true
        missatge = ""
        try {
            val resposta = ApiClient.api.meves()
            val totes = resposta.body()
            if (resposta.code() == 401) {
                Sessio.tancar()
                onSortir()
            } else if (!resposta.isSuccessful || totes == null) {
                missatge = ApiClient.missatgeError(resposta)
            } else {
                reserves = properes(totes)
            }
        } catch (e: IOException) {
            missatge = "No es pot connectar amb el servidor"
        }
        carregant = false
    }

    Column(modifier = modifier) {
        if (carregant) {
            CircularProgressIndicator()
        } else if (missatge.isNotEmpty()) {
            Text(missatge, color = MaterialTheme.colorScheme.error)
        } else if (reserves.isEmpty()) {
            Text("No tens cap reserva pendent")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reserves) { reserva ->
                    TargetaReserva(reserva, onCancelar = { reservaTriada = reserva })
                }
            }
        }
    }

    // Diàleg de confirmació: només surt quan hi ha una reserva triada
    val reserva = reservaTriada
    if (reserva != null) {
        AlertDialog(
            onDismissRequest = { reservaTriada = null },
            title = { Text("Cancel·lar reserva") },
            text = { Text("Segur que vols cancel·lar aquesta reserva?\n" + textReserva(reserva)) },
            confirmButton = {
                TextButton(onClick = {
                    reservaTriada = null
                    scope.launch {
                        avis = cancelar(reserva)
                        recarregar++
                    }
                }) {
                    Text("Sí, cancel·la-la")
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaTriada = null }) {
                    Text("No")
                }
            }
        )
    }

    // Diàleg amb el resultat de la cancel·lació
    if (avis.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { avis = "" },
            title = { Text("Cancel·lació") },
            text = { Text(avis) },
            confirmButton = {
                TextButton(onClick = { avis = "" }) {
                    Text("D'acord")
                }
            }
        )
    }
}

// Targeta d'una reserva: la pista, el dia i l'hora, i el botó de cancel·lar
@Composable
fun TargetaReserva(reserva: Reserva, onCancelar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(reserva.pista.nom, style = MaterialTheme.typography.titleMedium)
                Text(textDia(LocalDate.parse(reserva.data)) + ", de " + reserva.horaInici.take(5) + " a " + reserva.horaFi.take(5))
            }
            OutlinedButton(onClick = onCancelar) {
                Text("Cancel·lar")
            }
        }
    }
}

// Text curt d'una reserva per als diàlegs, per exemple "Pista 3, Demà a les 10:00"
fun textReserva(reserva: Reserva): String {
    return reserva.pista.nom + ", " + textDia(LocalDate.parse(reserva.data)) + " a les " + reserva.horaInici.take(5)
}

// De totes les reserves, només les actives que encara no s'han acabat, de la més propera a la més llunyana
fun properes(totes: List<Reserva>): List<Reserva> {
    val avui = LocalDate.now()
    val ara = LocalTime.now()
    val resultat = totes.filter { reserva ->
        val dia = LocalDate.parse(reserva.data)
        val fi = LocalTime.parse(reserva.horaFi)
        reserva.estat == "ACTIVA" && (dia.isAfter(avui) || (dia == avui && fi.isAfter(ara)))
    }
    // La data (2026-11-05) i l'hora (10:00:00) en text ja s'ordenen bé com a text
    return resultat.sortedBy { reserva -> reserva.data + " " + reserva.horaInici }
}

// Envia la cancel·lació al servidor i torna el text que veurà el jugador
suspend fun cancelar(reserva: Reserva): String {
    return try {
        val resposta = ApiClient.api.cancelar(reserva.id)
        if (resposta.isSuccessful) {
            "Reserva cancel·lada: " + textReserva(reserva)
        } else if (resposta.code() == 401) {
            "La sessió ha caducat. Tanca la sessió i torna a entrar."
        } else {
            ApiClient.missatgeError(resposta)
        }
    } catch (e: IOException) {
        "No es pot connectar amb el servidor"
    }
}