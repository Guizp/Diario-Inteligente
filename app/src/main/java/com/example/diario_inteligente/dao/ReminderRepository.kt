package com.example.diario_inteligente.dao

import com.example.diario_inteligente.helper.FirebaseHelper
import com.example.diario_inteligente.model.Reminder
import com.google.firebase.firestore.toObjects

class ReminderRepository {

    /*
    essa classe tem a função de manipular os lembretes no Firestores, ela desempenha o
    papel de inserir (salvar), listar  e deletar os reminders (lembretes)
    */
    private val db = FirebaseHelper.getFirestore() //instancia o firestore
    private val collection = db.collection("reminders")

    // salvar ou atualizar lembrete
    fun saveReminder(reminder: Reminder, onComplete: (Boolean) -> Unit) {
        val id = reminder.id.ifEmpty { collection.document().id }
        val finalReminder = reminder.copy(id = id)
        collection.document(id).set(finalReminder)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // listar apenas os lembretes do usuário logado
    fun getRemindersByUser(userId: String, onComplete: (List<Reminder>?) -> Unit) {
        collection.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects<Reminder>()
                onComplete(list)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    // deletar um lembrete
    fun deleteReminder(reminderId: String, onComplete: (Boolean) -> Unit) {
        collection.document(reminderId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}