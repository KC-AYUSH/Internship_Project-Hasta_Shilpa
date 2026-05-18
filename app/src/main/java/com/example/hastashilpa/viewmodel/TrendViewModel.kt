package com.example.hastashilpa.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hastashilpa.data.model.DesignItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrendViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _trends = MutableStateFlow<List<DesignItem>>(emptyList())
    val trends: StateFlow<List<DesignItem>> = _trends

    init {
        fetchTrends()
    }

    private fun fetchTrends() {
        db.collection("trends").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null) {
                val trendList = snapshot.documents.mapNotNull { doc ->
                    DesignItem(
                        id = doc.getLong("id")?.toInt() ?: 0,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        description = doc.getString("description") ?: "",
                        firestoreId = doc.id
                    )
                }
                _trends.value = trendList
            }
        }
    }
}
