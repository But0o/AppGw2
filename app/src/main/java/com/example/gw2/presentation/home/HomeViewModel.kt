package com.example.gw2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.ItemDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val api: IGw2Api) : ViewModel() {

    private val _recommendedItems = MutableStateFlow<List<ItemDetail>>(emptyList())
    val recommendedItems: StateFlow<List<ItemDetail>> = _recommendedItems

    init {
        loadRandomItems()
    }

    private fun loadRandomItems() {
        viewModelScope.launch {
            try {
                val allIds = api.getAllItemIds()
                val randomIds = allIds.shuffled().take(10)
                val items = api.getItemsByIds(randomIds.joinToString(","))
                _recommendedItems.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
