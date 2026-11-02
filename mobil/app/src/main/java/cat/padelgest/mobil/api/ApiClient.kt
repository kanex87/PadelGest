package cat.padelgest.mobil.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objecte únic que crea la connexió amb el servidor
object ApiClient {

    // 10.0.2.2 és, des de l'emulador, l'ordinador on corre el servidor
    private const val URL_BASE = "http://10.0.2.2:8080/api/"

    val api: ApiService = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}