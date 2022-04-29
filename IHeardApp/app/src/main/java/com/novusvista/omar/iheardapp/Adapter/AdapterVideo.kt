package com.novusvista.omar.iheardapp.Adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Model.ModelVideo

import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.row_video.view.*
import java.util.*
import kotlin.collections.ArrayList







class AdapterVideo (private var context:Context,private var videoArrayList:ArrayList<ModelVideo>?):RecyclerView.Adapter<AdapterVideo.HolderVideo>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderVideo {
        //inflate layout row_video.xml
val view=LayoutInflater.from(context).inflate(R.layout.row_video,parent,false)

        view.progressBar.max=1000
        val currentProgress=600
        ObjectAnimator.ofInt(view.progressBar,"progress",currentProgress)
            .setDuration(2000)
            .start()

        return HolderVideo(view)
    }



    override fun onBindViewHolder(holder: HolderVideo, position: Int) {
        /*-get data, set data, handle clicks etc-*/

    //get data
        val modelVideo=videoArrayList!![position]


        val id:String?=modelVideo.id
        val titie :String?=modelVideo.title
        val timestamp:String?=modelVideo.timestamp
        val videoUri:String?=modelVideo.videoUri

        val calendar=Calendar.getInstance()
        calendar.timeInMillis=timestamp!!.toLong()
        val formattedDateTime=android.text.format.DateFormat.format("dd/MM/yyyy K:mm a,calendar",calendar).toString()

        holder.titleTv.text=titie
        holder.timeTv.text=formattedDateTime
        publisherInfo(holder.profileImage, holder.userName, holder.userName,modelVideo.getPublisher())
        setVideoUrl(modelVideo,holder)


    }
    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object: ValueEventListener {
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


    private fun setVideoUrl(modelVideo: ModelVideo,holder: HolderVideo) {
        holder.progressBar.visibility=View.VISIBLE
        val videoUrl:String?=modelVideo.videoUri
        val  mediaController=MediaController(context)

        mediaController.setAnchorView(holder.videoView)
        val videoUri= Uri.parse(videoUrl)
        holder.videoView.setMediaController(mediaController)
        holder.videoView.setVideoURI(videoUri)
        holder.videoView.requestFocus()

        holder.videoView.setOnPreparedListener{mediaPlayer->
            mediaPlayer.start()

        }

        holder.videoView.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
            when(what){
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START->{
                    holder.progressBar.visibility=View.VISIBLE
                    return@OnInfoListener true

                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START->{
                    holder.progressBar.visibility=View.VISIBLE
                    return@OnInfoListener true

                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END->{
                    holder.progressBar.visibility=View.GONE
                    return@OnInfoListener true
                }

            }
            holder.progressBar.visibility=View.GONE
            false
        })
holder.videoView.setOnCompletionListener { mediaPlayer ->
mediaPlayer.start()

}


    }


    override fun getItemCount(): Int {
        return videoArrayList!!.size//return size/length or the arrayList
    }


    //view holder class holds and inits UI Views or row_video.xml
    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {
// init Views
        var userName:TextView=itemView.findViewById(R.id.publisher_name)
        var videoView:VideoView=itemView.findViewById(R.id.videoView)
        var titleTv:TextView=itemView.findViewById(R.id.titleTv)
        var timeTv:TextView=itemView.findViewById(R.id.timeTv)
        var progressBar:ProgressBar=itemView.findViewById(R.id.progressBar)
        var profileImage:CircleImageView=itemView.findViewById(R.id.user_profile_video_post)



        }


    }

