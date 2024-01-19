package GOLD.MLL.VirtualCloset.ProductDetails

import GOLD.MLL.VirtualCloset.Adapters.ProductAdapter
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.Home.Home
import GOLD.MLL.VirtualCloset.MatchingDetails.MatchingDetails
import GOLD.MLL.VirtualCloset.R
import ProductViewModel
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

class ProductDetails : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel
    private var adapter = ProductAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_screen)


        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val recyclerview = findViewById<RecyclerView>(R.id.matching)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        viewModel.setSelectedProduct(intent.getSerializableExtra("clothsItem") as Cloths)
        var clothsItem = intent.getSerializableExtra("clothsItem") as Cloths
        val switch = intent.getBooleanExtra("switch", true)

        adapter.updateList(clothsItem.matching)

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val edit = findViewById<ImageButton>(R.id.editButton)
        val add = findViewById<ImageButton>(R.id.addButton)

        viewModel.selectedProduct.observe(this) { selectedProduct ->
            clothsItem = selectedProduct
            adapter.updateList(clothsItem.matching)
            showClothData(selectedProduct)
        }
        add.setOnClickListener {
            if (switch) {
                val intent = Intent(this, MatchingDetails::class.java)
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
                val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
                val newName = dialogLayout.findViewById<EditText>(R.id.name)
                val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
                val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
                val typeSelector = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)
                val intent = Intent(this, Home::class.java)
                newName.setText(clothsItem.name)
                adamLike.setText(clothsItem.aLike.toString())
                shaharLike.setText(clothsItem.sLike.toString())
                typeSelector.isChecked = clothsItem.typeCloth == "Shirts"

                builder.setView(dialogLayout)
                builder.setNeutralButton("Delete") { _, _ ->
                    Log.d("Main", "Delete button clicked")
                    viewModel.deleteProduct(clothsItem)
                    startActivity(intent)

                }
                builder.setPositiveButton("Ok") { _, _ ->
                    Log.d("Main", "Positive button clicked")
                    val updates = hashMapOf(
                        "Folder" to if (typeSelector.isChecked) "Shirts" else "Pants",
                        "Name" to newName.text.toString(),
                        "AdamLikes" to adamLike.text.toString(),
                        "ShaharLikes" to shaharLike.text.toString(),
                        "URL" to clothsItem.photoUrl,
                        "BackSide" to clothsItem.backsideUrl,
                        "matching" to clothsItem.matching
                    )
                    viewModel.updateProduct(updates)
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                    Log.d("Main", "Negative button clicked")
                }
                builder.show()
            }
        }


        val btnReturnHome = findViewById<ImageButton>(R.id.btnReturnHome)
        btnReturnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        val backgroundResId = if (switch) {R.drawable.background_shahar} else {R.drawable.background_adam}
        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)
        showClothData(clothsItem)
    }

    private fun showClothData(clothsItem: Cloths) {
        val prodName = findViewById<TextView>(R.id.prodName)
        val prod = findViewById<ImageView>(R.id.prod)
        val prodBack = findViewById<ImageButton>(R.id.prodBack)
        val like = findViewById<TextView>(R.id.like)

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