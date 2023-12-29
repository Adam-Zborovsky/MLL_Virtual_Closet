package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class MatchingClothes(private var mList: ArrayList<String>,  private var switch: Boolean, private var nMatching: ArrayList<String>)  : RecyclerView.Adapter<MatchingClothes.ViewHolder>() {
    private var storageRef = Firebase.storage.reference
    private var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matching, parent, false)

        val add = view.findViewById<CheckBox>(R.id.addBox)
        if (switch){add.visibility = View.VISIBLE}

        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsList = mList[position].split(",")
        Log.e("clothsList", clothsList.toString())

        if (clothsList[0] != "[]") {

            for (i in nMatching) {
                holder.addBox.isChecked = i.split(',')[0] == clothsList[0]
            }
            Glide.with(holder.itemView.context)
                .asDrawable()
                .load(clothsList[4])
                .into(holder.prodImage)

            holder.details.text =
                "${clothsList[0]}\nAdam : ${clothsList[3]} Shahar: ${clothsList[2]}"
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun sortByALike() {
        val sortedList = ArrayList(mList.sortedByDescending { it.toList()[3]})
        Log.e("SortedList" , sortedList.toString())
        updateList(sortedList)
    }
    fun sortBySLike() {
        val sortedList = ArrayList(mList.sortedByDescending { it.toList()[2]})
        updateList(sortedList)
    }
    fun sortRandomly() {
        mList = ArrayList(mList.shuffled())
        updateList(mList)
    }
    fun filterCloths(typeCloth: String?) {
        val filteredList = ArrayList(mList.filter {it.split(',')[1] != typeCloth})
        nMatching = filteredList
        updateList(filteredList)
    }
    private fun updateList(newList: ArrayList<String>) {
        mList = newList
        Log.e("Here","Got Here")
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageView = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
        val addBox: CheckBox = this.itemView.findViewById(R.id.addBox)
    }
}