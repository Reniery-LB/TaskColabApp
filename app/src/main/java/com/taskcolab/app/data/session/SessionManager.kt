package com.taskcolab.app.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val isLoggedIn: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_TOKEN].isNullOrBlank().not()
        }

    suspend fun saveSession(
        token: String,
        userId: Int,
        isAdmin: Boolean,
        userName: String,
        userEmail: String
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USER_ID] = userId
            preferences[KEY_IS_ADMIN] = isAdmin
            preferences[KEY_USER_NAME] = userName
            preferences[KEY_USER_EMAIL] = userEmail
        }
    }

    suspend fun getToken(): String? =
        dataStore.data.map { it[KEY_TOKEN] }.first()

    suspend fun getUserId(): Int? =
        dataStore.data.map { it[KEY_USER_ID] }.first()

    suspend fun isAdmin(): Boolean =
        dataStore.data.map { it[KEY_IS_ADMIN] ?: false }.first()

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("session_token")
        val KEY_USER_ID = intPreferencesKey("user_id")
        val KEY_IS_ADMIN = booleanPreferencesKey("is_admin")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }
}
