package com.novusvista.omar.iheardapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Adapter.DmAdapter
import com.novusvista.omar.iheardapp.Adapter.UserAdapter
import com.novusvista.omar.iheardapp.Model.User

class ShowUserActivity : AppCompatActivity() {
    var id:String=""
    var title:String=""
    var userAdapter:UserAdapter?=null
    var dmAdapter:DmAdapter?=null
    var userList:List<User>?=null
    var idList:List<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        val intent=intent
        id=intent.getStringExtra("id")
        title=intent.getStringExtra("title")
        val toolbar:Toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }
 var recyclerView:RecyclerView
        recyclerView=findViewById(R.id.recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(this)
        userList=ArrayList()
        userAdapter=UserAdapter(this,userList as ArrayList<User>,false)
        recyclerView.adapter=userAdapter

        dmAdapter=DmAdapter(this,userList as ArrayList<User>,false)
        recyclerView.adapter=dmAdapter
        idList=ArrayList()
        when(title)
        {
            "likes"->getLikes()
            "following"->getFollowing()
            "followers"->getFollowers()
          "views"->getViews()
        }



    }

    private fun getViews() {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(id!!)
            .child(intent.getStringExtra("storyid"))
            .child("views")


        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {



                    (idList as ArrayList<String>).clear()
                    for(snapshot in datasnapshot.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    shoUsers()


            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists())
                {

                    (idList as ArrayList<String>).clear()
                    for(snapshot in datasnapshot.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    shoUsers()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun getFollowing() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                (idList as ArrayList<String>).clear()
                for(snapshot in datasnapshot.children)
                {
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                shoUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

        private fun getLikes() {
            val LikesRef= FirebaseDatabase.getInstance().reference
                .child("Likes").child(id!!)

            LikesRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if(datasnapshot.exists())
                    {

                        (idList as ArrayList<String>).clear()
                        for(snapshot in datasnapshot.children)
                        {
                            (idList as ArrayList<String>).add(snapshot.key!!)
                        }
                        shoUsers()
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

    private fun shoUsers() {
        val usersRef=FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (userList as ArrayList<User>).clear()
             for (snapshot in dataSnapshot.children)
             {
                 val user = snapshot.getValue(User::class.java)
                 for (id in idList!!)
                 {
                     if(user!!.getUid()==id)
                     {
                         (userList as ArrayList<User>).add(user!!)

                 }
             }
                    userAdapter?.notifyDataSetChanged()
                 dmAdapter?.notifyDataSetChanged()

            }

        }
                override fun onCancelled(error: DatabaseError) {

        }


    })
    }
   }





