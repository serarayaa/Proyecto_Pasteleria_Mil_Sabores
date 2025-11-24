package cl.duoc.milsabores.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulador Android -> tu notebook (localhost)
    private const val AUTH_BASE_URL = "http://10.0.2.2:8085/"     // auth-service
    private const val PRODUCT_BASE_URL = "http://10.0.2.2:8087/"  // product-service

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Retrofit para cada microservicio
    private val retrofitAuth: Retrofit = buildRetrofit(AUTH_BASE_URL)
    private val retrofitProduct: Retrofit = buildRetrofit(PRODUCT_BASE_URL)

    // APIs públicas
    val authApi: AuthApiService = retrofitAuth.create(AuthApiService::class.java)
    val productApi: ProductApiService = retrofitProduct.create(ProductApiService::class.java)
}
