package com.example.dailypicture

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_image, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val makePhoto_Button = view.findViewById<Button>(R.id.addImage_Button)

        if (checkIfTodaysImageExists()) {
            findNavController().navigate(R.id.browseImagesFragment)
        }


        makePhoto_Button.setOnClickListener {
            if (checkPermissions()) {
                findNavController().navigate(R.id.cameraFragment)
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkIfTodaysImageExists(): Boolean {
        val directory = File(requireContext().filesDir, "photos")

        val fileName = getTodaysFileName()
        for (file in directory.listFiles()) {
            println(file.name + "  " + fileName)

            if (file.name.toString() == fileName) {
                return true
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodaysFileName(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = currentDate.format(formatter)
        return date.toString() + ".png"
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}