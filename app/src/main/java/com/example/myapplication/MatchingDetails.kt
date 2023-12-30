package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MatchingDetails : AppCompatActivity(){
    private var typeCloth: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.matching_details)

        val fullList: ArrayList<String> = intent.getStringArrayListExtra("fullList")as ArrayList<String>? ?: arrayListOf()
        val matching: ArrayList<String> = intent.getStringArrayListExtra("matching")as ArrayList<String>? ?: arrayListOf()
        val typeCloth = intent.getStringExtra("typeCloth")
        val name= intent.getStringExtra("name")

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val recyclerview = findViewById<RecyclerView>(R.id.matching_edit)
        val openNav = findViewById<ImageButton>(R.id.openNav)
        val naviview = findViewById<NavigationView>(R.id.naviView)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_shahar, null)
        naviview.background = containerRL.background

        val adapter = MatchingAdapter(fullList,true, matching, name!!, typeCloth!!)
        Log.e("fullList",fullList.toString())
        Log.e("matching",matching.toString())
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        adapter.filterCloths(typeCloth)

        openNav.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            var filter = naviview.menu.findItem(R.id.shirtIco)
            if (typeCloth != "Shirt"){filter = naviview.menu.findItem(R.id.shirtIco)}
            if (typeCloth != "Pants"){filter = naviview.menu.findItem(R.id.pantsIco)}
            filter.isChecked = false
        }

        naviview.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.likeA, R.id.likeS, R.id.rnd -> {
                    naviview.menu.findItem(R.id.likeA).isChecked = false
                    naviview.menu.findItem(R.id.likeS).isChecked = false
                    naviview.menu.findItem(R.id.rnd).isChecked = false
                    menuItem.isChecked = true

                    when (menuItem.itemId) {
                        R.id.likeA -> adapter.sortByALike()
                        R.id.likeS -> adapter.sortBySLike()
                        R.id.rnd -> adapter.sortRandomly()
                    }
                    containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_shahar, null)
                }
            }
            false
        }

    }
}