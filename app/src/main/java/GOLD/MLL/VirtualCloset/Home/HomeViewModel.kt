import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.DataRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = DataRepository() // Assuming you have a repository class

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
            repository.uploadFile(metadata, uri, backUri)
        }
    }
}
