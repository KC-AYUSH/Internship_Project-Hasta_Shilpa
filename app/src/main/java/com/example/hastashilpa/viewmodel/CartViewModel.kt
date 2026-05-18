package com.example.hastashilpa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hastashilpa.data.model.DesignItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _cartItems = MutableStateFlow<List<DesignItem>>(emptyList())
    val cartItems: StateFlow<List<DesignItem>> = _cartItems

    private val _wishlistItems = MutableStateFlow<List<DesignItem>>(emptyList())
    val wishlistItems: StateFlow<List<DesignItem>> = _wishlistItems

    init {
        fetchCart()
        fetchWishlist()
    }

    private fun fetchCart() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("cart")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _cartItems.value = snapshot.documents.mapNotNull { doc ->
                        DesignItem(
                            id = doc.getLong("id")?.toInt() ?: 0,
                            name = doc.getString("name") ?: "",
                            category = doc.getString("category") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            artisanId = doc.getString("artisanId") ?: "",
                            firestoreId = doc.id
                        )
                    }
                }
            }
    }

    private fun fetchWishlist() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("wishlist")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _wishlistItems.value = snapshot.documents.mapNotNull { doc ->
                        DesignItem(
                            id = doc.getLong("id")?.toInt() ?: 0,
                            name = doc.getString("name") ?: "",
                            category = doc.getString("category") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            artisanId = doc.getString("artisanId") ?: "",
                            firestoreId = doc.id
                        )
                    }
                }
            }
    }

    fun addToCart(item: DesignItem) {
        val userId = auth.currentUser?.uid ?: return
        val docId = if (item.firestoreId.isNotEmpty()) item.firestoreId else 
                    if (item.id != 0) item.id.toString() else System.currentTimeMillis().toString()
        
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).collection("cart")
                    .document(docId).set(item).await()
            } catch (e: Exception) {}
        }
    }

    fun addToWishlist(item: DesignItem) {
        val userId = auth.currentUser?.uid ?: return
        val docId = if (item.firestoreId.isNotEmpty()) item.firestoreId else 
                    if (item.id != 0) item.id.toString() else System.currentTimeMillis().toString()
                    
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).collection("wishlist")
                    .document(docId).set(item).await()
            } catch (e: Exception) {}
        }
    }

    fun removeFromCart(firestoreId: String) {
        val userId = auth.currentUser?.uid ?: return
        if (firestoreId.isEmpty()) return
        
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).collection("cart")
                    .document(firestoreId).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun checkout(onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val items = _cartItems.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            try {
                items.forEach { item ->
                    val orderData = hashMapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "imageUrl" to item.imageUrl,
                        "buyerId" to userId,
                        "artisanId" to item.artisanId,
                        "timestamp" to System.currentTimeMillis()
                    )
                    // Add to global orders collection for history tracking
                    db.collection("orders").add(orderData).await()
                    
                    // Remove from cart
                    db.collection("users").document(userId).collection("cart")
                        .document(item.firestoreId).delete().await()
                }
                onSuccess()
            } catch (e: Exception) {}
        }
    }
}
