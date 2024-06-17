package com.example.dailypicture

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CameraFragment : Fragment() {
        lateinit var imagePreview_ImageView: ImageView
        lateinit var confirm_Button: Button
        lateinit var again_Button: Button

        lateinit var madePhoto: Bitmap

        val CAMERA_REQ_CODE = 11
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_camera, container, false)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            imagePreview_ImageView = view.findViewById(R.id.photoPreview_ImageView)
            confirm_Button = view.findViewById(R.id.confirmPhoto_Button)
            again_Button = view.findViewById(R.id.makePhotoAgain_Button)

            confirm_Button.setOnClickListener {
                saveImage(madePhoto, getTodaysDateAsString())
            }

            again_Button.setOnClickListener {
                makeAnPhoto()
            }

            makeAnPhoto()
        }

        private fun makeAnPhoto() {
            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(photoIntent, CAMERA_REQ_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode != RESULT_OK) {
                return
            }

            when(requestCode) {
                CAMERA_REQ_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imagePreview_ImageView.setImageBitmap(imageBitmap)
                    madePhoto = imageBitmap
                }
            }
        }

        private fun saveImage(image: Bitmap, name: String): String {
            val folder = "photos"
            val directory = File(requireContext().filesDir, folder)

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, name + ".jpg")

            val fileOutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()

            println(file.absolutePath)

            return file.absolutePath
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getTodaysDateAsString(): String {
            val currentDate = LocalDate.now().toString()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            currentDate.format(formatter)

            return currentDate
        }

    }