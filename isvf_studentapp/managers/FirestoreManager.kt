package es.efb.isvf_studentapp.managers

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import es.igs.android.classes.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirestoreManager {
    private val firestore by lazy { FirebaseFirestore.getInstance()}

    suspend fun getMessages(): MutableList<ChatMessage>?{
        return try {
            val result = firestore.collection("chat").get().await()
            result.toObjects(ChatMessage::class.java)

        } catch (e: Exception){
            null
        }
    }

    suspend fun getMessagesFlow(): Flow<MutableList<ChatMessage>> = callbackFlow {

        var messagesCollection: CollectionReference? = null

        try {
            messagesCollection = firestore.collection("chat")

            val subscription = messagesCollection.orderBy("timestamp")?.addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = mutableListOf<ChatMessage>()
                    snapshot.forEach {
                        val username = it.getString("username") ?: ""
                        val message = it.getString("message") ?: ""
                        val userUID = it.getString("userUID") ?: ""

                        messages.add(ChatMessage(username, message, userUID))
                    }

                    trySend(messages)
                }

            }

            awaitClose { subscription?.remove() }

        } catch (e: Exception) {
            close(e)
        }

    }


    suspend fun addProfessor(email: String): Boolean {
        return try {
            val data = hashMapOf(
                "email" to email
            )
            firestore.collection("professors").add(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeProfessor(email: String): Boolean {
        return try {
            val professorQuerySnapshot = firestore.collection("professors")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!professorQuerySnapshot.isEmpty) {
                val professorDocument = professorQuerySnapshot.documents.first()

                firestore.collection("professors").document(professorDocument.id).delete().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }


    suspend fun isProfessor(email: String): Boolean {
        return try {
            val professorQuery = firestore.collection("professors")
                .whereEqualTo("email", email)
                .get()
                .await()

            professorQuery.isEmpty == false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isAdministrador(email: String): Boolean {
        return try {
            val professorQuery = firestore.collection("administradors")
                .whereEqualTo("email", email)
                .get()
                .await()

            professorQuery.isEmpty == false
        } catch (e: Exception) {
            false
        }
    }



    suspend fun addMessage(msg:ChatMessage) : Boolean{
        return try {
            firestore.collection("chat").add(msg).await()
            return true
        } catch (e: Exception){
            false
        }
    }


}