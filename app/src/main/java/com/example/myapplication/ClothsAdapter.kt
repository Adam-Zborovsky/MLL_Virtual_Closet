    package com.example.myapplication
    import android.content.Intent
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide

    class ClothsAdapter(private var mList: List<Cloths>) : RecyclerView.Adapter<ClothsAdapter.ViewHolder>() {
        private var fullList: List<Cloths> = mList
        private var switch: Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_design, parent, false)
            val holder = ViewHolder(view)
            view.setOnClickListener{
                val intent = Intent(parent.context, ProductDetails::class.java)
                intent.putExtra("name", mList[holder.adapterPosition].name)
                intent.putExtra("typeCloth", mList[holder.adapterPosition].typeCloth)
                intent.putExtra("sLike", mList[holder.adapterPosition].sLike)
                intent.putExtra("aLike", mList[holder.adapterPosition].aLike)
                intent.putExtra("photoUrl", mList[holder.adapterPosition].photoUrl)
                intent.putExtra("matching", mList[holder.adapterPosition].matching)
                intent.putExtra("switch", switch)
                parent.context.startActivity(intent)
            }

            return holder
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val clothsList = mList[position]
            Glide.with(holder.itemView.context)
                .asDrawable()
                .load(clothsList.photoUrl)
                .centerCrop()
                .into(holder.imageView1)

            holder.textView1.text = "${clothsList.name}\nAdam : ${clothsList.aLike} Shahar: ${clothsList.sLike}"
        }
        fun switchFlip(){
            switch = !switch
        }
        fun sortByALike() {
            val sortedList = mList.sortedByDescending { it.aLike }
            updateList(sortedList)
        }
        fun sortBySLike() {
            val sortedList = mList.sortedByDescending { it.sLike }
            updateList(sortedList)
        }
        fun filterCloths(showShirts: Boolean, showPants: Boolean) {
            val filteredList = fullList.filter {
                (showShirts && it.typeCloth == "Shirts") || (showPants && it.typeCloth == "Pants")
            }
            Log.e("List", arrayListOf<Any>(showPants,showShirts,filteredList.toString()).toString())
            updateList(filteredList)
        }
        fun sortRandomly() {
            mList = mList.shuffled()
            updateList(mList)
        }
        private fun updateList(newList: List<Cloths>) {
            mList = newList
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView1: ImageView = this.itemView.findViewById(R.id.image)
            val textView1: TextView = this.itemView.findViewById(R.id.text)
        }
    }