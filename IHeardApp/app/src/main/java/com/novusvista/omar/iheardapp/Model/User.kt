package com.novusvista.omar.iheardapp.Model

class User {


    private var  bio:String=""
    private var  fullname:String=""
    private var  image:String=""
    private var uid:String=""
    private var username:String=""
    private var email:String=""
    private var profile:String=""
    private var status:String=""


 constructor()

    constructor(username:String,fullname: String, bio:String,image:String,uid:String,email:String,profile:String,status:String){
        this.email=email
        this.username=username
        this.bio=bio
        this.image=image
        this .uid=uid
        this.fullname=fullname
        this.profile=image
        this.status=status
    }
    fun getUsername():String
    {
        return username
    }
    fun setUsername(username: String){
        this.username=username
    }
    fun getFullname():String
    {
        return fullname
    }
    fun setFullname(fullname: String){
        this.fullname=fullname
    }
    fun getBio():String
    {
        return bio
    }
    fun  getEmail():String
    {
        return email
    }
    fun setEmail(email:String)
    {
        this.email=email
    }
    fun setBio(bio: String){
        this.bio=bio
    }
    fun getImage():String
    {
        return image
    }
    fun setImage(image: String){
        this.image=image
    }
    fun getUid():String
    {
        return uid
    }
    fun setUid(uid: String){
        this .uid=uid
    }
    fun  getProfile():String?
    {
        return profile
    }
    fun  setProfile(profile:String){
        this.profile=profile
    }
    fun getStatus():String?{
        return status
    }
    fun  setStatus(status:String){
        this.status=status
    }
}
