package cl.duoc.milsabores.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthApiClient {

    // ⚠ Ajusta esta URL a la de tu backend:
    // - Emulador: "http://10.0.2.2:8081/"
    // - Dispositivo físico: IP de tu PC + puerto
    private const val BASE_URL = "http://10.0.2.2:8081/"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 👇 Aquí estaba el problema: antes usabas AuthApi (no existe)
    val api: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
