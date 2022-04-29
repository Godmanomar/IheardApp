package com.novusvista.omar.iheardapp.data

import com.google.gson.annotations.SerializedName

class Video (
    @SerializedName("public_id")
    val publicId:String,
        val version:Int,
    val  format:String,
     val width:Int,
    val  type:String,
    @SerializedName("created_at")val createdAt:String
    )
