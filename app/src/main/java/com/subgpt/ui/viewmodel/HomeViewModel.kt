package com.subgpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.subgpt.models.BaseMessageSender
import com.subgpt.network.Resource
import com.stcc.mystore.network.repository.MainRepository
import kotlinx.coroutines.Dispatchers


class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun sendToChatGPT(msg: BaseMessageSender) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.chatGPT(msg)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}