package GOLD.MLL.VirtualCloset.Home

import GOLD.MLL.VirtualCloset.Adapters.HomeAdapter
import GOLD.MLL.VirtualCloset.R
import GOLD.MLL.VirtualCloset.Wishlist
import HomeViewModel
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView


class Home : ComponentActivity() {
    private var uri: Uri? = null
    private var backUri: Uri? = null
    private lateinit var viewModel: HomeViewModel
    private var adapter = HomeAdapter(listOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.loadProducts()

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerview.adapter = adapter

        val naviview = findViewById<NavigationView>(R.id.naviView)
        val uploadButton = findViewById<MaterialButton>(R.id.uploadBT)
        val wishlist = findViewById<ImageButton>(R.id.wishlist)
        val switchOnOff = findViewById<SwitchCompat>(R.id.switchOnOff)
        val containerRL = findViewById<RelativeLayout>(R.id.idRLContainer)
        val tvSwitchShahar = findViewById<TextView>(R.id.tvSwitchYes)
        val tvSwitchAdam = findViewById<TextView>(R.id.tvSwitchNo)
        var switch = false
        var (backgroundResId, shaharTextColor, adamTextColor) =
            Triple(R.drawable.background_adam, R.color.blue, R.color.white)
        containerRL.background = ResourcesCompat.getDrawable(resources, backgroundResId, null)
        tvSwitchShahar.setTextColor(ContextCompat.getColor(this, shaharTextColor))
        tvSwitchAdam.setTextColor(ContextCompat.getColor(this, adamTextColor))


        viewModel.products.observe(this) { products ->
            adapter.updateList(products)
            switchOnOff.isChecked = false
            switch = false
            containerRL.background = ResourcesCompat.getDrawable(resources, R.drawable.background_adam, null)
            naviview.background = containerRL.background
            tvSwitchShahar.setTextColor(ContextCompat.getColor(this, R.color.blue))
            tvSwitchAdam.setTextColor(ContextCompat.getColor(this, R.color.white))
        }


        switchOnOff.setOnClickListener {
            adapter.switchFlip()

            if (switchOnOff.isChecked) {
                backgroundResId = R.drawable.background_shahar
                shaharTextColor = R.color.white
                adamTextColor = R.color.blue
            } else {
                backgroundResId = R.drawable.background_adam
                shaharTextColor = R.color.blue
                adamTextColor = R.color.white
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
        val chooseImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val data = it.data
                val imgUri = data?.data
                if (imgUri != null) {
                    uri = imgUri
                }
            }
        val chooseBackImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val data = it.data
                val imgUri = data?.data
                if (imgUri != null) {
                    backUri = imgUri
                }
            }
        uploadButton.setOnClickListener {
            chooseImage.launch(pickImg)
            metaOfFile(chooseBackImage)
        }

        wishlist.setOnClickListener {
            val intent = Intent(this, Wishlist::class.java)
            this.startActivity(intent)
        }

        naviview.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.shirtIco, R.id.pantsIco -> {
                    menuItem.isChecked = !menuItem.isChecked
                    val showShirts = naviview.menu.findItem(R.id.shirtIco).isChecked
                    val showPants = naviview.menu.findItem(R.id.pantsIco).isChecked
                    containerRL.background =
                        ResourcesCompat.getDrawable(resources, backgroundResId, null)
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
                    containerRL.background =
                        ResourcesCompat.getDrawable(resources, backgroundResId, null)
                }
            }
            false
        }
    }

    private fun metaOfFile(chooseBackImage: ActivityResultLauncher<Intent>) {
        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.upload_dialog_box, null)
        val name = dialogLayout.findViewById<EditText>(R.id.name)
        val adamLike = dialogLayout.findViewById<EditText>(R.id.adamLike)
        val shaharLike = dialogLayout.findViewById<EditText>(R.id.shaharLike)
        val typeOfCloths = dialogLayout.findViewById<SwitchCompat>(R.id.switchCloths)
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        val dialog = builder.create()
        dialog.setView(dialogLayout)

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ -> }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ -> }
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add Back Side") { _, _ -> }
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            Log.d("Main", "Positive button clicked")
            val metadata: ArrayList<Any> = arrayListOf(
                name.text.toString(),
                adamLike.text.toString(),
                shaharLike.text.toString(),
                typeOfCloths.isChecked)
//            if (items.none { it.name == name.text.toString() }) {
            viewModel.uploadFile(metadata, uri, backUri)
//            } else (Toast.makeText(this, "Item With That Name Already Exists", Toast.LENGTH_SHORT).show())
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            Log.d("Main", "Negative button clicked")
            dialog.dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            chooseBackImage.launch(pickImg)
        }
    }
}

