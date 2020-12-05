package xyz.purema.binusmyforum.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.purema.binusmyforum.BuildConfig
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.remote.converter.InstantTypeAdapter
import xyz.purema.binusmyforum.data.remote.converter.LocalDateTimeTypeAdapter
import xyz.purema.binusmyforum.data.remote.converter.LocalDateTypeAdapter
import xyz.purema.binusmyforum.domain.utils.ApiUtils
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
            .create()
    }

    @Singleton
    @Provides
    fun okHttpClient(sharedPrefs: SharedPrefs): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()

                val newRequestBuilder = original.newBuilder()
                    .method(original.method(), original.body())

                if (!original.url().encodedPath().contains("auth")) {
                    val authToken = sharedPrefs.accessToken
                    if (authToken != null) {
                        newRequestBuilder.header("Authorization", "Bearer $authToken")
                    }
                }

                it.proceed(newRequestBuilder.build())
            }
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun retrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("http://mobileapi2.apps.binus.edu")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
    }

    @Singleton
    @Provides
    fun apiUtils(gson: Gson): ApiUtils = ApiUtils(gson)
}