package cat.padelgest.mobil.api

import cat.padelgest.mobil.model.LoginRequest
import cat.padelgest.mobil.model.LoginResponse
import cat.padelgest.mobil.model.RegistreRequest
import cat.padelgest.mobil.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Peticions que l'app pot fer al servidor (Retrofit en crea el codi)
interface ApiService {

    // Inicia sessió i torna el token
    @POST("auth/login")
    suspend fun login(@Body dades: LoginRequest): Response<LoginResponse>

    // Crea un compte de jugador nou
    @POST("auth/registre")
    suspend fun registre(@Body dades: RegistreRequest): Response<Usuari>

    // Dades de l'usuari del token
    @GET("auth/jo")
    suspend fun jo(): Response<Usuari>
}