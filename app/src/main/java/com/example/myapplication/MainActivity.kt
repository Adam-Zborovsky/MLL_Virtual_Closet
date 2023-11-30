package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


class MainActivity : ComponentActivity() {
    private var storageRef = Firebase.storage.reference
    private var db = Firebase.firestore
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        val switchOnOff = findViewById<SwitchCompat>(R.id.switchOnOff)
        val containerRL = findViewById<RelativeLayout>(R.id.idRLContainer)
        val naviview = findViewById<NavigationView>(R.id.idNaviView)
        val tvSwitchShahar = findViewById<TextView>(R.id.tvSwitchYes)
        val tvSwitchAdam = findViewById<TextView>(R.id.tvSwitchNo)
        var switch = false
        containerRL.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)

        switchOnOff.setOnClickListener {
            if (switchOnOff.isChecked) {
                switch = true
                containerRL.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.background_shahar, null)
                naviview.background = containerRL.background
                tvSwitchShahar.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvSwitchAdam.setTextColor(ContextCompat.getColor(this, R.color.blue))

            } else {
                switch = false
                containerRL.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)
                naviview.background = containerRL.background
                tvSwitchShahar.setTextColor(ContextCompat.getColor(this, R.color.blue))
                tvSwitchAdam.setTextColor(ContextCompat.getColor(this, R.color.white))

            }
        }
        menuButton.setOnClickListener {
            naviview.foregroundGravity
        }

        val uploadButton = findViewById<MaterialButton>(R.id.uploadBT)

        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val chooseImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
                val data = it.data
                val imgUri = data?.data
                Log.i("Uri",imgUri.toString())
                if (imgUri != null){uri = imgUri}
            }
        uploadButton.setOnClickListener {
            chooseImage.launch(pickImg).toString()
            metaOfFile(pickImg, chooseImage)
        }

    }

    private fun metaOfFile(pickImg:Intent, chooseImage: ActivityResultLauncher<Intent>){
        var metadata: ArrayList<Any>? = null
        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
        val name = dialogLayout.findViewById<EditText>(R.id.name)
        val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
        val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
        val typeOfCloths = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Ok") { _, _ ->
            Log.d("Main", "Positive button clicked")
            metadata = arrayListOf(name.text.toString(), adamLike.text.toString(), shaharLike.text.toString() ,typeOfCloths.isChecked)
            Log.i("Metadata", metadata.toString())
            uploadFile(metadata)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Log.d("Main", "Negative button clicked")}
        builder.show()

    }

    private fun uploadFile(metadata: ArrayList<Any>?) {
        if (uri != null) {
            val ref = storageRef.child(metadata!![0].toString())
            val uploadTask = ref.putFile(uri!!)
            val urlTask = uploadTask.continueWithTask {
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val folder = if (metadata[3] as Boolean){"Shirts"} else{"Pants"}
                    db.collection(folder).document(metadata[0].toString())
                        .set(hashMapOf("Url" to downloadUri, "AdamLikes" to metadata[1].toString().toInt(), "ShaharLikes" to metadata[2].toString().toInt()))
                        .addOnSuccessListener {
                            Log.d(
                                "db.collection",
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { Log.e("db.collection", "Error writing document",) }
                }
            }
        }
    }
}
