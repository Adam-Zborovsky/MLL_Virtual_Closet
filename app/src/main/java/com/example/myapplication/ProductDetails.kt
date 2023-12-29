package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
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
        val fullList: ArrayList<String> = intent.getStringArrayListExtra("fullList")as ArrayList<String>? ?: arrayListOf()

        val switch = intent.getBooleanExtra("switch", false)

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val like = findViewById<TextView>(R.id.like)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageButton>(R.id.addButton)
        val recyclerview = findViewById<RecyclerView>(R.id.matching)

        val adapter = MatchingAdapter(matching, false, arrayListOf(),"","")
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        add.setOnClickListener {
            if (switch){
                val intent = Intent(this, MatchingDetails::class.java)
                intent.putExtra("matching", matching)
                intent.putExtra("fullList", fullList)
                intent.putExtra("typeCloth", typeCloth)
                intent.putExtra("name", name)
                this.startActivity(intent)
            }
            else{Toast.makeText(this,"Can't Edit In This Mode", Toast.LENGTH_SHORT).show()}
        }

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

    }
}