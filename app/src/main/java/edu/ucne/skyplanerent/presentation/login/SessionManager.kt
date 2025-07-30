package edu.ucne.skyplanerent.presentation.login

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SkyPlaneRentPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAuthState(user: FirebaseUser) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, user.uid)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getCurrentUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}