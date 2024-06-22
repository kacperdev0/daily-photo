package com.example.dailypicture

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    lateinit var confirm_Button: Button
    lateinit var again_Button: Button
    lateinit var cameraPreview_PreviewView: PreviewView

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    lateinit var imgCapture: ImageCapture

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
            makeAnPhoto()
        }

        again_Button.setOnClickListener {
            // Reset or restart camera preview if needed
        }

        if (checkPermissions()) {
            startCameraPreview()
        }
    }

    private fun startCameraPreview() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFeature.addListener({
            val cp = cameraProviderFeature.get()
            val sideOfCamera = CameraSelector.DEFAULT_FRONT_CAMERA

            val preview = Preview.Builder().build().also { it.setSurfaceProvider(cameraPreview_PreviewView.surfaceProvider) }

            imgCapture = ImageCapture.Builder()
                .setTargetResolution(Size(720, 1280)) // Example resolution
                .build()

            cp.unbindAll()
            cp.bindToLifecycle(this, sideOfCamera, preview, imgCapture)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun makeFileForImage(name: String): File {
        val folder = "photos"
        val directory = File(requireContext().filesDir, folder)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val image_File = File(directory, "$name.png")

        return image_File
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeAnPhoto() {
        val imageCapture = imgCapture ?: return
        val imageFile = makeFileForImage(getTodaysDateAsString())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    cameraExecutor.execute {
                        var bitmap: Bitmap? = BitmapFactory.decodeFile(imageFile.absolutePath)
                        bitmap = rotateBitmap(bitmap!!)
                        val outputStream = FileOutputStream(imageFile)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

                        requireActivity().runOnUiThread {
                            findNavController().navigate(R.id.browseImagesFragment)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Image capture failed: ${exception.message}", exception)
                }
            }
        )
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodaysDateAsString(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(-90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
