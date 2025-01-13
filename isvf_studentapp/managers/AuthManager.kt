package es.efb.isvf_studentapp.managers

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.utils.PREFERENCES_EMAIL_SAVED
import es.efb.isvf_studentapp.utils.PREFERENCES_FILENAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class AuthManager {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val authManager : AuthManager by lazy { AuthManager() }

    suspend fun login(context: Context, email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            // Save UID in SharedPreferences
            val prefs = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putString(es.efb.isvf_studentapp.utils.PREFERENCES_USER_UID, user?.uid)
                apply()
            }

            user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun register(context: Context, email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthWeakPasswordException) {
            withContext(Dispatchers.Main) {
                //"Password should be at least 6 characters"
                Toast.makeText(context, context.getString(R.string.directiva1), Toast.LENGTH_SHORT).show()
            }
            //context.getString(R.string.directiva1)
            null
        } catch (e: FirebaseAuthUserCollisionException) {
            withContext(Dispatchers.Main) {
                //"The email address is already in use by another account."
                Toast.makeText(context, context.getString(R.string.directiva2), Toast.LENGTH_SHORT).show()
            }
            null
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            withContext(Dispatchers.Main) {
                //"The email address is badly formatted."
                Toast.makeText(context, context.getString(R.string.directiva3), Toast.LENGTH_SHORT).show()
            }
            null
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                //"Something went wrong."
                Toast.makeText(context, context.getString(R.string.directiva4), Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
            null
        }
    }

    suspend fun resetPassword(context: Context, email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                //"No se ha encontrado el email."
                Toast.makeText(context, context.getString(R.string.directiva5), Toast.LENGTH_SHORT).show()
            }
            false
        }catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun logOut(){
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser?{
        return auth.currentUser
    }


}