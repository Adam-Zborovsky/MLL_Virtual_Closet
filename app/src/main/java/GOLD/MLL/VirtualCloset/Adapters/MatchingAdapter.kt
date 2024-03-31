package GOLD.MLL.VirtualCloset.Adapters

import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@SuppressLint("NotifyDataSetChanged")
class MatchingAdapter(private var fullMatching: List<Cloths>, private var parent: Cloths)  : RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {
    private var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_matching_adapter, parent, false)
        val holder = ViewHolder(view)

        val prodImage = view.findViewById<ImageButton>(R.id.prodImage)
        prodImage.setOnClickListener { bigPicture(fullMatching[holder.adapterPosition], view) }

        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsItem = fullMatching[position]

        Glide.with(holder.itemView.context)
            .asDrawable()
            .load(clothsItem.photoUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(holder.prodImage)

        holder.details.text = "${clothsItem.name}\nAdam: ${clothsItem.aLike}\nShahar: ${clothsItem.sLike}"

        for (i in parent.matching) {
            if (i.split(",")[0] == clothsItem.name) {
                holder.addBox.isChecked = true}
        }

        holder.addBox.setOnCheckedChangeListener { _, isChecked ->
            uploadMatching(clothsItem, isChecked)
        }
    }

    private fun bigPicture(cloth: Cloths, view: View) {
        val builder = AlertDialog.Builder(view.context)
        val inflater: LayoutInflater = LayoutInflater.from(view.context)
        val dialogLayout = inflater.inflate(R.layout.big_picture, null)

        builder.setView(dialogLayout)

        val bigPicture = dialogLayout.findViewById<ImageView>(R.id.big_picture)
        Glide.with(view)
            .asDrawable()
            .load(cloth.photoUrl)
            .centerInside()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(bigPicture)
        builder.show()
    }

    private fun uploadMatching(clothsItem: Cloths, isChecked: Boolean) {

        val collectionRef = db.collection(parent.typeCloth)
        val documentRef = collectionRef.document(parent.name)


        val itemToModify = clothsItem.toString()

        Log.e("itemToModify", itemToModify)


        if (isChecked) {
            parent.matching.add(itemToModify)
            Log.e("Firestore", "Add Item")
//             Add the item to the array field in the document
            documentRef.update("matching", com.google.firebase.firestore.FieldValue.arrayUnion(itemToModify))
                .addOnSuccessListener { Log.d("Firestore", "Item successfully added to array!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Error adding item to array", e) }
        } else {
            parent.matching.remove(itemToModify)
            Log.e("Firestore", "Remove Item")
//             Remove the item from the array field in the document
            documentRef.update("matching", com.google.firebase.firestore.FieldValue.arrayRemove(itemToModify))
                .addOnSuccessListener { Log.d("Firestore", "Item successfully removed from array!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Error removing item from array", e) }
        }
    }
    override fun getItemCount(): Int {
        return fullMatching.size
    }
    fun sortByALike() {
        val sortedList = fullMatching.sortedByDescending { it.aLike }
        fullMatching = sortedList
        notifyDataSetChanged()
    }
    fun sortBySLike() {
        val sortedList = fullMatching.sortedByDescending { it.sLike }
        fullMatching = sortedList
        notifyDataSetChanged()
    }
    fun filterCloths(typeCloth: String) {
        val filteredList = fullMatching.filter {it.typeCloth != typeCloth}
        fullMatching = filteredList
        notifyDataSetChanged()
    }
    fun sortRandomly() {
        fullMatching = fullMatching.shuffled()
        notifyDataSetChanged()
    }
    fun updateList(newList: List<Cloths>, selectedItem: Cloths) {
        parent = selectedItem
        fullMatching = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageButton = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
        val addBox: CheckBox = this.itemView.findViewById(R.id.addBox)
    }
}