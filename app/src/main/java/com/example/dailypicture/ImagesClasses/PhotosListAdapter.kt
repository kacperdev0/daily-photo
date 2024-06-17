package com.example.dailypicture.ImagesClasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.dailypicture.R
import java.io.File

class PhotosListAdapter(context: Context, val data: Array<File>, val template: Int) : ArrayAdapter<File>(context, 0, data) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false)
        val file = data[position]

        val photo_ImageView = view.findViewById<ImageView>(R.id.photo_ImageView)
        val date_TextView = view.findViewById<TextView>(R.id.date_TextView)

        println(file.name + " " + file.absolutePath)

        val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)

        photo_ImageView.setImageBitmap(bitmap)
        date_TextView.text = file.name

        return view
    }
}