package com.novusvista.omar.iheardapp.ui


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.novusvista.omar.iheardapp.Adapter.DmAdapter
import com.novusvista.omar.iheardapp.Adapter.UserAdapter
import com.novusvista.omar.iheardapp.Model.Chatlist

import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.Notifications.Token

import com.novusvista.omar.iheardapp.R
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.user_item_layout.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {
    private lateinit var profileId: String
    private  var mUser:MutableList<User>?=null
    private  var userAdapter: UserAdapter?=null
    private  var dmAdapter: DmAdapter?=null
      var firebaseUser:FirebaseUser?=null
    private  var usersChatList:List<Chatlist>?=null
    lateinit var  recyclerviewchatlist:RecyclerView
    private lateinit var firebaseMessageid:FirebaseDatabase
    private  var recyclerView : RecyclerView?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerviewchatlist=view.findViewById(R.id.recycler_view_chatlists)
        recyclerviewchatlist?.setHasFixedSize(true)
        recyclerviewchatlist?.layoutManager=LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseMessageid= FirebaseDatabase.getInstance().reference.database

        usersChatList=ArrayList()

        val ref =FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")
        }
        retrieveChatList()

        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val user:User?=datasnapshot.getValue(User::class.java)
               // user_full_name_search.text=user!!.getUsername()
                Picasso.get().load(user!!.getProfile()).placeholder(R.drawable.profile).into(username_profile_image_search)

                (usersChatList as ArrayList).clear()
                for(snapshot in datasnapshot.children)
                {

                    val  chatlist=datasnapshot.getValue(Chatlist::class.java)


                    (usersChatList as ArrayList).add(chatlist!!)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

userInfo()
        updateToken(FirebaseInstanceId.getInstance().token)
        return  view


    }

    private fun updateToken(token: String?) {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1= Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    // gets the profile image from ProfileFragment
    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {

                    val user = datasnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_Frag)


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser!!.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser!!.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser!!.uid)
        pref?.apply()
    }




    private fun retrieveChatList()
    {
        mUser=ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
        ref!!.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot)
            {
                (mUser as ArrayList).clear()

                for(snapshot in datasnapshot.children)
                {
                    val user= datasnapshot.getValue(User::class.java)



                    for(eachChatList in usersChatList!!)
                    {
                        if(!user!!.getUid().equals(eachChatList.getId()))
                        {
                            (mUser as ArrayList).add(user!!)
                        }

                    }
                }
                userAdapter=UserAdapter(context!!,(mUser as ArrayList<User>),true)
                recyclerviewchatlist.adapter=userAdapter

                dmAdapter=DmAdapter(context!!,(mUser as ArrayList<User>),true)
                recyclerviewchatlist.adapter=dmAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }


    }













