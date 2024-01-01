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

class MatchingAdapter(private var mList: ArrayList<String>, private var switch: Boolean, private var oldMatching: ArrayList<String>, private var parentName :String, private var parentType :String)  : RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {
    private var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matching, parent, false)

        val add = view.findViewById<CheckBox>(R.id.addBox)
        if (switch){add.visibility = View.VISIBLE}

        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsList = mList[position].split(",").map { it.trim() }
        Log.e("clothlist",clothsList.toString())


        Glide.with(holder.itemView.context)
            .asDrawable()
            .load(clothsList[4])
            .into(holder.prodImage)

        holder.details.text = "${clothsList[0]}\nAdam : ${clothsList[3]} Shahar: ${clothsList[2]}"

        if (switch) {
            if (oldMatching.isNotEmpty()){
            holder.addBox.isChecked = oldMatching.none { it.split(",")[0] != clothsList[0] }}

            holder.addBox.setOnCheckedChangeListener { _, isChecked ->
                uploadMatching(clothsList, isChecked)
            }
        }
    }


    private fun uploadMatching(clothsList : List<String>, isChecked: Boolean) {

        val collectionRef = db.collection(parentType)
        val documentRef = collectionRef.document(parentName)

        var itemToModify = clothsList.toString()
        itemToModify = itemToModify.substring(1, itemToModify.length - 1)
//        Log.e("ClotList",clothsList.toString())

        if (isChecked) {
            Log.e("Firestore", "Add Item")
//             Add the item to the array field in the document
            documentRef.update("matching", com.google.firebase.firestore.FieldValue.arrayUnion(itemToModify))
                .addOnSuccessListener { Log.d("Firestore", "Item successfully added to array!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Error adding item to array", e) }
        } else {
            Log.e("Firestore", "Remove Item")
//             Remove the item from the array field in the document
            documentRef.update("matching", com.google.firebase.firestore.FieldValue.arrayRemove(itemToModify))
                .addOnSuccessListener { Log.d("Firestore", "Item successfully removed from array!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Error removing item from array", e) }
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun sortByALike() {
        val sortedList = ArrayList(mList.sortedByDescending { it.split(',')[3].toInt()})
        updateList(sortedList)
    }
    fun sortBySLike() {
        val sortedList = ArrayList(mList.sortedByDescending { it.split(',')[2].toInt()})
        updateList(sortedList)
    }
    fun sortRandomly() {
        mList = ArrayList(mList.shuffled())
        updateList(mList)
    }
    fun filterCloths(typeCloth: String?) {
        val filteredList = ArrayList(mList.filter {it.split(',')[1] != typeCloth})
        updateList(filteredList)
    }
    private fun updateList(newList: ArrayList<String>) {
        mList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageView = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
        val addBox: CheckBox = this.itemView.findViewById(R.id.addBox)
    }
}