package com.example.hastashilpa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hastashilpa.data.model.MaterialLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _logs = MutableStateFlow<List<MaterialLog>>(emptyList())
    val logs: StateFlow<List<MaterialLog>> = _logs

    init {
        fetchLogs()
    }

    private fun fetchLogs() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("material_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val logList = snapshot.documents.mapNotNull { doc ->
                        MaterialLog(
                            id = doc.id.hashCode(),
                            batchName = doc.getString("batchName") ?: "",
                            bambooPoles = doc.getLong("bambooPoles")?.toInt() ?: 0,
                            hoursWorked = doc.getDouble("hoursWorked")?.toFloat() ?: 0f,
                            notes = doc.getString("notes") ?: "",
                            dateAdded = doc.getString("dateAdded") ?: "",
                            docId = doc.id
                        )
                    }
                    _logs.value = logList
                }
            }
    }

    fun addLog(batchName: String, poles: Int, hours: Float, notes: String) {
        val userId = auth.currentUser?.uid ?: return
        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val logData = hashMapOf(
            "batchName" to batchName,
            "bambooPoles" to poles,
            "hoursWorked" to hours,
            "notes" to notes,
            "dateAdded" to date,
            "timestamp" to System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).collection("material_logs").add(logData)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteLog(docId: String) {
        val userId = auth.currentUser?.uid ?: return
        if (docId.isEmpty()) return
        
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).collection("material_logs")
                    .document(docId).delete()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
