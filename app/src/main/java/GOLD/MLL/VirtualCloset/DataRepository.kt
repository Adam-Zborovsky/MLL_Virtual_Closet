package GOLD.MLL.VirtualCloset

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import java.io.Serializable

class DataRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Product List", Context.MODE_PRIVATE)
    private val gson = Gson()
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
        cacheProducts(allProducts.shuffled())
        return allProducts.shuffled()
    }

    fun uploadFile(metadata: ArrayList<Any>, uri: Uri?, backUri: Uri?, onSuccess: () -> Unit) {
        uri?.let {
            val folder = if (metadata[3] as Boolean) "Shirts" else "Pants"
            val fileName = metadata[0].toString()
            val ref = storageRef.child("$folder/$fileName")
            val uploadTask = ref.putFile(it)


            fun updateDatabase(backSideUrl: String) {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val data = hashMapOf(
                        "Name" to metadata[0],
                        "ShaharLikes" to metadata[2].toString().toInt(),
                        "AdamLikes" to metadata[1].toString().toInt(),
                        "URL" to downloadUri.toString(),
                        "BackSide" to backSideUrl,
                        "matching" to arrayListOf<String>()
                    )
                    db.collection(folder).document(fileName)
                        .set(data)
                        .addOnSuccessListener {
                            cacheOneProduct(Cloths(data["Name"].toString(), folder, data["ShaharLikes"].toString().toInt(), data["AdamLikes"].toString().toInt(), data["URL"].toString(), data["BackSide"].toString(), data["matching"] as ArrayList<String>))
                            onSuccess()
                            Log.d("Upload To Database", "DocumentSnapshot successfully written!")
                        }
                }
            }

            uploadTask.addOnSuccessListener {
                if (backUri != null) {
                    val backRef = storageRef.child("BackSide/$folder/$fileName")
                    backRef.putFile(backUri).addOnSuccessListener {
                        backRef.downloadUrl.addOnSuccessListener { backDownloadUri ->
                            updateDatabase(backDownloadUri.toString())
                        }}} else {updateDatabase("")}
            }
        }
    }

    fun deleteProduct(clothsItem: Cloths): List<Cloths> {
        val fullList = getCachedProducts()
        val oldDocRef = db.collection(clothsItem.typeCloth).document(clothsItem.name)
        oldDocRef.delete()

        val fileRefByUrl =
            FirebaseStorage.getInstance().getReferenceFromUrl(clothsItem.photoUrl)
        fileRefByUrl.delete()
        if (clothsItem.backsideUrl != "") {
            val fileBackRefByUrl =
                FirebaseStorage.getInstance().getReferenceFromUrl(clothsItem.backsideUrl)
            fileBackRefByUrl.delete()
        }

        if (fullList != null) {
            for (i in fullList) {
                for (j in i.matching) {
                    if (clothsItem.name in j.split(",")) {
                        val docRef = db.collection(i.typeCloth).document(i.name)
                        i.matching.remove("${clothsItem.name},${clothsItem.typeCloth},${clothsItem.sLike},${clothsItem.aLike},${clothsItem.photoUrl},${clothsItem.backsideUrl},${clothsItem.matching}")
                        docRef.update("matching", i.matching)
                    }
                }
            }
        }
        val newList = fullList!!.toMutableList()
        newList.remove(clothsItem)
        cacheProducts(newList)
        return newList
    }
    fun updateProduct(updates: HashMap<String, Serializable>, clothsItem: Cloths): Cloths {
        val oldDocRef = db.collection(clothsItem.typeCloth).document(clothsItem.name)
        oldDocRef.delete()
        val fileRefByUrl = FirebaseStorage.getInstance().getReferenceFromUrl(clothsItem.photoUrl)
        val newFileRef = FirebaseStorage.getInstance().reference.child("${updates["Folder"].toString()}/${updates["Name"].toString()}")
        fileRefByUrl.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            newFileRef.putBytes(bytes).addOnSuccessListener {
                fileRefByUrl.delete()
                newFileRef.downloadUrl.addOnSuccessListener { newUrl ->
                    updates["URL"] = newUrl.toString()
                    Log.e("Here","Here")
                    val newDocRef = db.collection(updates["Folder"].toString()).document(updates["Name"].toString())
                    newDocRef.set(updates)
                }
            }
        }
        if (clothsItem.backsideUrl != "")  {
            val fileRefByUrl = FirebaseStorage.getInstance().getReferenceFromUrl(clothsItem.backsideUrl)
            val newFileRef = FirebaseStorage.getInstance().reference.child("BackSide/${updates["Folder"].toString()}/${updates["Name"].toString()}")
            fileRefByUrl.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                newFileRef.putBytes(bytes).addOnSuccessListener {
                    fileRefByUrl.delete()
                    newFileRef.downloadUrl.addOnSuccessListener { newUrl ->
                        updates["BackSide"] = newUrl.toString()
                        val newDocRef = db.collection(updates["Folder"].toString()).document(updates["Name"].toString())
                        newDocRef.set(updates)
                    }
                }
            }
        }
<<<<<<< Updated upstream
        return Cloths(updates["Name"].toString(), updates["Folder"].toString(), updates["ShaharLikes"].toString().toInt(), updates["AdamLikes"].toString().toInt(), updates["URL"].toString(), updates["BackSide"].toString(), updates["matching"] as ArrayList<String>)
=======
        val newProduct = Cloths(updates["Name"].toString(), updates["Folder"].toString(), updates["ShaharLikes"].toString().toInt(), updates["AdamLikes"].toString().toInt(), updates["URL"].toString(), updates["BackSide"].toString(), updates["matching"] as ArrayList<String>)
        val fullList = getCachedProducts()!!.toMutableList()
        Log.e("Fullist", fullList.toString())
        val iterator = fullList.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            Log.e("i.name", i.name)
            Log.e("clothsItem.name", clothsItem.name)
            if (i.name == clothsItem.name) {
                iterator.remove()
            }
        }
        cacheProducts(fullList)
        return newProduct
>>>>>>> Stashed changes

    }

    private fun cacheProducts(products: List<Cloths>) {
        val json = gson.toJson(products)
        sharedPreferences.edit().putString("Product List", json).apply()
    }
    private fun cacheOneProduct(product: Cloths) {
        val json = getCachedProducts()!!.toMutableList()
        json.add(product)
        sharedPreferences.edit().putString("Product List", gson.toJson(json)).apply()
    }
    fun getCachedProducts(): List<Cloths>? {
        val json = sharedPreferences.getString("Product List", null)
        return if (json != null) {
            val type = object : TypeToken<List<Cloths>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }
}