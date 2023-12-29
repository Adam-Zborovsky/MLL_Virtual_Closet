package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


class MainActivity : ComponentActivity() {
    private var storageRef = Firebase.storage.reference
    private var db = Firebase.firestore
    private var uri: Uri? = null
    private val items = ArrayList<Cloths>()
    private val adapter = ClothsAdapter(items)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downFromDatabase()

        val naviview = findViewById<NavigationView>(R.id.naviView)
        val uploadButton = findViewById<MaterialButton>(R.id.uploadBT)
        val switchOnOff = findViewById<SwitchCompat>(R.id.switchOnOff)
        val containerRL = findViewById<RelativeLayout>(R.id.idRLContainer)
        val tvSwitchShahar = findViewById<TextView>(R.id.tvSwitchYes)
        val tvSwitchAdam = findViewById<TextView>(R.id.tvSwitchNo)
        var switch = false
        containerRL.background =
            ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)

        switchOnOff.setOnClickListener {
            adapter.switchFlip()
            val (backgroundResId, shaharTextColor, adamTextColor) =
            if (switchOnOff.isChecked) {
                Triple(R.drawable.background_shahar, R.color.white, R.color.blue)
            }
            else {
                Triple(R.drawable.background_adam, R.color.blue, R.color.white)
            }
            containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)
            naviview.background = containerRL.background
            tvSwitchShahar.setTextColor(ContextCompat.getColor(this, shaharTextColor))
            tvSwitchAdam.setTextColor(ContextCompat.getColor(this, adamTextColor))

            switch = !switch
        }

        val openNav = findViewById<ImageButton>(R.id.openNav)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        openNav.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val chooseImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val data = it.data
                val imgUri = data?.data
                Log.i("Uri",imgUri.toString())
                if (imgUri != null){uri = imgUri}
            }
        uploadButton.setOnClickListener {
            Log.i("Upload Button", "pressed")
            chooseImage.launch(pickImg).toString()
            metaOfFile()
        }

        naviview.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.shirtIco, R.id.pantsIco -> {
                    menuItem.isChecked = !menuItem.isChecked
                    val showShirts = naviview.menu.findItem(R.id.shirtIco).isChecked
                    val showPants = naviview.menu.findItem(R.id.pantsIco).isChecked
                    adapter.filterCloths(showShirts, showPants)
                }
                R.id.likeA, R.id.likeS, R.id.rnd -> {
                    naviview.menu.findItem(R.id.likeA).isChecked = false
                    naviview.menu.findItem(R.id.likeS).isChecked = false
                    naviview.menu.findItem(R.id.rnd).isChecked = false
                    menuItem.isChecked = true

                    when (menuItem.itemId) {
                        R.id.likeA -> adapter.sortByALike()
                        R.id.likeS -> adapter.sortBySLike()
                        R.id.rnd -> adapter.sortRandomly()
                    }
                }
            }
            false
        }


    }

    private fun metaOfFile(){
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
            val metadata: ArrayList<Any> = arrayListOf(name.text.toString(), adamLike.text.toString(), shaharLike.text.toString() ,typeOfCloths.isChecked)

            if(items.none{it.name == name.text.toString()}){uploadFile(metadata)}
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Log.d("Main", "Negative button clicked")}
        builder.show()
    }

    private fun uploadFile(metadata: ArrayList<Any>) {
        if (uri != null) {
            val folder = if (metadata[3] as Boolean){"Shirts"} else{"Pants"}
            val ref = storageRef.child(folder +"/"+ metadata[0].toString())
            val uploadTask = ref.putFile(uri!!)
            uploadTask.addOnSuccessListener { Log.d("Upload To Storage", "Success") }
                .addOnFailureListener{Log.e("Upload To Storage", "Failed")}
            uploadTask.continueWithTask {
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    db.collection(folder).document(metadata[0].toString())
                        .set(hashMapOf("Name" to metadata[0], "ShaharLikes" to metadata[2].toString().toInt(), "AdamLikes" to metadata[1].toString().toInt(), "URL" to downloadUri, "matching" to "[]"))

                        .addOnSuccessListener {Log.d(  "Upload To Database","DocumentSnapshot successfully written!")}
                        .addOnFailureListener { Log.e("Upload To Database", "Error writing document") }
                }
            }
        }
    }

    private fun downFromDatabase() {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        db.collection("Shirts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dat = document.data
                    items.add(Cloths(dat["Name"].toString(),"Shirts", dat["ShaharLikes"].toString().toInt(), dat["AdamLikes"].toString().toInt(), dat["URL"].toString(), arrayListOf(dat["matching"].toString())))
                    items.shuffle()
                }
            }
            .addOnCompleteListener() {
                recyclerview.adapter = adapter
            }
            .addOnFailureListener { exception ->
                 Log.d("Error getting documents: ", exception.toString())
            }
        db.collection("Pants")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dat = document.data
                    items.add(Cloths(dat["Name"].toString(),"Pants", dat["ShaharLikes"].toString().toInt(), dat["AdamLikes"].toString().toInt(), dat["URL"].toString(), arrayListOf(dat["matching"].toString())))
                    items.shuffle()
                }
            }
            .addOnCompleteListener() {
                recyclerview.adapter = adapter
            }
            .addOnFailureListener { exception ->
                 Log.d("Error getting documents: ", exception.toString())
            }

    }
}
