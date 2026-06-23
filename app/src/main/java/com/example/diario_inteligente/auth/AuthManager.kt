package com.example.diario_inteligente.auth

import com.example.diario_inteligente.helper.FirebaseHelper
import com.example.diario_inteligente.model.User

class AuthManager {
    private val auth = FirebaseHelper.getAuth()
    private val db = FirebaseHelper.getFirestore()

    // cadastrar usuário no Firebase Auth e salvar no Firestore
    fun register(user: User, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val newUser = user.copy(id = uid)

                // Salva os dados extras do usuário no Firestore
                db.collection("users").document(uid).set(newUser)
                    .addOnSuccessListener { onComplete(true, null) }
                    .addOnFailureListener { e -> onComplete(true, "Usuário criado, mas erro ao salvar perfil: ${e.localizedMessage}") }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }

    // realizar Login
    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.localizedMessage) }
    }

    // verificar se usuário está logado
    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }

    // obter ID do usuário logado
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // Fazer Logout
    fun logout() {
        auth.signOut()
    }
}