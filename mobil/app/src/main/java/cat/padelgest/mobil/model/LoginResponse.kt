package cat.padelgest.mobil.model

// Resposta del login: el token i les dades bàsiques de l'usuari
data class LoginResponse(val token: String, val id: Long, val nom: String, val rol: String)