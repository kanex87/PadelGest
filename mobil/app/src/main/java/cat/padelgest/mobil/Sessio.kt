package cat.padelgest.mobil

import android.content.Context
import android.content.SharedPreferences

// Guarda el token i el nom de l'usuari mentre l'app està oberta i quan es tanca
object Sessio {

    private lateinit var preferencies: SharedPreferences

    var token: String? = null
        private set

    var nom: String? = null
        private set

    // Llegeix la sessió desada al mòbil (es crida en obrir l'app)
    fun iniciar(context: Context) {
        preferencies = context.getSharedPreferences("sessio", Context.MODE_PRIVATE)
        token = preferencies.getString("token", null)
        nom = preferencies.getString("nom", null)
    }

    // Desa el token i el nom després d'un login correcte
    fun desar(nouToken: String, nouNom: String) {
        token = nouToken
        nom = nouNom
        preferencies.edit()
            .putString("token", nouToken)
            .putString("nom", nouNom)
            .apply()
    }

    // Oblida la sessió (tancar sessió o token caducat)
    fun tancar() {
        token = null
        nom = null
        preferencies.edit().clear().apply()
    }
}