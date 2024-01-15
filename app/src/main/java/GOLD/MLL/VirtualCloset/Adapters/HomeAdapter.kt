    package GOLD.MLL.VirtualCloset.Adapters

    import GOLD.MLL.VirtualCloset.R
    import android.content.Intent
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import GOLD.MLL.VirtualCloset.Cloths
    import GOLD.MLL.VirtualCloset.ProductDetails.ProductDetails

    class HomeAdapter(private var mList: List<Cloths>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
        private var fullList: List<Cloths> = mList
        private var switch: Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cloth_adapter, parent, false)
            val holder = ViewHolder(view)
            view.setOnClickListener{
                val intent = Intent(parent.context, ProductDetails::class.java)
                intent.putExtra("fullList", ArrayList(mList))
                intent.putExtra("clothsItem", mList[holder.adapterPosition])
                intent.putExtra("switch", switch)
                parent.context.startActivity(intent)
            }

            return holder
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val clothsList = mList[position]
            Glide.with(holder.itemView.context).asDrawable().load(clothsList.photoUrl).centerCrop().into(holder.image)

            holder.name.text = clothsList.name
            holder.likes.text = "View User: ${clothsList.aLike}   Edit User: ${clothsList.sLike}"
        }

        override fun getItemCount(): Int {
            return mList.size
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
                Log.e("showPants", (showPants && it.typeCloth == "Pants").toString())
                Log.e("showShirts", (showShirts && it.typeCloth == "Shirts").toString())
                (showShirts && it.typeCloth == "Shirts") || (showPants && it.typeCloth == "Pants")
            }
            updateList(filteredList)
        }
        fun sortRandomly() {
            mList = mList.shuffled()
            updateList(mList)
        }
        fun updateList(newList: List<Cloths>) {
            mList = newList
            notifyDataSetChanged()
        }
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = this.itemView.findViewById(R.id.image)
            val name: TextView = this.itemView.findViewById(R.id.name)
            val likes: TextView = this.itemView.findViewById(R.id.likes)
        }
    }