package com.novusvista.omar.iheardapp.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novusvista.omar.iheardapp.data.VideoRepository


class PostViewModelFactory (
    private val videoRepository: VideoRepository
    ): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
return PostViewModel(
    videoRepository)as T

    }
        

}