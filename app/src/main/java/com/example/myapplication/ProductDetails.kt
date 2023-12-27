package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)

        val name = intent.getStringExtra("name")
        val typeCloth = intent.getStringExtra("typeCloth")
        val sLike = intent.getIntExtra("sLike", 0)
        val aLike = intent.getIntExtra("aLike", 0)
        val photoUrl = intent.getStringExtra("photoUrl")
        val matching: ArrayList<String> = intent.getStringArrayListExtra("matching")as ArrayList<String>? ?: arrayListOf()
        val switch = intent.getBooleanExtra("switch", false)

        val containerRL = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val like = findViewById<TextView>(R.id.like)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageView>(R.id.addButton)
        val recyclerview = findViewById<RecyclerView>(R.id.matching)

        val adapter = MatchingClothes(matching,switch)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        val backgroundResId = if (switch) {
                R.drawable.background_shahar
            } else {
                R.drawable.background_adam}
        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)

        Glide.with(this)
            .asDrawable()
            .load(photoUrl)
            .centerCrop()
            .into(prod)
        prodName.text = name
        like.text = "Adam: $aLike          Shahar: $sLike"

        add.setOnClickListener {
        }
    }
}