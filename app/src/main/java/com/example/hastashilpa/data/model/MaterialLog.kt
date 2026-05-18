package com.example.hastashilpa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "material_logs")
data class MaterialLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val batchName: String = "",
    val bambooPoles: Int = 0,
    val hoursWorked: Float = 0f,
    val notes: String = "",
    val dateAdded: String = "",
    val docId: String = "" // For Firestore
)
