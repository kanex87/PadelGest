package cat.padelgest.mobil.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cat.padelgest.mobil.api.ApiClient
import cat.padelgest.mobil.model.RegistreRequest
import java.io.IOException
import kotlinx.coroutines.launch

// Pantalla per crear un compte de jugador
@Composable
fun RegistrePantalla(onEntrar: () -> Unit, onTornar: () -> Unit, modifier: Modifier = Modifier) {
    var nom by remember { mutableStateOf("") }
    var cognoms by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf("") }
    var missatge by remember { mutableStateOf("") }
    var esperant by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // El botó enrere del mòbil torna al login en lloc de tancar l'app
    BackHandler { onTornar() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear compte", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = cognoms,
            onValueChange = { cognoms = it },
            label = { Text("Cognoms") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = contrasenya,
            onValueChange = { contrasenya = it },
            label = { Text("Contrasenya (mínim 8 caràcters)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = repetir,
            onValueChange = { repetir = it },
            label = { Text("Repeteix la contrasenya") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (nom.isBlank() || cognoms.isBlank() || email.isBlank() || contrasenya.isEmpty()) {
                    missatge = "Omple tots els camps"
                } else if (contrasenya != repetir) {
                    missatge = "Les contrasenyes no coincideixen"
                } else {
                    esperant = true
                    scope.launch {
                        val error = registrar(nom.trim(), cognoms.trim(), email.trim(), contrasenya)
                        esperant = false
                        if (error == null) {
                            onEntrar()
                        } else {
                            missatge = error
                        }
                    }
                }
            },
            enabled = !esperant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear compte")
        }
        TextButton(onClick = onTornar) {
            Text("Ja tinc compte")
        }

        if (missatge.isNotEmpty()) {
            Text(missatge, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Crea el compte i, si va bé, inicia sessió. Torna null si tot ha anat bé, o el missatge d'error
suspend fun registrar(nom: String, cognoms: String, email: String, contrasenya: String): String? {
    return try {
        val resposta = ApiClient.api.registre(RegistreRequest(nom, cognoms, email, contrasenya))
        if (resposta.isSuccessful) {
            entrar(email, contrasenya)
        } else {
            ApiClient.missatgeError(resposta)
        }
    } catch (e: IOException) {
        "No es pot connectar amb el servidor"
    }
}