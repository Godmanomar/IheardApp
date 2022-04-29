package com.novusvista.omar.iheardapp.ui.post


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.novusvista.omar.iheardapp.Adapter.PostAdapter
import com.novusvista.omar.iheardapp.Model.Post

import com.novusvista.omar.iheardapp.R
import com.novusvista.omar.iheardapp.ui.post.player.MediaPlayerImplementation
import com.novusvista.omar.iheardapp.ui.post.player.OnSwipeListener
import com.novusvista.omar.iheardapp.utilities.InjectorUtils
import kotlinx.android.synthetic.main.activity_add_video.*
import kotlin.properties.ReadOnlyProperty

/**
 * A simple [Fragment] subclass.
 */
class PostDetailsFragment : Fragment(),View.OnTouchListener {

    private val postViewModel:PostViewModel by viewModels{
        InjectorUtils.provideHomeViewModelFactory()
    }
private lateinit var playerView:PlayerView
    private lateinit var likeButton:ToggleButton
    private lateinit var shareButton:ImageView
    private lateinit var retryLayout:LinearLayout
    private lateinit var retryText:TextView
    private lateinit var loading:ImageView

    private lateinit var listOfUrl:List<String>
    private lateinit var mediaPlayer:MediaPlayerImplementation
    private lateinit var gestureDetector: GestureDetector

//fix later
    private fun viewModels(function: () -> Any): ReadOnlyProperty<PostDetailsFragment, PostViewModel> {
      return viewModels(function)
    }

    private  var postAdapter: PostAdapter?=null
    private  var postList:MutableList<Post>?=null
    private var postId:String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val  view= inflater.inflate(R.layout.fragment_post_details, container, false)
        playerView=view.findViewById(R.id.player_view)
        likeButton=view.findViewById(R.id.switch_like)
        shareButton=view.findViewById(R.id.share)
        retryLayout=view.findViewById(R.id.layout_retry)
        retryText=view.findViewById(R.id.text_retry)
        loading=view.findViewById(R.id.loading)

        val preferences=context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(preferences!=null)
        {
            postId= preferences.getString("postId","none")
        }

        var recyclerView:RecyclerView

        recyclerView=view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager=LinearLayoutManager(context)
       recyclerView.layoutManager=linearLayoutManager
        postList=ArrayList()
        postAdapter=context?.let{ PostAdapter(it,postList as ArrayList<Post>) }
        recyclerView.adapter=postAdapter

        retrievePosts()
        return view
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }



    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
       postViewModel.setPlayerState(playerView.player?:return)
        mediaPlayer.releasePlayer()
        playerView.player=null
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return  gestureDetector.onTouchEvent(event)
    }
    private fun play() {
        postViewModel.urls.observe(viewLifecycleOwner){urls->
            if (urls.isEmpty()){
                setViews(false)
            }else{
                setViews(true)
                listOfUrl=urls
                mediaPlayer.play(postViewModel.getPlayerState(),urls)
            }

        }
    }

    private fun initPlayer() {
        mediaPlayer=MediaPlayerImplementation.getInstance(requireContext())!!
        playerView.player=mediaPlayer.player
        mediaPlayer.player.addListener(object: Player.EventListener{
            override fun onPlayerError(error: ExoPlaybackException) {
                Log.d(TAG,"Player Error:${error.message!!}")
                postViewModel.setPlayerState(mediaPlayer.player)
                setViews(false)
                super.onPlayerError(error)
            } })
        play()
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId)
        postsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                postList?.clear()

                val post = datasnapshot.getValue(Post::class.java)

                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }




    private fun setViews(isEnabled: Boolean) {
         loading.visibility=View.GONE
        if(isEnabled){
            shareButton.visibility=View.VISIBLE
            likeButton.visibility=View.VISIBLE
            playerView.visibility=View.VISIBLE
            shareButton.setOnClickListener { share() }
             val onSwipeListener:OnSwipeListener=object:OnSwipeListener() {
                 override fun onSwipe(direction: Direction?): Boolean {
                     return when (direction) {
                         Direction.up -> {
                             if (mediaPlayer.player.hasNext()) {
                                 mediaPlayer.player.next()
                                 likeButton.isChecked = false
                             }
                             true
                         }
                         Direction.down -> {
                             if (mediaPlayer.player.hasNext()) {
                                 mediaPlayer.player.next()
                                 likeButton.isChecked = false
                             }
                             true

                         }
                         else -> super.onSwipe(direction)
                     }
                 }



             }
            gestureDetector= GestureDetector(requireContext(),onSwipeListener)
            playerView.setOnTouchListener(this)

        }else{
            playerView.visibility=View.GONE
            shareButton.visibility=View.GONE
            likeButton.visibility=View.GONE
        }
        setRetryLayout(!isEnabled)
    }

    private fun setRetryLayout(isEnabled: Boolean) {
if(isEnabled){
    retryLayout.visibility=View.VISIBLE
    retryText.visibility=View.VISIBLE
    retryLayout.setOnClickListener {
        postViewModel.retry.value=!(postViewModel.retry.value!!)
        mediaPlayer.player.retry()
        loading.visibility=View.VISIBLE
    }
}else{
    retryLayout.visibility=View.GONE
    retryText.visibility=View.GONE
}
    }
    private fun share(){
        if (listOfUrl.isNotEmpty()) {
            val url = listOfUrl[mediaPlayer.player.currentWindowIndex + 1]
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "saw this on IheardApp:\n$url")
            }
            requireContext().startActivity(Intent.createChooser(intent,"Share IheardApp"))
        }
    }


}
