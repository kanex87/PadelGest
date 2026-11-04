package cat.padelgest.mobil.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import cat.padelgest.mobil.model.FranjaLliure
import cat.padelgest.mobil.model.Pista
import java.io.IOException
import java.time.LocalDate

// Una pista activa amb les seves franges lliures del dia triat
data class PistaAmbFranges(val pista: Pista, val franges: List<FranjaLliure>)

// Noms curts dels dies de la setmana, de dilluns a diumenge
private val NOMS_DIES = listOf("dl", "dt", "dc", "dj", "dv", "ds", "dg")

// Text del botó d'un dia: "Avui", "Demà" o, per exemple, "dj 5/11"
fun textDia(dia: LocalDate): String {
    val avui = LocalDate.now()
    return when (dia) {
        avui -> "Avui"
        avui.plusDays(1) -> "Demà"
        else -> NOMS_DIES[dia.dayOfWeek.value - 1] + " " + dia.dayOfMonth + "/" + dia.monthValue
    }
}

// Triar un dia i veure les hores lliures de cada pista
@Composable
fun Disponibilitat(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    val avui = LocalDate.now()
    var dia by remember { mutableStateOf(avui) }
    var pistes by remember { mutableStateOf(listOf<PistaAmbFranges>()) }
    var missatge by remember { mutableStateOf("") }
    var carregant by remember { mutableStateOf(true) }

    // Es torna a executar cada vegada que canvia el dia
    LaunchedEffect(dia) {
        carregant = true
        missatge = ""
        try {
            val resposta = ApiClient.api.pistes()
            val totes = resposta.body()
            if (resposta.code() == 401) {
                // El token ha caducat
                Sessio.tancar()
                onSortir()
            } else if (!resposta.isSuccessful || totes == null) {
                missatge = ApiClient.missatgeError(resposta)
            } else {
                val resultat = mutableListOf<PistaAmbFranges>()
                for (pista in totes) {
                    if (pista.activa) {
                        val franges = ApiClient.api.disponibilitat(pista.id, dia.toString()).body() ?: listOf()
                        resultat.add(PistaAmbFranges(pista, franges))
                    }
                }
                pistes = resultat
            }
        } catch (e: IOException) {
            missatge = "No es pot connectar amb el servidor"
        }
        carregant = false
    }

    Column(modifier = modifier) {
        // Els propers 7 dies; el triat surt ple i els altres només amb la vora
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            for (i in 0..6) {
                val unDia = avui.plusDays(i.toLong())
                if (unDia == dia) {
                    Button(onClick = {}) {
                        Text(textDia(unDia))
                    }
                } else {
                    OutlinedButton(onClick = { dia = unDia }) {
                        Text(textDia(unDia))
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.height(16.dp))

        if (carregant) {
            CircularProgressIndicator()
        } else if (missatge.isNotEmpty()) {
            Text(missatge, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pistes) { dades ->
                    TargetaPista(dades)
                }
            }
        }
    }
}

// Targeta d'una pista: el nom, el tipus i les hores lliures
@Composable
fun TargetaPista(dades: PistaAmbFranges) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(dades.pista.nom, style = MaterialTheme.typography.titleMedium)
            Text(dades.pista.tipus.lowercase(), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))

            if (dades.franges.isEmpty()) {
                Text("No hi ha hores lliures")
            } else {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    for (franja in dades.franges) {
                        // De moment no fa res: demà servirà per reservar
                        OutlinedButton(onClick = {}) {
                            Text(franja.horaInici.take(5))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}