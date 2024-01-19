package GOLD.MLL.VirtualCloset.MatchingDetails

import GOLD.MLL.VirtualCloset.Adapters.MatchingAdapter
import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.ProductDetails.ProductDetails
import GOLD.MLL.VirtualCloset.R
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MatchingDetails : AppCompatActivity(){
    private lateinit var viewModel: MatchingViewModel
    private val adapter = MatchingAdapter(listOf(), Cloths("","",0,0,"","", arrayListOf()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.matching_details)


        viewModel = ViewModelProvider(this)[MatchingViewModel::class.java]

        val recyclerview = findViewById<RecyclerView>(R.id.matching_edit)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        val selectedItem  = intent.getSerializableExtra("clothsItem") as Cloths
        viewModel.getProducts()
        adapter.filterCloths(selectedItem.typeCloth)

        val containerRL = findViewById<ConstraintLayout>(R.id.containerRL)
        val openNav = findViewById<ImageButton>(R.id.openNav)
        val naviview = findViewById<NavigationView>(R.id.naviView)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        containerRL.background = ResourcesCompat.getDrawable(resources,
            R.drawable.background_shahar, null)
        naviview.background = containerRL.background

        viewModel.products.observe(this) { products ->
            adapter.updateList(products, selectedItem)
            adapter.filterCloths(selectedItem.typeCloth)
        }

        openNav.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            var filter = naviview.menu.findItem(R.id.shirtIco)
            if (selectedItem.typeCloth != "Shirt"){filter = naviview.menu.findItem(R.id.shirtIco)}
            if (selectedItem.typeCloth != "Pants"){filter = naviview.menu.findItem(R.id.pantsIco)}
            filter.isChecked = false
        }

        naviview.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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
                    containerRL.background = ResourcesCompat.getDrawable(resources,
                        R.drawable.background_shahar, null)
                }
            }
            false
        }

        val done = findViewById<ImageButton>(R.id.done)
        done.setOnClickListener {
            val intent = Intent(this, ProductDetails::class.java)
            intent.putExtra("clothsItem", selectedItem)
            startActivity(intent)
        }
    }
}