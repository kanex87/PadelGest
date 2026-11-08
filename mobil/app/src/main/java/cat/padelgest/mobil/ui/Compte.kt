package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.Sessio
import cat.padelgest.mobil.api.ApiClient
import cat.padelgest.mobil.model.CanviContrasenyaRequest
import java.io.IOException
import kotlinx.coroutines.launch

// Pestanya del compte: canviar la contrasenya i tancar la sessió
@Composable
fun Compte(onSortir: () -> Unit, modifier: Modifier = Modifier) {
    var actual by remember { mutableStateOf("") }
    var nova by remember { mutableStateOf("") }
    var repetida by remember { mutableStateOf("") }
    var missatge by remember { mutableStateOf("") }
    var esperant by remember { mutableStateOf(false) }
    // Mentre és true surt el diàleg "Contrasenya canviada"
    var fet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // verticalScroll: si el teclat tapa la pantalla, el jugador pot fer lliscar el formulari
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text("Canviar la contrasenya", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = actual,
            onValueChange = { actual = it },
            label = { Text("Contrasenya actual") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = nova,
            onValueChange = { nova = it },
            label = { Text("Contrasenya nova (mínim 8 caràcters)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = repetida,
            onValueChange = { repetida = it },
            label = { Text("Repeteix la contrasenya nova") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (actual.isEmpty() || nova.isEmpty()) {
                    missatge = "Omple tots els camps"
                } else if (nova != repetida) {
                    missatge = "Les dues contrasenyes noves no coincideixen"
                } else {
                    esperant = true
                    missatge = ""
                    scope.launch {
                        val error = canviarContrasenya(actual, nova)
                        esperant = false
                        if (error == null) {
                            // Ha anat bé: buidem el formulari i ho diem amb un diàleg
                            actual = ""
                            nova = ""
                            repetida = ""
                            fet = true
                        } else {
                            missatge = error
                        }
                    }
                }
            },
            enabled = !esperant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Canviar la contrasenya")
        }

        if (missatge.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(missatge, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(32.dp))
        OutlinedButton(
            onClick = {
                Sessio.tancar()
                onSortir()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tancar sessió")
        }
    }

    if (fet) {
        AlertDialog(
            onDismissRequest = { fet = false },
            title = { Text("Contrasenya") },
            text = { Text("Contrasenya canviada. La propera vegada entra amb la nova.") },
            confirmButton = {
                TextButton(onClick = { fet = false }) {
                    Text("D'acord")
                }
            }
        )
    }
}

// Envia el canvi al servidor. Torna null si ha anat bé, o el missatge d'error
suspend fun canviarContrasenya(actual: String, nova: String): String? {
    return try {
        val resposta = ApiClient.api.canviarContrasenya(CanviContrasenyaRequest(actual, nova))
        if (resposta.isSuccessful) {
            null
        } else if (resposta.code() == 401) {
            "La sessió ha caducat. Tanca la sessió i torna a entrar."
        } else {
            ApiClient.missatgeError(resposta)
        }
    } catch (e: IOException) {
        "No es pot connectar amb el servidor"
    }
}