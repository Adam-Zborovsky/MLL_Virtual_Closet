package GOLD.MLL.VirtualCloset.MatchingDetails

import GOLD.MLL.VirtualCloset.Cloths
import GOLD.MLL.VirtualCloset.DataRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class MatchingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository = DataRepository(application)

    // LiveData to hold your data, observable by the UI
    private val _products = MutableLiveData<List<Cloths>>() // Replace 'Product' with your data model
    val products: LiveData<List<Cloths>> = _products

    fun getProducts(){
        _products.postValue(repository.getCachedProducts())
    }
}
