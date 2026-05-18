package com.example.hastashilpa.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hastashilpa.data.model.DesignItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _boughtItems = MutableStateFlow<List<DesignItem>>(emptyList())
    val boughtItems: StateFlow<List<DesignItem>> = _boughtItems

    private val _soldItems = MutableStateFlow<List<DesignItem>>(emptyList())
    val soldItems: StateFlow<List<DesignItem>> = _soldItems

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        val userId = auth.currentUser?.uid ?: return

        // Fetch items bought by the user (simulated via 'orders' collection)
        db.collection("orders")
            .whereEqualTo("buyerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _boughtItems.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DesignItem::class.java)?.copy(firestoreId = doc.id)
                    }
                }
            }

        // Fetch items sold by the user (items where they are the artisan)
        db.collection("products")
            .whereEqualTo("artisanId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    _soldItems.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DesignItem::class.java)?.copy(firestoreId = doc.id)
                    }
                }
            }
    }
}
