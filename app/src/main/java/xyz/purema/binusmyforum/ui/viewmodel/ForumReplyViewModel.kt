package xyz.purema.binusmyforum.ui.viewmodel

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.domain.DataState
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.domain.repository.ForumRepository

@ExperimentalCoroutinesApi
class ForumReplyViewModel
@ViewModelInject constructor(
    private val forumRepository: ForumRepository
) : ViewModel() {
    private val _replyState: MutableLiveData<DataState<Any?>> = MutableLiveData()
    val replyState: LiveData<DataState<Any?>> get() = _replyState

    fun publishEvent(event: ForumReplyViewEvent) {
        viewModelScope.launch {
            when (event) {
                is ForumReplyViewEvent.ReplyForum -> replyForum(event.forum, event.attachmentUri)
            }
        }
    }

    private fun replyForum(forum: ForumThread, attachmentUri: Uri?) {
        viewModelScope.launch {
            _replyState.value = DataState.Loading
            _replyState.value = try {
                forumRepository.replyForum(forum, attachmentUri)
                DataState.Success(null)
            } catch (ex: AppException) {
                DataState.Error(ex)
            }
        }
    }
}

sealed class ForumReplyViewEvent {
    data class ReplyForum(val forum: ForumThread, val attachmentUri: Uri?) : ForumReplyViewEvent()
}