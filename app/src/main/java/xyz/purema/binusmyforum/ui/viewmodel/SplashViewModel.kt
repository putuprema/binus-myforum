package xyz.purema.binusmyforum.ui.viewmodel

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.worker.RefreshTokenWorker
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.student.Student
import xyz.purema.binusmyforum.domain.repository.StudentRepository

class SplashViewModel
@ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val studentRepository: StudentRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {
    private val _state: MutableLiveData<AuthState<Student>> = MutableLiveData()
    val state: LiveData<AuthState<Student>> get() = _state

    fun publishEvent(event: SplashViewEvent) {
        viewModelScope.launch {
            when (event) {
                is SplashViewEvent.ResumeSession -> resumeSession()
            }
        }
    }

    private fun resumeSession() {
        viewModelScope.launch {
            _state.value = try {
                val lastEmail = sharedPrefs.lastEmail
                if (lastEmail != null) {
                    val profile = studentRepository.getProfile()

                    // schedule refresh token job
                    WorkManager.getInstance(context)
                        .enqueueUniqueWork(
                            RefreshTokenWorker::class.java.simpleName,
                            ExistingWorkPolicy.KEEP,
                            OneTimeWorkRequestBuilder<RefreshTokenWorker>().build()
                        )

                    AuthState.Success(profile)
                } else {
                    AuthState.IsFirstLogin
                }
            } catch (ex: AppException) {
                ex.printStackTrace()
                AuthState.ResumeSessionError(ex)
            }
        }
    }
}

sealed class SplashViewEvent {
    object ResumeSession : SplashViewEvent()
}

sealed class AuthState<out R> {
    data class Success<out T>(val data: T) : AuthState<T>()
    data class ResumeSessionError(val exception: Exception) : AuthState<Nothing>()
    object IsFirstLogin : AuthState<Nothing>()
}