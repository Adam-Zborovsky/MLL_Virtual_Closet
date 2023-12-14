package com.example.myapplication

data class Cloths(
    val name: String,
    val sLike: Int,
    val aLike: Int,
    val photoUrl: String,
    val matching: ArrayList<String> = arrayListOf<String>()
    )
fun findUrlByName(clothsList: List<Cloths>, nameToFind: String): String? {
    val cloth = clothsList.find { it.name == nameToFind }
    return cloth?.photoUrl
}

