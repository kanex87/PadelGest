package cat.padelgest.mobil.model

// Dades per reservar una pista (POST /api/reserves). El jugador el treu el servidor del token
data class ReservaRequest(
    val idPista: Long,
    val data: String,
    val horaInici: String,
    val horaFi: String
)