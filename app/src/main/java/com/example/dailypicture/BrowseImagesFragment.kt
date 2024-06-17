package com.example.dailypicture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BrowseImagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrowseImagesFragment : Fragment() {

    lateinit var photosListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosListView = view.findViewById(R.id.photos_ListView)
        getPhotosUris()

    }

    private fun getPhotosUris(): Array<out File>? {
        val folder = File(requireContext().filesDir, "photos")

        for (file in folder.listFiles()) {
            println(file.name)
        }

        return folder.listFiles()
    }

}