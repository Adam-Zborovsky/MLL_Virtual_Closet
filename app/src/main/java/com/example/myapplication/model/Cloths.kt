package com.example.myapplication.model

data class Cloths(val name: String,
                  val aLikes: Int,
                  val sLikes: Int,
                  val sourceUrl: String,
                  val compatible: ArrayList<String>
)
