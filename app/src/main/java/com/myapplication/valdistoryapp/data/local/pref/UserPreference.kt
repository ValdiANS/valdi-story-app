package com.myapplication.valdistoryapp.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
        }
    }

    fun getSession(): Flow<UserModel> =
        dataStore.data.map { preferences ->
            UserModel(
                preferences[USER_ID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
            )
        }
    
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference =
            INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance

                instance
            }
    }
}