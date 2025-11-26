package cl.duoc.milsabores.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ============================
    //   BASE URL - AWS EC2
    // ============================

    private const val AUTH_BASE_URL = "http://3.236.240.163:8081/"
    private const val PRODUCT_BASE_URL = "http://3.236.240.163:8082/"
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/v1/"

    // ============================
    // LOGGING
    // ============================

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // ============================
    //   INTERCEPTOR (TOKEN FUTURO)
    // ============================

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            // Ej: .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(request)
    }

    // ============================
    //   OKHTTP CLIENT
    // ============================

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    // ============================
    //   GENERAR INSTANCIAS RETROFIT
    // ============================

    private fun buildRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Servicios
    private val retrofitAuth = buildRetrofit(AUTH_BASE_URL)
    private val retrofitProduct = buildRetrofit(PRODUCT_BASE_URL)
    private val retrofitWeather = buildRetrofit(WEATHER_BASE_URL)

    val authApi: AuthApiService = retrofitAuth.create(AuthApiService::class.java)
    val productApi: ProductApiService = retrofitProduct.create(ProductApiService::class.java)

    // ============================
    //   API CLIMA (sin cambios)
    // ============================

    interface WeatherApiService {
        @GET("forecast")
        suspend fun getCurrentWeather(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("current_weather") currentWeather: Boolean = true
        ): WeatherResponse
    }

    data class WeatherResponse(
        val latitude: Double,
        val longitude: Double,
        val current_weather: CurrentWeather
    )

    data class CurrentWeather(
        val temperature: Double,
        val windspeed: Double,
        val weathercode: Int
    )

    val weatherApi: WeatherApiService = retrofitWeather.create(WeatherApiService::class.java)
}
