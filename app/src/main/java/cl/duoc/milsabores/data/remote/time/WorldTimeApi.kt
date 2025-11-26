package cl.duoc.milsabores.data.remote.time

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface WorldTimeApi {

    @GET("America/Santiago")
    suspend fun getChileTime(): WorldTimeResponse

    companion object {
        fun create(): WorldTimeApi {
            return Retrofit.Builder()
                .baseUrl("https://worldtimeapi.org/api/timezone/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WorldTimeApi::class.java)
        }
    }
}
