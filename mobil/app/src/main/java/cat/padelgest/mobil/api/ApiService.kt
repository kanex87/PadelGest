package cat.padelgest.mobil.api

import cat.padelgest.mobil.model.LoginRequest
import cat.padelgest.mobil.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Peticions que l'app pot fer al servidor
interface ApiService {

    // Inicia sessió i torna el token
    @POST("auth/login")
    suspend fun login(@Body dades: LoginRequest): Response<LoginResponse>
}