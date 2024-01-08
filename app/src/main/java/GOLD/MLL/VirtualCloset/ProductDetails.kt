package GOLD.MLL.VirtualCloset

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import GOLD.MLL.VirtualCloset.Adapters.MatchingAdapter
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProductDetails : AppCompatActivity() {
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)

        val clothsItem = intent.getParcelableArrayExtra("mList")
        Log.e("clothsItem",clothsItem.toString())

        val clothsList = clothsItem?.map { it as Cloths }
        Log.e("clothsList",clothsList.toString())

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val like = findViewById<TextView>(R.id.like)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageButton>(R.id.addButton)
        val recyclerview = findViewById<RecyclerView>(R.id.matching)

        val adapter = clothsList?.let { MatchingAdapter(it) }
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
    }
//
//        add.setOnClickListener {
//            if (switch){
//                val intent = Intent(this, MatchingDetails::class.java)
//                intent.putExtra("mList", clothsItem)
//                this.startActivity(intent)
//            }
//            else{Toast.makeText(this,"Can't Edit In This Mode", Toast.LENGTH_SHORT).show()}
//        }
//        edit.setOnClickListener {
//            if (switch){
//                val builder = AlertDialog.Builder(this)
//                val inflater: LayoutInflater = layoutInflater
//                val oldDocRef = db.collection(clothsList.).document(name.toString())
//                val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
//                val newName = dialogLayout.findViewById<EditText>(R.id.name)
//                val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
//                val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
//                val typeSelector = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)
//                newName.setText(name)
//                adamLike.setText(aLike.toString())
//                shaharLike.setText(sLike.toString())
//                typeSelector.isChecked = typeCloth == "Shirts"
//
//                builder.setView(dialogLayout)
//                builder.setNeutralButton("Delete"){_, _ ->
//                    Log.d("Main", "Delete button clicked")
//                    oldDocRef.delete()
//                        .addOnSuccessListener {Log.d("Firestore","Item successfully Deleted ")}
//                        .addOnFailureListener { e ->Log.w("Firestore","Error Deleteding item", e)}
//
//                    val fileRefByUrl = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl.toString())
//                    fileRefByUrl.delete().addOnSuccessListener {
//                        Log.d("FirebaseStorage", "File deleted successfully")
//                    }.addOnFailureListener {
//                        Log.d("FirebaseStorage", "Error deleting file", it)
//                    }
//                }
//                builder.setPositiveButton("Ok") { _, _ ->
//                    Log.d("Main", "Positive button clicked")
//                    val updates = hashMapOf(
//                        "Name" to newName.text.toString(),
//                        "AdamLikes" to adamLike.text.toString().toInt(),
//                        "ShaharLikes" to shaharLike.text.toString().toInt(),
//                        "URL" to photoUrl,
//                        "matching" to matching
//                    )
//                    val folder = if (typeSelector.isChecked) {"Shirts"} else {"Pants" }
//                    val newDocRef = db.collection(folder).document(newName.text.toString())
//                    oldDocRef.delete()
//                        .addOnSuccessListener {Log.d("Firestore","Item successfully Deleted ")}
//                        .addOnFailureListener { e ->Log.w("Firestore","Error Deleteding item", e)}
//                    newDocRef.set(updates)
//                        .addOnFailureListener {Log.e("Upload To Database","Error writing document")}
//                        .addOnSuccessListener {Log.d("Firestore","Item successfully added to array!")}
//                        .addOnFailureListener { e ->Log.w("Firestore","Error adding item to array", e)}
//                }
//                builder.setNegativeButton("Cancel") { _, _ ->
//                    Log.d("Main", "Negative button clicked")}
//                builder.show()
//            }
//            else{Toast.makeText(this,"Can't Edit In This Mode", Toast.LENGTH_SHORT).show()}
//        }
//
//        val backgroundResId = if (switch) {
//                R.drawable.background_shahar
//            } else {
//                R.drawable.background_adam}
//        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)
//
//        val btnReturnHome = findViewById<ImageButton>(R.id.btnReturnHome)
//        btnReturnHome.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        Glide.with(this)
//            .asDrawable()
//            .load(photoUrl)
//            .centerCrop()
//            .into(prod)
//        prodName.text = name
//        like.text = "View User: $aLike          Edit User: $sLike"
//
//    }
}