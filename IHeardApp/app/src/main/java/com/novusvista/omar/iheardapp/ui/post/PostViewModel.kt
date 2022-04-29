package com.novusvista.omar.iheardapp.ui.post

import androidx.lifecycle.*
import com.google.android.exoplayer2.Player
import com.novusvista.omar.iheardapp.data.VideoRepository
import com.novusvista.omar.iheardapp.data.network.VideoServiceResult
import com.novusvista.omar.iheardapp.ui.post.player.PlayerState

class PostViewModel(private val repository:VideoRepository): ViewModel(){
    private val playerState= PlayerState(true,0,0L)

    val retry:MutableLiveData<Boolean> = MutableLiveData(false)
    private fun getVideoServiceResult():LiveData<VideoServiceResult> = liveData{
       val videos=repository.getVideoOnline().asLiveData()
        emitSource(videos)
    }
   private val _retry:LiveData<VideoServiceResult> =
           Transformations.switchMap(retry){getVideoServiceResult()}
    val urls:LiveData<List<String>> =
            Transformations.map(_retry) { videos ->
               val listOfUrl= mutableListOf<String>()
                when(videos){
                    is VideoServiceResult.Success ->{
                        videos.data.videos.forEach{video ->
                            if(video.format == "mp4" && !video.publicId.contains("")){
                                listOfUrl.add("https://res.cloudinary.com/demo/video/${video.type}/v${video.version}/${video.publicId}.${video.format}")
                            }
                        }
                        return@map listOfUrl
                    }
                    is VideoServiceResult.Error -> emptyList<String>()

                }
            }

    fun setPlayerState(player: Player){
        playerState.playWhenReady= player.playWhenReady
        playerState.currentWindow= player.currentWindowIndex
      playerState.playbackPosition= player.currentPosition
    }

    fun getPlayerState()=playerState


}