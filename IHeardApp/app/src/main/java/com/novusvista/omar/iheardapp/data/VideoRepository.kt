package com.novusvista.omar.iheardapp.data

import com.novusvista.omar.iheardapp.data.network.VideoServiceResult
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

import network.VideoService

import java.io.IOException


class VideoRepository(private val videoService:VideoService) {

     private val videoResult=ConflatedBroadcastChannel<VideoServiceResult>()

    suspend fun getVideoOnline():Flow<VideoServiceResult> {
        requestVideo()

        return videoResult.asFlow()
    }

    private suspend fun requestVideo() {

        try {
            val  response=videoService.fetchVideos()
            videoResult.offer(VideoServiceResult.Success(response))
        }catch (exception:IOException) {
            videoResult.offer(VideoServiceResult.Error(exception))
        }catch(exception:Exception){
            videoResult.offer(VideoServiceResult.Error(exception))

        }

    }

}


