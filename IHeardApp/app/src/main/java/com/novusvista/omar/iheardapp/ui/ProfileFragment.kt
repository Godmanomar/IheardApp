package com.novusvista.omar.iheardapp.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.novusvista.omar.iheardapp.*
import com.novusvista.omar.iheardapp.Adapter.MyImagesAdapter
import com.novusvista.omar.iheardapp.Model.Post

import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.edit_account_settings_btn
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    var postList:List<Post>?=null
   var myImagesAdapter:MyImagesAdapter?=null

    var myImagesAdapterSavedImg:MyImagesAdapter?=null
    var postListSaved:List<Post>?=null
    var mySavesImg:List<String>?=null
    var isProfile=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")
        }
        if (profileId == firebaseUser.uid) {
            isProfile=true
            view.edit_account_settings_btn.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid) {
            checkFollowersAndFollowingBtnStatus()
        }

        //recycler View for Uploaded Images
        var recyclerViewuploadImages:RecyclerView
        recyclerViewuploadImages=view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewuploadImages.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager=GridLayoutManager(context,3)
        recyclerViewuploadImages.layoutManager=linearLayoutManager

        postList=ArrayList()
        myImagesAdapter=context?.let{MyImagesAdapter(it,postList as ArrayList<Post>)}
        recyclerViewuploadImages.adapter=myImagesAdapter


        //recycler View for Saved Images


        var recyclerViewSavedImages:RecyclerView
        recyclerViewSavedImages=view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2:LinearLayoutManager=GridLayoutManager(context,3)
        recyclerViewSavedImages.layoutManager=linearLayoutManager2

        postListSaved=ArrayList()
        myImagesAdapterSavedImg=context?.let{MyImagesAdapter(it,postListSaved as ArrayList<Post>)}
        recyclerViewSavedImages.adapter=myImagesAdapterSavedImg


         recyclerViewSavedImages.visibility=View.GONE
        recyclerViewuploadImages.visibility=View.VISIBLE


        var uploadedImagesBtn:ImageButton
        uploadedImagesBtn=view.findViewById(R.id.images_grid_view_btn)
        uploadedImagesBtn.setOnClickListener{
            recyclerViewSavedImages.visibility=View.GONE
            recyclerViewuploadImages.visibility=View.VISIBLE
        }

        var SavedImagesBtn:ImageButton
        SavedImagesBtn=view.findViewById(R.id.images_save_btn)
        SavedImagesBtn.setOnClickListener{
            recyclerViewSavedImages.visibility=View.VISIBLE
            recyclerViewuploadImages.visibility=View.GONE

        }

           view.total_followers.setOnClickListener{
               val intent=Intent(context,ShowUserActivity::class.java)
               intent.putExtra("id",profileId)
               intent.putExtra("title","followers")
               startActivity(intent)



           }
        view.total_following.setOnClickListener{
            val intent=Intent(context,ShowUserActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","following")
            startActivity(intent)



        }
        view.chat_btn.setOnClickListener {

            val intent=Intent(context,MessageChatActivity::class.java)
            intent.putExtra("visit_id",profileId)

            addNotifications()
                startActivity(intent)

        }



        view.edit_account_settings_btn.setOnClickListener {
            val getButtonText = view.edit_account_settings_btn.text.toString()
            when {

                        getButtonText == "Edit Profile" -> startActivity(
                    Intent(context,AccountSettingsActivity::class.java)

                )




                        getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { it0 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it0.toString())
                            .child("Following").child(profileId)
                            .setValue(true)

                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    view.edit_account_settings_btn.text = "Following "
                                }
                                addNotification()

                            }
                    }
                }


                getButtonText == "Following " -> {
                    firebaseUser?.uid.let { it0 ->

                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it0.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference

                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()


                    }


                }

            }


        }











        getFollowers()
        getFollowing()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()


        return view

    }

    private fun checkFollowersAndFollowingBtnStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if (followingRef != null) {

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {

                    if(isProfile){

                        chat_btn.visibility=View.INVISIBLE
                    }else{
                        chat_btn.visibility=View.VISIBLE
                    }

                    if (datasnapshot.child(profileId).exists()) {


                        view?.edit_account_settings_btn?.text = "Following "

                        images_save_btn.visibility=View.INVISIBLE
                        images_grid_view_btn.visibility=View.INVISIBLE
                    } else {
                        view?.edit_account_settings_btn?.text = "Follow"
                        images_save_btn.visibility=View.INVISIBLE
                        images_grid_view_btn.visibility=View.INVISIBLE
                    }
                }


                override fun onCancelled(error: DatabaseError) {

                }

            })

        }

    }

    private fun getFollowers() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.total_followers?.text = snapshot.childrenCount.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun getFollowing() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.total_following?.text = snapshot.childrenCount.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }



    private fun myPhotos()
    {
        val postRef=FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot)
            {
              if(datasnapshot.exists())
              {
                  (postList as ArrayList<Post>).clear()
                  for(snapshot in datasnapshot.children)
                  {
                      val post=snapshot.getValue(Post::class.java)!!
                      if(post.getPublisher().equals(profileId))
                      {
                          (postList as ArrayList<Post>).add(post)
                      }
                      Collections.reverse(postList)
                      myImagesAdapter!!.notifyDataSetChanged()

                  }
              }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })




    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {

                    val user = datasnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_Frag)
                    view?.full_name_profile_frag?.text = user!!.getFullname()
                    view?.proffile_fragment_username?.text = user!!.getUsername()
                    view?.bio_profile_frag?.text = user!!.getBio()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
     private fun getTotalNumberOfPosts()
     {
         val postRefs= FirebaseDatabase.getInstance().reference.child("Posts")
         postRefs.addValueEventListener(object:ValueEventListener{
             override fun onDataChange(datasnapshot: DataSnapshot) {
                 if(datasnapshot.exists())
                 {
                     var postCounter=0
                     for(snapshot in datasnapshot.children)
                     {
                         val post= snapshot.getValue(Post::class.java)!!
                         if(post.getPublisher()==profileId)
                         {
                             postCounter++
                         }
                     }
                     total_posts.text=""+postCounter
                 }

             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             }
         })



}
    private fun mySaves(){
        mySavesImg=ArrayList()
        val savedRef=FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid)
        savedRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
            if(datasnapshot.exists())
            {
                for(snapshot in datasnapshot.children)
                {
                    ( mySavesImg as ArrayList<String>).add(snapshot.key!!)
                }
                readSavedimagesData()
            }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })



    }
     fun readSavedimagesData()
     {
      val postRef=FirebaseDatabase.getInstance().reference.child("Posts")
         postRef.addValueEventListener(object :ValueEventListener{
             override fun onDataChange(datasnapshot: DataSnapshot) {
            if(datasnapshot.exists())
            {


                ( postListSaved as ArrayList<Post>).clear()
                for(snapshot in datasnapshot.children)
                {
                   val post= snapshot.getValue(Post::class.java)
                    for(key in mySavesImg!!)
                    {
                        if(post!!.getPostid()==key)
                        {
                            (postListSaved as  ArrayList<Post>).add(post!!)
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }
                {

            }
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             }
         })
     }

     fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] =firebaseUser!!.uid
        notiMap["text"] = "started following you"
        notiMap["postid"]= ""
        notiMap["isIsPost"] = false

        notiRef.push().setValue(notiMap)
    }

    fun addNotifications() {




        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "was in your chats"
        notiMap["postid"] = ""
        notiMap["isIsPost"] = false

        notiRef.push().setValue(notiMap)



    }

    fun fromNottifyBtn()
    {
        val intent=Intent(context,MessageChatActivity::class.java)
        intent.putExtra("visit_id",profileId)
        startActivity(intent)
    }

}



