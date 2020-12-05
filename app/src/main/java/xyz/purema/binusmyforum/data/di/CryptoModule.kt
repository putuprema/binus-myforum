package xyz.purema.binusmyforum.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.purema.binusmyforum.data.remote.crypto.CryptoService
import xyz.purema.binusmyforum.data.remote.crypto.CryptoServiceImpl
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CryptoModule {
    @Singleton
    @Provides
    fun cryptoService(@ApplicationContext context: Context): CryptoService {
        // This module needs secret key and string used for IV (Initialization Vector) to
        // correctly encrypt/decrypt payload to/from BINUSMAYA. These two keys are loaded
        // from crypto.properties file located in src/main/assets directory.
        //
        // Please see crypto.properties.example file on that directory for instructions.
        // If you don't provide correct key for this module, the app will crash
        // at startup.

        val properties = Properties().also {
            it.load(context.assets.open("crypto.properties"))
        }

        return CryptoServiceImpl(
            secretKeyString = properties.getProperty("crypto.secret_key", ""),
            ivString = properties.getProperty("crypto.iv_string", "")
        )
    }
}