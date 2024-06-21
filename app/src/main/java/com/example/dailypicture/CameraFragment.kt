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
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class CameraFragment : Fragment() {
    lateinit var confirm_Button: Button
    lateinit var again_Button: Button
    lateinit var cameraPreview_PreviewView: PreviewView

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    lateinit var imgCapture: ImageCapture

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

        cameraPreview_PreviewView = view.findViewById(R.id.photoPreview_PreviewView)
        confirm_Button = view.findViewById(R.id.confirmPhoto_Button)
        again_Button = view.findViewById(R.id.makePhotoAgain_Button)

        confirm_Button.setOnClickListener {
            findNavController().navigate(R.id.browseImagesFragment)
        }

        again_Button.setOnClickListener {

        }

        startCameraPreview()
    }

    private fun startCameraPreview() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFeature.addListener({
            val cp = cameraProviderFeature.get()
            val sideOfCamera = CameraSelector.DEFAULT_FRONT_CAMERA

            val preview = Preview.Builder().build().also { it.setSurfaceProvider(cameraPreview_PreviewView.surfaceProvider) }

            imgCapture = ImageCapture.Builder().build()

            cp.unbindAll()
            cp.bindToLifecycle(this, sideOfCamera, preview)

        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun makeFileForImage(name: String) {
        val folder = "photos"
        val directory = File(requireContext().filesDir, folder)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        image_File = File(directory, "$name.png")
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.dailypicture.fileprovider",
            image_File
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeAnPhoto() {
        if (checkPermissions()) {
            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            makeFileForImage(getTodaysDateAsString())

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQ_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            val rotatedBitmap = rotateImage(bitmap)

            saveBitmap(rotatedBitmap)
            //imagePreview_ImageView.setImageBitmap(rotatedBitmap)
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val fileOutputStream: FileOutputStream?
        fileOutputStream = FileOutputStream(image_File)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    private fun rotateImage(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(-90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodaysDateAsString(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = currentDate.format(formatter)
        return date.toString()
    }
}
