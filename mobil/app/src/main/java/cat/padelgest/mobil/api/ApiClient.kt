package cat.padelgest.mobil.api

import cat.padelgest.mobil.Sessio
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Objecte únic que crea la connexió amb el servidor
object ApiClient {

    // Adreça del servidor desplegat a Render (amb HTTPS)
    private const val URL_BASE = "https://padelgest.onrender.com/api/"

    // Afegeix el token a totes les peticions, si hi ha una sessió oberta
    private val client = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val token = Sessio.token
            val peticio = if (token == null) {
                cadena.request()
            } else {
                cadena.request().newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build()

            }
            cadena.proceed(peticio)
        }
        // El servidor gratuït de Render s'atura si no el fa servir ningú i tarda fins a un minut a despertar, esperem 90 segons abans de donar error
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    // Text de l'error que ha enviat el servidor (o el codi, si no n'ha enviat cap)
    fun missatgeError(resposta: Response<*>): String {
        val text = resposta.errorBody()?.string()
        return if (text.isNullOrBlank()) "Error " + resposta.code() else text
    }
}