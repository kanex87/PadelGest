package cat.padelgest.mobil.model

// Dades que enviem al servidor per iniciar sessió (POST /api/auth/login)
data class LoginRequest(val email: String, val contrasenya: String)