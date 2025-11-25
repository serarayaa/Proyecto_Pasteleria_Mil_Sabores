package cl.duoc.milsabores.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val AUTH_BASE_URL = "http://10.0.2.2:8085/"
    private const val PRODUCT_BASE_URL = "http://10.0.2.2:8087/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            // Aquí agregarás token cuando lo guardemos
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val retrofitAuth = buildRetrofit(AUTH_BASE_URL)
    private val retrofitProduct = buildRetrofit(PRODUCT_BASE_URL)

    val authApi: AuthApiService = retrofitAuth.create(AuthApiService::class.java)
    val productApi: ProductApiService = retrofitProduct.create(ProductApiService::class.java)
}
