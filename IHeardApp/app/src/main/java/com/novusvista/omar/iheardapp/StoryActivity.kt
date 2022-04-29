package com.novusvista.omar.iheardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Adapter.StoryAdapter
import com.novusvista.omar.iheardapp.Model.Story
import com.novusvista.omar.iheardapp.Model.User
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.story_item.*
import java.util.*
import kotlin.collections.ArrayList

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {


    var  currentUserId:String=""
    var  userId:String=""
    var counter=0
    var pressTime=0L
    var limitTime=500L


    var imagesList:List<String>?=null
    var videosList:List<String>?=null
    var storyIdsList:List<String>?=null
    var storieesProgressView:StoriesProgressView?=null
    private val onTouchListener=View.OnTouchListener{view,motionEvent->
        when(motionEvent.action)
        {
            MotionEvent.ACTION_DOWN->
            {
pressTime=System.currentTimeMillis()
                storieesProgressView!!.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP->
            {
                val now=System.currentTimeMillis()
                storieesProgressView!!.resume()
                return@OnTouchListener limitTime<now-pressTime

            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
        currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        userId=intent.getStringExtra("userId")


        storieesProgressView=findViewById(R.id.stories_progress)
        layout_seen.visibility= View.GONE
        story_delete.visibility= View.GONE

        if(userId==currentUserId)
        {
            layout_seen.visibility= View.VISIBLE
            story_delete.visibility= View.VISIBLE

        }
        getStories(userId!!)
        userInfo(userId!!)

        val  reverse:View=findViewById(R.id.reverse)
        reverse.setOnClickListener{storieesProgressView!!.reverse()}
        reverse.setOnTouchListener(onTouchListener)

        val  skip:View=findViewById(R.id.skip)
        skip.setOnClickListener{storieesProgressView!!.skip()}
        skip.setOnTouchListener(onTouchListener)

seen_number.setOnClickListener{
    val intent= Intent(this@StoryActivity,ShowUserActivity::class.java)
    intent.putExtra("id",userId)
    intent.putExtra("storyid",storyIdsList!![counter])
    intent.putExtra("title","views")
    startActivity(intent)
}
        story_delete.setOnClickListener {
 val ref=FirebaseDatabase.getInstance().reference
     .child("Story")
     .child(userId)
     .child(storyIdsList!![counter])
            ref.removeValue().addOnCompleteListener{ task->

                    if(task.isSuccessful)
                    {
                        Toast.makeText(this@StoryActivity,"Deleted",Toast.LENGTH_LONG).show()

                    }
                


            }

        }
    }
private  fun  getStories(userId: String)
    {
        imagesList=ArrayList()
        storyIdsList=ArrayList()

        val ref= FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
        ref.addListenerForSingleValueEvent(object :ValueEventListener{


            override fun onDataChange(datasnapshot: DataSnapshot) {
                (imagesList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()
                for(snapshot in datasnapshot.children)
                {
                    val story: Story?=snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent=System.currentTimeMillis()
                    if(timeCurrent>story!!.getTimeStart()&& timeCurrent<story.getTimeEnd())
                    {
                        (imagesList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())

                    }
                }
                storieesProgressView!!.setStoriesCount((imagesList as ArrayList<String>).size)
                storieesProgressView!!.setStoryDuration(6000L)
                storieesProgressView!!.setStoriesListener(this@StoryActivity)
                storieesProgressView!!.startStories(counter)
                Picasso.get().load(imagesList!!.get(counter)).placeholder(R.drawable.back_ui_story).into(image_story)
                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
    private  fun  getVideoStories(userId: String)
    {
        videosList=ArrayList()
        storyIdsList=ArrayList()

        val ref= FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
        ref.addListenerForSingleValueEvent(object :ValueEventListener{


            override fun onDataChange(datasnapshot: DataSnapshot) {
                (videosList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()
                for(snapshot in datasnapshot.children)
                {
                    val story: Story?=snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent=System.currentTimeMillis()
                    if(timeCurrent>story!!.getTimeStart()&& timeCurrent<story.getTimeEnd())
                    {
                        (videosList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())

                    }
                }
                storieesProgressView!!.setStoriesCount((videosList as ArrayList<String>).size)
                storieesProgressView!!.setStoryDuration(6000L)
                storieesProgressView!!.setStoriesListener(this@StoryActivity)
                storieesProgressView!!.startStories(counter)
                Picasso.get().load(videosList!!.get(counter)).placeholder(R.drawable.back_ui_story).into(image_story)
                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
    private fun userInfo( userId:String) {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.exists()) {

                    val user = datasnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.back_ui_story)
                        .into(story_profile_image)
                    user_name_post .text=user!!.getUsername()
//                    story_username.text=user!!.getFullname()
//                    story_username.text=user!!.getUid()





                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private  fun addViewToStory(storyId:String) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
            .child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(true)




    }
  private  fun seenNumber(storyId:String)
    {
        val ref= FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
            .child(storyId)
            .child("views")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
              seen_number.text=""+ datasnapshot.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    override fun onComplete() {
      finish()
    }

    override fun onPrev() {
        if(counter>0) {
            Picasso.get().load(imagesList!![--counter]).placeholder(R.drawable.back_ui_story)
                .into(image_story)
            seenNumber(storyIdsList!![counter])
        }
    }

    override fun onNext() {
        Picasso.get().load(imagesList!![++counter]).placeholder(R.drawable.back_ui_story).into(image_story)
        addViewToStory(storyIdsList!![counter])
        seenNumber(storyIdsList!![counter])


    }

    override fun onDestroy() {
        super.onDestroy()
        storieesProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storieesProgressView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        storieesProgressView!!.pause()
    }
}
