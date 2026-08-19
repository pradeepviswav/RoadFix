package com.example.data.model

data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val preferredLanguageCode: String = "en",
    val isGuest: Boolean = false,
    val reportsFiledCount: Int = 3,
    val reportsFixedCount: Int = 1,
    val karmaPoints: Int = 180,
    val citizenRank: String = "Road Guardian Level 2"
)
