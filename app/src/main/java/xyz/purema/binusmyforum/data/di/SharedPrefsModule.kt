package xyz.purema.binusmyforum.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SharedPrefsModule {
    @Singleton
    @Provides
    fun sharedPrefs(@ApplicationContext context: Context) = SharedPrefs(context)
}