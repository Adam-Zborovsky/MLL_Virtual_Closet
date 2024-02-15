import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.DataRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.Serializable

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository = DataRepository(application)

    // LiveData to hold your data, observable by the UI
    private val _selectedProduct = MutableLiveData<Cloths>() // Replace 'Product' with your data model
    val selectedProduct: LiveData<Cloths> = _selectedProduct

    fun setSelectedProduct(prod: Cloths){
        _selectedProduct.postValue(prod)
    }
    fun deleteProduct(clothsItem: Cloths) {
        viewModelScope.launch {
            repository.deleteProduct(clothsItem)
        }
    }
    fun updateProduct(updates: HashMap<String, Serializable>) {
        val fetchedProducts = repository.updateProduct(updates, selectedProduct.value!!)
        _selectedProduct.postValue(fetchedProducts)
    }
}
