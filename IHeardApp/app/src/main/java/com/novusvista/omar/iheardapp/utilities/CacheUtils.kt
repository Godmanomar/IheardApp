package com.novusvista.omar.iheardapp.utilities

import android.content.Context
import java.io.File

object CacheUtils {

    public fun getVideoCacheDir(context: Context): File?{
        return File(context.externalCacheDir,"video-cache")
    }
    fun cleanVideoCacheDir(context:Context) {
     getVideoCacheDir(context) ?.deleteRecursively()
    }
}