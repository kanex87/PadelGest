package cat.padelgest.mobil.model

// Una reserva tal com l'envia el servidor (l'usuari no el necessitem: és el jugador mateix)
data class Reserva(
    val id: Long,
    val pista: Pista,
    val data: String,
    val horaInici: String,
    val horaFi: String,
    val estat: String
)