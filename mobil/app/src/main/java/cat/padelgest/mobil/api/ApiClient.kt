package cat.padelgest.mobil.api

import cat.padelgest.mobil.Sessio
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objecte únic que crea la connexió amb el servidor
object ApiClient {

    // 10.0.2.2 és, des de l'emulador, l'ordinador on corre el servidor
    private const val URL_BASE = "http://10.0.2.2:8080/api/"

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