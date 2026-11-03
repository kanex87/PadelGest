package cat.padelgest.mobil.model

// Dades per crear un compte de jugador (POST /api/auth/registre)
data class RegistreRequest(
    val nom: String,
    val cognoms: String,
    val email: String,
    val contrasenya: String
)