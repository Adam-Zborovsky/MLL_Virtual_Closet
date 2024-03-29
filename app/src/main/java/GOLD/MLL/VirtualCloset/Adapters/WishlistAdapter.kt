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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class WishlistAdapter(private var mList: MutableMap<String, String>)  : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    private val entriesList = mList.entries.toMutableList()
    var removeItemListener: OnRemoveItemListener? = null

    interface OnRemoveItemListener {
        fun removeItem(key: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wishlist_adapter, parent, false)
        val holder = ViewHolder(view)

        val prodImage = view.findViewById<ImageButton>(R.id.prodImage)
        prodImage.setOnClickListener {
            val url = entriesList[holder.adapterPosition].value
            bigPicture(url, view)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (entriesList.isNotEmpty()) {


            val url = entriesList[position].value

            Glide.with(holder.itemView.context)
                .asDrawable()
                .load(url)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(holder.prodImage)

            holder.details.text = entriesList[position].key
            holder.remove.setOnClickListener{
                removeItemListener?.removeItem(entriesList[position].key)
            }
        }
        else{
            holder.prodImage.visibility = View.GONE
            holder.details.visibility = View.GONE
            holder.remove.visibility = View.GONE
        }
    }
    private fun bigPicture(url: String, view: View) {
        val builder = AlertDialog.Builder(view.context)
        val inflater: LayoutInflater = LayoutInflater.from(view.context)
        val dialogLayout = inflater.inflate(R.layout.big_picture, null)

        builder.setView(dialogLayout)
        val bigPicture = dialogLayout.findViewById<ImageView>(R.id.big_picture)
        Glide.with(view).asDrawable().load(url).centerInside().into(bigPicture)
        builder.show()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageView = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
        val remove: ImageButton = this.itemView.findViewById(R.id.remove)
    }
}
