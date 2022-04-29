package com.novusvista.omar.iheardapp.Model

class Story {

    private  var videourl:String=""
    private  var imageurl:String=""
    private  var timestart:Long=0
    private  var timeend:Long=0
    private  var storyid:String=""
    private var userid:String=""

    constructor()

    constructor(videourl:String,imageurl: String, timestart: Long, timeend: Long, storyid: String, userid: String) {
        this.videourl = videourl
        this.imageurl = imageurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }


    fun getVideoUrl():String{

        return videourl
    }

    fun getImageUrl():String{

        return imageurl
    }

    fun getTimeStart():Long{

        return timestart
    }

    fun getTimeEnd():Long{

        return timeend
    }

    fun getStoryId():String{

        return storyid
    }
    fun getUserId():String{

        return userid
    }

    fun setVideoUrl(videourl: String)
    {
        this.videourl=videourl
    }
    fun setImageUrl(imageurl: String)
    {
        this.imageurl=imageurl
    }
    fun setTimeStart(timestart: Long)
    {
        this.timestart=timestart
    }
    fun setTimeEnd(timeend: Long)
    {
        this.timeend=timeend
    }
    fun  setStoryId(storyid:String){

        this.storyid=storyid
    }
    fun  setUserId(userid:String){

        this.userid=userid
    }

}