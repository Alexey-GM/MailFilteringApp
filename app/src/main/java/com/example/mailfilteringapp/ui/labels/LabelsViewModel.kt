package com.example.mailfilteringapp.ui.labels

import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LabelsViewModel(private val mailsRepository: MailsRepository) : ViewModel() {
    private val _labels: MutableLiveData<List<Label>> = MutableLiveData()
    val labels: LiveData<List<Label>> get() = _labels
    private val _isError: MutableLiveData<Event<String>> = MutableLiveData()
    val isError: LiveData<Event<String>> get() = _isError

    init {
        getLabels()
    }

    private fun getLabels() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mailsRepository.getLabels("me")) {
                is Result.Success -> _labels.postValue(result.data!!)
                is Result.Error -> _isError.postValue(Event(result.message))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class LabelsViewModelFactory(
    private val mailsRepository: MailsRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = LabelsViewModel(mailsRepository)
        return viewModel as T
    }
}