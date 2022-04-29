package com.novusvista.omar.iheardapp.Model

class Chat
{
    private var sender:String=""
    private var message:String=""
    private var receiver:String=""
    private var isseen=false
    private var url:String=""
    private var messageId:String=""
    private var didMessages=false


    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        didmessage:Boolean,
        url: String,
        messageId: String

    ) {
        this.didMessages=didMessages
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId

    }
    fun  getdidMessage():Boolean{
        return  didMessages
    }
    fun  setdidMessage(didmessage: Boolean){
        this.didMessages=didMessages!!
    }

    fun  getSender():String?{
        return  sender
    }
    fun  setSender(sender:String?){
        this.sender=sender!!
    }
    fun  getMessage():String?{
        return  message
    }
    fun  setMessage(message:String?){
        this.message=message!!
    }
    fun  getReceiver():String?{
    return  receiver
    }
    fun  setReceiver(receiver:String?){
        this.receiver=receiver!!
    }
    fun  isIsSeen():Boolean{
    return  isseen
    }
    fun  setIsSeen(isseen: Boolean){
        this.isseen=isseen!!
    }
    fun  getUrl():String?{
    return  url
    }
    fun  setUrl(url: String?){
        this.url=url!!
    }
    fun  getMessageId():String?{
    return  messageId
    }

    fun  setMessageId(messageId:String?){
        this.messageId=messageId!!

     }

   }