import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.DataRepository
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository = DataRepository(application)

    // LiveData to hold your data, observable by the UI
    private val _products = MutableLiveData<List<Cloths>>() // Replace 'Product' with your data model
    val products: LiveData<List<Cloths>> = _products
    fun loadProducts() {
        viewModelScope.launch {
            val fetchedProducts = repository.loadProducts()
            _products.postValue(fetchedProducts)
        }
    }
    fun uploadFile(metadata: ArrayList<Any>, uri: Uri?, backUri: Uri?) {
        viewModelScope.launch {
            repository.uploadFile(metadata, uri, backUri) {
                val fetchedProducts = repository.getCachedProducts()
                Log.e("fetchedProducts",fetchedProducts.toString())
                _products.postValue(fetchedProducts)
            }

        }
    }
}
