package com.example.tryingsomething.ui.main

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
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
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.jar.Manifest
import javax.annotation.meta.When

class MainActivityFragment : Fragment() {
    val IMAGE_GALLERY_CODE = 2
    val CAMERA_REQUEST_CODE = 1000
    val CAMERA_TAKE_PICUTRE = 1
    companion object {
        fun newInstance() = MainActivityFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.runners.observe(viewLifecycleOwner, {
            runners -> autoCompleteTextView.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, runners))
        })

        button_take_photo.setOnClickListener {
            prepTakePhoto()
        }
        imageGallery.setOnClickListener {
            prepOpenGallery()
        }
    }

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
            }
        }
    }

}