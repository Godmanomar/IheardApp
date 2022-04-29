package com.novusvista.omar.iheardapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.MainActivity
import com.novusvista.omar.iheardapp.MessageChatActivity
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.ui.ProfileFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class DmAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var Messaged: Boolean = false,
    private var isFragment: Boolean = false) : RecyclerView.Adapter<DmAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DmAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return DmAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: DmAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()
        holder.userFulNameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile)
            .into(holder!!.userProfileImage)




        checkFollowingStatus(user.getUid(), holder.followButton)

        holder.chatButton.setOnClickListener {

            val intent = Intent(mContext, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user.getUid())
            mContext.startActivity(intent)
           addNotifications(user.getUid())

        }

        holder.itemView.setOnClickListener {

            if(!isFragment)
            {
                val pref= mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                pref.putString("profileId",user.getUid())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container,ProfileFragment())
                    .commit()

            }
            else
            {


                val intent= Intent(mContext,MainActivity::class.java)
                intent.putExtra("publisherId",user.getUid())
                mContext.startActivity(intent)
            }

        }
        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(user.getUid())
                                    .child("Followers").child(user.getUid())
                                    .setValue(true).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {


                                        }
                                    }
                            }
                        }
                }

                    addNotification(user.getUid())
            }


            else
                if (holder.followButton.text.toString() == "Following") {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(user.getUid())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(user.getUid())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {


                                            }
                                        }
                                }
                            }
                    }
                }


        }


    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var userFulNameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage: CircleImageView =
            itemView.findViewById(R.id.username_profile_image_search)

        var followButton: Button = itemView.findViewById(R.id.follow_btn_search)
        var chatButton: Button = itemView.findViewById(R.id.chat_btn_search)


    }

    private fun checkFollowingStatus(uid: String, followButton: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
        followingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(uid).exists()) {
                    followButton.text = "Following"
                } else {
                    followButton.text = "Follow"

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun addNotifications(userId:String) {




            val notiRef = FirebaseDatabase.getInstance()
                .reference.child("Notifications")
                .child(userId)

            val notiMap = HashMap<String, Any>()
            notiMap["userid"] = firebaseUser!!.uid
            notiMap["text"] = "was in your chats"
            notiMap["postid"] = ""
            notiMap["isIsPost"] = false

            notiRef.push().setValue(notiMap)



        }


     fun addNotification(userId:String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "started following you"
        notiMap["postid"] = ""
        notiMap["isIsPost"] = false

        notiRef.push().setValue(notiMap)



    }
    fun changeBool(){
        this.Messaged=true
    }




    }



