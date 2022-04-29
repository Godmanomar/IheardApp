package com.novusvista.omar.iheardapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.novusvista.omar.iheardapp.Adapter.AdapterVideo
import com.novusvista.omar.iheardapp.Model.ModelVideo
import com.novusvista.omar.iheardapp.Model.User
import com.novusvista.omar.iheardapp.ui.ProfileFragment
import kotlinx.android.synthetic.main.activity_add_video.*


class AddVideoActivity : AppCompatActivity() {
    private lateinit var actionBar: ActionBar
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private lateinit var cameraPermission: Array<String>
    private lateinit var progressdialog:ProgressDialog
    private lateinit var videoArrayList: ArrayList<ModelVideo>

    private lateinit var  adapterVideo:AdapterVideo

    private var videoUri: Uri? = null

    private var myUrl = ""
    private  var title:String = ""
    private var mContext: Context?=null
    private var mUser: List<User>?=null
    private val profile:ProfileFragment?=null
    private  var uploadHappen=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        //  actionBar=supportActionBar!!
        //  title="Add New Video"
        // actionBar.setDisplayHomeAsUpEnabled(true)
        // actionBar.setDisplayShowHomeEnabled(true)
        loadVideosFromFirebase()
        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
         //Init()ProgressBar
        progressdialog= ProgressDialog(this)
        progressdialog.setTitle("Please wait")
        progressdialog.setMessage("Uploading Video...")
        progressdialog.setCanceledOnTouchOutside(false)



//
//        image_story_btn.setOnClickListener {
//
//
//            val intent = Intent(this@AddVideoActivity, AddPostActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        video_story_btn.setOnClickListener {
//            // val story:StoryActivity?=null
//            //  story!!.isVideo=true
////            val intent = Intent(this@AddVideoActivity, AddVideoActivity::class.java)
////            startActivity(intent)
//
//            title=titleEt.text.toString().trim()
//            if (TextUtils.isEmpty(title)){
//                //no title is entered
//           Toast.makeText(this,"Title is required",Toast.LENGTH_SHORT).show()
//
//            }
//            else if (videoUri==null){
//
//                Toast.makeText(this,"pick video first",Toast.LENGTH_SHORT).show()
//
//        }
//            else{
//                //title entered and video picked, upload video
//                //new call to cloudinary here*
//                 uploadVideoFirebase()
//            }
//
//
//        }
//        imagefab.setOnClickListener {
//            image_story_btn.visibility = View.VISIBLE
//        }
//        fab.setOnClickListener {
//            videoPickDialog()
//
//            video_story_btn.visibility = View.VISIBLE
//        }
//        playvideo_btn.setOnClickListener{
//            playvideo_btn.visibility=View.GONE
//        }
//
//
//
   }

    private fun loadVideosFromFirebase() {
      videoArrayList=ArrayList()
        
        // put new clouldify db reference here comment out the old one
        val ref=FirebaseDatabase.getInstance().getReference("Videos")
        ref.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                videoArrayList.clear()
                for (ds in snapshot.children) {
                    val modelVideo = ds.getValue(ModelVideo::class.java)
                    videoArrayList.add(modelVideo!!)

                }

                adapterVideo = AdapterVideo(this@AddVideoActivity, videoArrayList)
                //videosRv.adapter = adapterVideo
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

            }
        })}
    private fun uploadVideoFirebase() {

       progressdialog.show()

        //*  want to connect to CDN here      //*
//        val url = Intent.getIntent(myUrl).getStringExtra("url")
//        Log.d(myUrl,"Url is $url")
//        val videoView = findViewById(R.id.videoView) as VideoView
//        videoView.setVideoPath(url + "?id=" + FirebaseAuth.getInstance().currentUser)
//
        val timestamp=""+System.currentTimeMillis()
        val filePathAndName="Videos/video_$timestamp"
        // put new clouldify storage reference here comment out the old one

        val storageReference=FirebaseStorage.getInstance().getReference(filePathAndName)
        //uploading video using url of video to storage
        var uploadTask: StorageTask<*>
        uploadTask = storageReference.putFile(videoUri!!)
        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful)
            {
                task.exception?.let {
                    throw it
                   progressdialog.dismiss()
                }
            }
            return@Continuation storageReference.downloadUrl
        })

            .addOnCompleteListener (OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()
                    // put new clouldify db here comment out the old one
                    val dbReference=FirebaseDatabase.getInstance().reference.child("Videos")

        val hashMap=HashMap<String,Any?>()


        hashMap["id"]="$timestamp"
        hashMap["title"]="$title"
        hashMap["timestamp"]="$timestamp"
                    hashMap["videoUri"]="$downloadUrl"
                    hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
        dbReference.push().setValue(hashMap)
                    Toast.makeText(this, "Video uploaded successfully.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@AddVideoActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                    progressdialog.dismiss()


                }else{
                    progressdialog.dismiss()
                  // playvideo_btn.visibility=View.GONE
                }
            })

        uploadHappen=true
    }


    private fun setVideoToVideoView() {

        //video play controllers
      val mediaController=MediaController(this)
        mediaController.setAnchorView(player_view)
        //set media controller
       // videosRv.setMediaController(mediaController)
        //set video uri
      // video_view.setVideoURI(videoUri)
       // if(uploadHappen) {

         //   playvideo_btn.visibility = View.VISIBLE
       // }
      //  video_view.requestFocus()
       // video_view.setOnPreparedListener {
           // //when video is ready, by default don't play automatically
          //  video_view.pause()
        }
    }


    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Pick Video From").setItems(options) { dialogInterface, i ->
