package GOLD.MLL.VirtualCloset

import GOLD.MLL.VirtualCloset.Adapters.WishlistAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Wishlist: AppCompatActivity(), WishlistAdapter.OnRemoveItemListener {
    private var db = Firebase.firestore
    private var uri: Uri? = null
    private var wishlistDict = mutableMapOf<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wishlist)
        getLinks()

        val header = findViewById<TextView>(R.id.header)
        val upload = findViewById<ImageButton>(R.id.upload)
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val chooseImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            val imgUri = data?.data
            if (imgUri != null) {
                uri = imgUri
            }
        }

        header.setOnClickListener{
            getLinks()
        }

        upload.setOnClickListener {
            chooseImage.launch(pickImg)
            uploadLink()
        }
    }

    private fun uploadLink() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.wishlist_upload_dialog_box, null)
        val name = dialogLayout.findViewById<EditText>(R.id.name)

        builder.setView(dialogLayout)
            .setPositiveButton("Ok") { _, _ ->
            Log.d("Main", "Positive button clicked")
            val updates = hashMapOf(
                "URL" to uri
            )
            db.collection("Wishlist").document(name.text.toString())
                .set(updates)
                .addOnSuccessListener {
                    Log.d("Firestore", "Document successfully uploaded!")
                    getLinks()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error uploading document", e)
                }
        }
            .setNegativeButton("Cancel") { _, _ ->
            Log.d("Main", "Negative button clicked")
        }
            .show()
    }

    private fun getLinks() {
        val recyclerview = findViewById<RecyclerView>(R.id.wishlist_items)
        Log.e("here","here")
        db.collection("Wishlist").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.e("here1","here1")
                    val key = document.id
                    val value = document.getString("URL")
                    if (value != null) {
                        wishlistDict[key] = value
                    }
                }
                val adapter = WishlistAdapter(wishlistDict)
                recyclerview.layoutManager = LinearLayoutManager(this)
                recyclerview.adapter = adapter
                adapter.removeItemListener = this
            }
    }

    override fun removeItem(key: String) {
        db.collection("Wishlist").document(key).delete()
            .addOnSuccessListener{
                wishlistDict.remove(key)
                getLinks()
            }
    }
}