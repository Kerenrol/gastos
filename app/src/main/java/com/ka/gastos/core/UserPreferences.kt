package com.ka.gastos.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ka.gastos.features.auth.data.remote.dto.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    suspend fun saveUser(user: User) {
        context.dataStore.edit {
            it[USER_ID] = user.id
            it[USER_NAME] = user.userName
            it[USER_EMAIL] = user.email
        }
    }

    val user: Flow<User?> = context.dataStore.data.map {
        val id = it[USER_ID]
        val name = it[USER_NAME]
        val email = it[USER_EMAIL]

        if (id != null && name != null && email != null) {
            User(id, name, email)
        } else {
            null
        }
    }

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }
}
