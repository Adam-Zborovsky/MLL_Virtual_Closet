package GOLD.MLL.VirtualCloset.Adapters

import GOLD.MLL.VirtualCloset.R
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import GOLD.MLL.VirtualCloset.Cloths
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MatchingAdapter(private var mList: List<Cloths>)  : RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {
    private var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matrching_adapter, parent, false)
        val holder = ViewHolder(view)

        val prodImage = view.findViewById<ImageButton>(R.id.prodImage)
//        prodImage.setOnClickListener { bigPicture(mList[holder.adapterPosition], view) }

        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val clothsList = mList[position].split(",").map { it.trim() }

//
//        Glide.with(holder.itemView.context)
//            .asDrawable()
//            .load(clothsList[4])
//            .into(holder.prodImage)
//
//        holder.details.text = "${clothsList[0]}\nView User: ${clothsList[3]}\nEdit User: ${clothsList[2]}"
    }
    private fun bigPicture(clothsList: String, view: View) {
        val builder = AlertDialog.Builder(view.context)
        val inflater: LayoutInflater = LayoutInflater.from(view.context)
        val dialogLayout = inflater.inflate(R.layout.big_picture, null)
        val clothsList = clothsList.split(",").map { it.trim() }

        Log.e("clothsList", clothsList.toString())
        builder.setView(dialogLayout)

        val bigPicture = dialogLayout.findViewById<ImageView>(R.id.big_picture)
        Glide.with(view)
            .asDrawable()
            .load(clothsList[4])
            .into(bigPicture)
        builder.show()
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun sortByALike() {
        val sortedList = mList.sortedByDescending { it.aLike}
        updateList(sortedList)
    }
    fun sortBySLike() {
        val sortedList = mList.sortedByDescending { it.sLike}
        updateList(sortedList)
    }
    fun sortRandomly() {
        mList = mList.shuffled()
        updateList(mList)
    }
    fun filterCloths(typeCloth: String?) {
        val filteredList = mList.filter {it.typeCloth != typeCloth}
        updateList(filteredList)
    }
    private fun updateList(newList: List<Cloths>) {
        mList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageButton = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
    }
}