//            //handle Item clicks
//            if (i === 0) {
//                //camera clicked
//
//                if (!checkCameraPermissions()) {
//                    requestCameraPermission()
//                } else {
//                    videoPickCamera()
//                }
//            } else {
//                //gallery clicked
//                videoPickGallery()
//
//
//            }
//
//
//        }.show()
//
    }


    private fun requestCameraPermission() {
   //     ActivityCompat.requestPermissions(
//            this,
//            cameraPermission,
//            CAMERA_REQUEST_CODE
       // )

    }

    private fun checkCameraPermissions(): Boolean {
//        val result1 = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//        val result2 = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//        return result1 && result2

        return false

    }

    private fun videoPickGallery() {
//        val intent = Intent()
//        intent.type = "video/*"
//        intent.action=Intent.ACTION_GET_CONTENT
//        startActivityForResult(
//            Intent.createChooser(intent, "Choose video"), VIDEO_PICK_GALLERY_CODE
//        )

    }

    private fun videoPickCamera() {
//        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE)

    }

//    override fun onSupportNavigateUp(): Boolean {
//       onBackPressed()
//        return super.onSupportNavigateUp()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode){
//            CAMERA_REQUEST_CODE->
//                if(grantResults.size>0){
//                    //check if permissions is denied or allowed
//                    val cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED
//                    val storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED
//                    if(cameraAccepted&&storageAccepted)
//                    {
//                        // both permission
//                        videoPickCamera()
//
//                    }
//                    else{
//                        Toast.makeText(this,"Permissions denied",Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//if(resultCode== Activity.RESULT_OK) {
//    if (requestCode == VIDEO_PICK_CAMERA_CODE) {
//        //video picked from camera
//        videoUri==data!!.data
//
//        setVideoToVideoView()
//
//    }else if(requestCode==VIDEO_PICK_GALLERY_CODE) {
//        //video pick from gallery
//        videoUri=data!!.data
//        setVideoToVideoView()
//
//    }
//
//}
//        else{
//    Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
//}
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private  fun moveToFragment(fragment: Fragment){

//        val pref = mContext!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
//        val user=FirebaseAuth.getInstance().currentUser
//        pref.putString("profileId",user!!.getUid())
//        pref.apply()
//        val fragmentTrans = supportFragmentManager.beginTransaction()
//        fragmentTrans.replace(R.id.fragment_container,fragment)
//        fragmentTrans.commit()
//


    }


