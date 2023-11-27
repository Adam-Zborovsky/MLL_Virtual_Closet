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
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage


class MainActivity : ComponentActivity() {
    private var storageRef = Firebase.storage.reference
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
        var selcetedUri: Uri? = null
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val chooseImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            val data = it.data
            val imgUri = data?.data
            imgUri
        }
        var metadata: ArrayList<Any>? = null
        uploadButton.setOnClickListener {
            metadata = metaOfFile(pickImg, chooseImage)
        }


    }

    private fun metaOfFile(pickImg:Intent, chooseImage: ActivityResultLauncher<Intent>): ArrayList<Any>? {
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
            Log.i("Name", name.text.toString())
            Log.i("Adam Like", adamLike.text.toString())
            Log.i("Shahar Like", shaharLike.text.toString())
            Log.i("typeOfCloths", typeOfCloths.isChecked.toString())
            metadata = arrayListOf(name.text.toString(), adamLike.text.toString(), shaharLike.text.toString() ,typeOfCloths.isChecked)
            chooseImage.launch(pickImg)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Log.d("Main", "Negative button clicked")}
        builder.show()

        return metadata
    }
}
