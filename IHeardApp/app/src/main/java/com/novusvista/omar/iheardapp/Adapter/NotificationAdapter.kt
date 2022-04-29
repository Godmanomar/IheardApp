package com.novusvista.omar.iheardapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Model.Notification
import com.novusvista.omar.iheardapp.Model.Post
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.ui.post.PostDetailsFragment
import com.novusvista.omar.iheardapp.ui.ProfileFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private val mContext:Context, private  val mNotification:List<Notification> )

    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()

{

    private lateinit var profileId: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val notification= mNotification[position]


        if(notification.getText().equals("started following you"))
        {
            holder.text.text="started following you"
        }
        else if(notification.getText().equals("was in your chats"))
        {
            holder.text.text="was in your chats"
        }
        else if(notification.getText().equals("liked your post"))
        {
            holder.text.text="liked your post"
        }
        else if(notification.getText().contains("commented:")) {
            holder.text.text = notification.getText().replace("commented:","commented: ")
        }
        else
        {
            holder.text.text=notification.getText()
        }


        userInfo(holder.profileImage,holder.userName,notification.getUserId())
        if (notification.isIsPost()) {
            holder.postImage.visibility=View.VISIBLE
            if (notification.getText().contains("commented:")) {
                holder.postImage.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    if (notification.isIsPost()) {
                        val editor =
                            mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        editor.putString("postId", notification.getPostId())
                        editor.apply()
                        (mContext as FragmentActivity).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,
                                PostDetailsFragment()
                            ).commit()
                    } else {
                        val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        editor.putString("profileId", notification.getUserId())
                        editor.apply()
                        (mContext as FragmentActivity).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, ProfileFragment()).commit()

                    }
                }
            }


            if (!notification.isIsPost()) {
                if (notification.getText().contains("liked your post")) {
                    holder.postImage.visibility = View.VISIBLE
                    getPostImage(holder.postImage, notification.getPostId())

                }

            } else
                if (notification.getText().contains("commented:")) {
                    holder.postImage.visibility = View.VISIBLE
                    getPostImage(holder.postImage, notification.getPostId())

                } else

                    if (notification.getText().contains("liked your post")) {
                        holder.postImage.visibility = View.VISIBLE
                        holder.itemView.setOnClickListener{
                            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                            editor.putString("postId",notification.getPostId())
                            editor.apply()
                            (mContext as FragmentActivity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,
                                    PostDetailsFragment()
                                ).commit()
                        }
                        getPostImage(holder.postImage, notification.getPostId())

                    } else if (notification.getText().contains("started following you")) {

                        holder.postImage.visibility = View.GONE
                        holder.itemView.setOnClickListener{
                            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                            editor.putString("profileId",notification.getUserId())
                            editor.apply()
                            (mContext as FragmentActivity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,ProfileFragment()).commit()
                        }

                    } else if (notification.getText().contains("was in your chats")) {
                        holder.postImage.visibility = View.GONE
                        holder.mssgBtn.visibility=View.GONE

                        holder.itemView.setOnClickListener {
                            val editor =
                                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            editor.putString("profileId", notification.getUserId())
                            editor.apply()
                            (mContext as FragmentActivity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,ProfileFragment()).commit()


                        }
                        holder.mssgBtn.setOnClickListener {
                            val editor =
                                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            editor.putString("profileId", notification.getUserId())
                            editor.apply()
                            (mContext as FragmentActivity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,ProfileFragment()).commit()





                        }
                    }

        }

//
    }


    inner class  ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var profileImage:CircleImageView
        var postImage:ImageView
        var mssgBtn:Button
        var userName:TextView
        var text:TextView

        init {
            profileImage=itemView.findViewById(R.id.notifications_profile_image)
            postImage=itemView.findViewById(R.id.notifications_post_image)
            userName=itemView.findViewById(R.id.username_notification)
            text=itemView.findViewById(R.id.comment_notification)
            mssgBtn=itemView.findViewById(R.id.notifications_mssgBtn)
        }
    }
    private fun userInfo(imageView:ImageView,userName:TextView,publisherId:String) {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {

                    val user = datasnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)
                   userName.text = user!!.getUsername()


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPostImage(imageView:ImageView,postId:String)
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId)

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val post= p0.getValue<Post>(Post::class.java)

                    Picasso.get().load(post!!.getPostimage()).placeholder(R.drawable.profile).into(imageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}