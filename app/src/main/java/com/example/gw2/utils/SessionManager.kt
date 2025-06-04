// app/src/main/java/com/example/gw2/utils/SessionManager.kt

package com.example.gw2.utils

/**
 * SessionManager: guarda en memoria el userId, email y si es invitado.
 * Se limpia al cerrar sesión.
 */
object SessionManager {
    var userId: String? = null
    var email: String? = null
    var isGuest: Boolean = false

    fun clear() {
        userId = null
        email = null
        isGuest = false
    }
}
