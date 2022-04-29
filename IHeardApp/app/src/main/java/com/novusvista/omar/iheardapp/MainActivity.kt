package com.novusvista.omar.iheardapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Model.Chat
import com.novusvista.omar.iheardapp.ui.*
import kotlinx.android.synthetic.main.activity_main.*

class
MainActivity : AppCompatActivity() {
    internal var selectedFragment: Fragment?=null
    private  lateinit var image:ImageView
    private  lateinit var button: Button
    private lateinit var textMessage: TextView
    var  firebaseUser = FirebaseAuth.getInstance().currentUser!!


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
           button=findViewById(R.id.search_ready_btn)

        when (item.itemId) {
                R.id.nav_home -> {

                    moveToFragment(HomeFragment())
                    StopAnim()
                    image.visibility = View.INVISIBLE
                    button.visibility = View.GONE

//    learning purposes            selectedFragment= HomeFragment()
                    //chat_profile

                }
            R.id.nav_search -> {
//                startActivity(Intent(this@MainActivity,BeforeSearchActivity::class.java))
//                return@OnNavigationItemSelectedListener true

                Anim()
                moveToFragment(SearchFragment())
                image.visibility = View.VISIBLE
                button.visibility = View.VISIBLE

                return@OnNavigationItemSelectedListener true

//      learning purposes       selectedFragment= SearchFragment()

            }
            R.id.nav_add_post -> {
         item.isChecked=false

                    startActivity(Intent(this@MainActivity, AddVideoActivity::class.java))



                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {

                moveToFragment(NotificationsFragment())
                StopAnim()
                image.visibility = View.INVISIBLE
                button.visibility = View.GONE
                return@OnNavigationItemSelectedListener true
            }
//
////  learning purposes       selectedFragment= NotificationsFragment()
//
//            }
            R.id.nav_profile -> {
                button.visibility = View.GONE
                moveToFragment(ProfileFragment())
                StopAnim()
                image.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
//      learning purposes        selectedFragment= ProfileFragment()
            }

//                R.id.nav_chat_profile -> {
//                    moveToFragment(ChatsFragment())
//                    return@OnNavigationItemSelectedListener true
//
////      learning purposes       selectedFragment= ChatsFragment()
//
//                }
        }
//   learning purposes     if(selectedFragment !==null){
//
//            supportFragmentManager.beginTransaction().replace(
//                R.id.fragment_container,
//           selectedFragment!!
//            ).commit()
//
//
//        }


        false
    }
    override fun onStart() {
        super.onStart()
        image.visibility = View.INVISIBLE


}
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        image=findViewById(R.id.image_view)
        val ref=FirebaseDatabase.getInstance().reference.child("chats")
        ref!!.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                var countUnreadMessages=0
                for(dataSnapshot in snapshot.children )
                {
                    val  chat =dataSnapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid)&&!chat.isIsSeen())
                    {
                        countUnreadMessages+=1
                    }
                }
                if(countUnreadMessages==0)
                {}
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())


        search_ready_btn.setOnClickListener{
            StopAnim()
           button.visibility = View.GONE
            image.visibility = View.INVISIBLE


        }

    }
    private  fun moveToFragment(fragment: Fragment){

        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()

    }
private fun Anim(){


        val rotate= AnimationUtils.loadAnimation( this,R.anim.rotate_aniamtion)


        image.animation=rotate



    }
    private fun StopAnim(){


        val stoprotate= AnimationUtils.loadAnimation( this,R.anim.no_rotation)


        image.animation=stoprotate



    }
//    private fun startRemove(){
//        button.visibility = View.GONE
//        image.visibility = View.INVISIBLE
//    }


}



