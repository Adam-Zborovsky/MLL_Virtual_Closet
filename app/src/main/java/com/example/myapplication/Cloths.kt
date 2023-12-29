package com.example.myapplication

data class Cloths(
    val name: String,
    val typeCloth: String,
    val sLike: Int,
    val aLike: Int,
    val photoUrl: String,
    val matching: ArrayList<String> = arrayListOf<String>()
    ) {
    override fun toString(): String {
        return "$name,$typeCloth,$sLike,$aLike,$photoUrl,[${matching.toString()}]"
    }
}