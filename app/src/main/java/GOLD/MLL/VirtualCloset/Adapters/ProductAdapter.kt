package GOLD.MLL.VirtualCloset.Adapters

import GOLD.MLL.VirtualCloset.R
import android.app.AlertDialog
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

class ProductAdapter(private var mList: List<String>)  : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matching_adapter, parent, false)
        val holder = ViewHolder(view)

        val prodImage = view.findViewById<ImageButton>(R.id.prodImage)
        prodImage.setOnClickListener { bigPicture(mList[holder.adapterPosition], view) }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cloth = mList[position].split(",")


        Glide.with(holder.itemView.context)
            .asDrawable()
            .load(cloth[4])
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(holder.prodImage)

        holder.details.text = "${cloth[0]}\nView User: ${cloth[2]}\nEdit User: ${cloth[3]}"

    }
    private fun bigPicture(cloth: String, view: View) {
        val builder = AlertDialog.Builder(view.context)
        val inflater: LayoutInflater = LayoutInflater.from(view.context)
        val dialogLayout = inflater.inflate(R.layout.big_picture, null)

        val url = cloth.split(",")[4]
        builder.setView(dialogLayout)

        val bigPicture = dialogLayout.findViewById<ImageView>(R.id.big_picture)
        Glide.with(view)
            .asDrawable()
            .load(url)
            .centerInside()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(bigPicture)
        builder.show()
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    fun updateList(newList: List<String>) {
        mList = newList
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageButton = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
    }
}