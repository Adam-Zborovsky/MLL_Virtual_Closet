package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide

class ProductDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)

        val name = intent.getStringExtra("name")
//        val typeCloth = intent.getStringExtra("typeCloth")
        val sLike = intent.getIntExtra("sLike", 0)
        val aLike = intent.getIntExtra("aLike", 0)
        val photoUrl = intent.getStringExtra("photoUrl")
//        val matching = intent.getStringExtra("matching")

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val like = findViewById<TextView>(R.id.like)

        constraintLayout.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)

        Glide.with(this)
            .asDrawable()
            .load(photoUrl)
            .centerCrop()
            .into(prod)

        prodName.text = name
        like.text = "Adam: $aLike          Shahar: $sLike"
    }
}