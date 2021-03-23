package com.example.tryingsomething

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.tryingsomething.ui.main.AuthFragment
import com.example.tryingsomething.ui.main.MainActivityFragment
import com.example.tryingsomething.ui.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*

class MainActivity : AppCompatActivity() {
    private lateinit var authFragment: AuthFragment
    private lateinit var mainFragment: MainActivityFragment

    private var user: FirebaseUser? = null
    private val AUTH_REQ_CODE = 4
    val LOCATION_PERMISSION_CODE = 3
    val IMAGE_GALLERY_CODE = 2
    val CAMERA_REQUEST_CODE = 1000
    val CAMERA_TAKE_PICUTRE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity1_activity)

        mainFragment = MainActivityFragment.newInstance()
        authFragment = AuthFragment.newInstance()
       val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commitNow()
        }
        //check if auth is ok open main fragment, else Auth fragment
    }

}