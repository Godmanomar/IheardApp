package com.novusvista.omar.iheardapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.novusvista.omar.iheardapp.Adapter.ChatsAdapter
import com.novusvista.omar.iheardapp.Adapter.DmAdapter
//import com.novusvista.omar.iheardapp.Adapter.UserAdapter
import com.novusvista.omar.iheardapp.Model.Chat
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.Notifications.*
import com.novusvista.omar.iheardapp.ui.APIService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {
   // var useradapter:UserAdapter?=null
    var userIdVisit:String=""
    var firebaseUser:FirebaseUser?=null
    var chatsAdapter:ChatsAdapter?=null
    var mChatlist:List<Chat>?=null
    lateinit var recycler_view_chats:RecyclerView
    var reference:DatabaseReference?=null
    private var postId = ""
    private var publisherId = ""
    var user:User?=null
    var notify=false
    var  apiService: APIService?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent=Intent(this@MessageChatActivity,SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
         apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent=intent


        //debug notification button crashes onClick fix userIdVisit null
        userIdVisit=intent.getStringExtra("visit_id")
        firebaseUser=FirebaseAuth.getInstance().currentUser

        recycler_view_chats=findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recycler_view_chats.layoutManager=linearLayoutManager

        val reference=FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(datasnapshot: DataSnapshot) {
           val user:User?=datasnapshot.getValue(User::class.java)
                username_mchat.text=user!!.getUsername()
                Picasso.get().load(user.getImage()).into(profile_image_mchat)
                //Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid,userIdVisit,user.getImage())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        send_message_btn.setOnClickListener{
            notify=true
                val message=text_message.text.toString()
            if(message=="")
            {
                Toast.makeText(this@MessageChatActivity,"Please write a message first...",Toast.LENGTH_LONG).show()
            }
            else{

                sendMessageToUser(firebaseUser!!.uid,userIdVisit,message)

                addNotifications()

            }
            text_message.setText("")
        }
        attact_image_file_btn.setOnClickListener{
            notify =true
        val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)
        }

       seenMessage(userIdVisit)
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key
        val messageHashMap=HashMap<String,Any?>()
        messageHashMap["sender"]=senderId
        messageHashMap["message"]=message
        messageHashMap["receiver"]=receiverId
        messageHashMap["isseen"]=false
        messageHashMap["didMessages"]=true
        messageHashMap["url"]=""
        messageHashMap["messageId"]=messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener {task ->
                if(task.isSuccessful)
                {
                    val chatsListReference=FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(datasnapshot: DataSnapshot) {
                            if(!datasnapshot.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef=FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)

                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })

                    //do push notifications using fcm
                   addNotification()
                   // addNotifications(userId = publisherId)

                }
            }
        val usersReference=FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)
        usersReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                if(notify)
                {
                    sendNotification(userIdVisit, user!!.getUsername(),message)
                } else{
                    notify =false
                }
                notify =false
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun sendNotification(receiverId: String?, username: String, message: String) {

        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
          for(dataSnapshot in p0.children)
          {
              val token:Token?=dataSnapshot.getValue(Token::class.java)
              val data=Data(firebaseUser!!.uid,
                  R.mipmap.ic_launcher,
                  "$username:$message",
                  "New Message",userIdVisit
                  )
              val sender=Sender(data!!,token!!.getToken().toString())
              apiService!!.sendNotification(sender)
                  .enqueue(object:Callback<MyResponse>
                  {

                      override fun onResponse(
                          call: Call<MyResponse>?,
                          response: Response<MyResponse>
                      )
                      {
                         if(response.code()==200){
                             if(response.body()!!.success !==1)
                             {
                                 Toast.makeText(this@MessageChatActivity,"Failed,Nothing happened",Toast.LENGTH_LONG).show()
                             }

                         }
                      }
                      override fun onFailure(call: Call<MyResponse>?, t: Throwable?) {
                          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                      }


                  })
          }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    override fun onActivityResult(requestCode:Int,resultCode:Int,data: Intent?){
         super.onActivityResult(requestCode, resultCode, data)
         if(requestCode==438 && resultCode==RESULT_OK &&data!=null && data!!.data!=null)
         {
val progressBar=ProgressDialog(this)
             progressBar.setMessage("image is uploading,please wait...")
             progressBar.show()

             val fileURI=data.data
             val storageReference=FirebaseStorage.getInstance().reference.child("Chats Images")
             val ref=FirebaseDatabase.getInstance().reference
             val messageId=ref.push().key
             val filepath=storageReference.child("$messageId.jpg")


             var uploadTask: StorageTask<*>
             uploadTask=filepath.putFile(fileURI!!)
             uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task->
                 if(!task.isSuccessful)
                 {
                     task.exception?.let{
                         throw it
                     }

                 }
                 return@Continuation filepath.downloadUrl
             }).addOnCompleteListener { task ->
                 if(task.isSuccessful) {
                     progressBar.dismiss()
                     val downloadUrl = task.result
                     val url = downloadUrl.toString()

                     val messageHashMap=HashMap<String,Any?>()
                     messageHashMap["sender"]=firebaseUser!!.uid
                     messageHashMap["message"]="sent you an image."
                     messageHashMap["receiver"]=userIdVisit
                     messageHashMap["isseen"]=false
                     messageHashMap["url"]=url
                     messageHashMap["messageId"]=messageId
                     ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                         .addOnCompleteListener { task -> if(task.isSuccessful){

                         }  }

                 }}
         }
     }

    private fun retrieveMessages(senderId:String, receiverId: String?, recieverImageUrl: String?)
    {
       mChatlist=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                (mChatlist as ArrayList<Chat>).clear()
                for (snapshot in datasnapshot.children)
                {
                    val chat=snapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(senderId)&& chat.getSender().equals(receiverId)||chat.getReceiver()
                            .equals(receiverId)&&chat.getSender().equals(senderId))
                    {
                        (mChatlist as ArrayList<Chat>).add(chat)

                    }
                    chatsAdapter=ChatsAdapter(this@MessageChatActivity,(mChatlist as ArrayList<Chat>),recieverImageUrl!!)
                      recycler_view_chats.adapter=chatsAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }
    var seenListner:ValueEventListener?=null
    private fun seenMessage(userid:String)
    {
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        seenListner=reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
               for(dataSnapshot in p0.children)
               {
                   val chat=dataSnapshot.getValue(Chat::class.java)
                   if(chat!!.getReceiver().equals(firebaseUser!!.uid)&& chat!!.getSender().equals(userid))
                   {
                       val hashMap=HashMap<String,Any>()
                       hashMap["isseen"]=true
                       dataSnapshot.ref.updateChildren(hashMap)


                   }
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun onPause() {
        super.onPause()

    }

//
    private fun addNotification() {
    updateToken(FirebaseInstanceId.getInstance().token)
    val reference = FirebaseDatabase.getInstance().reference.child("Notifications")
    reference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {


            val notiRef = FirebaseDatabase.getInstance()
                .reference.child("Notifications")

            val notiMap = HashMap<String, Any>()
            notiMap["userid"] = firebaseUser!!.uid
            notiMap["text"] = "sent you a messages:"
            notiMap["postid"] = ""
            notiMap["isIsPost"] = false

            notiRef.push().setValue(notiMap)

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })


    val notiRef = FirebaseDatabase.getInstance()
        .reference.child("Notifications")
          .child("text")
    val notiMap = HashMap<String, Any>()
    notiMap["userid"] = firebaseUser!!.uid
    notiMap["text"] = "sent you a messages:"
    notiMap["postid"] = ""
    notiMap["isIsPost"] = false

    notiRef.push().setValue(notiMap)
    var userid:String=String()
    var dmAdapter: DmAdapter? = null
  //  var handler: Handler? = null
    dmAdapter?.changeBool()
    // handler?.postDelayed({dmAdapter!!.addNotification(user!!.getUid())},3000)
    dmAdapter?.addNotification(userid)


//
}
    fun addNotifications() {




        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")


        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "was in your chats"
        notiMap["postid"] = ""
        notiMap["isIsPost"] = false

        notiRef.push().setValue(notiMap)



    }

    private fun updateToken(token: String?) {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1= Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }





    }


