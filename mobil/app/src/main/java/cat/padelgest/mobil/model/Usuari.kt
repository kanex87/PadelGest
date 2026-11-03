package cat.padelgest.mobil.model

// Dades d'un usuari tal com les envia el servidor (sense la contrasenya)
data class Usuari(
    val id: Long,
    val nom: String,
    val cognoms: String,
    val email: String,
    val rol: String,
    val actiu: Boolean
)