package com.example.dailypicture

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CameraFragment : Fragment() {
        lateinit var imagePreview_ImageView: ImageView

        val CAMERA_REQ_CODE = 11
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_camera, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            imagePreview_ImageView = view.findViewById<ImageView>(R.id.photoPreview_ImageView)

            makeAnPhoto()
        }

        private fun makeAnPhoto() {
            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(photoIntent, CAMERA_REQ_CODE)
        }

        private fun checkPermissions(): Boolean {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    0
                )
                return false
            }
            return true
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
                }
            }
        }
    }