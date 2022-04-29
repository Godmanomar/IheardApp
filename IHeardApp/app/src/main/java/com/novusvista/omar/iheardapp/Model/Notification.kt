package com.novusvista.omar.iheardapp.Model

class Notification {
    private var userid:String=""
    private var text:String=""
    private var postid:String=""
    private var ispost=true
    private var ischatpost=true


    constructor()

    constructor(userid: String, text: String, postid: String, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.ispost = ispost
        this.ischatpost=ischatpost

    }


    fun  getPostId():String
    {
        return  postid
    }

    fun  getText():String
    {
     return text
    }


    fun  getUserId():String
    {
        return  userid
    }

    fun isIsPost():Boolean
    {
        return ispost
    }
    fun isIsChatpost():Boolean
    {
        return ischatpost
    }


    fun setUserId(userid:String)
    {
       this.userid=userid
    }

    fun setText(text:String)
    {
       this.text=text
    }

    fun setPostId(postid:String)
    {
        this.postid=postid

    }
       fun setIsPost(ispost: Boolean)
    {
        this.ispost=ispost
    }
    fun setIsChatpost(ischatpost: Boolean)
    {
        this.ischatpost=ischatpost
    }


}