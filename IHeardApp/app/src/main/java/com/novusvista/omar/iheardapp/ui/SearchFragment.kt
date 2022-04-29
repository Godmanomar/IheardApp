package com.novusvista.omar.iheardapp.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Adapter.DmAdapter

import com.novusvista.omar.iheardapp.Adapter.UserAdapter
import com.novusvista.omar.iheardapp.Model.Chat
import com.novusvista.omar.iheardapp.Model.User

import com.novusvista.omar.iheardapp.R

import kotlinx.android.synthetic.main.fragment_search.view.search_edit_text

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private  var recyclerView : RecyclerView?= null
    private  var userAdapter: UserAdapter?=null
    private  var dmAdapter: DmAdapter?=null
    private  var mUser:MutableList<User>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView=view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)

        mUser=ArrayList()
        userAdapter=context?.let{UserAdapter(it,mUser as ArrayList<User>,true)}
        recyclerView?.adapter=userAdapter
        dmAdapter=context?.let{DmAdapter(it,mUser as ArrayList<User>,true)}
        recyclerView?.adapter=dmAdapter
        view.search_edit_text.addTextChangedListener(object:TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
               if(view.search_edit_text.text.toString() == "")
               {

               }
                else
               {
                   recyclerView?.visibility=View.VISIBLE

                   retrieveUsers()
                   searchUsers(s.toString().toLowerCase())

               }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        return  view
    }

    private fun searchUsers(input: String) {
        val query=FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object:ValueEventListener
        {

            override fun onDataChange(dataSnapshot: DataSnapshot)
            {


                mUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    val chat = snapshot.getValue(Chat::class.java)
                    if (user !=null){
                        mUser?.add(user)
                        chat!!.getdidMessage()



                }
                userAdapter?.notifyDataSetChanged()
                    dmAdapter?.notifyDataSetChanged()

            }
        }
                override fun onCancelled(p0: DatabaseError) {


        }

        } )

    }




    private fun retrieveUsers(){
      val usersRef=FirebaseDatabase.getInstance().getReference().child("Users")
      usersRef.addValueEventListener(object:ValueEventListener{

          override fun onDataChange(dataSnapshot: DataSnapshot)
          {
              if (view?.search_edit_text?.text.toString() == "")
              {
                  mUser?.clear()
                  for (snapshot in dataSnapshot.children) {
                      val user = snapshot.getValue(User::class.java)
                      if (user !=null){
                          mUser?.add(user)

                      }

                  }
                  userAdapter?.notifyDataSetChanged()
                  dmAdapter?.notifyDataSetChanged()

              }
          }
          override fun onCancelled(p0: DatabaseError) {


          }

      } )

 }
}
