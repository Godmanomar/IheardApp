package network

import com.google.gson.annotations.SerializedName
import com.novusvista.omar.iheardapp.data.Video

class VideoServiceResponse (@SerializedName("resources")val videos:List<Video>)
