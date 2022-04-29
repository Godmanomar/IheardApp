package com.novusvista.omar.iheardapp.data.network

import network.VideoServiceResponse

 sealed  class VideoServiceResult {
    data class Success(val data:VideoServiceResponse):VideoServiceResult()
    data class  Error(val error:Exception):VideoServiceResult()
 }