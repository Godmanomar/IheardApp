package com.novusvista.omar.iheardapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.AddStoryActivity
import com.novusvista.omar.iheardapp.MainActivity
import com.novusvista.omar.iheardapp.Model.Story
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.StoryActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*

class StoryAdapter (private val mContext:Context,private val mStory:List<Story>):
RecyclerView.Adapter<StoryAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return if(viewType==0){
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false)
          ViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false)
            return ViewHolder(view)

        }
    }

    override fun getItemCount(): Int {
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStory[position]
        userInfo(holder,story.getUserId(),position)

        if(holder.adapterPosition!==0)
        {
            seenStory(holder,story.getUserId())
        }
        if(holder.adapterPosition===0)
        {
            myStories(holder.addStory_text!!,holder.story_plus_btn_!!,false)
        }
        holder.itemView.setOnClickListener{

            if(holder.adapterPosition===0){
                myStories(holder.addStory_text!!,holder.story_plus_btn_!!,true)

            }
            else
            {
                val intent = Intent(mContext, StoryActivity::class.java)
                intent.putExtra("userId",story.getUserId())
                mContext.startActivity(intent)

            }

    }
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        //StoryItem

        var story_image_seen: CircleImageView?=null
        var story_image_: CircleImageView?=null
        var story_username: TextView?=null

        //AddStoryItem
        var story_plus_btn_: ImageView?=null
        var addStory_text: TextView?=null
        init {
            //StoryItem
            story_image_seen=itemView.findViewById(R.id.story_image_seen)

            story_image_=itemView.findViewById(R.id.story_image)

            story_username=itemView.findViewById(R.id.story_username)

            //AddStoryItem
            story_plus_btn_=itemView.findViewById(R.id.story_add)
            addStory_text=itemView.findViewById(R.id.add_story_text)

        }


    }

    override fun getItemViewType(position: Int): Int {
        if(position==0)
        {
            return 0
        }
        return 1
    }

    private fun userInfo(viewHolder: ViewHolder,userId:String,position: Int) {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {

                    val user = datasnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(viewHolder.story_image_)
                    if(position!=0){
                        Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                            .into(viewHolder.story_image_seen)
                        viewHolder.story_username!!.text=user.getUsername()
                    }


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun myStories(textView: TextView,imageView:ImageView,click: Boolean) {

        val storyRef = FirebaseDatabase.getInstance().reference.child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)
       storyRef.addListenerForSingleValueEvent(object :ValueEventListener{
           override fun onDataChange(datasnapshot: DataSnapshot) {

               var counter = 0
               val timeCurrent = System.currentTimeMillis()

               for (snapshot in datasnapshot.children) {
                   val story = snapshot.getValue(Story::class.java)
                   if (timeCurrent > story!!.getTimeStart() && timeCurrent < story!!.getTimeEnd()) {
                       counter++
                   }

               }
               if (click) {
                   if (counter > 0) {
                       val  alertDialog=AlertDialog.Builder(mContext).create()
                       alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"View Story")
                       {
                           dialogInterface, which ->
                           val intent = Intent(mContext, StoryActivity::class.java)
                           intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                           mContext.startActivity(intent)
                           dialogInterface.dismiss()
                       }
                       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Add Story")
                       {
                               dialogInterface, which->
                           val intent = Intent(mContext, AddStoryActivity::class.java)
                           intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                           mContext.startActivity(intent)
                           dialogInterface.dismiss()
                       }
                       alertDialog.show()

                   }else
                   {
                       val intent = Intent(mContext, AddStoryActivity::class.java)
                       intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                       mContext.startActivity(intent)

                   }

           }
               else
               {
                   if(counter>0)
                   {

                       textView.text="My Story"
                       imageView.visibility=View.GONE
                   }
                   else
                   {
                       textView.text="Add Story"
                       imageView.visibility=View.VISIBLE

                   }


               }

           }
           override fun onCancelled(error: DatabaseError) {

           }
       })
    }

    private fun seenStory(viewHolder: ViewHolder,userId: String)
    {
        var outofbounds=false
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story").child(userId)
        storyRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {

            var i=0
                for(snapshot  in datasnapshot.children)
                {
                    if(! snapshot.child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).exists()&&
                        System.currentTimeMillis() < snapshot.getValue(Story::class.java)!!.getTimeEnd())
                    {
                        i++


                    }

                }
                if(i>0)
                {
                    viewHolder.story_image_!!.visibility=View.VISIBLE
                    viewHolder.story_image_seen!!.visibility=View.GONE
                }
                else{

                    viewHolder.story_image_!!.visibility=View.GONE
                    viewHolder.story_image_seen!!.visibility=View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

}