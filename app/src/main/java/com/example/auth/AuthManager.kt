package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import com.example.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sadak_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow(loadUser())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _currentLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("PREF_LANG", "en") ?: "en")
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private fun loadUser(): UserProfile {
        val isGuest = prefs.getBoolean("IS_GUEST", false)
        val name = prefs.getString("USER_NAME", "Aarav Sharma") ?: "Aarav Sharma"
        val email = prefs.getString("USER_EMAIL", "aarav.sharma@example.com") ?: "aarav.sharma@example.com"
        val userId = prefs.getString("USER_ID", "usr_citizen_001") ?: "usr_citizen_001"
        val karma = prefs.getInt("USER_KARMA", 240)
        val lang = prefs.getString("PREF_LANG", "en") ?: "en"

        return UserProfile(
            userId = userId,
            name = name,
            email = email,
            preferredLanguageCode = lang,
            isGuest = isGuest,
            reportsFiledCount = prefs.getInt("REPORTS_FILED", 4),
            reportsFixedCount = prefs.getInt("REPORTS_FIXED", 2),
            karmaPoints = karma,
            citizenRank = if (karma > 200) "Civic Champion ⭐" else "Road Sentinel"
        )
    }

    fun signInWithGoogle(name: String = "Pradeep Viswanathan", email: String = "pradeepviswav@gmail.com") {
        prefs.edit()
            .putBoolean("IS_GUEST", false)
            .putString("USER_NAME", name)
            .putString("USER_EMAIL", email)
            .putString("USER_ID", "usr_google_" + email.hashCode().toString().takeLast(6))
            .apply()
        _currentUser.value = loadUser()
    }

    fun continueAsGuest() {
        prefs.edit()
            .putBoolean("IS_GUEST", true)
            .putString("USER_NAME", "Citizen Guest")
            .putString("USER_EMAIL", "guest@sadakrakshak.in")
            .putString("USER_ID", "guest_anon_user")
            .apply()
        _currentUser.value = loadUser()
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("PREF_LANG", lang.code).apply()
        _currentLanguage.value = lang
        _currentUser.value = _currentUser.value.copy(preferredLanguageCode = lang.code)
    }

    fun addKarmaPoints(points: Int) {
        val newKarma = _currentUser.value.karmaPoints + points
        prefs.edit().putInt("USER_KARMA", newKarma).apply()
        _currentUser.value = _currentUser.value.copy(
            karmaPoints = newKarma,
            citizenRank = if (newKarma > 200) "Civic Champion ⭐" else "Road Sentinel"
        )
    }

    fun incrementReportsFiled() {
        val count = _currentUser.value.reportsFiledCount + 1
        prefs.edit().putInt("REPORTS_FILED", count).apply()
        _currentUser.value = _currentUser.value.copy(reportsFiledCount = count)
    }

    fun incrementReportsFixed() {
        val count = _currentUser.value.reportsFixedCount + 1
        prefs.edit().putInt("REPORTS_FIXED", count).apply()
        _currentUser.value = _currentUser.value.copy(reportsFixedCount = count)
    }
}
