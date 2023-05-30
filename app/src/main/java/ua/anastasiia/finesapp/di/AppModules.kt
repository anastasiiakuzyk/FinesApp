package ua.anastasiia.finesapp.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ua.anastasiia.finesapp.BuildConfig
import ua.anastasiia.finesapp.web.CarService
import ua.anastasiia.finesapp.web.Manager

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    fun provideRetrofit(): Retrofit {

        val baseUrl = "https://api.platerecognizer.com/v1/"
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header(
                    "Authorization", "TOKEN ${BuildConfig.TOKEN}"
                )
                return@Interceptor chain.proceed(builder.build())
            })
        }.build()

        return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl).client(httpClient).build()
    }

    @Provides
    fun provideCarService(
        retrofit: Retrofit
    ): CarService = retrofit.create(CarService::class.java)

    @Provides
    fun provideManager(
        carService: CarService,
    ): Manager = Manager(service = carService)
}