package cat.padelgest.mobil.model

// Dades d'una pista tal com les envia el servidor
data class Pista(val id: Long, val nom: String, val tipus: String, val activa: Boolean)