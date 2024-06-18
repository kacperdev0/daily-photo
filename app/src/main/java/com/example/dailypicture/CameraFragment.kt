package com.example.dailypicture

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.Manifest
import android.graphics.Matrix
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CameraFragment : Fragment() {
    lateinit var imagePreview_ImageView: ImageView
    lateinit var confirm_Button: Button
    lateinit var again_Button: Button
    lateinit var image_File: File
    lateinit var imageUri: Uri

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
            findNavController().navigate(R.id.browseImagesFragment)
        }

        again_Button.setOnClickListener {
            makeAnPhoto()
        }

        makeAnPhoto()
    }

    private fun makeFileForImage(name: String) {
        val folder = "photos"
        val directory = File(requireContext().filesDir, folder)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        image_File = File(directory, "$name.png")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeAnPhoto() {
        if (checkPermissions()) {
            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            makeFileForImage(getTodaysDateAsString())

            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.dailypicture.fileprovider",
                image_File
            )
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(photoIntent, CAMERA_REQ_CODE)
        }
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

        if (requestCode == CAMERA_REQ_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            val rotatedBitmap = rotateImage(bitmap, -90f)

            saveBitmap(rotatedBitmap)
            imagePreview_ImageView.setImageBitmap(rotatedBitmap)
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val fileOutputStream: FileOutputStream?
        fileOutputStream = FileOutputStream(image_File)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }
    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodaysDateAsString(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy")
        return currentDate.format(formatter)
    }
}
