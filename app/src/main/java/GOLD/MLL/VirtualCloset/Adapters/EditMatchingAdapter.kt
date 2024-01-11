package GOLD.MLL.VirtualCloset.Adapters

import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.R
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EditMatchingAdapter(private var fullMatching: List<Cloths>, private var parent: Cloths)  : RecyclerView.Adapter<EditMatchingAdapter.ViewHolder>() {
    private var db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_matrching_adapter, parent, false)
        val holder = ViewHolder(view)

        val add = view.findViewById<CheckBox>(R.id.addBox)
        add.visibility = View.VISIBLE
        val prodImage = view.findViewById<ImageButton>(R.id.prodImage)
        prodImage.setOnClickListener { bigPicture(fullMatching[holder.adapterPosition], view) }

        return holder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothsItem = fullMatching[position]

        Glide.with(holder.itemView.context).asDrawable().load(clothsItem.photoUrl).into(holder.prodImage)

        holder.details.text = "${clothsItem.name}\nView User: ${clothsItem.aLike}\nEdit User: ${clothsItem.sLike}"

        for (i in parent.matching) {
            if (i.split(",")[0] == clothsItem.name) {
                holder.addBox.isChecked = true}
        }

        holder.addBox.setOnCheckedChangeListener { _, isChecked ->
            uploadMatching(clothsItem, isChecked)
        }
    }

    private fun bigPicture(clothsList: Cloths, view: View) {
        val builder = AlertDialog.Builder(view.context)
        val inflater: LayoutInflater = LayoutInflater.from(view.context)
        val dialogLayout = inflater.inflate(R.layout.big_picture, null)

        Log.e("clothsList",clothsList.toString())
        builder.setView(dialogLayout)

        val bigPicture = dialogLayout.findViewById<ImageView>(R.id.big_picture)
        Glide.with(view)
            .asDrawable()
            .load(clothsList.photoUrl)
            .into(bigPicture)
        builder.show()
    }

    private fun uploadMatching(clothsItem: Cloths, isChecked: Boolean) {

        val collectionRef = db.collection(parent.typeCloth)
        val documentRef = collectionRef.document(parent.name)

        val itemToModify = clothsItem.toString()


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
        val sortedList = fullMatching.sortedByDescending { it.aLike}
        updateList(sortedList)
    }
    fun sortBySLike() {
        val sortedList = fullMatching.sortedByDescending { it.sLike}
        updateList(sortedList)
    }
    fun sortRandomly() {
        fullMatching = fullMatching.shuffled()
        updateList(fullMatching)
    }
    fun filterCloths(typeCloth: String?) {
        val filteredList = fullMatching.filter {it.typeCloth != typeCloth}
        updateList(filteredList)
    }
    private fun updateList(newList: List<Cloths>) {
        fullMatching = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodImage: ImageButton = this.itemView.findViewById(R.id.prodImage)
        val details: TextView = this.itemView.findViewById(R.id.details)
        val addBox: CheckBox = this.itemView.findViewById(R.id.addBox)
    }
}