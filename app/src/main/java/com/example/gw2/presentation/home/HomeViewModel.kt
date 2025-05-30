package com.example.gw2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gw2.data.api.IGw2Api
import com.example.gw2.data.model.ItemDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(
    private val api: IGw2Api
) : ViewModel() {

    private val _items = MutableStateFlow<List<ItemDetail>>(emptyList())
    val items: StateFlow<List<ItemDetail>> = _items

    init {
        loadRandomItems()
    }

    private fun loadRandomItems() {
        viewModelScope.launch {
            try {
                val allIds = api.getAllItemIds()
                val randomIds = allIds.shuffled().take(10)
                val items = api.getItemsByIds(randomIds.joinToString(","))
                _items.value = items
            } catch (e: Exception) {
                // log error
                e.printStackTrace()
            }
        }
    }
}
