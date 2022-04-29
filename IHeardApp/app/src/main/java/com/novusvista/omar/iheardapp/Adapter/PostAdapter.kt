package com.novusvista.omar.iheardapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.novusvista.omar.iheardapp.CommentsActivity
import com.novusvista.omar.iheardapp.MainActivity
import com.novusvista.omar.iheardapp.Model.Post
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.ShowUserActivity
import com.novusvista.omar.iheardapp.ui.post.PostDetailsFragment
import com.novusvista.omar.iheardapp.ui.ProfileFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class PostAdapter
    (private val mContext: Context,
     private val mPost:List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private  var firebaseUser: FirebaseUser?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val  view=LayoutInflater.from(mContext).inflate(R.layout.posts_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPost[position]


        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        if(post.getDescription().equals(""))
        {

            holder.description.visibility = View.GONE
        }
        else{

            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(),holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments,post.getPostid())
        checkSavedStatus(post.getPostid(),holder.saveButton)

        holder.postImage.setOnClickListener{
            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId",post.getPostid())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                    PostDetailsFragment()
                ).commit()

        }
        holder.publisher.setOnClickListener {
            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId",post.getPublisher())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()


        }
        holder.profileImage.setOnClickListener {
            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId",post.getPublisher())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()


        }
        holder.postImage.setOnClickListener {

        }



        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

                addNotification(post.getPublisher(),post.getPostid())

            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()
                val intent=Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)


            }


        }
        holder.likes.setOnClickListener {
            val intent=Intent(mContext, ShowUserActivity::class.java)
            intent.putExtra("id",post.getPostid())
            intent.putExtra("title","likes")
            mContext.startActivity(intent)

        }

        holder.commentButton.setOnClickListener{

            val intentComment=Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId",post.getPostid())
            intentComment.putExtra("publisherId",post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener{

            val intentComment=Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId",post.getPostid())
            intentComment.putExtra("publisherId",post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.saveButton.setOnClickListener{
            if(holder.saveButton.tag =="Save")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .removeValue()

            }

        }


    }

    private fun numberOfLikes(likes: TextView, postid: String) {
        val LikesRef=FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists())
                {
                    likes.text=datasnapshot.childrenCount.toString()+ " likes"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun  getTotalComments(comments:TextView, postid: String) {
        val commentsRef=FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists())
                {
                    comments.text="view All "+datasnapshot.childrenCount.toString()+ " comments"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    private fun isLikes(postid: String, likeButton: ImageView) {
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val LikesRef=FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)
        LikesRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag="Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag="Like"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var profileImage:CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton:ImageView
        var userName:TextView
        var saveButton:ImageView
        var likes: TextView
        var publisher: TextView
        var description : TextView
        var comments: TextView

        init {
            profileImage=itemView.findViewById(R.id.user_profile_image_post)
            postImage=itemView.findViewById(R.id.post_image_home)
            likeButton=itemView.findViewById(R.id.post_image_like_btn)
            commentButton=itemView.findViewById(R.id.post_image_comment_btn)
            userName=itemView.findViewById(R.id.user_name_post)
            saveButton=itemView.findViewById(R.id.save_btn)
            likes=itemView.findViewById(R.id.likes)
            publisher=itemView.findViewById(R.id.publisher)
            description=itemView.findViewById(R.id.description)
            comments=itemView.findViewById(R.id.comments)



        }


    }
    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val userRef=FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)


                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text=user!!.getUsername()
                    publisher.text=user!!.getFullname()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun checkSavedStatus(postid: String,imageView: ImageView)
    {
        val savesRef=FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag ="Saved"


                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag ="Save"

                }


            }



            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun addNotification(userId:String,postId:String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] =firebaseUser!!.uid
        notiMap["text"] = "liked your post"
        notiMap["postid"]= postId
        notiMap["isIsPost"] = true

        notiRef.push().setValue(notiMap)
    }


}