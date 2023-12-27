package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MatchingClothes(private var mList: ArrayList<String>,  private var switch: Boolean)  : RecyclerView.Adapter<MatchingClothes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_matching, parent, false)
        val holder = ViewHolder(view)
//        view.setOnClickListener{
//
//        }

        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsList = mList[position].split(",")
        Log.e("List", clothsList.toString())

        Glide.with(holder.itemView.context)
            .asDrawable()
            .load(clothsList[4])
            .into(holder.prodImage)

        holder.details.text = "${clothsList[0]}\nAdam : ${clothsList[3]} Shahar: ${clothsList[2]}"
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageView = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
    }
}