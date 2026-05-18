package com.example.hastashilpa.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "artisan",
    val profilePicUrl: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                _profile.value = UserProfile(
                    name = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: "",
                    phone = snapshot.getString("phone") ?: "",
                    role = snapshot.getString("role") ?: "artisan",
                    profilePicUrl = snapshot.getString("profilePicUrl") ?: ""
                )
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).update(
                    "name", name,
                    "phone", phone
                ).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isUploading.value = true
            _uploadError.value = null
            try {
                val ref = storage.reference.child("profile_pics/$userId.jpg")
                // Use the newer way to upload
                ref.putFile(uri).await()
                val url = ref.downloadUrl.await().toString()
                db.collection("users").document(userId).update("profilePicUrl", url).await()
            } catch (e: Exception) {
                _uploadError.value = e.localizedMessage ?: "Upload failed"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun clearUploadError() {
        _uploadError.value = null
    }
}
