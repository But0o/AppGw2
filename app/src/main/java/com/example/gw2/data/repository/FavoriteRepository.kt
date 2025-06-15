// app/src/main/java/com/example/gw2/data/repository/FavoritesRepository.kt
package com.example.gw2.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

    /** Añade item a favoritos. */
    fun addFavorite(itemId: Int) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(itemId.toString())
            .set(mapOf("timestamp" to System.currentTimeMillis()))
    }

    /** Quita item de favoritos. */
    fun removeFavorite(itemId: Int) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(itemId.toString())
            .delete()
    }

    /** Recupera la lista de IDs favoritos. */
    suspend fun getFavoriteIds(): List<Int> {
        val snap = db.collection("users")
            .document(uid)
            .collection("favorites")
            .orderBy("timestamp")
            .get()
            .await()
        return snap.documents.mapNotNull { it.id.toIntOrNull() }
    }

    companion object
}
