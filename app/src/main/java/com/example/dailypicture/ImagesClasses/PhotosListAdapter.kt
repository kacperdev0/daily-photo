package com.example.dailypicture.ImagesClasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.dailypicture.R
import java.io.File

class PhotosListAdapter(context: Context, val data: MutableList<File>, val template: Int) : ArrayAdapter<File>(context, template, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.single_item, parent, false)
        val file = getItem(position) ?: return view

        val photo_ImageView = view.findViewById<ImageView>(R.id.photo_ImageView)
        val date_TextView = view.findViewById<TextView>(R.id.date_TextView)
        val delete_ImageButton = view.findViewById<ImageButton>(R.id.delete_ImageButton)

        // Decode bitmap in a background thread to avoid blocking the UI thread
        val bitmap: Bitmap? = decodeSampledBitmapFromFile(file.absolutePath, 100, 100)

        bitmap?.let {
            photo_ImageView.setImageBitmap(it)
        }

        date_TextView.text = file.name.substringBeforeLast(".")

        delete_ImageButton.setOnClickListener {
            removeItem(position, file)
        }

        return view
    }

    private fun removeItem(position: Int, file: File) {
        data.removeAt(position)
        file.delete()
        notifyDataSetChanged()
    }

    private fun decodeSampledBitmapFromFile(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
