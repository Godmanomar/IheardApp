package com.novusvista.omar.iheardapp.Model

import com.google.firebase.auth.FirebaseAuth

class ModelVideo {

    private  var publisher = FirebaseAuth.getInstance().currentUser!!.uid
    var id: String? = null
    var title: String? = null
    var timestamp: String? = null
    var videoUri: String? = null



    constructor()

    constructor(id: String?, publisher: String, title: String?, timestamp: String?, videoUri: String?) {

        this.publisher = publisher

    }

    fun getPublisher():String{
        return publisher
    }

    fun setPublisher(publisher: String){
        this.publisher=publisher

    }


}
