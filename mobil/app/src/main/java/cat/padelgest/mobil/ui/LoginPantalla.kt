package cat.padelgest.mobil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import cat.padelgest.mobil.Sessio
import cat.padelgest.mobil.api.ApiClient
import cat.padelgest.mobil.model.LoginRequest
import java.io.IOException
import kotlinx.coroutines.launch

// Pantalla d'inici de sessió
@Composable
fun LoginPantalla(onEntrar: () -> Unit, onRegistre: () -> Unit, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var contrasenya by remember { mutableStateOf("") }
    var missatge by remember { mutableStateOf("") }
    var esperant by remember { mutableStateOf(false) }
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
            label = { Text("Contrasenya") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || contrasenya.isEmpty()) {
                    missatge = "Escriu l'email i la contrasenya"
                } else {
                    esperant = true
                    scope.launch {
                        val error = entrar(email.trim(), contrasenya)
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
            Text("Entrar")
        }
        TextButton(onClick = onRegistre) {
            Text("No tens compte? Registra't")
        }

        if (missatge.isNotEmpty()) {
            Text(missatge, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Fa el login i desa la sessió. Torna null si ha anat bé, o el missatge d'error
suspend fun entrar(email: String, contrasenya: String): String? {
    return try {
        val resposta = ApiClient.api.login(LoginRequest(email, contrasenya))
        val dades = resposta.body()
        if (resposta.isSuccessful && dades != null) {
            Sessio.desar(dades.token, dades.nom)
            null
        } else {
            ApiClient.missatgeError(resposta)
        }
    } catch (e: IOException) {
        "No es pot connectar amb el servidor"
    }
}