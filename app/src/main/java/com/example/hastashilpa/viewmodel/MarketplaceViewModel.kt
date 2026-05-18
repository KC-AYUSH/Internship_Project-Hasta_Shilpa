package com.example.hastashilpa.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hastashilpa.data.model.DesignItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MarketplaceViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    private val _items = MutableStateFlow<List<DesignItem>>(emptyList())
    val items: StateFlow<List<DesignItem>> = _items

    init {
        fetchItems()
    }

    private fun fetchItems() {
        db.collection("products").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null) {
                val productList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DesignItem::class.java)?.copy(firestoreId = doc.id)
                }
                _items.value = productList
            }
        }
    }

    fun addProduct(name: String, category: String, price: Double, desc: String, imageUri: Uri?) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                var imageUrl = ""
                if (imageUri != null) {
                    val ref = storage.reference.child("product_images/${System.currentTimeMillis()}.jpg")
                    ref.putFile(imageUri).await()
                    imageUrl = ref.downloadUrl.await().toString()
                }

                val product = DesignItem(
                    name = name,
                    category = category,
                    price = price,
                    description = desc,
                    imageUrl = imageUrl,
                    artisanId = userId
                )
                db.collection("products").add(product).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
