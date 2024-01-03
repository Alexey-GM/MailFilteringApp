package com.example.mailfilteringapp.ui.messages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mailfilteringapp.domain.MailsRepository
import com.example.mailfilteringapp.domain.model.MessagesToFilter
import com.example.mailfilteringapp.domain.model.Result
import com.example.mailfilteringapp.ui.generic.Event
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagesViewModel(private val mailsRepository: MailsRepository) : ViewModel() {
    private val _messages: MutableLiveData<List<Message>> = MutableLiveData()
    val messages: LiveData<List<Message>> get() = _messages
    private val _nextPageToken: MutableLiveData<String?> = MutableLiveData()
    val nextPageToken: LiveData<String?> get() = _nextPageToken
    private val _isError: MutableLiveData<Event<String>> = MutableLiveData()
    val isError: LiveData<Event<String>> get() = _isError
    private val _isUpdateSuccess: MutableLiveData<Event<String>> = MutableLiveData()
    val isUpdateSuccess: LiveData<Event<String>> get() = _isUpdateSuccess
    private val _isUpdateError: MutableLiveData<Event<String>> = MutableLiveData()
    val isUpdateError: LiveData<Event<String>> get() = _isUpdateError
    private var currentPageToken: String? = ""
    private var currentLabel: String = ""

    fun getMessages(labelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentLabel = labelId
            when (val result = mailsRepository.getMessages(labelId, nextPageToken.value)) {
                is Result.Success -> {
                    currentPageToken = nextPageToken.value
                    _messages.postValue(result.data.messages!!)
                    _nextPageToken.postValue(result.data.nextPageToken)
                }
                is Result.Error -> _isError.postValue(Event(result.message))
            }
        }
    }

    fun updateMessages(labelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mailsRepository.getMessages(labelId, currentPageToken)) {
                is Result.Success -> {
                    _messages.postValue(result.data.messages!!)
                }
                is Result.Error -> _isError.postValue(Event(result.message))
            }
        }
    }

    fun filterMessages(keywords: String?, senders: String?, labelId: String) {
        val messagesToFilter = mutableListOf<MessagesToFilter>()
        val keywordList = keywords?.split(",")?.map { it.trim() }
        val senderList = senders?.split(",")?.map { it.trim() }
        for (message in _messages.value ?: emptyList()) {
            val headers = message.payload.headers
            val from = headers.find { it.name == "From" }?.value ?: "No Sender"
            val snippet = message.snippet

            if ((keywordList == null || keywordList.any { snippet.contains(it, ignoreCase = true) }) &&
                (senderList == null || senderList.any { from.contains(it, ignoreCase = true) })) {
                messagesToFilter.add(MessagesToFilter(labelId, listOf(message.id)))
            }
        }

        if (messagesToFilter.isNotEmpty()) {
            updateLabels(messagesToFilter)
        } else {
            _isUpdateError.postValue(Event("Не было найдено подходящих сообщений"))
        }
    }

    private fun updateLabels(messages: List<MessagesToFilter>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mailsRepository.addLabelsToMessages("me", messages)) {
                is Result.Success -> {
                    _isUpdateSuccess.postValue(Event(result.data))
                    updateMessages(currentLabel)
                }
                is Result.Error -> _isUpdateError.postValue(Event(result.message))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MessagesViewModelFactory(
    private val mailsRepository: MailsRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MessagesViewModel(mailsRepository)
        return viewModel as T
    }
}