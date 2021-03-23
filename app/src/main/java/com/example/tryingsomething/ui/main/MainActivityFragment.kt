package com.example.tryingsomething.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.tryingsomething.R
import com.example.tryingsomething.dto.Another
import com.example.tryingsomething.dto.Runner
import com.example.tryingsomething.dto.ControlPoint
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*

class MainActivityFragment : Fragment() {
    private var user: FirebaseUser? = null
    private val AUTH_REQ_CODE = 4
    val LOCATION_PERMISSION_CODE = 3
    val IMAGE_GALLERY_CODE = 2
    val CAMERA_REQUEST_CODE = 1000
    val CAMERA_TAKE_PICUTRE = 1
    companion object {
        fun newInstance() = MainActivityFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var locationViewModel: MainViewModel
    private var _runnerBib = 0
    private var controlPoint = ControlPoint()
    private var runners: ArrayList<Runner> = ArrayList<Runner>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            viewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.plants.observe(viewLifecycleOwner, Observer{
            runners -> autoCompleteTextView.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, runners))
        })
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            var selectedRunner = parent.getItemAtPosition(position) as Another
            _runnerBib = selectedRunner.bib
        }
        viewModel.controlPoints.observe(viewLifecycleOwner, Observer {
            specimens ->
            spinner_runners.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, specimens)
        })
        button_take_photo.setOnClickListener {
            prepTakePhoto()
        }
        imageGallery.setOnClickListener {
            prepOpenGallery()
        }
        button_save.setOnClickListener {
            saveSpecie()
        }

        prepLocationUpdates()
        checkAuthForCP()
    }

    private fun checkAuthForCP() {
        if (user == null){
            logon()
        }
        user ?: return
    }

    private fun logon() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH_REQ_CODE
        )
    }

    private fun saveSpecie() {
        storeSpecimen()
        //change user!! with user?
        viewModel.save(controlPoint, user!!, runners)
        controlPoint = ControlPoint()
        runners = ArrayList<Runner>()
    }
    internal fun storeSpecimen(){
        controlPoint.apply {
            latitude = editTextLatitude.text.toString()//from service
            longitude = editTextLongitude.text.toString()//from service
            date = System.currentTimeMillis().toString()
        }
        var runner = Runner( runnerBib = editTextBibNumber.text.toString().toLong(),
                runnerName = editTextName.text.toString(),
                dateTime = System.currentTimeMillis().toString())
        runners.add(runner)
        viewModel.controlPoint = controlPoint
    }
    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
          //  requestLocationUpdates()
        }else {
            val permisionRequest = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permisionRequest, LOCATION_PERMISSION_CODE)
        }
    }

//    private fun requestLocationUpdates() {
//        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
//        locationViewModel.getLocationLiveData().observe(this, Observer {
//
//        })
//    }

    private fun prepOpenGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            startActivityForResult(this, IMAGE_GALLERY_CODE)
        }
    }

    private fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            takePhoto()
        } else{
            //staveno e array zatoa sto mozi povekje permisii odednas da barame sega e samo edna
            val permissionRequest = arrayOf(android.Manifest.permission.CAMERA)
            requestPermissions(permissionRequest, CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //ako barame dozvola za povekje permisii naednas togas treba da hendlame mozi da vrati povekje result naednas
                    takePhoto()
                }
            }
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //    requestLocationUpdates()
                }
            }
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePictureIntent -> takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, CAMERA_TAKE_PICUTRE)
        }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_TAKE_PICUTRE){
                val imageBitmap = data!!.extras!!.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
            }else if (requestCode == IMAGE_GALLERY_CODE){
                if (data != null && data.data != null){
                    val image = data.data
                    val source = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        ImageDecoder.createSource(activity!!.contentResolver, image!!)
                    } else {
                        TODO("VERSION.SDK_INT < P")
                    }
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(bitmap)
                }
            }else if (requestCode == AUTH_REQ_CODE){
                user = FirebaseAuth.getInstance().currentUser
            }
        }
    }
}