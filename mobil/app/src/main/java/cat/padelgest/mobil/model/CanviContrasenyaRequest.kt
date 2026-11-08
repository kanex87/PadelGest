package cat.padelgest.mobil.model

// El que enviem per canviar la contrasenya: l'actual (per seguretat) i la nova
data class CanviContrasenyaRequest(
    val contrasenyaActual: String,
    val contrasenyaNova: String
)