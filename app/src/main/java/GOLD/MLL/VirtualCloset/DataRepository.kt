package GOLD.MLL.VirtualCloset

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class DataRepository {
    private var storageRef = Firebase.storage.reference
    private var db = Firebase.firestore
    suspend fun loadProducts(): List<Cloths> {
        val collections = listOf("Shirts", "Pants")
        val allProducts = mutableListOf<Cloths>()

        collections.forEach { collectionName ->
            val result = db.collection(collectionName).get().await()
            val products = result.map { document ->
                val dat = document.data
                Cloths(
                    dat["Name"].toString(),
                    collectionName,
                    dat["ShaharLikes"].toString().toInt(),
                    dat["AdamLikes"].toString().toInt(),
                    dat["URL"].toString(),
                    dat["BackSide"].toString(),
                    dat["matching"] as ArrayList<String>
                )
            }
            allProducts.addAll(products)
        }
        Log.e("allProducts", allProducts.shuffled().toString())
        return allProducts.shuffled()
    }

    fun uploadFile(metadata: ArrayList<Any>, uri: Uri?, backUri: Uri?) {
        uri?.let {
            val folder = if (metadata[3] as Boolean) "Shirts" else "Pants"
            val fileName = metadata[0].toString()
            val ref = storageRef.child("$folder/$fileName")
            val uploadTask = ref.putFile(it)
            if (backUri != null) {
                val backRef = storageRef.child("BackSide/$fileName")
                backRef.putFile(backUri).addOnSuccessListener {
                    backRef.downloadUrl.addOnSuccessListener { backDownloadUri ->
                        metadata.add(backDownloadUri.toString())
                    }
                }
            }
            uploadTask.addOnSuccessListener {
                Log.d("Upload To Storage", "Success")
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val data = hashMapOf(
                        "Name" to metadata[0],
                        "ShaharLikes" to metadata[2].toString().toInt(),
                        "AdamLikes" to metadata[1].toString().toInt(),
                        "URL" to downloadUri.toString(),
                        "BackSide" to metadata[4].toString(),
                        "matching" to arrayListOf<String>()
                    )
                    db.collection(folder).document(fileName)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d("Upload To Database", "DocumentSnapshot successfully written!")
                        }
                }
            }
        }
    }
}