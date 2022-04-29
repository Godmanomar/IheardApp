package com.novusvista.omar.iheardapp.utilities


import com.novusvista.omar.iheardapp.data.VideoRepository
import com.novusvista.omar.iheardapp.ui.post.PostViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import network.VideoService


@FlowPreview
@ExperimentalCoroutinesApi
object InjectorUtils {



    fun provideHomeViewModelFactory():PostViewModelFactory{
        val repository=VideoRepository(VideoService.create())
        return PostViewModelFactory(repository)

    }
}