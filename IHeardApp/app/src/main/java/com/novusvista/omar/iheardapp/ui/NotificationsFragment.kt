package com.novusvista.omar.iheardapp.ui


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Adapter.NotificationAdapter
import com.novusvista.omar.iheardapp.Model.Notification

import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.SignInActivity
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {
    private var notificationsList:List<Notification>?=null
    private var notificationAdapter:NotificationAdapter?=null

    override fun onStart() {
        super.onStart()
        notifications_close_btn.setOnClickListener{
            val intent=Intent(context, SignInActivity::class.java)
            startActivity(intent)
    }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {


        // Inflate the layout for this fragment
        val  view = inflater.inflate(R.layout.fragment_notifications, container, false)

   var recyclerView:RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)
        notificationsList=ArrayList()
        notificationAdapter= NotificationAdapter(context!!,notificationsList as ArrayList<Notification>)
        recyclerView.adapter=notificationAdapter

        readNotifications()



        return view
    }



    private fun readNotifications()
    {
        val notiRef= FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        notiRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
             if(datasnapshot.exists())
             {
                 (notificationsList as ArrayList<Notification>).clear()
                 for(snapshot in datasnapshot.children)
                 {
                     val notification=snapshot.getValue(Notification::class.java)
                     (notificationsList as ArrayList<Notification>).add(notification!!)
                 }
                 Collections.reverse(notificationsList)
                 notificationAdapter!!.notifyDataSetChanged()
             }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }




}
