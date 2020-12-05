package xyz.purema.binusmyforum.ui.viewmodel

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.data.worker.ForumDataSyncWorker
import xyz.purema.binusmyforum.domain.DataState
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.forum.GslcForumQueryResult
import xyz.purema.binusmyforum.domain.repository.ForumRepository
import xyz.purema.binusmyforum.domain.repository.StudentRepository

@ExperimentalCoroutinesApi
class HomeViewModel
@ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val forumRepository: ForumRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {
    private val _forumListState: MutableLiveData<DataState<GslcForumQueryResult>> =
        MutableLiveData()
    val forumListState: LiveData<DataState<GslcForumQueryResult>> get() = _forumListState

    private val _logoutState: MutableLiveData<DataState<*>> = MutableLiveData()
    val logoutState: LiveData<DataState<*>> get() = _logoutState

    val forumSyncWork =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(ForumDataSyncWorker.TAG)

    fun publishEvent(event: HomeViewEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeViewEvent.GetGslcForum -> getForumList()
                is HomeViewEvent.Logout -> logout()
                is HomeViewEvent.RefreshForumListCache -> refreshForumListCache()
            }
        }
    }

    private suspend fun refreshForumListCache() {
        forumRepository.syncForumData()
    }

    private fun getForumList() {
        viewModelScope.launch {
            _forumListState.value = DataState.Loading
            _forumListState.value = try {
                val result = forumRepository.getGslcForum()
                DataState.Success(result)
            } catch (ex: AppException) {
                DataState.Error(ex)
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            studentRepository.logout()
            _logoutState.value = DataState.Success(null)
        }
    }
}

sealed class HomeViewEvent {
    object GetGslcForum : HomeViewEvent()
    object RefreshForumListCache : HomeViewEvent()
    object Logout : HomeViewEvent()
}