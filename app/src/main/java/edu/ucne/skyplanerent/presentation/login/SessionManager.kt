package edu.ucne.skyplanerent.presentation.login

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SessionManager  @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SkyPlaneRentPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_USER_ID, null) != null
    }

    fun saveUserData(userId: String, email: String?) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            email?.let { putString("user_email", it) }
            apply()
        }
    }

    fun getCurrentUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}