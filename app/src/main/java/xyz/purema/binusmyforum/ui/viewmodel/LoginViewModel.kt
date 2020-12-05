package xyz.purema.binusmyforum.ui.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.student.Student
import xyz.purema.binusmyforum.domain.repository.StudentRepository

@ExperimentalCoroutinesApi
class LoginViewModel
@ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository
) : ViewModel() {
    private val _state: MutableLiveData<AuthStage> = MutableLiveData()
    val state: LiveData<AuthStage> get() = _state

    fun publishEvent(event: LoginViewEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginViewEvent.Login -> login(event.email, event.password)
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthStage.Login
            try {
                val profile = studentRepository.login(email, password)
                _state.value = AuthStage.SyncAccountData

                studentRepository.syncStudentData(profile)
                _state.value = AuthStage.Done(profile)
            } catch (ex: AppException) {
                _state.value = AuthStage.Error(ex)
            }
        }
    }
}

sealed class LoginViewEvent {
    data class Login(val email: String, val password: String) : LoginViewEvent()
}

sealed class AuthStage {
    object Login : AuthStage()
    object SyncAccountData : AuthStage()
    data class Done(val student: Student) : AuthStage()
    data class Error(val exception: Exception) : AuthStage()
}