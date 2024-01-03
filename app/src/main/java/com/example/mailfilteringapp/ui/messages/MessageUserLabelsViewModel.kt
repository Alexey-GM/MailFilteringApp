package com.example.mailfilteringapp.ui.messages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mailfilteringapp.domain.MailsRepository
import com.example.mailfilteringapp.domain.model.Result
import com.example.mailfilteringapp.ui.generic.Event
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessageUserLabelsViewModel(private val mailsRepository: MailsRepository) : ViewModel() {
    private val _messages: MutableLiveData<List<Message>> = MutableLiveData()
    val messages: LiveData<List<Message>> get() = _messages
    private val _isError: MutableLiveData<Event<String>> = MutableLiveData()
    val isError: LiveData<Event<String>> get() = _isError
    private val _nextPageToken: MutableLiveData<String?> = MutableLiveData()
    private val nextPageToken: LiveData<String?> get() = _nextPageToken

    fun getMessages(labelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mailsRepository.getMessages(labelId, nextPageToken.value)) {
                is Result.Success -> {
                    _messages.postValue(result.data.messages!!)
                    _nextPageToken.postValue(result.data.nextPageToken)
                }
                is Result.Error -> _isError.postValue(Event(result.message))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MessageUserLabelsViewModelFactory(
    private val mailsRepository: MailsRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MessageUserLabelsViewModel(mailsRepository)
        return viewModel as T
    }
}