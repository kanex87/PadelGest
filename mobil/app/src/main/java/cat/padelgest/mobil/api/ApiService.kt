package cat.padelgest.mobil.api

import cat.padelgest.mobil.model.FranjaLliure
import cat.padelgest.mobil.model.LoginRequest
import cat.padelgest.mobil.model.LoginResponse
import cat.padelgest.mobil.model.Pista
import cat.padelgest.mobil.model.RegistreRequest
import cat.padelgest.mobil.model.Reserva
import cat.padelgest.mobil.model.ReservaRequest
import cat.padelgest.mobil.model.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    // Totes les pistes del club
    @GET("pistes")
    suspend fun pistes(): Response<List<Pista>>

    // Franges lliures d'una pista en un dia (la data en format 2026-11-04)
    @GET("pistes/{id}/disponibilitat")
    suspend fun disponibilitat(@Path("id") idPista: Long, @Query("data") data: String): Response<List<FranjaLliure>>

    // Reserva una pista per al jugador del token
    @POST("reserves")
    suspend fun reservar(@Body dades: ReservaRequest): Response<Reserva>
}