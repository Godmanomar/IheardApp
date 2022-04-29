package com.novusvista.omar.iheardapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import com.novusvista.omar.iheardapp.Adapter.ViewPagerAdapter
import com.novusvista.omar.iheardapp.Interface.EditImageFragmentListener
import com.novusvista.omar.iheardapp.Interface.FilterListFragmentListener
import com.novusvista.omar.iheardapp.Utils.BitmapUtils

import com.novusvista.omar.iheardapp.Utils.NonSwipeableViewPager
import com.theartofdev.edmodo.cropper.CropImage
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_main_filter.*
import kotlinx.android.synthetic.main.content_main_filter.*

class MainFilterActivity : AppCompatActivity(), FilterListFragmentListener,
    EditImageFragmentListener {

    val SELECT_GALLERY_PERMISSION = 1000

    init {
        System.loadLibrary("NativeImageProcessor")

    }

    object Main {
        val IMAGE_NAME = "myimage.jpg"
    }


    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        image_preview.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        image_preview.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onConstrantChanged(contrast: Float) {
        contrastFinal = contrast
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrast))
        image_preview.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )

    }

    override fun onEditStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditCompleted() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrastFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        finalImage = myFilter.processFilter(bitmap)


    }


    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)


    }

    private fun resetControls() {
        if (editImageFragment != null)
            editImageFragment.resetControls()
        brightnessFinal = 0
        saturationFinal = 1.0f
        contrastFinal = 1.0f

    }

    internal var originalImage: Bitmap? = null
    internal lateinit var filteredImage: Bitmap
    internal lateinit var finalImage: Bitmap
    internal lateinit var filterListFragment: FilterListFragment
    internal lateinit var editImageFragment: EditImageFragment

    internal var brightnessFinal = 0
    internal var saturationFinal = 1.0f
    internal var contrastFinal = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_filter)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Iheardapp Filter"
        loadImage()
        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)


    }



    private fun setupViewPager(viewPager: NonSwipeableViewPager) {

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //add filter list fragment
        filterListFragment = FilterListFragment()
        filterListFragment.setListener(this)
        // add edit image fragment
        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)


        adapter.addFragment(filterListFragment, "FILTERS")
        adapter.addFragment(editImageFragment, "EDIT")

        viewPager.adapter = adapter

    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Main.IMAGE_NAME, 50, 50)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(originalImage)


    }



    //Ctrl+O
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == R.id.action_open) {
            openImageFromGallery()
            return true
        } else if (id == R.id.action_save) {
            saveImageToGallery()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImageToGallery() {



        Dexter.withActivity(this)

            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val path = BitmapUtils.insertImage(
                            contentResolver,
                            finalImage,
                            System.currentTimeMillis().toString() + "_profile.jpg",
                            ""
                        )


                        if (!TextUtils.isEmpty(path)) {
                            val snackbar = Snackbar.make(
                                coordinator,
                                "Image saved to gallery",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("OPEN", {
                                    openImage(path)
                                })
                            snackbar.show()
                        } else {
                            val snackbar = Snackbar.make(
                                coordinator,
                                "Unable to save image",
                                Snackbar.LENGTH_LONG
                            )

                            snackbar.show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Permissions denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()

                }


            }).check()


    }



    private fun openImage(path: String?) {
        val intent=Intent()
        intent.action=Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path),"image/*")
        startActivity(intent)


    }

    private fun openImageFromGallery() {
        //we will use Dexter to request runtime permission and process
              Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                 if(report!!.areAllPermissionsGranted())
                 {
                     val intent=Intent(Intent.ACTION_PICK)
                     intent.type= "image/*"
                     startActivityForResult(intent,SELECT_GALLERY_PERMISSION)

                 }
                    else
                     Toast.makeText(applicationContext,"Permissions denied",Toast.LENGTH_SHORT).show()

                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token:PermissionToken?) {
                    token!!.continuePermissionRequest()
                }

            }).check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if(resultCode==Activity.RESULT_OK && requestCode == SELECT_GALLERY_PERMISSION)
       {
     val bitmap= BitmapUtils.getBitmapFromGallery(this,data!!.data!!,50,50)

           //clear bitmap memory
           originalImage!!.recycle()
           finalImage!!.recycle()
           filteredImage!!.recycle()


           originalImage=bitmap?.copy(Bitmap.Config.ARGB_8888,true)
           filteredImage=originalImage!!.copy(Bitmap.Config.ARGB_8888,true)
           finalImage=originalImage!!.copy(Bitmap.Config.ARGB_8888,true)

          bitmap?.recycle()

           //render select image thumb
           filterListFragment.displayImage(bitmap)

       }
    }
}

