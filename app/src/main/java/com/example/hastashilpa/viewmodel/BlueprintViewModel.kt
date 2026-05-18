package com.example.hastashilpa.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hastashilpa.ui.screens.Blueprint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BlueprintViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _blueprints = MutableStateFlow<List<Blueprint>>(emptyList())
    val blueprints: StateFlow<List<Blueprint>> = _blueprints

    init {
        fetchBlueprints()
    }

    private fun fetchBlueprints() {
        db.collection("blueprints").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    Blueprint(
                        name = doc.getString("name") ?: "",
                        width = doc.getString("width") ?: "",
                        height = doc.getString("height") ?: "",
                        depth = doc.getString("depth") ?: "",
                        material = doc.getString("material") ?: "",
                        poles = doc.getString("poles") ?: "",
                        difficulty = doc.getString("difficulty") ?: ""
                    )
                }
                _blueprints.value = list
            }
        }
    }
}
