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
import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.R
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class ProductDetails : AppCompatActivity() {
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)

        val fullList = intent.getSerializableExtra("fullList") as List<Cloths>
        val clothsItem = intent.getSerializableExtra("clothsItem") as Cloths
        val switch = intent.getBooleanExtra("switch", true)


        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val prodBack = findViewById<ImageButton>(R.id.prodBack)
        val like = findViewById<TextView>(R.id.like)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageButton>(R.id.addButton)
        val recyclerview = findViewById<RecyclerView>(R.id.matching)

        val adapter = MatchingAdapter(clothsItem.matching)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        add.setOnClickListener {
            if (switch) {
                val intent = Intent(this, MatchingDetails::class.java)
                intent.putExtra("fullList", ArrayList(fullList))
                intent.putExtra("clothsItem", clothsItem)
                this.startActivity(intent)
            } else {
                Toast.makeText(this, "Can't Edit In This Mode", Toast.LENGTH_SHORT).show()
            }
        }
        edit.setOnClickListener {
            if (switch) {
                val builder = AlertDialog.Builder(this)
                val inflater: LayoutInflater = layoutInflater
                val oldDocRef = db.collection(clothsItem.typeCloth).document(clothsItem.name)
                val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
                val newName = dialogLayout.findViewById<EditText>(R.id.name)
                val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
                val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
                val typeSelector = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)
                newName.setText(clothsItem.name)
                adamLike.setText(clothsItem.aLike.toString())
                shaharLike.setText(clothsItem.sLike.toString())
                typeSelector.isChecked = clothsItem.typeCloth == "Shirts"

                builder.setView(dialogLayout)
                builder.setNeutralButton("Delete") { _, _ ->
                    Log.d("Main", "Delete button clicked")
                    oldDocRef.delete()
                        .addOnSuccessListener { Log.d("Firestore", "Item successfully Deleted ") }
                        .addOnFailureListener { e ->
                            Log.w(
                                "Firestore",
                                "Error Deleteding item",
                                e
                            )
                        }

                    val fileRefByUrl =
                        FirebaseStorage.getInstance().getReferenceFromUrl(clothsItem.photoUrl)
                    fileRefByUrl.delete().addOnSuccessListener {
                        Log.d("FirebaseStorage", "File deleted successfully")
                    }.addOnFailureListener {
                        Log.d("FirebaseStorage", "Error deleting file", it)
                    }
                }
                builder.setPositiveButton("Ok") { _, _ ->
                    Log.d("Main", "Positive button clicked")
                    val updates = hashMapOf(
                        "Name" to newName.text.toString(),
                        "AdamLikes" to adamLike.text.toString().toInt(),
                        "ShaharLikes" to shaharLike.text.toString().toInt(),
                        "URL" to clothsItem.photoUrl,
                        "BackSide" to clothsItem.backsideUrl,
                        "matching" to clothsItem.matching
                    )
                    val folder = if (typeSelector.isChecked) {
                        "Shirts"
                    } else {
                        "Pants"
                    }
                    val newDocRef = db.collection(folder).document(newName.text.toString())
                    oldDocRef.delete()
                    newDocRef.set(updates)
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                    Log.d("Main", "Negative button clicked")
                }
                builder.show()
            } else {
                Toast.makeText(this, "Can't Edit In This Mode", Toast.LENGTH_SHORT).show()
            }
        }

        val backgroundResId = if (switch) {
            R.drawable.background_shahar
        } else {
            R.drawable.background_adam
        }
        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)

        val btnReturnHome = findViewById<ImageButton>(R.id.btnReturnHome)
        btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (clothsItem.backsideUrl != ""){
            val backsideBackground = findViewById<View>(R.id.backsideBackground)
            backsideBackground.visibility = View.VISIBLE
        }

        var currentProdUrl = clothsItem.photoUrl
        var currentProdBackUrl = clothsItem.backsideUrl

        Glide.with(this).asDrawable().load(currentProdUrl).centerCrop().into(prod)
        prodName.text = clothsItem.name
        like.text = "View User: ${clothsItem.aLike}          Edit User: ${clothsItem.sLike}"

        if (currentProdBackUrl != "") {
            Glide.with(this).asDrawable().load(currentProdBackUrl).centerCrop().into(prodBack)


            prodBack.setOnClickListener {
                val tempUrl = currentProdUrl
                currentProdUrl = currentProdBackUrl
                currentProdBackUrl = tempUrl
                Glide.with(this).asDrawable().load(currentProdUrl).centerCrop().into(prod)
                Glide.with(this).asDrawable().load(currentProdBackUrl)
                    .centerCrop()
                    .into(prodBack)

            }
        }
    }
}