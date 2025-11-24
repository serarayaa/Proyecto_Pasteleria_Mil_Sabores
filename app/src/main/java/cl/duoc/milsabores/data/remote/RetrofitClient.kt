package cl.duoc.milsabores.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulador Android → localhost de tu PC
    private const val AUTH_BASE_URL = "http://10.0.2.2:8085/"    // auth-service
    private const val PRODUCT_BASE_URL = "http://10.0.2.2:8087/" // product-service

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // APIs disponibles
    val authApi: AuthApiService by lazy {
        buildRetrofit(AUTH_BASE_URL).create(AuthApiService::class.java)
    }

    val productApi: ProductApiService by lazy {
        buildRetrofit(PRODUCT_BASE_URL).create(ProductApiService::class.java)
    }
}
