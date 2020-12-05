package xyz.purema.binusmyforum.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.remote.BinusmayaApiClient
import xyz.purema.binusmyforum.data.remote.BinusmayaRemoteService
import xyz.purema.binusmyforum.data.remote.crypto.CryptoService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RemoteServiceModule {
    @Singleton
    @Provides
    fun binusmayaApiClient(retrofit: Retrofit.Builder): BinusmayaApiClient {
        return retrofit.build().create(BinusmayaApiClient::class.java)
    }

    @Singleton
    @Provides
    fun binusmayaRemoteService(
        cryptoService: CryptoService,
        binusmayaApiClient: BinusmayaApiClient,
        sharedPrefs: SharedPrefs
    ): BinusmayaRemoteService {
        return BinusmayaRemoteService(binusmayaApiClient, cryptoService, sharedPrefs)
    }
}