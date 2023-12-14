package com.example.myapplication
import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.view.ViewGroup

class ClothsAdapter(private val mList: List<Cloths>) : RecyclerView.Adapter<ClothsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.view_design, parent, false)

        return ViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsList = mList[position]

        holder.imageView1.setImageResource(R.drawable.pants)
        holder.textView1.text = "${clothsList.name}\nAdam : ${clothsList.aLike} Shahar: ${clothsList.sLike}"
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView1: ImageView = this.itemView.findViewById(R.id.image)
        val textView1: TextView = this.itemView.findViewById(R.id.text)
    }
}