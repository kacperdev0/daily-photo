package com.example.dailypicture.ImagesClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.dailypicture.R
import java.io.File

class PhotosListAdapter(context: Context, data: Array<File>, val template: Int) : ArrayAdapter<File>(context, 0, data) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false)

        return view
    }
}