package com.openclassrooms.p8vitesse

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

// Charge un fichier image dans une ImageView
fun loadImageWithGlide(filePath: String, imageView: ImageView) {


    Glide.with(imageView.context)
        .load(File(filePath))
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true) // Très important : car sinon Glide gère mal les changements d'image qui ne changent pas de nom
        .into(imageView)

}