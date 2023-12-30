package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProductDetails : AppCompatActivity() {
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)

        val name = intent.getStringExtra("name")
        val typeCloth = intent.getStringExtra("typeCloth")
        val sLike = intent.getIntExtra("sLike", 0)
        val aLike = intent.getIntExtra("aLike", 0)
        val photoUrl = intent.getStringExtra("photoUrl")
        val matching: ArrayList<String> = intent.getStringArrayListExtra("matching")as ArrayList<String>? ?: arrayListOf()
        val fullList: ArrayList<String> = intent.getStringArrayListExtra("fullList")as ArrayList<String>? ?: arrayListOf()

        val switch = intent.getBooleanExtra("switch", false)

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val like = findViewById<TextView>(R.id.like)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageButton>(R.id.addButton)
        val recyclerview = findViewById<RecyclerView>(R.id.matching)

        val adapter = MatchingAdapter(matching, false, arrayListOf(),"","")
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        add.setOnClickListener {
            if (switch){
                val intent = Intent(this, MatchingDetails::class.java)
                intent.putExtra("matching", matching)
                intent.putExtra("fullList", fullList)
                intent.putExtra("typeCloth", typeCloth)
                intent.putExtra("name", name)
                this.startActivity(intent)
            }
            else{Toast.makeText(this,"Can't Edit In This Mode", Toast.LENGTH_SHORT).show()}
        }
        edit.setOnClickListener {
            if (switch){

                val builder = AlertDialog.Builder(this)
                val inflater: LayoutInflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
                val newName = dialogLayout.findViewById<EditText>(R.id.name)
                val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
                val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
                val typeSelector = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)

                builder.setView(dialogLayout)
                builder.setPositiveButton("Ok") { _, _ ->
                    Log.d("Main", "Positive button clicked")
                    if (fullList.none { it.split(",")[0] == newName.text.toString()}) {
                        Log.e("name" ,newName.text.toString())
                        val updates = hashMapOf(
                            "URL" to photoUrl,
                            "matching" to matching
                        )
                        if(newName.text.toString().isNotEmpty()) {updates["Name"] = newName.text.toString()}
                        if(adamLike.text.toString().isNotEmpty()) {updates["AdamLike"] = adamLike.text.toString().toInt()}
                        if(shaharLike.text.toString().isNotEmpty()) {updates["ShaharLike"] = shaharLike.text.toString().toInt()}
                        val folder = if (typeSelector.isChecked) {"Shirts"} else {"Pants" }
                        val oldDocRef = db.collection(typeCloth.toString()).document(name.toString())
                        val newDocRef = db.collection(folder).document(newName.text.toString())
                        oldDocRef.delete()
                            .addOnSuccessListener {Log.d("Firestore","Item successfully Deleted ")}
                            .addOnFailureListener { e ->Log.w("Firestore","Error Deleteding item", e)}
                        newDocRef.set(updates)
                            .addOnFailureListener {Log.e("Upload To Database","Error writing document")}
                            .addOnSuccessListener {Log.d("Firestore","Item successfully added to array!")}
                            .addOnFailureListener { e ->Log.w("Firestore","Error adding item to array", e)}
                    }
                    else{Toast.makeText(this,"Item With That Name Already Exists", Toast.LENGTH_SHORT).show()}
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                    Log.d("Main", "Negative button clicked")}
                builder.show()
            }
            else{Toast.makeText(this,"Can't Edit In This Mode", Toast.LENGTH_SHORT).show()}
        }

        val backgroundResId = if (switch) {
                R.drawable.background_shahar
            } else {
                R.drawable.background_adam}
        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)

        Glide.with(this)
            .asDrawable()
            .load(photoUrl)
            .centerCrop()
            .into(prod)
        prodName.text = name
        like.text = "Adam: $aLike          Shahar: $sLike"

    }
}