package com.android.imagepicker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import lib.android.imagepicker.ImagePicker

class MainActivity : AppCompatActivity(), ImagePicker.OnImageSelectedListener {

    lateinit var imagePicker: ImagePicker
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagePicker = ImagePicker(this, BuildConfig.APPLICATION_ID)
        imagePicker.setImageSelectedListener(this)

        imageView = findViewById(R.id.image)

        findViewById<Button>(R.id.take_photo_from_camera).setOnClickListener {
            imagePicker.takePhotoFromCamera()
        }

        findViewById<Button>(R.id.take_photo_from_gallery).setOnClickListener {
            imagePicker.takePhotoFromGallery()
        }
    }

    override fun onImageSelectSuccess(imagePath: String) {
        imageView?.let {
            Glide.with(this@MainActivity)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .load(imagePath)
                .into(it)
        }
    }

    override fun onImageSelectFailure() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }
}